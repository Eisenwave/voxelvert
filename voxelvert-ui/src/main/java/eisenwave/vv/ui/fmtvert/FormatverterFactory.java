package eisenwave.vv.ui.fmtvert;

import eisenwave.vv.clsvert.*;
import eisenwave.vv.object.Language;
import eisenwave.spatium.enums.Direction;
import eisenwave.spatium.enums.Face;
import eisenwave.torrens.img.Texture;
import eisenwave.torrens.object.Vertex3i;
import eisenwave.torrens.schematic.BlockStructure;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.torrens.wavefront.MTLLibrary;
import eisenwave.torrens.wavefront.OBJModel;
import eisenwave.vv.object.MCModel;
import eisenwave.vv.rp.BlockColorTable;
import eisenwave.vv.io.DeserializerBCT;
import eisenwave.vv.io.DeserializerBCE;
import eisenwave.vv.rp.BlockColorExtractor;
import eisenwave.vv.ui.error.FormatverterArgumentException;
import eisenwave.vv.ui.user.VVUser;
import eisenwave.torrens.voxel.QBModel;
import eisenwave.torrens.stl.STLModel;
import eisenwave.torrens.voxel.VoxelMesh;
import eisenwave.vv.ui.util.Sets;
import org.jetbrains.annotations.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import static eisenwave.vv.ui.fmtvert.Format.*;

@SuppressWarnings("Duplicates")
public final class FormatverterFactory {
    
    private final static Option
        OPTION_COLORS = new Option("c", "colors"),
        OPTION_CROP = new Option("C", "crop"),
        OPTION_DIRECTION = new Option("d", "direction"),
        OPTION_NO_ANTI_BLEED = new Option("b", "no_anti_bleed"),
        OPTION_RESOLUTION = new Option("R", "resolution"),
        OPTION_VERBOSE = new Option("v", "verbose");
    
    public final static String
        DEFAULT_BCE = "color_extractors/default.json",
        DEFAULT_BCT = "colors/default.bct";
    
    private final static Logger VERBOSE_LOGGER;
    
    static {
        VERBOSE_LOGGER = Logger.getLogger("vv");
        VERBOSE_LOGGER.setLevel(Level.FINE);
    }
    
    private final static FormatverterFactory instance = new FormatverterFactory();
    
    @Contract(pure = true)
    public static FormatverterFactory getInstance() {
        return instance;
    }
    
    private final FormatverterMap map = new FormatverterMap();
    
    public FormatverterFactory() {
        final Supplier<Formatverter>
            fv_va_qef = () -> new InventoryFormatverter(VOXEL_ARRAY, QEF),
            //fv_schematic_ba = () -> new InventoryFormatverter(SCHEMATIC, BLOCK_ARRAY),
            fv_qef_va = () -> new InventoryFormatverter(QEF, VOXEL_ARRAY),
            fv_schematic_va = () -> new CompoundFormatverter(
                new InventoryFormatverter(SCHEMATIC, BLOCK_ARRAY),
                new FV_BA_VA());
        
        put(BLOCK_ARRAY, IMAGE, () -> new CompoundFormatverter(new FV_BA_VA(), new FV_VA_IMAGE()));
        put(BLOCK_ARRAY, MODEL, () -> new CompoundFormatverter(new FV_BA_VA(), new FV_VA_MODEL()));
        put(BLOCK_ARRAY, QB, () -> new CompoundFormatverter(new FV_BA_VA(), new FV_VA_QB()));
        put(BLOCK_ARRAY, QEF, () -> new CompoundFormatverter(new FV_BA_VA(), fv_va_qef.get()));
        put(BLOCK_ARRAY, SCHEMATIC, FV_BA_SCHEMATIC::new);
        put(BLOCK_ARRAY, WAVEFRONT, () -> new CompoundFormatverter(new FV_BA_VA(), new FV_VA_WAVEFRONT()));
    
        put(IMAGE, IMAGE, FV_IMAGE_IMAGE::new);
        put(IMAGE, QEF, () -> new CompoundFormatverter(new FV_IMAGE_VA(), fv_va_qef.get()));
        put(IMAGE, QB, () -> new CompoundFormatverter(new FV_IMAGE_VA(), new FV_VA_QB()));
    
        put(QB, IMAGE, () -> new CompoundFormatverter(new FV_QB_VA(), new FV_VA_IMAGE()));
        put(QB, MODEL, () -> new CompoundFormatverter(new FV_QB_VA(), new FV_VA_MODEL()));
        put(QB, QB, CopyFormatverter::new);
        put(QB, QEF, () -> new CompoundFormatverter(new FV_QB_VA(), fv_va_qef.get()));
        put(QB, SCHEMATIC, () -> new CompoundFormatverter(new FV_QB_VA(), new FV_VA_SCHEMATIC()));
        put(QB, STL, () -> new CompoundFormatverter(new FV_QB_VA(), new FV_VA_STL()));
        put(QB, WAVEFRONT, () -> new CompoundFormatverter(new FV_QB_VA(), new FV_VA_WAVEFRONT()));
    
        put(QEF, IMAGE, () -> new CompoundFormatverter(fv_qef_va.get(), new FV_VA_IMAGE()));
        put(QEF, MODEL, () -> new CompoundFormatverter(fv_qef_va.get(), new FV_VA_MODEL()));
        put(QEF, QB, () -> new CompoundFormatverter(fv_qef_va.get(), new FV_VA_QB()));
        put(QEF, QEF, CopyFormatverter::new);
        put(QEF, SCHEMATIC, () -> new CompoundFormatverter(fv_qef_va.get(), new FV_VA_SCHEMATIC()));
        put(QEF, STL, () -> new CompoundFormatverter(fv_qef_va.get(), new FV_VA_STL()));
        put(QEF, WAVEFRONT, () -> new CompoundFormatverter(fv_qef_va.get(), new FV_VA_WAVEFRONT()));
    
        put(RESOURCE_PACK, BLOCK_COLOR_TABLE, FV_RP_COLORS::new);
        put(RESOURCE_PACK, RESOURCE_PACK, CopyFormatverter::new);
    
        put(SCHEMATIC, IMAGE, () -> new CompoundFormatverter(fv_schematic_va.get(), new FV_VA_IMAGE()));
        put(SCHEMATIC, MODEL, () -> new CompoundFormatverter(fv_schematic_va.get(), new FV_VA_MODEL()));
        put(SCHEMATIC, QB, () -> new CompoundFormatverter(fv_schematic_va.get(), new FV_VA_QB()));
        put(SCHEMATIC, QEF, () -> new CompoundFormatverter(fv_schematic_va.get(), fv_va_qef.get()));
        put(SCHEMATIC, SCHEMATIC, CopyFormatverter::new);
        put(SCHEMATIC, STL, () -> new CompoundFormatverter(fv_schematic_va.get(), new FV_VA_STL()));
        put(SCHEMATIC, WAVEFRONT, () -> new CompoundFormatverter(fv_schematic_va.get(), new FV_VA_WAVEFRONT()));
    
        put(STL, MODEL, () -> new CompoundFormatverter(new FV_STL_VA(), new FV_VA_MODEL()));
        put(STL, QEF, () -> new CompoundFormatverter(new FV_STL_VA(), fv_va_qef.get()));
        put(STL, QB, () -> new CompoundFormatverter(new FV_STL_VA(), new FV_VA_QB()));
        put(STL, SCHEMATIC, () -> new CompoundFormatverter(new FV_STL_VA(), new FV_VA_SCHEMATIC()));
        put(STL, STL, CopyFormatverter::new);
    
        put(WAVEFRONT, IMAGE, () -> new CompoundFormatverter(new FV_WAVEFRONT_VA(), new FV_VA_IMAGE()));
        put(WAVEFRONT, MODEL, () -> new CompoundFormatverter(new FV_WAVEFRONT_VA(), new FV_VA_MODEL()));
        put(WAVEFRONT, QB, () -> new CompoundFormatverter(new FV_WAVEFRONT_VA(), new FV_VA_QB()));
        put(WAVEFRONT, QEF, () -> new CompoundFormatverter(new FV_WAVEFRONT_VA(), fv_va_qef.get()));
        put(WAVEFRONT, SCHEMATIC, () -> new CompoundFormatverter(new FV_WAVEFRONT_VA(), new FV_VA_SCHEMATIC()));
        put(WAVEFRONT, STL, FV_WAVEFRONT_STL::new);
        put(WAVEFRONT, WAVEFRONT, CopyFormatverter::new);
    }
    
    // API
    
    /**
     * Returns a {@link Formatverter} which converts between a given source and target format.
     *
     * @param source the source format
     * @param target the target format
     * @return a formatverter for the two formats
     */
    @Nullable
    public Formatverter fromFormats(Format source, Format target) {
        return map.get(source, target);
    }
    
    /**
     * Returns a set containing all possible input formats.
     *
     * @return all input formats
     */
    public Set<Format> getInputFormats() {
        return map.sourceSet();
    }
    
    /**
     * Returns a set containing all possible output formats.
     *
     * @return all output formats
     */
    public Set<Format> getOutputFormats() {
        return map.targetSet();
    }
    
    /**
     * Returns a set containing all formatverters.
     *
     * @return all formatverters
     */
    public Collection<Formatverter> getFormatverters() {
        return map.getFormatverters();
    }
    
    /**
     * Returns all input formats which can be converted into a given output format.
     *
     * @param output the output format
     * @return all the output format's input formats
     */
    public Format[] getInputFormats(Format output) {
        return map.getInputFormats(output);
    }
    
    /**
     * Returns all output formats which can be converted from a given input format.
     *
     * @param input the input format
     * @return all the input format's output formats
     */
    public Format[] getOutputFormats(Format input) {
        return map.getOutputFormats(input);
    }
    
    /**
     * Registers a new supplier of formatverters for a given input format and output format.
     *
     * @param input the input format
     * @param output the target format
     * @param supplier the supplier of formatverters for the given formats
     * @return true if no previous formatverter was replace by this operation, else false
     */
    public boolean put(@NotNull Format input, @NotNull Format output,
                       @NotNull Supplier<? extends Formatverter> supplier) {
        return map.put(input, output, supplier);
    }
    
    // FIRST ORDER FORMATVERTERS
    
    private static class FV_BA_SCHEMATIC extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 2;
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            //Logger logger = verbose? user.getLogger() : null;
    
            BlockStructure blocks = (BlockStructure) user.getInventory().load(BLOCK_ARRAY, from);
            assert blocks != null;
            if (verbose) user.print(lang.get("from_block_array.blocks"), blocks.getBlockCount());
            set(1);
    
            user.getInventory().save(SCHEMATIC, blocks, to);
            set(2);
        }
        
    }
    
    private static class FV_BA_VA extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 4;
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_COLORS, OPTION_VERBOSE);
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            BlockColorTable bct;
            {
                String optBctId = OPTION_COLORS.getId();
                if (!args.containsKey(optBctId)) {
                    bct = defaultBCT();
                }
                else {
                    String bctPath = args.get(optBctId);
                    bct = (BlockColorTable) user.getInventory().load(BLOCK_COLOR_TABLE, bctPath);
                    if (bct == null)
                        throw new FormatverterArgumentException(OPTION_COLORS, "no BCT found at " + bctPath);
                }
            }
            if (verbose) user.print(lang.get("from_colors.colors"), bct.size());
            set(1);
    
            BlockStructure blocks = (BlockStructure) user.getInventory().load(BLOCK_ARRAY, from);
            set(2);
            
            assert blocks != null;
            if (verbose) user.print(lang.get("from_block_array.blocks"), blocks.getBlockCount());
    
            VoxelArray voxels = new CvBlocksToVoxelArray().invoke(blocks, bct, 0);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(3);
    
            user.getInventory().save(VOXEL_ARRAY, voxels, to);
            set(4);
        }
        
    }
    
    private static class FV_IMAGE_IMAGE extends Formatverter {
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @Override
        public int getMaxProgress() {
            return 2;
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Texture img = (Texture) user.getInventory().load(IMAGE, from);
            assert img != null;
            set(1);
    
            user.getInventory().save(IMAGE, img, to);
            set(2);
        }
        
    }
    
    private static class FV_IMAGE_VA extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 3;
        }
        
        @Override
        public Set<Option> getMandatoryOptions() {
            return Sets.ofArray(OPTION_DIRECTION);
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            String d = args.get(OPTION_DIRECTION.getId());
            Direction dir = parseDirection(d);
            //Logger logger = verbose? user.getLogger() : null;
            
            if (verbose) user.print(lang.get("from_image.face"), from, d, dir);
    
            Texture img = (Texture) user.getInventory().load(IMAGE, from);
            set(1);
            
            VoxelArray va = new CvTextureToVoxelArray().invoke(img, dir);
            set(2);
    
            user.getInventory().save(VOXEL_ARRAY, va, to);
            set(3);
        }
        
    }
    
    private static class FV_QB_VA extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 4;
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            //Language lang = user.getVoxelVert().getLanguage();
            //Logger logger = verbose? user.getLogger() : null;
    
            QBModel model = (QBModel) user.getInventory().load(QB, from);
            set(1);
    
            assert model != null;
            VoxelMesh vm = new CvQBToVoxelMesh().invoke(model);
            set(2);
    
            VoxelArray va = new CvVoxelMeshToVoxelArray().invoke(vm);
            set(3);
    
            user.getInventory().save(VOXEL_ARRAY, va, to);
            set(4);
        }
        
    }
    
    private static class FV_RP_COLORS extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 4;
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            //Logger logger = verbose? user.getLogger() : null;
    
            BlockColorExtractor extractor = new DeserializerBCE().fromResource(getClass(), DEFAULT_BCE);
            set(1);
            if (verbose) {
                String grass = extractor.getGrassMap();
                if (grass != null) user.print(lang.get("from_rp.grass"), grass);
                else user.print(lang.get("from_rp.no_grass"));
        
                String foliage = extractor.getFoliageMap();
                if (foliage != null) user.print(lang.get("from_rp.foliage"), foliage);
                else user.print(lang.get("from_rp.no_foliage"));
            }
    
            ZipFile zip = (ZipFile) user.getInventory().load(RESOURCE_PACK, from);
            if (verbose) user.print(lang.get("from_rp.colors"), extractor.size(), from);
            set(2);
    
            BlockColorTable colors = extractor.extract(zip);
            set(3);
    
            user.getInventory().save(BLOCK_COLOR_TABLE, colors, to);
            set(4);
        }
        
    }
    
    private static class FV_STL_VA extends Formatverter {
        
        @Override
        public Set<Option> getMandatoryOptions() {
            return Sets.ofArray(OPTION_RESOLUTION);
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @Override
        public int getMaxProgress() {
            return 3;
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            Logger logger = verbose? user.getLogger() : null;
            int res = parseInt(OPTION_RESOLUTION, args.get(OPTION_RESOLUTION.getId()), IntegerType.NATURAL);
            
            if (verbose) user.print(lang.get("from_stl.canvas"), res, res, res);
            
            STLModel stl = (STLModel) user.getInventory().load(STL, from);
            assert stl != null;
            if (verbose) user.print(lang.get("from_stl.triangles"), stl.size());
            set(1);
            
            VoxelArray voxels = new CvSTLToVoxelArray(logger).invoke(stl, res);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(2);
            
            user.getInventory().save(VOXEL_ARRAY, voxels, to);
            set(3);
        }
        
    }
    
    private static class FV_VA_IMAGE extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 5;
        }
        
        @Override
        public Set<Option> getMandatoryOptions() {
            return Sets.ofArray(OPTION_DIRECTION);
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE, OPTION_CROP);
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            boolean crop = args.containsKey(OPTION_CROP.getId());
            String d = args.get(OPTION_DIRECTION.getId());
            Direction dir = parseDirection(d);
            Logger logger = verbose? user.getLogger() : null;
            
            /*
            VoxelArray voxels;
            {
                BlockStructure blocks = (BlockStructure) user.getInventory().load(BLOCK_ARRAY, from);
                set(1);
                
                BlockColorTable colors = defaultBCT();
                set(2);
                
                assert blocks != null;
                if (verbose) {
                    user.print(lang.get("from_block_array.blocks"), blocks.getBlockCount());
                    user.print(lang.get("from_colors.colors"), colors.size());
                }
                
                voxels = new CvBlocksToVoxelArray().invoke(blocks, colors, 0);
                set(3);
            }
            */
            VoxelArray voxels = (VoxelArray) user.getInventory().load(VOXEL_ARRAY, from);
            assert voxels != null;
            set(1);
            if (verbose) {
                user.print(lang.get("to_voxels.voxels"), voxels.size());
                user.print(lang.get("to_image.render"), from, d, dir);
            }
    
            Texture image = new CvVoxelArrayToTexture(logger).invoke(voxels, dir, true, crop);
            if (verbose) user.print(lang.get("to_image.crop"), image.getWidth(), image.getHeight());
            set(4);
    
            user.getInventory().save(IMAGE, image, to);
            set(5);
        }
        
    }
    
    private static class FV_VA_MODEL extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 5;
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE, OPTION_NO_ANTI_BLEED);
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            boolean noAntiBleed = args.containsKey(OPTION_NO_ANTI_BLEED.getId());
            Logger logger = verbose? user.getLogger() : null;
    
            VoxelArray voxels = (VoxelArray) user.getInventory().load(VOXEL_ARRAY, from);
            assert voxels != null;
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(1);
            
            VoxelMesh mesh = new CvVoxelArrayToVoxelMesh(logger).invoke(voxels);
            set(2);
            if (verbose) user.print(lang.get("to_mesh.elements"), mesh.size());
            
            MCModel model = new CvVoxelMeshToMC(logger).invoke(mesh);
            set(3);
            if (verbose) {
                user.print(lang.get("to_model.elements"), model.getElementCount());
                switch (model.getTextureCount()) {
                    
                    case 0: user.print(lang.get("to_model.textures.none")); break;
                    
                    case 1: {
                        Texture texture = model.getTexture(model.getTextures().iterator().next());
                        int w = texture.getWidth(), h = texture.getHeight();
                        user.print(lang.get("to_model.textures.single"), w, h);
                        break;
                    }
                    
                    default: user.print(lang.get("to_model.textures.multiple"), model.getTextureCount()); break;
                }
            }
            model.setAntiBleed(!noAntiBleed);
            if (noAntiBleed) user.print(lang.get("to_model.no_anti_bleed"));
            set(4);
    
            user.getInventory().save(MODEL, model, to);
            set(5);
        }
        
    }
    
    private static class FV_VA_QB extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 3;
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            //Logger logger = verbose? user.getLogger() : null;
    
            VoxelArray voxels = (VoxelArray) user.getInventory().load(VOXEL_ARRAY, from);
            assert voxels != null;
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(1);
    
            QBModel qb = new CvVoxelArrayToQB().invoke(voxels);
            if (verbose) user.print(lang.get("to_qb.matrices"), qb.getMatrices().length);
            set(2);
    
            user.getInventory().save(QB, qb, to);
            set(3);
        }
        
    }
    
    private static class FV_VA_SCHEMATIC extends Formatverter {
        
        @Override
        public Set<Option> getMandatoryOptions() {
            return Sets.ofArray(OPTION_RESOLUTION);
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @Override
        public int getMaxProgress() {
            return 5;
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            //Logger logger = verbose? user.getLogger() : null;
    
            VoxelArray voxels = (VoxelArray) user.getInventory().load(VOXEL_ARRAY, from);
            assert voxels != null;
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(1);
            
            BlockColorTable bct = defaultBCT();
            set(2);
            
            BlockStructure blocks = new CvVoxelArrayToBlocks().invoke(voxels, bct);
            if (verbose) user.printLocalized("to_blocks.blocks", blocks.getBlockCount());
            set(3);
    
            user.getInventory().save(SCHEMATIC, blocks, to);
            set(4);
        }
        
    }
    
    private static class FV_VA_STL extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 3;
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            //Logger logger = verbose? user.getLogger() : null;
    
            VoxelArray voxels = (VoxelArray) user.getInventory().load(VOXEL_ARRAY, from);
            assert voxels != null;
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(1);
    
            STLModel stl = new CvVoxelArrayToSTL().invoke(voxels);
            if (verbose) user.print(lang.get("to_stl.triangles"), stl.size());
            set(2);
    
            user.getInventory().save(STL, stl, to);
            set(3);
        }
        
    }
    
    private static class FV_VA_WAVEFRONT extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 3;
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            //Logger logger = verbose? user.getLogger() : null;
    
            VoxelArray voxels = (VoxelArray) user.getInventory().load(VOXEL_ARRAY, from);
            assert voxels != null;
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(1);
    
            OBJModel obj = new CvVoxelArrayToOBJ().invoke(voxels);
            if (verbose) user.print(lang.get("to_wavefront.content"), obj.getVertexCount(), obj.getFaceCount());
            set(2);
    
            user.getInventory().save(WAVEFRONT, obj, to);
            set(3);
        }
        
    }
    
    private static class FV_WAVEFRONT_STL extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 3;
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            //Logger logger = verbose? user.getLogger() : null;
            
            OBJModel obj = (OBJModel) user.getInventory().load(WAVEFRONT, from);
            assert obj != null;
            if (verbose)
                user.print(lang.get("from_wavefront.content"), obj.getVertexCount(), obj.getFaceCount());
            set(1);
            
            STLModel stl = new CvOBJToSTL().invoke(obj);
            
            user.getInventory().save(STL, stl, to);
            set(3);
        }
        
    }
    
    private static class FV_WAVEFRONT_VA extends Formatverter {
        
        @Override
        public Set<Option> getMandatoryOptions() {
            return Sets.ofArray(OPTION_RESOLUTION);
        }
        
        @Override
        public Set<Option> getOptionalOptions() {
            return Sets.ofArray(OPTION_VERBOSE);
        }
        
        @Override
        public int getMaxProgress() {
            return 3;
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey(OPTION_VERBOSE.getId());
            Logger logger = verbose? user.getLogger() : null;
            int res = parseInt(OPTION_RESOLUTION, args.get(OPTION_RESOLUTION.getId()), IntegerType.NATURAL);
            
            if (verbose) user.print(lang.get("from_wavefront.canvas"), res, res, res);
    
            OBJModel obj = (OBJModel) user.getInventory().load(WAVEFRONT, from);
            assert obj != null;
            if (verbose) {
                user.print(lang.get("from_wavefront.content"), obj.getVertexCount(), obj.getFaceCount());
                if (obj.hasMaterials()) {
                    MTLLibrary lib = obj.getMaterials();
                    assert lib != null;
                    user.print(lang.get("from_wavefront.mtllib"), lib.getName(), lib.size());
                }
            }
            set(1);
            
            VoxelArray voxels = new CvOBJToVoxelArray(logger).invoke(obj, res);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(2);
    
            user.getInventory().save(VOXEL_ARRAY, voxels, to);
            set(3);
        }
        
    }
    
    // UTIL
    
    private static BlockColorTable defBCT = null;
    
    @NotNull
    private static BlockColorTable defaultBCT() throws IOException {
        return defBCT != null? defBCT :
            (defBCT = new DeserializerBCT().fromResource(FormatverterFactory.class, DEFAULT_BCT));
    }
    
    /**
     * Parses a resolution of format <code>{x-coordinate}[x{y-coordinate}x{z-coordinate}]</code>
     *
     * @param str the string
     * @return the parsed resolution
     * @throws IllegalArgumentException if the string was not provided in the proper format
     */
    @NotNull
    private static Vertex3i parseResolution(String str) {
        if (str.contains("x")) {
            String[] split = str.split("x", 3);
            if (split.length != 3)
                throw new FormatverterArgumentException(OPTION_RESOLUTION,
                    "resolution %s is not of format <X>x<Y>x<Z>");
            int[] result = new int[3];
            
            for (int i = 0; i < 3; i++) {
                result[i] = Integer.parseInt(split[i]);
                if (result[i] < 1)
                    throw new FormatverterArgumentException(OPTION_RESOLUTION, "resolution must be >= 1 on all axes");
            }
            
            return new Vertex3i(result[0], result[1], result[2]);
        }
        else {
            int result = Integer.parseInt(str);
            if (result < 1) throw new FormatverterArgumentException(OPTION_RESOLUTION, "resolution must be >= 1");
            
            return new Vertex3i(result, result, result);
        }
    }
    
    @NotNull
    private static int parseInt(Option opt, String str, IntegerType type) throws FormatverterArgumentException {
        try {
            int result = Integer.parseInt(str);
            switch (type) {
                case NATURAL:
                    if (result < 1) throw new FormatverterArgumentException(opt, result + "must be at least 1");
                    break;
                case POSITIVE:
                    if (result < 0) throw new FormatverterArgumentException(opt, result + "must be positive");
                    break;
                case ANY:
                default: break;
            }
            return result;
        } catch (NumberFormatException ex) {
            throw new FormatverterArgumentException(opt, str + " is not a valid integer");
        }
    }
    
    @NotNull
    private static Direction parseDirection(String str) throws FormatverterArgumentException {
        Face f = Face.fromString(str);
        if (f == null)
            throw new FormatverterArgumentException(OPTION_DIRECTION, "unknown direction: " + str);
        return f.direction();
    }
    
    private static enum IntegerType {NATURAL, POSITIVE, ANY}
    
}
