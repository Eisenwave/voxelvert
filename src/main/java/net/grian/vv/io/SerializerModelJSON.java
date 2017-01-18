package net.grian.vv.io;

import com.google.gson.stream.JsonWriter;
import net.grian.spatium.enums.Direction;
import net.grian.spatium.geo.AxisAlignedBB;
import net.grian.spatium.geo.Vector;
import net.grian.vv.core.*;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SerializerModelJSON implements Serializer<MCModel> {

    public final static String
            COMMENT = "Designed using VoxelVertG - http://grian.net",
            INDENT = "\t";

    private MCModel model;

    @Override
    public void serialize(MCModel model, OutputStream stream) throws IOException {
        serialize(model, new OutputStreamWriter(stream));
    }

    public void serialize(MCModel model, Writer writer) throws IOException {
        serialize(model, new JsonWriter(writer));
    }

    public void serialize(MCModel model, JsonWriter writer) throws IOException {
        this.model = model;
        writer.setIndent(INDENT);
        writeRoot(writer);
        writer.close();
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
            writer.name(texture).value("voxelvert/"+texture);
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
        AxisAlignedBB bounds = element.getShape();

        writer.name("from");
        writeVector(bounds.getMin(), writer);

        writer.name("to");
        writeVector(bounds.getMax(), writer);

        writer.name("faces").beginObject();
        for (Direction dir : Direction.values()) {
            if (element.hasUV(dir)) {
                writer.name(dir.face().name().toLowerCase());
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

        writer.name("texture").value("#"+textureName);
        if (rotation != 0)
            writer.name("rotation").value(rotation);

        writer.endObject();
        writer.setIndent(INDENT);
    }

    private int[] getDimensions(String textureName) throws IOException {
        Texture texture = model.getTexture(textureName);
        if (texture == null) throw new IOException("missing texture reference: "+textureName);
        return new int[] {
                texture.getWidth(),
                texture.getHeight()
        };
    }

    private static float[] toJSONUV(MCUV uv, int width, int height) {
        final float mx = 16F / width, my = 16F / height;

        return new float[] {
                uv.getMinX() * mx,
                uv.getMinY() * my,
                uv.getMaxX() * mx,
                uv.getMaxY() * my
        };
    }

    private final static DecimalFormat UV_DEC_FORMAT;
    static {
        UV_DEC_FORMAT = new DecimalFormat("#.##");
        UV_DEC_FORMAT.setRoundingMode(RoundingMode.DOWN);
    }

    private final static float ANTI_BLEED = 1F / 1024;

    /**
     * Rounds a number towards 8, leaving two decimals of accuracy. This both reduces JSON file size and prevents edge
     * bleeding.
     *
     * @param coordinate the coordinate to round
     * @return a rounded coordinate
     */
    public static float preventBleed(float coordinate) {
        return (coordinate >= 8)? coordinate-ANTI_BLEED : coordinate+ANTI_BLEED;
    }

    private void writeVector(Vector vector, JsonWriter writer) throws IOException {
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
