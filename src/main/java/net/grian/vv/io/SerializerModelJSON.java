package net.grian.vv.io;

import com.google.gson.stream.JsonWriter;
import net.grian.spatium.enums.Direction;
import net.grian.spatium.geo.AxisAlignedBB;
import net.grian.spatium.geo.Vector;
import net.grian.vv.core.MCUV;
import net.grian.vv.core.Texture;
import net.grian.vv.core.MCElement;
import net.grian.vv.core.MCModel;

import java.io.*;

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
        int width, height;
        {
            Texture texture = model.getTexture(textureName);
            if (texture == null) throw new IOException("missing texture reference: "+textureName);
            width = texture.getWidth();
            height = texture.getHeight();
        }

        writer.beginObject();
        writer.name("uv")
                .beginArray()
                .value((uv.getMinX()+1) * 16F / width)
                .value((uv.getMinY()+1) * 16F / height)
                .value((uv.getMaxX()+1) * 16F / width)
                .value((uv.getMaxY()+1) * 16F / height)
                .endArray();

        writer.name("texture").value(textureName);
        writer.endObject();
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
