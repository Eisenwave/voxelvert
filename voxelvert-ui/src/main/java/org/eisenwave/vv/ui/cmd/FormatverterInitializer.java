package org.eisenwave.vv.ui.cmd;

import net.grian.spatium.cache.CacheMath;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.ui.fmtvert.*;
import org.eisenwave.vv.ui.user.VVInventory;
import org.eisenwave.vv.ui.user.VVInventoryVariable;
import org.eisenwave.vv.ui.user.VVUser;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FormatverterInitializer implements VVInitializer {
    
    @RegExp
    private final static String
    PARAM_INPUT   = "^(i|input)$",
    PARAM_OUTPUT  = "^(o|output)$",
    PARAM_REPLACE = "^(r|replace)$",
    PARAM_VERBOSE = "^(v|verbose)$";
    
    private final static Option[] OPTIONS = {
        new Option("i", "input"),
        new Option("o", "output"),
        new Option("r", "replace"),
        new Option("v", "verbose")
    };
    
    @Override
    public Set<Option> getAcceptedOptions() {
        Set<Option> result = new HashSet<>();
    
        for (Formatverter fv : FormatverterFactory.getInstance().getFormatverters()) {
            result.addAll(Arrays.asList(fv.getMandatoryOptions()));
            result.addAll(Arrays.asList(fv.getOptionalOptions()));
        }
    
        result.addAll(Arrays.asList(OPTIONS));
        
        return result;
    }
    
    @Override
    public VoxelVertTask execute(VVUser user, CommandCall args) throws VVInitializerException {
        final VVInventory inv = user.getInventory();
        final Language lang = user.getVoxelVert().getLanguage();
        final boolean verbose = args.matchKeyword(PARAM_VERBOSE);
        
        if (args.getArgCount() < 2) {
            throw new VVInitializerException(lang.get("main.err.missing_in_out"));
        }
    
        final String input = args.get(0), output = args.get(1);
        final Format inType;
        {
            if (inv.hasVariable(input)) {
                if (args.matchKeyword(PARAM_INPUT)) {
                    String i = args.getMatch(PARAM_INPUT);
                    throw new VVInitializerException(lang.get("main.err.var_in_format_conflict"), i);
                }
        
                VVInventoryVariable var = inv.getVariable(input);
                assert var != null;
                if (!var.isSet())
                    throw new VVInitializerException(lang.get("main.err.var_in_unset"), input);
                
                inType = var.getFormat();
            }
            else if (args.matchKeyword(PARAM_INPUT)) {
                inType = Format.getById(args.getMatch(PARAM_INPUT));
                if (inType == null)
                    throw new VVInitializerException(lang.get("main.err.unknown_format_i"), args.get("i"));
            }
            else {
                String inExt = extensionOf(input);
                if (inExt == null)
                    throw new VVInitializerException(lang.get("main.err.missing_ext_i"));
                inType = Format.getByExtension(inExt);
                if (inType == null)
                    throw new VVInitializerException(lang.get("main.err.unknown_ext_i"), inExt);
            }
        }
        
        final Format outType;
        {
            if (inv.hasVariable(output)) {
                if (args.matchKeyword(PARAM_OUTPUT)) {
                    String o = args.getMatch(PARAM_OUTPUT);
                    throw new VVInitializerException(lang.get("main.err.var_out_format_conflict"), o);
                }
        
                VVInventoryVariable var = inv.getVariable(output);
                assert var != null;
    
                outType = var.getFormat();
            }
            else if (args.matchKeyword(PARAM_OUTPUT)) {
                outType = Format.getById(args.getMatch(PARAM_OUTPUT));
                if (outType == null)
                    throw new VVInitializerException(lang.get("main.err.unknown_format_o"), args.get("o"));
            }
            else {
                String outExt = extensionOf(output);
                if (outExt == null)
                    throw new VVInitializerException(lang.get("main.err.missing_ext_o"));
                outType = Format.getByExtension(outExt);
                if (outType == null) {
                    throw new VVInitializerException(lang.get("main.err.unknown_ext_o"), outExt);
                }
            }
        }
    
        Formatverter fmtverter = FormatverterFactory.getInstance().fromFormats(inType, outType);
        if (fmtverter == null) {
            throw new VVInitializerException(lang.get("conv.err.unsupported"), inType, outType);
        }
    
        for (Option param : fmtverter.getMandatoryOptions()) {
            if (!args.matchKeyword(param)) {
                throw new VVInitializerException(lang.get("main.err.missing_param"), param);
            }
        }
    
        if (inType.isFile() && !inv.getFile(input).canRead())
            throw new VVInitializerException(lang.get("main.err.file_unreadable", input));
    
        if (outType.isFile() && inv.getFile(output).exists() && !inv.getFile(output).canWrite())
            throw new VVInitializerException(lang.get("main.err.file_unwritable", output));
    
        if (!inv.contains(inType, input))
            throw new VVInitializerException("input %s does not exist", input);
    
        {
            final boolean override = args.matchKeyword(PARAM_REPLACE);
        
            if (inv.contains(outType, output) && !override) {
                throw new VVInitializerException(lang.get("main.err.overwrite"));
            }
        }
    
        return new VoxelVertTask(user, inType, input, outType, output) {
            
            private final ProgressListener listener = (now, max, rel) -> this.set(now);
            private final int maxProgress = fmtverter.getMaxProgress();
            
            @Override
            protected int getMaxProgress() {
                return maxProgress;
            }
    
            @Override
            public void run() {
                Language lang = this.user.getVoxelVert().getLanguage();
                
                user.print(lang.get("conv.ongoing"), sourceFormat, source, targetFormat, target);
                long now = System.currentTimeMillis();
                
                try {
                    fmtverter.addListener(listener);
                    fmtverter.convert(getUser(), source, target, args.getKwArgs());
        
                    long millis = System.currentTimeMillis() - now;
                    double secs = millis / CacheMath.THOUSAND;
                    user.print(lang.get("main.done"), millis, secs);
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    this.user.error(ex.getClass().getSimpleName()+" \""+ex.getMessage()+"\"");
                    
                } finally {
                    // remove listener from formatverter
                    fmtverter.removeListener(listener);
                }
            }
            
        };
    }
    
    // UTIL
    
    @Nullable
    public static String extensionOf(String filepath) {
        int index = filepath.lastIndexOf('.');
        return  (index != -1)? filepath.substring(index+1) : null;
    }
    
}
