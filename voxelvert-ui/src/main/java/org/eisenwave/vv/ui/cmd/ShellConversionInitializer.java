package org.eisenwave.vv.ui.cmd;

import eisenwave.commons.io.ANSI;
import org.apache.commons.io.IOUtils;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.ui.fmtvert.Format;
import org.eisenwave.vv.ui.fmtvert.FormatverterFactory;
import org.eisenwave.vv.ui.fmtvert.ProgressListener;
import org.eisenwave.vv.ui.user.VVInventory;
import org.eisenwave.vv.ui.user.VVUser;
import org.eisenwave.vv.ui.util.StringProgressBar;
import org.eisenwave.vv.ui.util.StringTable;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.*;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

/**
 * <p>
 *     An initializer which wraps a {@link FormatverterInitializer} and extends it by adding options for seeing the
 *     help, seeing a list of formats, setting a task timeout and setting the number of threads to be used.
 * </p>
 * <p>
 *     This initializer is intended to be called directly by the main function and used for shell-purposes.
 * </p>
 */
public class ShellConversionInitializer implements VVInitializer {
    
    @RegExp
    public final static String
    PARAM_FORMATS = "^(f|formats)$",
    PARAM_HELP    = "^(h|help)$",
    //PARAM_TIMEOUT = "^(t|timeout)$",
    //PARAM_THREADS = "^(T|threads)$",
    PARAM_VERBOSE = "^(v|verbose)$";
    
    private final static String[] OPTIONS = {
        "f", "formats",
        "h", "help"
        //"t", "timeout",
        //"T", "threads",
    };
    
    public final static long TIMEOUT = 5000;
    
    private final VVInitializer handle = new FormatverterInitializer();
    
    @Override
    public Set<String> getAcceptedOptions() {
        Set<String> result = new HashSet<>();
        result.addAll(handle.getAcceptedOptions());
        result.addAll(Arrays.asList(OPTIONS));
        
        return result;
    }
    
    @Nullable
    public VoxelVertTask execute(VVUser user, CommandCall args) throws VVInitializerException {
        Language lang = user.getVoxelVert().getLanguage();
        VVInventory inv = user.getInventory();
        //String usage = ChatColor.RED+"Usage: /"+label+" ";
    
        final boolean verbose = args.matchKeyword(PARAM_VERBOSE);
        
        if (verbose) {
            user.print(lang.get("main.verbose"));
            user.print(lang.get("main.wd"), inv.getDirectory());
            //user.print(lang.get("main.lang_info"), lang.getName(), lang.size());
        }
        
        if (args.isEmpty() || args.matchKeyword(PARAM_HELP)) {
            try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("help.txt")) {
                //noinspection unchecked
                for (String s : (List<String>) IOUtils.readLines(stream)) {
                    user.print(s);
                }
            } catch (Throwable ex) {
                user.error(lang.get("main.err.help"), ex.getClass().getSimpleName());
                if (verbose)
                    ex.printStackTrace();
            }
            
            return null;
        }
        
        if (args.matchKeyword(PARAM_FORMATS)) {
            user.print("List of available formats:\n");
            
            StringTable table = new StringTable(16, 3, "    ");
            table.add("FORMAT:", "EXTENSION(S):", "CONVERTIBLE TO:");
            table.addEmpty();
            
            FormatverterFactory factory = FormatverterFactory.getInstance();
            final Format[] inputs;
            {
                Set<Format> inputSet = factory.getInputFormats();
                if (inputSet.isEmpty()) throw new VVInitializerException("No input formats detected!");
                
                inputs = inputSet.toArray(new Format[inputSet.size()]);
                Arrays.sort(inputs);
            }
            
            for (Format f : inputs) {
                Format[] outputs = factory.getOutputFormats(f);
                if (outputs.length == 0) continue;
                
                Arrays.sort(outputs);
                String convertible = Arrays.toString(outputs);
                convertible = convertible.substring(1, convertible.length()-1).toLowerCase();
    
                String extensions = Arrays.toString(f.getExtensions());
                extensions = extensions.substring(1, extensions.length()-1);
                
                table.add(
                    f.toString(),
                    extensions,
                    convertible);
            }
            
            for (int i = 0; i < table.size(); i++)
                user.print(table.printRow(i));
                
            return null;
        }
        
        /*
        final long timeout;
        {
            if (args.matchKeyword(PARAM_TIMEOUT)) {
                String timeoutStr = args.getMatch(PARAM_TIMEOUT);
                try {
                    timeout = Long.parseLong(timeoutStr);
                } catch (NumberFormatException ex) {
                    throw new VVInitializerException(lang.get("main.parse.timeout.num"), timeoutStr);
                }
    
                if (timeout < 0) {
                    throw new VVInitializerException(lang.get("main.parse.timeout.positive"), timeoutStr);
                }
    
                if (verbose) user.print(lang.get("main.parse.timeout"), timeout);
            }
            
            else timeout = 0;
        }
        
        final int threads;
        {
            if (args.matchKeyword(PARAM_THREADS)) {
                String threadsStr = args.getMatch(PARAM_THREADS);
                try {
                    threads = Integer.parseInt(threadsStr);
                } catch (NumberFormatException ex) {
                    throw new VVInitializerException(lang.get("main.parse.threads.num"), threadsStr);
                }
        
                if (timeout < 0) {
                    throw new VVInitializerException(lang.get("main.parse.threads.min"), threadsStr);
                }
        
                if (verbose) user.print(lang.get("main.parse.threads"), threads);
            }
        
            else threads = 1;
        }
        */
        
        VoxelVertTask result = handle.execute(user, args);
        
        if (user.acceptsUpdates()) {
            final String
                prefix = ANSI.BOLD, suffix = ANSI.BOLD_OFF,
                preOn = ANSI.FG_GREEN, sufOn = ANSI.FG_RESET,
                updateFormat = prefix+"Progress at %03d%% [%s]"+suffix;
    
            StringProgressBar bar = new StringProgressBar(preOn, '\u2588', sufOn, "", '=', "", 50);
    
            ProgressListener listener = (now, max, rel) ->
                user.update(updateFormat, (int) (rel * 100), bar.print(rel));
    
            // we can safely add a listener without ever removing it because vv-tasks are throwaway objects and only
            // meant to be run once
            result.addListener(listener);
            
            listener.update(0, 1, 0);
        }
        
        return result;
    }
    
    public static void runWithoutTimeout(Runnable task, int threads) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        
        Future<?> future = executor.submit(task);
        executor.shutdown();
        
        future.get();
    
        if (!executor.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS)) {
            executor.shutdownNow();
        }
    }
    
    public static void runWithTimeout(Runnable task, int threads, long maxMillis) throws ExecutionException,
        InterruptedException, TimeoutException {
        
        ExecutorService executor = Executors.newFixedThreadPool(threads);
    
        Future<?> future =  executor.submit(task);
        executor.shutdown();
    
        try {
            future.get(maxMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            future.cancel(true);
            throw ex;
        }
        
        if (!executor.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS)) {
            executor.shutdownNow();
        }
    }
    
    @Nullable
    public static String extensionOf(String filepath) {
        int index = filepath.lastIndexOf('.');
        return  (index != -1)? filepath.substring(index+1) : null;
    }
    
    
    
}
