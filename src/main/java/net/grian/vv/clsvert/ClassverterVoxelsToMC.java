package net.grian.vv.clsvert;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.geo3.BlockSelection;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.img.BaseRectangle;
import net.grian.torrens.img.BaseTexture;
import net.grian.torrens.img.Texture;
import net.grian.torrens.mc.MCElement;
import net.grian.torrens.mc.MCModel;
import net.grian.torrens.mc.MCUV;
import net.grian.vv.core.RectangleArrangement;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.util.Arguments;
import net.grian.vv.util.ConvertUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class ClassverterVoxelsToMC implements Classverter<VoxelMesh, MCModel> {

    private final static String TEXTURE_NAME = "texture";

    @Override
    public Class<VoxelMesh> getFrom() {
        return VoxelMesh.class;
    }

    @Override
    public Class<MCModel> getTo() {
        return MCModel.class;
    }

    private final Logger logger;

    public ClassverterVoxelsToMC(Logger logger) {
        this.logger = logger;
    }

    public ClassverterVoxelsToMC() {
        this(Logger.getGlobal());
    }

    @Override
    public MCModel invoke(VoxelMesh from, Object[] args) {
        return invoke(from);
    }

    public MCModel invoke(VoxelMesh from) {
        Arguments.requireAllNonnull(from, "from must not be null");

        //create new empty model
        MCModel model = new MCModel();

        //convert mesh voxels into boxes and fill model with resulting mc-elements
        Collection<BlockyBox> boxes = toBlockyBoxes(from, model);
        System.out.println("generated "+boxes.size()+" boxes");

        //disable geometrically obstructed faces
        optimizeFaces(boxes);

        /*
        //remove all boxes with disabled faces only
        optimizeBoxes(boxes);
        System.out.println("optimized boxes ("+boxes.size()+" remaining)");
        */

        //convert voxel colors into textures where faces remain enabled
        Collection<ArrangeableTexture> faces = renderFaces(boxes);
        Arguments.requireAllNonnull(faces); //contains null from this point on
        System.out.println("rendered "+faces.size()+" faces");

        //arrange all textures in a single rectangle
        RectangleArrangement arrangement = arrangeTextures(faces);
        System.out.println("arranged textures: "+arrangement);

        //render all arranged rectangles and set uv inside mc-elements
        Texture texture = renderTextureArrangement(arrangement, TEXTURE_NAME);

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
    private static Collection<BlockyBox> toBlockyBoxes(VoxelMesh mesh, MCModel model) {
        Collection<BlockyBox> result = new LinkedList<>();

        for (VoxelMesh.Element voxels : mesh) {
            VoxelArray array = voxels.getArray();
            BlockSelection blockBounds = array.getBoundaries();
            blockBounds.move(voxels.getMinX(), voxels.getMinY(), voxels.getMinZ());
            
            MCElement element = new MCElement(blockBounds.toBoundingBox());
            model.addElement(element);

            BlockyBox box = new BlockyBox(voxels, blockBounds, element);
            result.add(box);
        }

        return result;
    }

    /**
     * Disables any box faces which are completely covered by other boxes and removes them from the collection,
     * should all their faces be removed.
     *
     * @param boxes the box collection
     */
    private static void optimizeFaces(Collection<BlockyBox> boxes) {
        boxes.forEach(box -> {
            final BlockSelection shape = box.getShape();

            for (Direction side : Direction.values()) {
                BlockSelection surface = getSurface(shape, side);

                //check whether surface min and max are both inside any other box
                for (BlockyBox box2 : boxes) {
                    BlockSelection shape2 = box2.getShape();
                    if (shape2.contains(surface)) {
                        box.disableTexture(side);
                        break;
                    }
                }

            }
        });
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
        ClassverterVoxelsToTexture converter = new ClassverterVoxelsToTexture();
        Queue<ArrangeableTexture> result = new ConcurrentLinkedQueue<>();

        boxes.parallelStream().forEach(box -> {
            VoxelArray array = box.getSource().getArray();

            for (Direction dir : Direction.values()) if (box.hasEnabledTexture(dir)) {
                Texture texture = converter.invoke(array, dir, false, false);
                box.setTexture(dir, texture);
                result.add(new ArrangeableTexture(box, dir, texture));
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
    private static Texture renderTextureArrangement(RectangleArrangement arrange, final String txName) {
        Texture result = new Texture(arrange.getWidth(), arrange.getHeight());

        arrange.forEach(entry -> {
            ArrangeableTexture arrTexture = (ArrangeableTexture) entry.getRectangle();
            result.paste(arrTexture.getTexture(), entry.getU(), entry.getV());

            Direction side = arrTexture.getSide();
            MCUV uv = new MCUV(txName, entry.getU(), entry.getV(),
                    entry.getU()+arrTexture.getWidth(),
                    entry.getV()+arrTexture.getHeight(),
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
    private static RectangleArrangement arrangeTextures(Collection<? extends BaseRectangle> rectangles) {
        Arguments.requireAllNonnull(rectangles);
        BaseRectangle[] array = rectangles.toArray(new BaseRectangle[rectangles.size()]);
        return ConvertUtil.convert(array, RectangleArrangement.class);
    }

    public static BlockSelection getSurface(BlockSelection box, Direction side) {
        int coord;
        switch (side) {
            case NEGATIVE_X: coord = box.getMinX()-1; break;
            case POSITIVE_X: coord = box.getMaxX()+1; break;
            case NEGATIVE_Y: coord = box.getMinY()-1; break;
            case POSITIVE_Y: coord = box.getMaxY()+1; break;
            case NEGATIVE_Z: coord = box.getMinZ()-1; break;
            case POSITIVE_Z: coord = box.getMaxZ()+1; break;
            default: throw new IllegalArgumentException("direction has no axis");
        }

        switch (side.axis()) {
            case X: return BlockSelection.fromPoints(
                    coord, box.getMinY(), box.getMinZ(),
                    coord, box.getMaxY(), box.getMaxZ());
            case Y: return BlockSelection.fromPoints(
                    box.getMinX(), coord, box.getMinZ(),
                    box.getMaxX(), coord, box.getMaxZ());
            case Z: return BlockSelection.fromPoints(
                    box.getMinX(), box.getMinY(), coord,
                    box.getMaxX(), box.getMaxY(), coord);
            default: throw new AssertionError(side.axis());
        }
    }

    /**
     * Temporary stare representing a transitive state between elements of voxel meshes and elements of models.
     */
    private static class BlockyBox {

        private final VoxelMesh.Element source;
        private final BlockSelection shape;
        private final MCElement handle;

        private final Texture[] textures = new Texture[Direction.values().length];

        private byte visMask = 0b00111111;

        public BlockyBox(VoxelMesh.Element source, BlockSelection shape, MCElement handle) {
            this.source = source;
            this.shape = shape;
            this.handle = handle;
        }

        public VoxelMesh.Element getSource() {
            return source;
        }

        public BlockSelection getShape() {
            return shape;
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
