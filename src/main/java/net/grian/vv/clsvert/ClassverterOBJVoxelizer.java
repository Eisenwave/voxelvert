package net.grian.vv.clsvert;

import net.grian.spatium.geo3.AxisAlignedBB3;
import net.grian.spatium.geo3.Triangle3;
import net.grian.spatium.geo3.Vector3;
import net.grian.spatium.transform.Transformation;
import net.grian.spatium.util.ColorMath;
import net.grian.spatium.util.PrimMath;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.img.Texture;
import net.grian.torrens.object.Vertex2f;
import net.grian.torrens.object.Vertex3f;
import net.grian.torrens.wavefront.*;
import net.grian.vv.core.VoxelCanvas;
import net.grian.vv.util.Arguments;

import javax.annotation.*;
import java.util.logging.Logger;

public class ClassverterOBJVoxelizer implements Classverter<OBJModel, VoxelArray> {
    
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
    
    public ClassverterOBJVoxelizer(Logger logger) {
        this.logger = logger;
    }
    
    public ClassverterOBJVoxelizer() {
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
    public VoxelArray invoke(OBJModel from, Object... args) {
        Arguments.requireMin(args, 3);
        Arguments.requireType(args, Number.class);
        Arguments.requireType(args, Number.class);
        Arguments.requireType(args, Number.class);
        
        return invoke(from,
            ((Number) args[0]).intValue(),
            ((Number) args[1]).intValue(),
            ((Number) args[2]).intValue());
    }
    
    public VoxelArray invoke(OBJModel model, int x, int y, int z) {
        VoxelCanvas canvas = new VoxelCanvas(x, y, z);
        MTLLibrary mtllib = model.getMaterials();
        Transformation transform = transformOf(model.getBoundaries(), x, y, z);
        
        if (logger != null) {
            if (mtllib == null)
                logger.fine("voxelizing model without mtllib");
            else
                logger.fine("voxelizing model with mtllib: "+mtllib);
        }
        
        drawGroup(model, mtllib, model.getDefaultGroup(), transform, canvas);
        for (OBJGroup group : model.getGroups()) {
            drawGroup(model, mtllib, group, transform, canvas);
        }
        
        return canvas.getContent();
    }
    
    private static Transformation transformOf(AxisAlignedBB3 box, int x, int y, int z) {
        Vector3 translation = box.getMin().negate();
        double maxDim = PrimMath.max(box.getSizeX(), box.getSizeY(), box.getSizeZ());
        int maxDesiredDim = PrimMath.max(x, y, z);
        double scale = (maxDesiredDim / (maxDim*1.01));
        
        return v -> v.add(translation).multiply(scale);
    }
    
    private void drawGroup(OBJModel model, MTLLibrary mtllib, OBJGroup group, Transformation transform, VoxelCanvas canvas) {
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
                    warning("invalid usemtl reference '"+material+"'");
                }
                else {
                    String diffMap = mtl.getDiffuseMap();
                    if (diffMap == null) {
                        texture = null;
                        warning("material '"+mtl+"' has no diffuse map");
                    }
                    else {
                        texture = mtllib.getMap(diffMap);
                        if (texture == null) {
                            warning("invalid map_Kd reference: "+diffMap);
                        }
                        else
                            warning("drawing group with diffuse map of "+mtl);
                    }
                }
            }
        }
        
        for (OBJFace polygon : group) {
            OBJFace[] triangles = tesselate(polygon);
            for (OBJFace triangle : triangles)
                drawTriangle(model, triangle, texture, transform, canvas);
        }
    }
    
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
    
    private static void drawTriangle(OBJModel model,
                                     OBJFace face,
                                     @Nullable Texture texture,
                                     Transformation transform,
                                     VoxelCanvas canvas) {
        OBJTriplet
            tA = face.getTriplet(0),
            tB = face.getTriplet(1),
            tC = face.getTriplet(2);
        Vertex3f
            vA = model.getVertex(tA.getVertexIndex()-1),
            vB = model.getVertex(tB.getVertexIndex()-1),
            vC = model.getVertex(tC.getVertexIndex()-1);
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
            return texture.get(u, 1-v);
        });
    
        /*
        canvas.drawLine(triangle.getA().toBlockVector(), triangle.getB().toBlockVector(), ColorMath.SOLID_RED);
        canvas.drawLine(triangle.getB().toBlockVector(), triangle.getC().toBlockVector(), ColorMath.SOLID_RED);
        canvas.drawLine(triangle.getC().toBlockVector(), triangle.getA().toBlockVector(), ColorMath.SOLID_RED);
        canvas.draw(triangle.getA().toBlockVector(), ColorMath.SOLID_BLUE);
        canvas.draw(triangle.getB().toBlockVector(), ColorMath.SOLID_BLUE);
        canvas.draw(triangle.getC().toBlockVector(), ColorMath.SOLID_BLUE);
        */
    }
    
}
