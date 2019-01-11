package eisenwave.vv.clsvert;

import eisenwave.vv.object.BaseRectangle;
import eisenwave.vv.object.RectangleArrangement;
import eisenwave.spatium.array.BooleanArray3;
import eisenwave.spatium.enums.Direction;
import eisenwave.spatium.util.FastMath;
import eisenwave.spatium.util.PrimMath;
import eisenwave.torrens.img.BaseTexture;
import eisenwave.torrens.img.Texture;
import eisenwave.torrens.object.BoundingBox6f;
import eisenwave.torrens.object.BoundingBox6i;
import eisenwave.torrens.object.Vertex3i;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.object.MCElement;
import eisenwave.vv.object.MCModel;
import eisenwave.vv.object.MCUV;
import eisenwave.torrens.voxel.VoxelMesh;
import eisenwave.vv.util.Arguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class CvVoxelMeshToMC implements Classverter<VoxelMesh, MCModel> {
    
    private final static String TEXTURE_NAME = "texture";
    
    @Nullable
    private final Logger logger;
    
    public CvVoxelMeshToMC(@Nullable Logger logger) {
        this.logger = logger;
    }
    
    public CvVoxelMeshToMC() {
        this(null);
    }
    
    @Deprecated
    @Override
    public MCModel invoke(@NotNull VoxelMesh from, @NotNull Object[] args) {
        return invoke(from);
    }
    
    public MCModel invoke(@NotNull VoxelMesh mesh) {
        Arguments.requireAllNonnull(mesh, "from must not be null");
        
        // create new empty model
        MCModel model = new MCModel();
        BoundingBox6i meshBounds = mesh.getBoundaries();
        //System.out.println(meshBounds);
        
        // convert mesh voxels into boxes and paste model with resulting mc-elements
        Collection<BlockyBox> boxes = toBlockyBoxes(meshBounds, mesh, model);
        //if (logger != null) logger.fine(String.format("generated %d boxes", boxes.size()));
        
        // convert mesh to boolean^3 array for easier occlusion tests
        BooleanArray3 opaque = createOpaqueArray(meshBounds, boxes);
        
        // disable geometrically occluded faces
        int removedFaces = optimizeFaces(boxes, opaque);
        if (logger != null) {
            int all = boxes.size() * 6;
            int left = all - removedFaces;
            int percent = (int) (100F * left / all);
            logger.info(String.format("optimized %d faces of %d elements, %d / %d (%d%%) remaining",
                removedFaces, boxes.size(), left, all, percent));
        }
        
        // adjust position of all elements so that they fit into the central 16x16x16 cube
        scaleModel(meshBounds, boxes, logger);

        /*
        * remove all boxes with disabled faces only
        * temporarily disabled since it appears that the face optimization NEVER produces a cuboid with all faces
        * covered, however further proof of this is required
        *
        * optimizeBoxes(boxes);
        * System.out.println("optimized boxes ("+boxes.size()+" remaining)");
        * */
        
        //convert voxel colors into textures where faces remain enabled
        Collection<ArrangeableTexture> faces = renderFaces(boxes);
        Arguments.requireAllNonnull(faces); //contains null from this point on
        
        //arrange all textures in a single rectangle
        RectangleArrangement<ArrangeableTexture> arrangement = arrangeTextures(faces);
        //System.out.println("arranged textures: "+arrangement);
        if (logger != null)
            logger.info(
                String.format("arranged %d textures in %dx%d bounding box",
                    faces.size(),
                    arrangement.getWidth(),
                    arrangement.getHeight()));
        
        //render all arranged rectangles and set uv inside mc-elements
        Texture texture = renderTextureArrangement(arrangement, TEXTURE_NAME);
        if (logger != null) logger.info(String.format("rendered arrangement as \"%s\"", TEXTURE_NAME));
        
        //add rendered texture to the model with same name as reference in elements
        model.addTexture(TEXTURE_NAME, texture);
        
        //return model with both complete uv and element data
        return model;
    }
    
    /**
     * Puts all elements of a voxel mesh into their transitive {@link BlockyBox} state and adds the {@link MCElement}s
     * into the model.
     *
     * @param mesh the voxel mesh
     * @param model the model
     * @return a collection containing all boxes
     */
    private static List<BlockyBox> toBlockyBoxes(BoundingBox6i meshBounds, VoxelMesh mesh, MCModel model) {
        List<BlockyBox> result = new LinkedList<>();
        
        final int tx = -meshBounds.getMinX(), ty = -meshBounds.getMinY(), tz = -meshBounds.getMinZ();
        
        for (VoxelMesh.Element voxels : mesh) {
            BoundingBox6i bounds = voxels.getBoundaries().translate(tx, ty, tz);
            
            MCElement element = new MCElement(new BoundingBox6f(bounds));
            model.addElement(element);
            
            BlockyBox box = new BlockyBox(voxels, bounds, element);
            result.add(box);
        }
        
        result.sort((a, b) -> {
            if (a.isShapeSingularity())
                return b.isShapeSingularity()? 0 : 1;
            else
                return b.isShapeSingularity()? -1 : 0;
        });
        
        return result;
    }
    
    /**
     * Disables any box faces which are completely covered by other boxes and removes them from the collection,
     * should all their faces be removed.
     *
     * @param boxes the box collection
     */
    private static int optimizeFaces(Collection<BlockyBox> boxes, BooleanArray3 opaque) {
        int count = 0;
        BoundingBox6i modelBounds = new BoundingBox6i(0, 0, 0,
            opaque.getSizeX() - 1,
            opaque.getSizeY() - 1,
            opaque.getSizeZ() - 1);
        //System.out.println("model bounds="+modelBounds);
        
        for (BlockyBox box : boxes) {
            BoundingBox6i shape = box.getShape();
            
            for (Direction side : Direction.values()) {
                BoundingBox6i surface = getSurface(shape, side);
                
                // first check if surface is inside model because only then can it get visually occluded by anything
                if (modelBounds.contains(surface) && isOccluded(surface, opaque)) {
                    box.disableTexture(side);
                    count++;
                }
            }
        }
        
        return count;
    }
    
    private static BooleanArray3 createOpaqueArray(BoundingBox6i meshBounds, Collection<BlockyBox> boxes) {
        final int dx = meshBounds.getSizeX(), dy = meshBounds.getSizeY(), dz = meshBounds.getSizeZ();
        
        BooleanArray3 result = new BooleanArray3(dx, dy, dz);
        //System.out.println(meshBounds+" -> "+dx+"x"+dy+"x"+dz+" -> "+result);
        
        for (BlockyBox box : boxes) {
            BoundingBox6i bounds = box.getShape();
            
            final int maxX = bounds.getMaxX(), maxY = bounds.getMaxY(), maxZ = bounds.getMaxZ();
            
            for (int x = bounds.getMinX(); x <= maxX; x++)
                for (int y = bounds.getMinY(); y <= maxY; y++)
                    for (int z = bounds.getMinZ(); z <= maxZ; z++)
                        result.enable(x, y, z);
        }
        
        return result;
    }
    
            /*if (box.isShapeSingularity()) {
                Vertex3i shape = box.getPosition();
                
                for (Direction side : Direction.values()) {
                    Vertex3i surface = getSurface(shape, side);
                    if (isOccluded(surface, boxes)) {
                        box.disableTexture(side);
                        count++;
                        break;
                    }
        
                }
            }*/
    
    private static boolean isOccluded(Vertex3i vertex, Iterable<BlockyBox> boxes) {
        for (BlockyBox box : boxes)
            if (box.getShape().contains(vertex))
                return true;
        
        return false;
    }
    
    private static boolean isOccluded(BoundingBox6i surface, Iterable<BlockyBox> boxes) {
        for (BlockyBox box : boxes)
            if (box.getShape().contains(surface))
                return true;
        
        return false;
    }
    
    private static boolean isOccluded(BoundingBox6i surface, BooleanArray3 opaque) {
        final int maxX = surface.getMaxX(), maxY = surface.getMaxY(), maxZ = surface.getMaxZ();
        //System.out.println(surface);
        
        for (int x = surface.getMinX(); x <= maxX; x++)
            for (int y = surface.getMinY(); y <= maxY; y++)
                for (int z = surface.getMinZ(); z <= maxZ; z++) {
                    //System.out.println(x+" "+y+" "+z);
                    if (!opaque.get(x, y, z)) return false;
                }
        
        return true;
    }
    
    private static void scaleModel(BoundingBox6i bounds, Collection<BlockyBox> boxes, @Nullable Logger logger) {
        /*Vertex3i min = bounds.getMin();
        final int
            tx = -min.getX(),
            ty = -min.getY(),
            tz = -min.getZ();*/
        
        final float scale;
        {
            int maxSize = PrimMath.max(bounds.getSizeX(), bounds.getSizeY(), bounds.getSizeZ());
            scale = 16F / FastMath.greaterPow2(maxSize);
        }
        
        boxes.forEach(box -> {
            BoundingBox6f shape = box.getHandle().getShape()
                //.translate(tx, ty, tz)
                .scale(scale);
            
            box.getHandle().setShape(shape);
        });
        
        if (logger != null) {
            int dividend = (int) (1 / scale);
            logger.fine(String.format("scaled by a factor of %s = 1/%d", scale, dividend));
        }
        
        //System.out.println(tx+" "+ty+" "+tz+" "+scale);
    }
    
    /**
     * Unused since the merging process makes it impossible for boxes to be completely disabled.
     *
     * @param boxes the boxes
     */
    public static void optimizeBoxes(Collection<BlockyBox> boxes) {
        Iterator<BlockyBox> iter = boxes.iterator();
        while (iter.hasNext())
            if (iter.next().isFullyDisabled())
                iter.remove();
    }
    
    /**
     * Renders all blocky box faces unless the face is disabled.
     *
     * @param boxes the box collection
     * @return a list containing all generated textures
     */
    private static Collection<ArrangeableTexture> renderFaces(Collection<BlockyBox> boxes) {
        CvVoxelArrayToTexture converter = new CvVoxelArrayToTexture();
        Queue<ArrangeableTexture> result = new ConcurrentLinkedQueue<>();
        
        boxes.parallelStream().forEach(box -> {
            VoxelArray array = box.getSource().getArray();
            
            for (Direction dir : Direction.values()) {
                if (box.hasEnabledTexture(dir)) {
                    Texture texture = converter.invoke(array, dir, false, false);
                    box.setTexture(dir, texture);
                    result.add(new ArrangeableTexture(box, dir, texture));
                }
            }
        });
        
        return result;
    }
    
    /**
     * Renders a rectangle arrangement, assuming that the arranged rectangles are textures.
     *
     * @param arrange the arrangement of textures
     * @return a texture composed using the arrangement
     */
    private static <T extends BaseRectangle> Texture renderTextureArrangement(RectangleArrangement<T> arrange,
                                                                              final String txName) {
        Texture result = Texture.alloc(arrange.getWidth(), arrange.getHeight());
        
        arrange.forEach(entry -> {
            ArrangeableTexture arrTexture = (ArrangeableTexture) entry.getRectangle();
            result.paste(arrTexture.getTexture(), entry.getU(), entry.getV());
            
            Direction side = arrTexture.getSide();
            MCUV uv = new MCUV(txName, entry.getU(), entry.getV(),
                entry.getU() + arrTexture.getWidth(),
                entry.getV() + arrTexture.getHeight(),
                getUVRotation(side));
            
            arrTexture.getParent().getHandle().setUV(side, uv);
        });
        
        return result;
    }
    
    private final static int[] rotations;
    
    static {
        rotations = new int[Direction.values().length];
        rotations[Direction.NEGATIVE_Y.ordinal()] = 270;
        rotations[Direction.POSITIVE_Y.ordinal()] = 90;
    }
    
    private static int getUVRotation(Direction direction) {
        return rotations[direction.ordinal()];
    }
    
    /**
     * Arranges a sorted list of rectangles.
     *
     * @param rectangles a sorted rectangle list
     * @return a single texture
     */
    private static <T extends BaseRectangle> RectangleArrangement<T> arrangeTextures(Collection<T> rectangles) {
        Arguments.requireAllNonnull(rectangles);
        BaseRectangle[] array = rectangles.toArray(new BaseRectangle[0]);
        //noinspection unchecked
        return new CvRectangleArranger().invoke(array);
    }
    
    @NotNull
    public static Vertex3i getSurface(Vertex3i v, Direction side) {
        return v.plus(side.x(), side.y(), side.z());
    }
    
    @NotNull
    public static BoundingBox6i getSurface(BoundingBox6i box, Direction side) {
        int coord;
        switch (side) {
            case NEGATIVE_X: coord = box.getMinX() - 1; break;
            case POSITIVE_X: coord = box.getMaxX() + 1; break;
            case NEGATIVE_Y: coord = box.getMinY() - 1; break;
            case POSITIVE_Y: coord = box.getMaxY() + 1; break;
            case NEGATIVE_Z: coord = box.getMinZ() - 1; break;
            case POSITIVE_Z: coord = box.getMaxZ() + 1; break;
            default: throw new IllegalArgumentException("direction has no axis");
        }
        
        switch (side.axis()) {
            case X: return new BoundingBox6i(
                coord, box.getMinY(), box.getMinZ(),
                coord, box.getMaxY(), box.getMaxZ());
            case Y: return new BoundingBox6i(
                box.getMinX(), coord, box.getMinZ(),
                box.getMaxX(), coord, box.getMaxZ());
            case Z: return new BoundingBox6i(
                box.getMinX(), box.getMinY(), coord,
                box.getMaxX(), box.getMaxY(), coord);
            default: throw new AssertionError(side.axis());
        }
    }
    
    /**
     * Class representing a transitive state between elements of voxel meshes and elements of models.
     */
    private static class BlockyBox {
        
        private final VoxelMesh.Element source;
        private final BoundingBox6i shape;
        private final MCElement handle;
        private final boolean singularity;
        
        private final Texture[] textures = new Texture[Direction.values().length];
        
        private byte visMask = 0b00111111;
        
        public BlockyBox(VoxelMesh.Element source, BoundingBox6i shape, MCElement handle) {
            this.source = source;
            this.singularity = shape.isSingularity();
            this.shape = /*shape.isSingularity()? shape.getMin() :*/ shape;
            this.handle = handle;
        }
        
        public VoxelMesh.Element getSource() {
            return source;
        }
        
        public BoundingBox6i getShape() {
            return shape;
        }
        
        public Vertex3i getPosition() {
            return shape.getMin();
        }
        
        public boolean isShapeSingularity() {
            return singularity;
        }
        
        public MCElement getHandle() {
            return handle;
        }
        
        public Texture getTexture(Direction side) {
            return textures[side.ordinal()];
        }
        
        public void setTexture(Direction side, Texture texture) {
            textures[side.ordinal()] = texture;
        }
        
        public boolean hasEnabledTexture(Direction side) {
            return (visMask & (1 << side.ordinal())) != 0;
        }
        
        public void disableTexture(Direction side) {
            visMask &= ~(1 << side.ordinal());
        }
        
        public boolean isFullyDisabled() {
            return visMask == 0;
        }
        
    }
    
    private static class ArrangeableTexture implements BaseRectangle {
        
        private final BlockyBox parent;
        private final Direction side;
        private final BaseTexture texture;
        
        private ArrangeableTexture(BlockyBox parent, Direction side, BaseTexture texture) {
            this.parent = parent;
            this.side = side;
            this.texture = texture;
        }
        
        public BlockyBox getParent() {
            return parent;
        }
        
        public Direction getSide() {
            return side;
        }
        
        public BaseTexture getTexture() {
            return texture;
        }
        
        @Override
        public int getWidth() {
            return texture.getWidth();
        }
        
        @Override
        public int getHeight() {
            return texture.getHeight();
        }
        
    }
    
}
