package eisenwave.vv.bukkit.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.util.HttpUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class PostUploadHandler implements HttpHandler {
    
    private final VoxelVertPlugin plugin;
    
    public PostUploadHandler(@NotNull VoxelVertPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }
        
        Map<String, String> query = HttpUtil.parseQuery(exchange.getRequestURI().getQuery());
        if (!query.containsKey("id")) {
            exchange.sendResponseHeaders(404, -1); // Not Found
            return;
        }
        
        FileTransferManager manager = plugin.getFileTransferManager();
        FileTransferManager.UploadEntry entry = manager.getUpload(query.get("id"));
        if (entry == null) {
            exchange.sendResponseHeaders(410, -1); // Not Found
            return;
        }
        
        System.out.println("receiving upload");
        
        try (InputStream bodyStream = exchange.getRequestBody()) {
            bodyStream.close();
        }
    }
    
}
