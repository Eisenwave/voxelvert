package org.eisenwave.vv.ui.fmtvert;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.enums.Face;
import net.grian.torrens.img.Texture;
import net.grian.torrens.object.Vertex3i;
import net.grian.torrens.schematic.ArrayBlockStructure;
import net.grian.torrens.voxel.VoxelArray;
import net.grian.torrens.wavefront.MTLLibrary;
import net.grian.torrens.wavefront.OBJModel;
import org.eisenwave.vv.object.MCModel;
import org.eisenwave.vv.rp.BlockColorTable;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.clsvert.*;
import org.eisenwave.vv.io.DeserializerBCT;
import org.eisenwave.vv.io.DeserializerBCE;
import org.eisenwave.vv.rp.BlockColorExtractor;
import org.eisenwave.vv.ui.user.VVUser;
import net.grian.torrens.voxel.QBModel;
import net.grian.torrens.stl.STLModel;
import net.grian.torrens.voxel.VoxelMesh;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import static org.eisenwave.vv.ui.fmtvert.Format.*;

@SuppressWarnings("Duplicates")
public final class FormatverterFactory {
    
    public final static String DEFAULT_BCT = "colors/default.bct";
    
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
        map.put(BLOCK_ARRAY, IMAGE, new FV_BA_IMAGE());
        map.put(BLOCK_ARRAY, MODEL, new FV_BA_MODEL());
        map.put(BLOCK_ARRAY, QB, new FV_BA_QB());
        map.put(BLOCK_ARRAY, QEF, new FV_BA_QEF());
        map.put(BLOCK_ARRAY, STL, new FV_BA_STL());
        map.put(BLOCK_ARRAY, SCHEMATIC, new FV_BA_SCHEMATIC());
        map.put(BLOCK_ARRAY, WAVEFRONT, new FV_BA_WAVEFRONT());
        
        map.put(IMAGE, IMAGE, new FV_IMAGE_IMAGE());
        map.put(IMAGE, QEF, new FV_IMAGE_QEF());
        map.put(IMAGE, QB, new FV_IMAGE_QB());
        
        map.put(QB, IMAGE, new FV_QB_IMAGE());
        map.put(QB, MODEL, new FV_QB_MODEL());
        map.put(QB, QB, new CopyFormatverter());
        map.put(QB, QEF, new FV_QB_QEF());
        map.put(QB, STL, new FV_QB_STL());
        map.put(QB, WAVEFRONT, new FV_QB_WAVEFRONT());
        
        map.put(QEF, IMAGE, new FV_QEF_IMAGE());
        map.put(QEF, MODEL, new FV_QEF_MODEL());
        map.put(QEF, QB, new FV_QEF_QB());
        map.put(QEF, QEF, new CopyFormatverter());
        map.put(QEF, STL, new FV_QEF_STL());
        map.put(QEF, WAVEFRONT, new FV_QEF_WAVEFRONT());
        
        map.put(RESOURCE_PACK, BLOCK_COLOR_TABLE, new FV_RP_COLORS());
        map.put(RESOURCE_PACK, RESOURCE_PACK, new CopyFormatverter());
    
        map.put(SCHEMATIC, IMAGE, new InventoryFormatverter(SCHEMATIC, BLOCK_ARRAY, new FV_BA_IMAGE()));
        map.put(SCHEMATIC, MODEL, new InventoryFormatverter(SCHEMATIC, BLOCK_ARRAY, new FV_BA_MODEL()));
        map.put(SCHEMATIC, QB, new InventoryFormatverter(SCHEMATIC, BLOCK_ARRAY, new FV_BA_QB()));
        map.put(SCHEMATIC, QEF, new InventoryFormatverter(SCHEMATIC, BLOCK_ARRAY, new FV_BA_QEF()));
        map.put(SCHEMATIC, SCHEMATIC, new CopyFormatverter());
        map.put(SCHEMATIC, STL, new InventoryFormatverter(SCHEMATIC, BLOCK_ARRAY, new FV_BA_STL()));
        map.put(SCHEMATIC, WAVEFRONT, new InventoryFormatverter(SCHEMATIC, BLOCK_ARRAY, new FV_BA_STL()));
        
        map.put(STL, MODEL, new FV_STL_MODEL());
        map.put(STL, QEF, new FV_STL_QEF());
        map.put(STL, QB, new FV_STL_QB());
        map.put(STL, STL, new CopyFormatverter());
        
        map.put(WAVEFRONT, IMAGE, new FV_WAVEFRONT_IMAGE());
        map.put(WAVEFRONT, MODEL, new FV_WAVEFRONT_MODEL());
        map.put(WAVEFRONT, QB, new FV_WAVEFRONT_QB());
        map.put(WAVEFRONT, QEF, new FV_WAVEFRONT_QEF());
        map.put(WAVEFRONT, STL, new FV_WAVEFRONT_STL());
        map.put(WAVEFRONT, WAVEFRONT, new CopyFormatverter());
    }
    
    // GETTERS
    
    public Formatverter fromFormats(Format from, Format to) {
        return map.get(from, to);
    }
    
    public Set<Format> getInputFormats() {
        return map.sourceSet();
    }
    
    public Set<Format> getOutputFormats() {
        return map.targetSet();
    }
    
    public Formatverter[] getFormatverters() {
        return map.getFormatverters();
    }
    
    public Format[] getInputFormats(Format output) {
        return map.getInputFormats(output);
    }
    
    public Format[] getOutputFormats(Format input) {
        return map.getOutputFormats(input);
    }
    
    // MUTATORS
    
    public boolean put(@NotNull Format input, @NotNull Format output, @NotNull Formatverter fmtverter) {
        return map.put(input, output, fmtverter);
    }
    
    // FIRST ORDER FORMATVERTERS
    
    
    
    private static class FV_BA_IMAGE extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 5;
        }
        
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"d"};
        }
        
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose", "C", "crop"};
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            boolean crop = args.containsKey("C") || args.containsKey("crop");
            String d = args.get("d");
            Direction dir = parseDirection(d);
            Logger logger = verbose? user.getLogger() : null;
            
            VoxelArray voxels;
            {
                ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(Format.BLOCK_ARRAY, from);
                set(1);
                
                BlockColorTable colors = new DeserializerBCT().fromResource(getClass(), DEFAULT_BCT);
                set(2);
                
                assert blocks != null;
                if (verbose) {
                    user.print(lang.get("from_block_array.blocks"), blocks.size());
                    user.print(lang.get("from_colors.colors"), colors.size());
                }
                
                voxels = new CvBlocksToVoxelArray().invoke(blocks, colors, 0);
                set(3);
            }
            
            if (verbose) {
                user.print(lang.get("to_voxels.voxels"), voxels.size());
                user.print(lang.get("to_image.render"), from, d, dir);
            }
            
            Texture image = new CvVoxelArrayToTexture(logger)
                .invoke(voxels, dir, true, crop);
            if (verbose) user.print(lang.get("to_image.crop"), image.getWidth(), image.getHeight());
            set(4);
            
            user.getInventory().save(Format.IMAGE, image, to);
            set(5);
        }
        
    }
    
    private static class FV_BA_MODEL extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 7;
        }
        
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose", "no_anti_bleed"};
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            boolean noAntiBleed = args.containsKey("no_anti_bleed");
            Logger logger = verbose? user.getLogger() : null;
            
            VoxelArray voxels;
            {
                ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(Format.BLOCK_ARRAY, from);
                set(1);
                
                BlockColorTable colors = new DeserializerBCT().fromResource(getClass(), DEFAULT_BCT);
                set(2);
                
                assert blocks != null;
                if (verbose) {
                    user.print(lang.get("from_block_array.blocks"), blocks.size());
                    user.print(lang.get("from_colors.colors"), colors.size());
                }
                
                voxels = new CvBlocksToVoxelArray().invoke(blocks, colors, 0);
                set(3);
            }
            
            VoxelMesh mesh = new CvVoxelArrayToVoxelMesh(logger).invoke(voxels);
            set(4);
            if (verbose) user.print(lang.get("to_mesh.elements"), mesh.size());
            
            MCModel model = new CvVoxelMeshToMC(logger).invoke(mesh);
            set(5);
            if (verbose) {
                user.print(lang.get("to_model.elements"), model.getElementCount());
                switch (model.getTextureCount()) {
                    
                    case 0:  user.print(lang.get("to_model.textures.none")); break;
                    
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
            set(6);
            
            user.getInventory().save(Format.MODEL, model, to);
            set(7);
        }
        
    }
    
    private static class FV_BA_QB extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 5;
        }
        
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(Format.BLOCK_ARRAY, from);
            set(1);
            
            BlockColorTable colors = new DeserializerBCT().fromResource(getClass(), DEFAULT_BCT);
            set(2);
            
            assert blocks != null;
            if (verbose) {
                user.print(lang.get("from_block_array.blocks"), blocks.size());
                user.print(lang.get("from_colors.colors"), colors.size());
            }
            
            VoxelArray voxels = new CvBlocksToVoxelArray().invoke(blocks, colors, 0);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(3);
            
            QBModel qb = new CvVoxelArrayToQB().invoke(voxels);
            if (verbose) user.print(lang.get("to_qb.matrices"), qb.getMatrices().length);
            set(4);
            
            user.getInventory().save(Format.QB, qb, to);
            set(5);
        }
        
    }
    
    private static class FV_BA_QEF extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 4;
        }
        
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(Format.BLOCK_ARRAY, from);
            set(1);
            
            BlockColorTable colors = new DeserializerBCT().fromResource(getClass(), DEFAULT_BCT);
            set(2);
            
            assert blocks != null;
            if (verbose) {
                user.print(lang.get("from_block_array.blocks"), blocks.size());
                user.print(lang.get("from_colors.colors"), colors.size());
            }
            
            VoxelArray voxels = new CvBlocksToVoxelArray().invoke(blocks, colors, 0);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(3);
            
            user.getInventory().save(Format.QEF, voxels, to);
            set(4);
        }
        
    }
    
    private static class FV_BA_STL extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 4;
        }
        
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(Format.BLOCK_ARRAY, from);
            assert blocks != null;
            if (verbose) user.print(lang.get("from_block_array.blocks"), blocks.size());
            set(1);
            
            VoxelArray voxels = new CvBlocksToVoxelArray().invoke(blocks, null, 0);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(2);
            
            STLModel stl = new CvVoxelArrayToSTL().invoke(voxels);
            if (verbose) user.print(lang.get("to_stl.triangles"), stl.size());
            set(3);
            
            user.getInventory().save(Format.STL, stl, to);
            set(4);
        }
        
    }
    
    private static class FV_BA_SCHEMATIC extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 2;
        }
        
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(Format.BLOCK_ARRAY, from);
            assert blocks != null;
            if (verbose) user.print(lang.get("from_block_array.blocks"), blocks.size());
            set(1);
            
            user.getInventory().save(Format.SCHEMATIC, blocks, to);
            set(2);
        }
        
    }
    
    private static class FV_BA_WAVEFRONT extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 5;
        }
        
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(Format.BLOCK_ARRAY, from);
            set(1);
            
            BlockColorTable colors = new DeserializerBCT().fromResource(getClass(), DEFAULT_BCT);
            set(2);
            
            assert blocks != null;
            if (verbose) {
                user.print(lang.get("from_block_array.blocks"), blocks.size());
                user.print(lang.get("from_colors.colors"), colors.size());
            }
            
            VoxelArray voxels = new CvBlocksToVoxelArray().invoke(blocks, colors, 0);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(3);
            
            OBJModel obj = new CvVoxelArrayToOBJ().invoke(voxels);
            if (verbose) user.print(lang.get("to_wavefront.content"), obj.getVertexCount(), obj.getFaceCount());
            set(4);
            
            user.getInventory().save(Format.WAVEFRONT, obj, to);
            set(5);
        }
        
    }
    
    private static class FV_IMAGE_IMAGE extends Formatverter {
        
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public int getMaxProgress() {
            return 2;
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Texture img = (Texture) user.getInventory().load(Format.IMAGE, from);
            assert img != null;
            set(1);
            
            user.getInventory().save(Format.IMAGE, img, to);
            set(2);
        }
        
    }
    
    private static class FV_IMAGE_QEF extends Formatverter {
    
        @Override
        public int getMaxProgress() {
            return 3;
        }
        
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"d"};
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            String d = args.get("d");
            Direction dir = parseDirection(d);
            //Logger logger = verbose? user.getLogger() : null;
            
            if (verbose) user.print(lang.get("from_image.face"), from, d, dir);
    
            Texture img = (Texture) user.getInventory().load(Format.IMAGE, from);
            set(1);
            
            VoxelArray va = new CvTextureToVoxelArray().invoke(img, dir);
            set(2);
            
            user.getInventory().save(Format.QEF, va, to);
            set(3);
        }
        
    }
    
    private static class FV_IMAGE_QB extends Formatverter {
    
        @Override
        public int getMaxProgress() {
            return 4;
        }
        
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"d"};
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            String d = args.get("d");
            Direction dir = parseDirection(d);
            //Logger logger = verbose? user.getLogger() : null;
            
            if (verbose) user.print(lang.get("from_image.face"), from, d, dir);
            
            Texture img = (Texture) user.getInventory().load(Format.IMAGE, from);
            set(1);
            
            VoxelArray va = new CvTextureToVoxelArray().invoke(img, dir);
            set(2);
            
            QBModel qb = new CvVoxelArrayToQB().invoke(va);
            if (verbose) user.print(lang.get("to_qb.matrices"), qb.getMatrices().length);
            set(3);
            
            user.getInventory().save(Format.QB, qb, to);
            set(4);
        }
        
    }
    
    private static class FV_QEF_IMAGE extends Formatverter {
    
        @Override
        public int getMaxProgress() {
            return 3;
        }
    
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"d"};
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose", "C", "crop"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            boolean crop = args.containsKey("C") || args.containsKey("crop");
            String d = args.get("d");
            Direction dir = parseDirection(d);
            Logger logger = verbose? user.getLogger() : null;
    
            if (verbose) user.print(lang.get("to_image.render"), from, d, dir);
            
            VoxelArray va = (VoxelArray) user.getInventory().load(Format.QEF, from);
            set(1);
            assert va != null;
            
            Texture img = new CvVoxelArrayToTexture(logger).invoke(va, d, true, crop);
            set(2);
            
            user.getInventory().save(Format.IMAGE, img, to);
            set(3);
        }
        
    }
    
    private static class FV_QEF_MODEL extends Formatverter {
    
        @Override
        public int getMaxProgress() {
            return 4;
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose", "no_anti_bleed"};
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            boolean noAntiBleed = args.containsKey("no_anti_bleed");
            Logger logger = verbose? user.getLogger() : null;
            
            VoxelArray va = (VoxelArray) user.getInventory().load(Format.QEF, from);
            set(1);
            assert va != null;
            
            VoxelMesh mesh = new CvVoxelArrayToVoxelMesh(logger).invoke(va, 3);
            if (verbose) user.print(lang.get("to_mesh.elements"), mesh.size());
            set(2);
            
            MCModel model = new CvVoxelMeshToMC(logger).invoke(mesh);
            if (verbose) {
                user.print(lang.get("to_model.elements"), model.getElementCount());
                switch (model.getTextureCount()) {
                    
                    case 0:  user.print(lang.get("to_model.textures.none")); break;
                    
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
            set(3);
            
            user.getInventory().save(Format.MODEL, model, to);
            set(4);
        }
        
    }
    
    private static class FV_QEF_QB extends Formatverter {
    
        @Override
        public int getMaxProgress() {
            return 3;
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
    
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            VoxelArray va = (VoxelArray) user.getInventory().load(Format.QEF, from);
            set(1);
            
            QBModel qb = new CvVoxelArrayToQB().invoke(va);
            if (verbose) user.print(lang.get("to_qb.matrices"), qb.getMatrices().length);
            set(2);
            
            user.getInventory().save(Format.QB, qb, to);
            set(3);
        }
        
    }
    
    private static class FV_QEF_STL extends Formatverter {
    
        @Override
        public int getMaxProgress() {
            return 3;
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
    
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            VoxelArray va = (VoxelArray) user.getInventory().load(Format.QEF, from);
            set(1);
            
            STLModel stl = new CvVoxelArrayToSTL().invoke(va);
            if (verbose) user.print(lang.get("to_stl.triangles"), stl.size());
            set(2);
            
            user.getInventory().save(Format.STL, stl, to);
            set(3);
        }
        
    }
    
    private static class FV_QEF_WAVEFRONT extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 3;
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
    
            VoxelArray voxels = (VoxelArray) user.getInventory().load(Format.QEF, from);
            set(1);
            
            OBJModel obj = new CvVoxelArrayToOBJ().invoke(voxels);
            if (verbose) user.print(lang.get("to_wavefront.content"), obj.getVertexCount(), obj.getFaceCount());
            set(2);
            
            user.getInventory().save(Format.WAVEFRONT, obj, to);
            set(3);
        }
        
    }
    
    private static class FV_QB_IMAGE extends Formatverter {
    
        @Override
        public int getMaxProgress() {
            return 5;
        }
        
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"d"};
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose", "C", "crop"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            boolean crop = args.containsKey("C") || args.containsKey("crop");
            String d = args.get("d");
            Direction dir = parseDirection(d);
            Logger logger = verbose? user.getLogger() : null;
            
            if (verbose) user.print(lang.get("to_image.render"), from, d, dir);
    
            QBModel qb = (QBModel) user.getInventory().load(Format.QB, from);
            set(1);
            
            VoxelMesh vm = new CvQBToVoxelMesh().invoke(qb);
            set(2);
            
            VoxelArray va = new CvVoxelMeshToVoxelArray().invoke(vm);
            set(3);
            
            Texture img = new CvVoxelArrayToTexture(logger).invoke(va, dir, true, crop);
            set(4);
            
            user.getInventory().save(Format.IMAGE, img, to);
            set(5);
        }
        
    }
    
    private static class FV_QB_MODEL extends Formatverter {
    
        @Override
        public int getMaxProgress() {
            return 6;
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose", "no_anti_bleed"};
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            boolean noAntiBleed = args.containsKey("no_anti_bleed");
            Logger logger = verbose? user.getLogger() : null;
    
            final VoxelArray voxels;
            {
                QBModel qb = (QBModel) user.getInventory().load(Format.QB, from);
                set(1);
                assert qb != null;
    
                VoxelMesh mesh = new CvQBToVoxelMesh().invoke(qb);
                set(2);
                
                voxels = new CvVoxelMeshToVoxelArray().invoke(mesh);
                set(3);
            }
            
            VoxelMesh mesh = new CvVoxelArrayToVoxelMesh(logger).invoke(voxels);
            if (verbose) user.print(lang.get("to_mesh.elements"), mesh.size());
            set(4);
            
            MCModel model = new CvVoxelMeshToMC(logger).invoke(mesh);
            if (verbose) {
                user.print(lang.get("to_model.elements"), model.getElementCount());
                switch (model.getTextureCount()) {
                    
                    case 0:  user.print(lang.get("to_model.textures.none")); break;
                    
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
            set(5);
            
            user.getInventory().save(Format.MODEL, model, to);
            set(6);
        }
        
    }
    
    private static class FV_QB_QEF extends Formatverter {
    
        @Override
        public int getMaxProgress() {
            return 4;
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            //Language lang = user.getVoxelVert().getLanguage();
            //Logger logger = verbose? user.getLogger() : null;
            
            QBModel model = (QBModel) user.getInventory().load(Format.QB, from);
            set(1);
            
            VoxelMesh vm = new CvQBToVoxelMesh().invoke(model);
            set(2);
            
            VoxelArray va = new CvVoxelMeshToVoxelArray().invoke(vm);
            set(3);
            
            user.getInventory().save(Format.QEF, va, to);
            set(4);
        }
        
    }
    
    private static class FV_QB_STL extends Formatverter {
    
        @Override
        public int getMaxProgress() {
            return 5;
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
    
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            QBModel model = (QBModel) user.getInventory().load(Format.QB, from);
            set(1);
            
            VoxelMesh vm = new CvQBToVoxelMesh().invoke(model);
            set(2);
            
            VoxelArray va = new CvVoxelMeshToVoxelArray().invoke(vm);
            set(3);
            
            STLModel stl = new CvVoxelArrayToSTL().invoke(va);
            if (verbose) user.print(lang.get("to_stl.triangles"), stl.size());
            set(4);
            
            user.getInventory().save(Format.STL, stl, to);
            set(5);
        }
        
    }
    
    private static class FV_QB_WAVEFRONT extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 5;
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            QBModel model = (QBModel) user.getInventory().load(Format.QB, from);
            set(1);
            
            VoxelMesh vm = new CvQBToVoxelMesh().invoke(model);
            set(2);
            
            VoxelArray va = new CvVoxelMeshToVoxelArray().invoke(vm);
            set(3);
            
            OBJModel obj = new CvVoxelArrayToOBJ().invoke(va);
            if (verbose) user.print(lang.get("to_wavefront.content"), obj.getVertexCount(), obj.getFaceCount());
            set(4);
            
            user.getInventory().save(Format.WAVEFRONT, obj, to);
            set(5);
        }
        
    }
    
    private static class FV_RP_COLORS extends Formatverter {
    
        @Override
        public int getMaxProgress() {
            return 4;
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
    
            String name = "color_extractors/default.json";
            BlockColorExtractor extractor = new DeserializerBCE().fromResource(getClass(), name);
            set(1);
            
            ZipFile zip = (ZipFile) user.getInventory().load(Format.RESOURCE_PACK, from);
            if (verbose) user.print(lang.get("from_rp.colors"), extractor.size(), from);
            set(2);
            
            BlockColorTable colors = extractor.extract(zip);
            set(3);
            
            user.getInventory().save(Format.BLOCK_COLOR_TABLE, colors, to);
            set(4);
        }
        
    }
    
    private static class FV_STL_QB extends Formatverter {
        
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"R"};
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public int getMaxProgress() {
            return 4;
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            Logger logger = verbose? user.getLogger() : null;
            int res = Integer.parseInt(args.get("R"));
            
            if (verbose) user.print(lang.get("from_stl.canvas"), res, res, res);
            
            final VoxelArray voxels;
            {
                STLModel stl = (STLModel) user.getInventory().load(Format.STL, from);
                assert stl != null;
                if (verbose) user.print(lang.get("from_stl.triangles"), stl.size());
                set(1);
                
                voxels = new CvSTLToVoxelArray(logger).invoke(stl, res);
                if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
                set(2);
            }
            
            QBModel model = new CvVoxelArrayToQB().invoke(voxels);
            set(3);
            
            user.getInventory().save(Format.QB, model, to);
            set(4);
        }
        
    }
    
    private static class FV_STL_MODEL extends Formatverter {
        
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"R"};
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose", "no_anti_bleed"};
        }
        
        @Override
        public int getMaxProgress() {
            return 6;
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            boolean noAntiBleed = args.containsKey("no_anti_bleed");
            Logger logger = verbose? user.getLogger() : null;
            int res = Integer.parseInt(args.get("R"));
    
            if (verbose) user.print(lang.get("from_stl.canvas"), res, res, res);
            
            final VoxelArray voxels;
            {
                STLModel stl = (STLModel) user.getInventory().load(Format.STL, from);
                assert stl != null;
                if (verbose) user.print(lang.get("from_stl.triangles"), stl.size());
                set(1);
    
                voxels = new CvSTLToVoxelArray(logger).invoke(stl, res);
                if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
                set(2);
            }
    
            VoxelMesh mesh = new CvVoxelArrayToVoxelMesh(logger).invoke(voxels);
            set(3);
            if (verbose) user.print(lang.get("to_mesh.elements"), mesh.size());
    
            MCModel model = new CvVoxelMeshToMC(logger).invoke(mesh);
            set(4);
            if (verbose) {
                user.print(lang.get("to_model.elements"), model.getElementCount());
                switch (model.getTextureCount()) {
            
                    case 0:  user.print(lang.get("to_model.textures.none")); break;
            
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
            set(5);
    
            user.getInventory().save(Format.MODEL, model, to);
            set(6);
        }
        
    }
    
    private static class FV_STL_QEF extends Formatverter {
        
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"R"};
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public int getMaxProgress() {
            return 3;
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            Logger logger = verbose? user.getLogger() : null;
            int res = Integer.parseInt(args.get("R"));
            
            if (verbose) user.print(lang.get("from_stl.canvas"), res, res, res);
            
            STLModel stl = (STLModel) user.getInventory().load(Format.STL, from);
            assert stl != null;
            if (verbose) user.print(lang.get("from_stl.triangles"), stl.size());
            set(1);
            
            VoxelArray voxels = new CvSTLToVoxelArray(logger).invoke(stl, res);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(2);
            
            user.getInventory().save(Format.QEF, voxels, to);
            set(3);
        }
        
    }
    
    private static class FV_WAVEFRONT_IMAGE extends Formatverter {
        
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"R", "d"};
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose", "C", "crop"};
        }
        
        @Override
        public int getMaxProgress() {
            return 4;
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            boolean crop = args.containsKey("C") || args.containsKey("crop");
            String d = args.get("d");
            Direction dir = parseDirection(d);
            Logger logger = verbose? user.getLogger() : null;
            int res = Integer.parseInt(args.get("R"));
            
            if (verbose) user.print(lang.get("from_wavefront.canvas"), res, res, res);
            
            final VoxelArray voxels;
            {
                OBJModel obj = (OBJModel) user.getInventory().load(Format.WAVEFRONT, from);
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
                
                voxels = new CvOBJToVoxelArray(logger).invoke(obj, res);
                if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
                set(2);
            }
    
            if (verbose) {
                user.print(lang.get("to_image.render"), from, d, dir);
            }
    
            Texture image = new CvVoxelArrayToTexture(logger)
                .invoke(voxels, dir, true, crop);
            if (verbose) user.print(lang.get("to_image.crop"), image.getWidth(), image.getHeight());
            set(3);
            
            user.getInventory().save(Format.IMAGE, image, to);
            set(4);
        }
        
    }
    
    @SuppressWarnings("Duplicates")
    private static class FV_WAVEFRONT_MODEL extends Formatverter {
        
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"R"};
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose", "no_anti_bleed"};
        }
        
        @Override
        public int getMaxProgress() {
            return 6;
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            boolean noAntiBleed = args.containsKey("no_anti_bleed");
            Logger logger = verbose? user.getLogger() : null;
            int res = Integer.parseInt(args.get("R"));
            
            if (verbose) user.print(lang.get("from_wavefront.canvas"), res, res, res);
            
            final VoxelArray voxels;
            {
                OBJModel obj = (OBJModel) user.getInventory().load(Format.WAVEFRONT, from);
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
                
                voxels = new CvOBJToVoxelArray(logger).invoke(obj, res);
                if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
                set(2);
            }
    
            VoxelMesh mesh = new CvVoxelArrayToVoxelMesh(logger).invoke(voxels);
            set(3);
            if (verbose) user.print(lang.get("to_mesh.elements"), mesh.size());
    
            MCModel model = new CvVoxelMeshToMC(logger).invoke(mesh);
            set(4);
            if (verbose) {
                user.print(lang.get("to_model.elements"), model.getElementCount());
                switch (model.getTextureCount()) {
            
                    case 0:  user.print(lang.get("to_model.textures.none")); break;
            
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
            set(5);
    
            user.getInventory().save(Format.MODEL, model, to);
            set(6);
        }
        
    }
    
    @SuppressWarnings("Duplicates")
    private static class FV_WAVEFRONT_QB extends Formatverter {
        
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"R"};
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @Override
        public int getMaxProgress() {
            return 4;
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            Logger logger = verbose? user.getLogger() : null;
            int res = Integer.parseInt(args.get("R"));
            
            if (verbose) user.print(lang.get("from_wavefront.canvas"), res, res, res);
    
            final VoxelArray voxels;
            {
                OBJModel obj = (OBJModel) user.getInventory().load(Format.WAVEFRONT, from);
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
    
                voxels = new CvOBJToVoxelArray(logger).invoke(obj, res);
                if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
                set(2);
            }
    
            QBModel model = new CvVoxelArrayToQB().invoke(voxels);
            set(3);
            
            user.getInventory().save(Format.QB, model, to);
            set(4);
        }
        
    }
    
    private static class FV_WAVEFRONT_QEF extends Formatverter {
    
        @Override
        public String[] getMandatoryParams() {
            return new String[] {"R"};
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
    
        @Override
        public int getMaxProgress() {
            return 3;
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            Logger logger = verbose? user.getLogger() : null;
            int res = Integer.parseInt(args.get("R"));
            
            if (verbose) user.print(lang.get("from_wavefront.canvas"), res, res, res);
    
            OBJModel obj = (OBJModel) user.getInventory().load(Format.WAVEFRONT, from);
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
            
            user.getInventory().save(Format.QEF, voxels, to);
            set(3);
        }
        
    }
    
    private static class FV_WAVEFRONT_STL extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 3;
        }
    
        @Override
        public String[] getOptionalParams() {
            return new String[] {"v", "verbose"};
        }
        
        @SuppressWarnings("Duplicates")
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            OBJModel obj = (OBJModel) user.getInventory().load(Format.WAVEFRONT, from);
            assert obj != null;
            if (verbose)
                user.print(lang.get("from_wavefront.content"), obj.getVertexCount(), obj.getFaceCount());
            set(1);
            
            STLModel stl = new CvOBJToSTL().invoke(obj);
            
            user.getInventory().save(Format.STL, stl, to);
            set(3);
        }
        
    }
    
    // UTIL
    
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
            if (split.length != 3) throw new IllegalArgumentException("resolution %s is not of format <X>x<Y>x<Z>");
            int[] result = new int[3];
            
            for (int i = 0; i < 3; i++) {
                result[i] = Integer.parseInt(split[i]);
                if (result[i] < 1) throw new IllegalArgumentException("resolution must be >= 1 on all axes");
            }
            
            return new Vertex3i(result[0], result[1], result[2]);
        }
        else {
            int result = Integer.parseInt(str);
            if (result < 1) throw new IllegalArgumentException("resolution must be >= 1");
            
            return new Vertex3i(result, result, result);
        }
    }
    
    @Nullable
    private static Direction parseDirection(String str) {
        Face f = Face.fromString(str);
        return f==null? null : f.direction();
    }
    
}
