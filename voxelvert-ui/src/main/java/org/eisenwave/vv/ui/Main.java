package org.eisenwave.vv.ui;

import org.eisenwave.vv.ui.cmd.CommandCall;
import org.eisenwave.vv.ui.cmd.ShellConversionInitializer;
import org.eisenwave.vv.ui.cmd.VVInitializerException;
import org.eisenwave.vv.ui.cmd.VoxelVertTask;
import org.eisenwave.vv.ui.user.shell.ShellVVUser;
import org.eisenwave.vv.ui.user.shell.ShellVoxelVert;

public final class Main {
    
    private Main() {}
    
    public static void main(String[] args) throws Exception {
        ShellVoxelVert vv = new ShellVoxelVert();
        ShellVVUser user = new ShellVVUser(vv);
    
        // make sure that all formats are loaded
        // noinspection ResultOfMethodCallIgnored
        //FormatverterFactory.getInstance().getInputFormats();
        ShellConversionInitializer cmd = new ShellConversionInitializer();
        
        CommandCall call = new CommandCall();
        call.setStrictOrder(true);
        call.setStrictKwArgs(true);
        cmd.getAcceptedOptions().forEach(call::addKeyword);
        call.parse(args);
    
        try {
            VoxelVertTask task = cmd.execute(user, call);
            if (task != null)
                task.run();
        } catch (VVInitializerException ex) {
            user.error(ex.getMessage());
        } finally {
            user.update(null);
        }
        System.out.println();
    }
    
}
