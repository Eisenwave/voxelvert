package eisenwave.vv.bukkit.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eisenwave.spatium.util.Strings;
import eisenwave.torrens.io.DeserializerByteArray;
import eisenwave.torrens.io.TextDeserializerPlain;
import eisenwave.torrens.util.ANSI;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.util.CommandUtil;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class PostUploadHandler implements HttpHandler {
    
    private final static boolean DEBUG_REQUEST = false;
    
    private final VoxelVertPlugin plugin;
    private final boolean verbose;
    private final static String RESPONSE;
    
    static {
        try {
            String[] lines = new TextDeserializerPlain()
                .fromResource(PostUploadHandler.class, "html/upload_response.html");
            RESPONSE = Strings.join("\n", lines);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
    
    public PostUploadHandler(@NotNull VoxelVertPlugin plugin) {
        this.plugin = plugin;
        this.verbose = plugin.getVVConfig().hasVerbosityOnRuntime();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }
    
        String[] path = exchange.getRequestURI().getPath().split("/");
        String id = path[path.length - 1];
    
        FileTransferManager manager = plugin.getFileTransferManager();
        FileTransferManager.UploadEntry entry = manager.getUpload(id);
        if (entry == null) {
            respond(exchange, 410, "Invalid upload token.\nRun the upload command again.");
            return;
        }
    
        String lengthStr = exchange.getRequestHeaders().getFirst("Content-Length");
        if (lengthStr == null) {
            respond(exchange, 412, "File length not specified in HTTP request!\n(Try a different browser)");
            return;
        }
        final int length;
        try {
            length = Integer.parseInt(lengthStr);
            if (length > entry.getMaxSize()) {
                respond(exchange, 413, "File too large!"); // Payload Too Large
                return;
            }
        } catch (NumberFormatException ex) {
            exchange.sendResponseHeaders(412, -1); // Precondition failed
            return;
        }
    
        String[] contentType = exchange.getRequestHeaders().getFirst("Content-Type").split(";", 2);
        for (int i = 0; i < contentType.length; i++) {
            contentType[i] = contentType[i].trim();
            //System.out.println(ANSI.BG_CYAN + contentType[i] + ANSI.BG_RESET);
        }
        //System.out.println(Arrays.toString(contentType.toArray()));
        if (contentType.length < 2 ||
            !contentType[0].equals("multipart/form-data") ||
            !contentType[1].startsWith("boundary="))
            exchange.sendResponseHeaders(412, -1);
        String boundary = contentType[1].substring(9);
    
        if (verbose) {
            String printLength = CommandUtil.printFileSize(length) + " form data";
            plugin.getLogger().info("file upload with id=" + id + " in progress (" + printLength + ") ...");
        }
    
        MultipartFormEntry[] form;
        try (InputStream bodyStream = exchange.getRequestBody();
             BufferedInputStream bufferStream = new BufferedInputStream(bodyStream)) {
            if (DEBUG_REQUEST) {
                byte[] bytes = new DeserializerByteArray().fromStream(bufferStream);
                System.out.println(ANSI.BG_BLUE + new String(bytes) + ANSI.RESET);
                form = new DeserializerMultipartForm(boundary).fromBytes(bytes);
            }
            else
                form = new DeserializerMultipartForm(boundary).fromStream(bufferStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            respond(exchange, 500, "Server error while receiving your file.");
            return;
        }
        String filename = null;
        for (MultipartFormEntry formEntry : form) {
            if ("file".equals(formEntry.getName())) {
                filename = formEntry.getFilename();
                if (filename == null) filename = id;
                filename = sanitizeFilename(entry.getDirectory(), filename);
                if (formEntry.getData().length > entry.getMaxSize()) {
                    respond(exchange, 413, "File too large!");
                    return;
                }
                byte[] bytes = formEntry.getData();
                File file = new File(entry.getDirectory(), filename);
                try {
                    Files.write(file.toPath(), bytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    respond(exchange, 500, "Server error while saving your file.");
                    return;
                }
                if (verbose) {
                    String printFileLength = CommandUtil.printFileSize(bytes.length);
                    plugin.getLogger().info("... id=" + id + ": " + printFileLength + " written to " + file);
                }
            }
            //System.out.println(ANSI.BG_YELLOW + new String(formEntry.getData()).replace("\r", ANSI.BOLD + "CR" + ANSI.RESET + ANSI.BG_YELLOW) + ANSI.BG_RESET);
        }
    
        if (verbose) {
            plugin.getLogger().info("... file upload with id=" + id + " completed");
        }
        manager.removeUpload(id);
    
        entry.getUser().printLocalized("upload.done", filename);
        respond(exchange, 200, "Upload complete! :)");
    }
    
    private void respond(HttpExchange exchange, int code, String msg) throws IOException {
        byte[] responseBytes = RESPONSE
            .replace("$msg", msg)
            .replace("$color", code / 100 == 2? "green" : "red")
            .getBytes(Charset.forName("UTF-8"));
        
        exchange.sendResponseHeaders(code, responseBytes.length);
        try (OutputStream bodyStream = exchange.getResponseBody()) {
            bodyStream.write(responseBytes);
        }
    }
    
    private void readFully(InputStream stream, byte[] bytes) throws IOException {
        for (int off = 0, increment; off < bytes.length; off += increment) {
            int buffSize = 1024;
            if (off + buffSize > bytes.length)
                buffSize = bytes.length - off;
            byte[] buffer = new byte[buffSize];
            increment = stream.read(buffer);
            //System.out.println(increment);
            /* if (read != buffSize) {
                    System.err.println("Read "+read+" into buffer of "+buffSize);
                    exchange.sendResponseHeaders(500, -1);
                    return;
                } */
            System.arraycopy(buffer, 0, bytes, off, buffSize);
        }
    }
    
    private static String sanitizeFilename(File directory, String filename) {
        StringBuilder builder = new StringBuilder();
        for (char c : filename.toCharArray()) {
            if (Character.isAlphabetic(c) || Character.isDigit(c) || c == '.' || c == '_')
                builder.append(c);
            else if (c == ' ')
                builder.append('_');
        }
        filename = builder.toString();
        
        if (filename.length() > 32) {
            String[] nameExt = CommandUtil.nameAndExtensionOf(filename);
            nameExt[0] = nameExt[0].substring(0, Math.max(1, 32 - nameExt.length - 1));
            filename = nameExt[0] + "." + nameExt[1];
        }
        
        if (new File(directory, filename).exists()) {
            String[] nameExt = CommandUtil.nameAndExtensionOf(filename);
            
            for (int i = 1; new File(directory, filename).exists(); i++) {
                filename = nameExt[0] + "_" + i + "." + nameExt[1];
            }
        }
        
        return filename;
    }
    
}
