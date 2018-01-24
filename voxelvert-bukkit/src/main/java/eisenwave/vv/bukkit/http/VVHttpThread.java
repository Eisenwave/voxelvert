package eisenwave.vv.bukkit.http;

import com.sun.net.httpserver.HttpServer;
import eisenwave.vv.bukkit.VoxelVertConfig;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.util.HttpUtil;

import java.io.IOException;
import java.net.BindException;
import java.util.logging.Logger;

public class VVHttpThread extends Thread {
    
    private final VoxelVertPlugin plugin;
    
    private String httpHost;
    private boolean startupSuccess = false;
    
    public VVHttpThread(VoxelVertPlugin plugin) {
        this.setName("VoxelVert Http");
        
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        VoxelVertConfig config = plugin.getVVConfig();
        Logger logger = plugin.getLogger();
        
        HttpServer server;
        try {
            server = HttpServer.create(config.getHttpAddress(), 0);
        } catch (BindException e) {
            logger.severe("Failed to start HTTP server because it couldn't be bound to : "
                + config.getHttpAddress()
                + "\nThis may be caused by lack of administrator permissions on your operating system");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            logger.severe("Failed to start HTTP server");
            e.printStackTrace();
            return;
        }
        
        final String path;
        try {
            String configHost = config.getHttpHost().replace("$port", Integer.toString(server.getAddress().getPort()));
            if (configHost.contains("$localhost")) {
                configHost = configHost.replace("$localhost", HttpUtil.getPublicIP());
            }
            httpHost = configHost;
            path = config.getHttpDownloadPath();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        server.createContext("/", new GetRootHandler());
        server.createContext(path, new GetDownloadHandler(plugin));
        server.start();
        startupSuccess = true;
        plugin.setDownloadManager(new DownloadManager(plugin, httpHost));
        
        logger.info("HTTP server was started");
        if (config.hasVerbosityOnEnable()) {
            logger.info("http_host = " + httpHost);
            logger.info("http_download_path = " + path);
        }
    }
    
    public boolean hasStartupSuccess() {
        return startupSuccess;
    }
    
    public String getResolvedHttpHost() {
        return httpHost;
    }
}
