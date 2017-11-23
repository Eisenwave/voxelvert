package org.eisenwave.vv.ui.cmd;

import net.grian.spatium.cache.CacheMath;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.ui.fmtvert.*;
import org.eisenwave.vv.ui.user.VVInventory;
import org.eisenwave.vv.ui.user.VVInventoryVariable;
import org.eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FormatverterInitializer implements VVInitializer {
    
    public final static Option
        OPTION_INPUT = new Option("i", "input"),
        OPTION_OUTPUT = new Option("o", "output"),
        OPTION_REPLACE = new Option("r", "replace"),
        OPTION_VERBOSE = new Option("v", "verbose");
    
    private final static Option[] OPTIONS = {
        OPTION_INPUT,
        OPTION_OUTPUT,
        OPTION_REPLACE,
        OPTION_VERBOSE
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
        final boolean verbose = args.hasKeyword(OPTION_VERBOSE.getId());
        
        if (args.getArgCount() < 2) {
            throw new VVInitializerException(lang.get("main.err.missing_in_out"));
        }
        
        final String input = args.get(0), output = args.get(1);
        final Format inType;
        {
            if (inv.hasVariable(input)) {
                if (args.hasKeyword(OPTION_INPUT.getId())) {
                    String i = args.get(OPTION_INPUT.getId());
                    throw new VVInitializerException(lang.get("main.err.var_in_format_conflict"), i);
                }
                
                VVInventoryVariable var = inv.getVariable(input);
                assert var != null;
                if (!var.isSet())
                    throw new VVInitializerException(lang.get("main.err.var_in_unset"), input);
                
                inType = var.getFormat();
            }
            else if (args.hasKeyword(OPTION_INPUT.getId())) {
                inType = Format.getById(args.get(OPTION_INPUT.getId()));
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
                if (args.hasKeyword(OPTION_OUTPUT.getId())) {
                    String o = args.get(OPTION_OUTPUT.getId());
                    throw new VVInitializerException(lang.get("main.err.var_out_format_conflict"), o);
                }
                
                VVInventoryVariable var = inv.getVariable(output);
                assert var != null;
                
                outType = var.getFormat();
            }
            else if (args.hasKeyword(OPTION_OUTPUT.getId())) {
                outType = Format.getById(args.get(OPTION_OUTPUT.getId()));
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
            if (!args.hasKeyword(param.getId())) {
                throw new VVInitializerException(lang.get("main.err.missing_param"), param);
            }
        }
        
        if (inType.isFile() && !inv.getFile(input).canRead())
            throw new VVInitializerException(lang.get("main.err.file_unreadable", input));
        
        if (outType.isFile() && inv.getFile(output).exists() && !inv.getFile(output).canWrite())
            throw new VVInitializerException(lang.get("main.err.file_unwritable", output));
    
        if (!inv.contains(input))
            throw new VVInitializerException("input %s does not exist", input);
        
        {
            final boolean override = args.hasKeyword(OPTION_REPLACE.getId());
    
            if (inv.contains(output) && !override) {
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
                    this.user.error(ex.getClass().getSimpleName() + " \"" + ex.getMessage() + "\"");
                    
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
        return (index != -1)? filepath.substring(index + 1) : null;
    }
    
}
