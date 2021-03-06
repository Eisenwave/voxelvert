package eisenwave.vv.bukkit.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eisenwave.torrens.io.DeserializerByteArray;
import eisenwave.torrens.io.DeserializerString;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.vv.bukkit.util.HttpUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

public class GetUploadHandler implements HttpHandler {
    
    private final static String TEMPLATE;
    
    static {
        try {
            TEMPLATE = new DeserializerString().fromResource(GetUploadHandler.class, "html/upload.html");
        } catch (IOException ex) {
            throw new IOError(ex);
        }
    }
    
    private final VoxelVertPlugin plugin;
    
    public GetUploadHandler(@NotNull VoxelVertPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }
        
        Map<String, String> query = HttpUtil.parseQuery(exchange.getRequestURI().getQuery());
        if (!query.containsKey("id")) {
            exchange.sendResponseHeaders(404, -1); // Not Found
            return;
        }
        
        String id = query.get("id");
        FileTransferManager.UploadEntry entry = plugin.getFileTransferManager().getUpload(id);
        if (entry == null) {
            exchange.sendResponseHeaders(410, -1); // Gone
            return;
        }
    
        byte[] response = TEMPLATE
            .replace("$id", id)
            .replace("$max", CommandUtil.printFileSize(entry.getMaxSize()))
            .getBytes(Charset.forName("US-ASCII"));
        
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, response.length);
        
        try (OutputStream bodyStream = exchange.getResponseBody()) {
            bodyStream.write(response);
        }
    }
    
}
