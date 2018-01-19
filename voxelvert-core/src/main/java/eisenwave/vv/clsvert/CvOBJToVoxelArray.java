package eisenwave.vv.clsvert;

import eisenwave.torrens.wavefront.*;
import eisenwave.spatium.util.PrimMath;
import eisenwave.torrens.object.*;
import eisenwave.torrens.util.ColorMath;
import eisenwave.torrens.img.Texture;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.util.Arguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class CvOBJToVoxelArray implements Classverter<OBJModel, VoxelArray> {
    
    @Override
    public Class<OBJModel> getFrom() {
        return OBJModel.class;
    }
    
    @Override
    public Class<VoxelArray> getTo() {
        return VoxelArray.class;
    }
    
    @Nullable
    private final Logger logger;
    
    public CvOBJToVoxelArray(@org.jetbrains.annotations.Nullable Logger logger) {
        this.logger = logger;
    }
    
    public CvOBJToVoxelArray() {
        this(null);
    }
    
    private void warning(String msg) {
        if (logger != null)
            logger.warning(msg);
    }
    
    private void debug(String msg) {
        if (logger != null)
            logger.fine(msg);
    }
    
    @Override
    public VoxelArray invoke(@NotNull OBJModel from, @NotNull Object... args) {
        Arguments.requireMin(args, 1);
    
        return invoke(from, Arguments.requireType(args[0], Number.class).intValue());
    }
    
    public VoxelArray invoke(OBJModel model, int res) {
        MTLLibrary mtllib = model.getMaterials();
        BoundingBox6f bounds = model.getBoundaries();
        Transformation transform = transformOf(bounds, res);
        
        Vertex3f dims = transform.transform(bounds.getMax());
        VoxelArray canvas = new VoxelArray(
            (int) dims.getX() + 1,
            (int) dims.getY() + 1,
            (int) dims.getZ() + 1);
        
        if (logger != null)
            logger.fine(String.format("voxelizing %s sized model using %s canvas", dims, canvas));
        //System.out.println(canvas);
        
        if (logger != null) {
            if (mtllib == null)
                logger.fine("voxelizing model without mtllib");
            else
                logger.fine("voxelizing model with mtllib: " + mtllib);
        }
        
        drawGroup(model, mtllib, model.getDefaultGroup(), transform, canvas);
        for (OBJGroup group : model.getGroups()) {
            drawGroup(model, mtllib, group, transform, canvas);
        }
        
        return canvas;
    }
    
    @NotNull
    private static Transformation transformOf(BoundingBox6f box, int res) {
        Vertex3f translation = box.getMin().multiplied(-1);
        
        float maxDim = PrimMath.max(box.getSizeX(), box.getSizeY(), box.getSizeZ());
        float scale = (res - 1) / maxDim;
        
        return v -> v.plus(translation).multiplied(scale);
    }
    
    private void drawGroup(OBJModel model, MTLLibrary mtllib, OBJGroup group, Transformation transform, VoxelArray canvas) {
        Texture texture;
        if (mtllib == null) texture = null;
        else {
            String material = group.getMaterial();
            if (material == null) {
                texture = null;
                warning("drawing group without material");
            }
            else {
                MTLMaterial mtl = mtllib.getMaterial(material);
                if (mtl == null) {
                    texture = null;
                    warning("invalid usemtl reference '" + material + "'");
                }
                else {
                    String diffMap = mtl.getDiffuseMap();
                    if (diffMap == null) {
                        texture = null;
                        warning("material '" + mtl + "' has no diffuse map");
                    }
                    else {
                        texture = mtllib.getMap(diffMap);
                        if (texture == null) {
                            warning("invalid map_Kd reference: " + diffMap);
                        }
                        else
                            warning("drawing group with diffuse map of " + mtl);
                    }
                }
            }
        }
        
        for (OBJFace polygon : group) {
            OBJFace[] triangles = tesselate(polygon);
            for (OBJFace triangle : triangles) {
                OBJTriplet
                    ta = triangle.getTriplet(0),
                    tb = triangle.getTriplet(1),
                    tc = triangle.getTriplet(2);
                
                Vertex3f
                    vA = transform.transform(model.getVertex(ta.getVertexIndex())),
                    vB = transform.transform(model.getVertex(tb.getVertexIndex())),
                    vC = transform.transform(model.getVertex(tc.getVertexIndex()));
                
                if (texture == null)
                    drawTriangle(vA, vB, vC, canvas);
                
                else drawTriangle(vA, vB, vC,
                    model.getTexture(ta.getTextureIndex()),
                    model.getTexture(tb.getTextureIndex()),
                    model.getTexture(tc.getTextureIndex()),
                    texture,
                    canvas);
            }
        }
    }
    
    private static void drawTriangle(Vertex3f a, Vertex3f b, Vertex3f c,
                                     Vertex2f uvA, Vertex2f uvB, Vertex2f uvC,
                                     Texture texture, VoxelArray canvas) {
        
        if (isOneSideTooLong(a, b, c)) {
            Vertex3f ab = a.midPoint(b), bc = b.midPoint(c), ca = c.midPoint(a);
            Vertex2f uvAB = uvA.midPoint(uvB), uvBC = uvB.midPoint(uvC), uvCA = uvC.midPoint(uvA);
            
            drawTriangle(a, ab, ca, uvA, uvAB, uvCA, texture, canvas);
            drawTriangle(b, bc, ab, uvB, uvBC, uvAB, texture, canvas);
            drawTriangle(c, ca, bc, uvC, uvCA, uvBC, texture, canvas);
            drawTriangle(ab, bc, ca, uvAB, uvBC, uvCA, texture, canvas);
        }
        
        else {
            drawVertex(a, texture.get(uvA.getX(), 1 - uvA.getY()), canvas);
            drawVertex(b, texture.get(uvB.getX(), 1 - uvB.getY()), canvas);
            drawVertex(c, texture.get(uvC.getX(), 1 - uvC.getY()), canvas);
        }
    }
    
    @SuppressWarnings("Duplicates")
    private static void drawTriangle(Vertex3f a, Vertex3f b, Vertex3f c, VoxelArray canvas) {
        
        if (isOneSideTooLong(a, b, c)) {
            Vertex3f ab = a.midPoint(b), bc = b.midPoint(c), ca = c.midPoint(a);
            
            drawTriangle(a, ab, ca, canvas);
            drawTriangle(b, bc, ab, canvas);
            drawTriangle(c, ca, bc, canvas);
            drawTriangle(ab, bc, ca, canvas);
        }
        
        else {
            drawVertex(a, ColorMath.SOLID_WHITE, canvas);
            drawVertex(b, ColorMath.SOLID_WHITE, canvas);
            drawVertex(c, ColorMath.SOLID_WHITE, canvas);
        }
    }
    
    private static void drawVertex(Vertex3f a, int rgb, VoxelArray canvas) {
        canvas.setRGB((int) a.getX(), (int) a.getY(), (int) a.getZ(), rgb);
    }
    
    
    /*
    private static void drawTriangleIteratively(Vertex3f a, Vertex3f b, Vertex3f c,
                                                @Nullable Texture texture, VoxelArray canvas) {
        Triangle3 triangle = Triangle3.fromPoints(
            vA.getX(), vA.getY(), vA.getZ(),
            vB.getX(), vB.getY(), vB.getZ(),
            vC.getX(), vC.getY(), vC.getZ());
        transform.transform(triangle);
        
        if (texture == null) {
            canvas.drawTriangle(triangle, ColorMath.SOLID_WHITE);
            return;
        }
        
        Vertex2f
            vtA = model.getTexture(tA.getTextureIndex()-1),
            vtB = model.getTexture(tB.getTextureIndex()-1),
            vtC = model.getTexture(tC.getTextureIndex()-1);
    
        canvas.drawBarycentrics(triangle, (barA, barB, barC) -> {
            float u = barA*vtA.getX() + barB*vtB.getX() + barC*vtC.getX();
            float v = barA*vtA.getY() + barB*vtB.getY() + barC*vtC.getY();
            //float u = vtA.getX();
            //float v = vtA.getY();
        
            //int rgbU = ColorMath.scaleRGB(ColorMath.SOLID_RED, u);
            //int rgbV = ColorMath.scaleRGB(ColorMath.SOLID_GREEN, v);
            //return ColorMath.blend(rgbU, rgbV, 0.5F);
            //return texture.get(u, 1-v);
            return ColorMath.SOLID_WHITE;
        });
        
        canvas.selectAll();
        canvas.setTransparentDraw(false);
        canvas.drawLine(triangle.getA().toBlockVector(), triangle.getB().toBlockVector(), lineARGB);
        canvas.drawLine(triangle.getB().toBlockVector(), triangle.getC().toBlockVector(), lineARGB);
        canvas.drawLine(triangle.getC().toBlockVector(), triangle.getA().toBlockVector(), lineARGB);
        canvas.draw(triangle.getA().toBlockVector(), rootVertexARGB);
        canvas.draw(triangle.getB().toBlockVector(), adjVertexARGB);
        canvas.draw(triangle.getC().toBlockVector(), adjVertexARGB);
    }
    */
    
    // UTIL
    
    @FunctionalInterface
    private static interface Transformation {
        
        abstract Vertex3f transform(Vertex3f v);
        
    }
    
    private final static float MAX_EDGE = 1 - 1E-6F;
    
    private static boolean isOneSideTooLong(Vertex3f a, Vertex3f b, Vertex3f c) {
        return b.minus(a).getLengthSquared() > MAX_EDGE
            || c.minus(b).getLengthSquared() > MAX_EDGE
            || a.minus(c).getLengthSquared() > MAX_EDGE;
    }
    
    @SuppressWarnings("Duplicates")
    private static OBJFace[] tesselate(OBJFace polygon) {
        final int vertices = polygon.size();
        if (vertices < 3) return new OBJFace[0];
        if (vertices == 3) return new OBJFace[] {polygon};
        if (vertices == 4) {
            OBJTriplet
                a = polygon.getTriplet(0),
                b = polygon.getTriplet(1),
                c = polygon.getTriplet(2),
                d = polygon.getTriplet(3);
            return new OBJFace[] {
                new OBJFace(a, b, c),
                new OBJFace(c, d, a)};
        }
        throw new IllegalArgumentException("face must be triangle or quad");
        /*
        disabled tesselation of higher order polygons
        List<Integer> indices = new LinkedList<>();
        for (int i = 0; i<vertices; i++)
            indices.set(i, i);
        
        List<OBJFace> faces = new ArrayList<>();
        for (int i = 1; i<vertices; i += 2) {
            OBJFace triangle = new OBJFace(
                polygon.getTriplet(i-1),
                polygon.getTriplet(i),
                polygon.getTriplet((i+1)%vertices));
            
            faces.add(triangle);
        }
        */
    }
    
    /*
    private final static int
        lineARGB = ColorMath.fromRGB(255, 0, 0, 127),
        adjVertexARGB = ColorMath.fromRGB(0, 255, 0, 127),
        rootVertexARGB = ColorMath.fromRGB(0, 0, 255, 127);
    */
    
}
