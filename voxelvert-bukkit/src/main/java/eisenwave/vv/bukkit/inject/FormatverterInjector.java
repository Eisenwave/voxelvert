package eisenwave.vv.bukkit.inject;

import eisenwave.vv.ui.fmtvert.*;

import static eisenwave.vv.ui.fmtvert.Format.*;

@SuppressWarnings("Duplicates")
public class FormatverterInjector {
    
    public final static Format BLOCKS_FORMAT = new Format("blocks", true);
    
    public static void inject(FormatverterFactory factory) {
        factory.put(BLOCKS_FORMAT, Format.IMAGE, () -> new CompoundFormatverter(
            new InventoryFormatverter(BLOCKS_FORMAT, BLOCK_ARRAY), factory.fromFormats(BLOCK_ARRAY, IMAGE)));
    
        factory.put(BLOCKS_FORMAT, Format.MODEL, () -> new CompoundFormatverter(
            new InventoryFormatverter(BLOCKS_FORMAT, BLOCK_ARRAY), factory.fromFormats(BLOCK_ARRAY, MODEL)));
    
        factory.put(BLOCKS_FORMAT, Format.QB, () -> new CompoundFormatverter(
            new InventoryFormatverter(BLOCKS_FORMAT, BLOCK_ARRAY), factory.fromFormats(BLOCK_ARRAY, QB)));
    
        factory.put(BLOCKS_FORMAT, Format.QEF, () -> new CompoundFormatverter(
            new InventoryFormatverter(BLOCKS_FORMAT, BLOCK_ARRAY), factory.fromFormats(BLOCK_ARRAY, QEF)));
        
        factory.put(BLOCKS_FORMAT, Format.SCHEMATIC,
            () -> new InventoryFormatverter(BLOCKS_FORMAT, SCHEMATIC));
    
        factory.put(BLOCKS_FORMAT, Format.STL, () -> new CompoundFormatverter(
            new InventoryFormatverter(BLOCKS_FORMAT, BLOCK_ARRAY), factory.fromFormats(BLOCK_ARRAY, STL)));
    
        factory.put(BLOCKS_FORMAT, Format.WAVEFRONT, () -> new CompoundFormatverter(
            new InventoryFormatverter(BLOCKS_FORMAT, BLOCK_ARRAY), factory.fromFormats(BLOCK_ARRAY, WAVEFRONT)));
        /*
        factory.put(BLOCKS_FORMAT, Format.IMAGE, new FV_BLOCKS_IMAGE());
        factory.put(BLOCKS_FORMAT, Format.MODEL, new FV_BLOCKS_MODEL());
        factory.put(BLOCKS_FORMAT, Format.QB, new FV_BLOCKS_QB());
        factory.put(BLOCKS_FORMAT, Format.QEF, new FV_BLOCKS_QEF());
        factory.put(BLOCKS_FORMAT, Format.STL, new FV_BLOCKS_STL());
        factory.put(BLOCKS_FORMAT, Format.WAVEFRONT, new FV_BLOCKS_WAVEFRONT());
        */
    }
    
    /*
    private static class FV_BLOCKS_IMAGE extends Formatverter {
        
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
                ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(BLOCKS_FORMAT, from);
                set(1);
                
                ColorMap colors = new DeserializerColors().fromResource(getClass(), "colors/default.colors");
                set(2);
                
                assert blocks != null;
                if (verbose) {
                    user.print(lang.get("from_schematic.blocks"), blocks.size());
                    user.print(lang.get("from_schematic.colors"), colors.size());
                }
                
                voxels = new ClassverterBlocksToVoxels().invoke(blocks, colors, 0);
                set(3);
            }
            
            if (verbose) {
                user.print(lang.get("to_voxels.voxels"), voxels.size());
                user.print(lang.get("to_image.render"), from, d, dir);
            }
            
            Texture image = new ClassverterVoxelsToTexture(logger)
                .invoke(voxels, dir, true, crop);
            if (verbose) user.print(lang.get("to_image.crop"), image.getWidth(), image.getHeight());
            set(4);
            
            user.getInventory().save(Format.IMAGE, image, to);
            set(5);
        }
        
    }
    
    private static class FV_BLOCKS_MODEL extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 7;
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
                ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(BLOCKS_FORMAT, from);
                set(1);
                
                ColorMap colors = new DeserializerColors().fromResource(getClass(), "colors/default.colors");
                set(2);
                
                assert blocks != null;
                if (verbose) {
                    user.print(lang.get("from_schematic.blocks"), blocks.size());
                    user.print(lang.get("from_schematic.colors"), colors.size());
                }
                
                voxels = new ClassverterBlocksToVoxels().invoke(blocks, colors, 0);
                set(3);
            }
            
            VoxelMesh mesh = new ClassverterVoxelMerger(logger).invoke(voxels);
            set(4);
            if (verbose) user.print(lang.get("to_mesh.elements"), mesh.size());
            
            MCModel model = new ClassverterVoxelsToMC(logger).invoke(mesh);
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
    
    private static class FV_BLOCKS_QB extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 5;
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(BLOCKS_FORMAT, from);
            set(1);
            
            ColorMap colors = new DeserializerColors().fromResource(getClass(), "colors/default.colors");
            set(2);
            
            assert blocks != null;
            if (verbose) {
                user.print(lang.get("from_schematic.blocks"), blocks.size());
                user.print(lang.get("from_schematic.colors"), colors.size());
            }
            
            VoxelArray voxels = new ClassverterBlocksToVoxels().invoke(blocks, colors, 0);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(3);
            
            QBModel qb = new ClassverterVoxelsToQB().invoke(voxels);
            if (verbose) user.print(lang.get("to_qb.matrices"), qb.getMatrices().length);
            set(4);
            
            user.getInventory().save(Format.QB, qb, to);
            set(5);
        }
        
    }
    
    private static class FV_BLOCKS_QEF extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 4;
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(BLOCKS_FORMAT, from);
            set(1);
            
            ColorMap colors = new DeserializerColors().fromResource(getClass(), "colors/default.colors");
            set(2);
            
            assert blocks != null;
            if (verbose) {
                user.print(lang.get("from_schematic.blocks"), blocks.size());
                user.print(lang.get("from_schematic.colors"), colors.size());
            }
            
            VoxelArray voxels = new ClassverterBlocksToVoxels().invoke(blocks, colors, 0);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(3);
            
            user.getInventory().save(Format.QEF, voxels, to);
            set(4);
        }
        
    }
    
    private static class FV_BLOCKS_STL extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 4;
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(BLOCKS_FORMAT, from);
            set(1);
            
            VoxelArray voxels = new ClassverterBlocksToVoxels().invoke(blocks, null, 0);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(2);
            
            STLModel stl = new ClassverterVoxelsToSTL().invoke(voxels);
            if (verbose) user.print(lang.get("to_stl.triangles"), stl.size());
            set(3);
            
            user.getInventory().save(Format.STL, stl, to);
            set(4);
        }
        
    }
    
    private static class FV_BLOCKS_WAVEFRONT extends Formatverter {
        
        @Override
        public int getMaxProgress() {
            return 5;
        }
        
        @Override
        public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
            Language lang = user.getVoxelVert().getLanguage();
            
            boolean verbose = args.containsKey("v") || args.containsKey("verbose");
            //Logger logger = verbose? user.getLogger() : null;
            
            ArrayBlockStructure blocks = (ArrayBlockStructure) user.getInventory().load(BLOCKS_FORMAT, from);
            set(1);
            
            ColorMap colors = new DeserializerColors().fromResource(getClass(), "colors/default.colors");
            set(2);
            
            assert blocks != null;
            if (verbose) {
                user.print(lang.get("from_schematic.blocks"), blocks.size());
                user.print(lang.get("from_schematic.colors"), colors.size());
            }
            
            VoxelArray voxels = new ClassverterBlocksToVoxels().invoke(blocks, colors, 0);
            if (verbose) user.print(lang.get("to_voxels.voxels"), voxels.size());
            set(3);
            
            OBJModel obj = new ClassverterVoxelsToOBJ().invoke(voxels);
            if (verbose) user.print(lang.get("to_wavefront.content"), obj.getVertexCount(), obj.getFaceCount());
            set(4);
            
            user.getInventory().save(Format.WAVEFRONT, obj, to);
            set(5);
        }
        
    }
    
    @Nullable
    private static Direction parseDirection(String str) {
        Face f = Face.fromString(str);
        return f==null? null : f.direction();
    }
    */
    
}
