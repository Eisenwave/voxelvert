package eisenwave.vv.bukkit.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class GetRootHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }
        
        @SuppressWarnings("StringBufferReplaceableByString")
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("VoxelVert server started\n");
        
        byte[] response = responseBuilder.toString().getBytes();
        
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, response.length);
        
        try (OutputStream bodyStream = exchange.getResponseBody()) {
            bodyStream.write(response);
        }
    }
    
}
