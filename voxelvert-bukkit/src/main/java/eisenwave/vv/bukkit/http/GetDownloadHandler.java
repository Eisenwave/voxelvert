package eisenwave.vv.bukkit.http;

import com.google.common.net.MediaType;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.util.HttpUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class GetDownloadHandler implements HttpHandler {
    
    private final VoxelVertPlugin plugin;
    
    public GetDownloadHandler(@NotNull VoxelVertPlugin plugin) {
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
        FileTransferManager.DownloadEntry entry = plugin.getFileTransferManager().removeDownload(id);
        if (entry == null) {
            exchange.sendResponseHeaders(410, -1); // Gone
            return;
        }
        
        MediaType type = entry.getMediaType();
        String disposition = type.is(MediaType.ANY_IMAGE_TYPE)? "inline" : "attachment";
        
        byte[] response = Files.readAllBytes(entry.getFile().toPath());
        
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", type.toString());
        responseHeaders.add("Content-Disposition", disposition + "; filename=\"" + entry.getFile().getName() + "\"");
        exchange.sendResponseHeaders(200, response.length);
        
        try (OutputStream bodyStream = exchange.getResponseBody()) {
            bodyStream.write(response);
        }
        entry.getUser().printLocalized("download.done", entry.getFile().getName());
    }
    
}
