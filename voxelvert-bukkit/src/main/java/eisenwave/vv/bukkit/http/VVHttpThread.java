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
    
    private HttpServer server;
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
        
        try {
            String configHost = config.getHttpHost().replace("$port", Integer.toString(server.getAddress().getPort()));
            if (configHost.contains("$localhost")) {
                configHost = configHost.replace("$localhost", HttpUtil.getPublicIP());
            }
            httpHost = configHost;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    
        String
            downloadPath = config.getHttpDownloadPath(),
            uploadPath = config.getHttpUploadPath();
        
        server.createContext("/", new GetRootHandler());
        server.createContext(downloadPath, new GetDownloadHandler(plugin));
        server.createContext(uploadPath, new GetUploadHandler(plugin));
        server.createContext("/vv/uploadFile", new PostUploadHandler(plugin));
        server.start();
        startupSuccess = true;
        plugin.setFileTransferManager(new FileTransferManager(plugin, httpHost));
        
        logger.info("HTTP server was started");
        if (config.hasVerbosityOnEnable()) {
            logger.info("http_host = " + httpHost);
            logger.info("http_download_path = " + downloadPath);
            logger.info("http_upload_path = " + uploadPath);
            logger.info("http_upload_file_path = " + "/vv/uploadFile");
        }
    }
    
    @Override
    public void interrupt() {
        if (server != null) {
            plugin.getLogger().info("HTTP server was stopped");
            server.stop(0);
            return;
        }
        super.interrupt();
    }
    
    public boolean hasStartupSuccess() {
        return startupSuccess;
    }
    
    public String getResolvedHttpHost() {
        return httpHost;
    }
}
