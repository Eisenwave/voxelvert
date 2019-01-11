package eisenwave.vv.ui;

import eisenwave.vv.ui.cmd.ShellConversionInitializer;
import eisenwave.vv.ui.cmd.VVInitializerException;
import eisenwave.vv.ui.cmd.VoxelVertTask;
import eisenwave.vv.ui.user.shell.ShellVVUser;
import eisenwave.vv.ui.user.shell.ShellVoxelVert;
import eisenwave.vv.ui.cmd.CommandCall;

public final class Main {
    
    private Main() {}
    
    public static void main(String[] args) throws Exception {
        ShellVoxelVert vv = new ShellVoxelVert();
        ShellVVUser user = new ShellVVUser(vv);
    
        // make sure that all formats are loaded
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
    }
    
}
