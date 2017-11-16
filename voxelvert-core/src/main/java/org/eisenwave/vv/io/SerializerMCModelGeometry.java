package org.eisenwave.vv.io;

import com.google.gson.stream.JsonWriter;
import net.grian.spatium.enums.Direction;
import net.grian.torrens.img.Texture;
import net.grian.torrens.io.TextSerializer;
import net.grian.torrens.object.BoundingBox6f;
import net.grian.torrens.object.Vertex3f;
import org.eisenwave.vv.object.MCUV;
import org.eisenwave.vv.object.MCElement;
import org.eisenwave.vv.object.MCModel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * <p>
 * A serializer for <b>Minecraft Model Geometry (.json)</b> files.
 * <p>
 * These files use the <b>JSON</b> file structure.
 * <p>
 * No version restrictions exist.
 */
public class SerializerMCModelGeometry implements TextSerializer<MCModel> {
    
    public final static String
        COMMENT = "Exported by VoxelVert, developed by Eisenwave",
        INDENT = "\t";
    
    private MCModel model;
    
    @Override
    public void toWriter(MCModel model, Writer writer) throws IOException {
        toWriter(model, new JsonWriter(writer));
    }
    
    public void toWriter(MCModel model, JsonWriter writer) throws IOException {
        this.model = model;
        writer.setIndent(INDENT);
        writeRoot(writer);
        writer.flush();
    }
    
    private void writeRoot(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("_comment").value(COMMENT);
        writeTextures(writer);
        writeElements(writer);
        writer.endObject();
    }
    
    private void writeTextures(JsonWriter writer) throws IOException {
        writer.name("textures").beginObject();
        for (String texture : model.getTextures())
            writer.name(texture).value(texture);
        writer.endObject();
    }
    
    private void writeElements(JsonWriter writer) throws IOException {
        writer.name("elements").beginArray();
        for (MCElement element : model)
            writeElement(element, writer);
        writer.endArray();
    }
    
    private void writeElement(MCElement element, JsonWriter writer) throws IOException {
        writer.beginObject();
        BoundingBox6f bounds = element.getShape();
        
        writer.name("from");
        writeVector(bounds.getMin(), writer);
        
        writer.name("to");
        writeVector(bounds.getMax(), writer);
        
        writer.name("faces").beginObject();
        for (Direction dir : Direction.values()) {
            if (element.hasUV(dir)) {
                String name = dir.face().name();
                writer.name(name.toLowerCase());
                writeUV(element, dir, writer);
            }
        }
        writer.endObject();
        
        writer.endObject();
    }
    
    private void writeUV(MCElement element, Direction dir, JsonWriter writer) throws IOException {
        MCUV uv = element.getUV(dir);
        assert uv != null;
        String textureName = uv.getTexture();
        int[] dims = getDimensions(textureName);
        float[] jsonUV = toJSONUV(uv, dims[0], dims[1]);
        int rotation = uv.getRotation();
        
        writer.beginObject().setIndent("");
        writer.name("uv")
            .beginArray()
            .value(jsonUV[0]).value(jsonUV[1]).value(jsonUV[2]).value(jsonUV[3])
            .endArray();
        
        writer.name("texture").value("#" + textureName);
        if (rotation != 0)
            writer.name("rotation").value(rotation);
        
        writer.endObject();
        writer.setIndent(INDENT);
    }
    
    @NotNull
    private int[] getDimensions(String textureName) throws IOException {
        Texture texture = model.getTexture(textureName);
        if (texture == null) throw new IOException("missing texture reference: " + textureName);
        return new int[] {
            texture.getWidth(),
            texture.getHeight()
        };
    }
    
    @NotNull
    private float[] toJSONUV(MCUV uv, int width, int height) {
        final float multiX = 16F / width, multiY = 16F / height;
        
        if (model.hasAntiBleed()) return new float[] {
            down(uv.getMinX() * multiX + ANTI_BLEED),
            down(uv.getMinY() * multiY + ANTI_BLEED),
            down(uv.getMaxX() * multiX - ANTI_BLEED),
            down(uv.getMaxY() * multiY - ANTI_BLEED)
        };
        else return new float[] {
            down(uv.getMinX() * multiX),
            down(uv.getMinY() * multiY),
            down(uv.getMaxX() * multiX),
            down(uv.getMaxY() * multiY)
        };
    }
    
    private final static DecimalFormat UV_DEC_FORMAT;
    
    static {
        UV_DEC_FORMAT = new DecimalFormat("##.######");
        UV_DEC_FORMAT.setRoundingMode(RoundingMode.DOWN);
    }
    
    private final static float ANTI_BLEED = 1 / 128F;
    
    /**
     * <p>
     * Rounds a floating point number towards zero to a precision of <code>2<sup>-8</sup></code>.
     * </p>
     * <p>
     * This is done to reduce file size by lowering the amount of floating point decimals which are unneeded for
     * uv-coordinates.
     * </p>
     *
     * @param number the number
     * @return a rounded number
     * @see RoundingMode#DOWN
     */
    @Contract(pure = true)
    private static float down(float number) {
        return (int) (number * 256) / 256F;
    }
    
    private void writeVector(Vertex3f vector, JsonWriter writer) throws IOException {
        writer.beginArray().setIndent("");
        writer.value(vector.getX()).value(vector.getY()).value(vector.getZ());
        writer.endArray().setIndent(INDENT);
    }
    
    private void writeFloats(float[] floats, JsonWriter writer) throws IOException {
        writer.beginArray().setIndent("");
        for (float f : floats) writer.value(f);
        writer.endArray().setIndent(INDENT);
    }
    
    
}
