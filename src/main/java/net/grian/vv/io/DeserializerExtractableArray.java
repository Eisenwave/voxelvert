package net.grian.vv.io;

import com.google.gson.*;
import net.grian.utilities.JsonUtil;
import net.grian.vv.core.BlockKey;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class DeserializerExtractableArray implements Deserializer<ExtractableColor[]> {

    @Override
    public ExtractableColor[] deserialize(InputStream stream) throws IOException {
        return deserialize(IOUtils.toString(stream));
    }

    public ExtractableColor[] deserialize(String json) throws FileSyntaxException {
        JsonElement element = JsonUtil.parse(json);
        if (!(element instanceof JsonArray)) throw new FileSyntaxException("json root must be an array");
        return deserialize((JsonArray) element);
    }

    public ExtractableColor[] deserialize(JsonArray root) throws FileSyntaxException {
        final int length = root.size();
        ExtractableColor[] colors = new ExtractableColor[length];

        for (int i = 0; i<length; i++) {
            JsonElement obj = root.get(i);
            if (!(obj instanceof JsonObject))
                throw new FileSyntaxException("root must be array of objects");
            colors[i] = deserializeColor((JsonObject) obj);
        }

        return colors;
    }

    public static ExtractableColor deserializeColor(JsonObject json) throws FileSyntaxException {
        JsonElement elementBlock = json.get("block");
        if (elementBlock == null) throw new FileSyntaxException(json+" is missing element 'block'");
        if (!elementBlock.isJsonPrimitive()) throw new FileSyntaxException("'block' must be primitive");

        JsonElement elementData = json.get("data");
        if (elementData == null) throw new FileSyntaxException(json+" is missing element 'data'");
        if (!elementData.isJsonPrimitive())  throw new FileSyntaxException("'data' must be primitive");

        JsonElement elementType = json.get("type");
        if (elementType == null) throw new FileSyntaxException(json+" is missing element 'type'");
        if (!elementType.isJsonPrimitive())  throw new FileSyntaxException("'type' must be primitive");

        JsonElement elementValue = json.get("value");
        if (elementValue == null) throw new FileSyntaxException(json+" is missing element 'value'");
        if (!elementValue.isJsonPrimitive())  throw new FileSyntaxException("'value' must be primitive");

        JsonElement elementVoxels = json.get("voxels");
        if (elementVoxels == null) throw new FileSyntaxException(json+" is missing element 'voxels'");
        if (!elementVoxels.isJsonPrimitive())  throw new FileSyntaxException("'voxels' must be primitive");

        JsonElement elementTint = json.get("tint");
        if (elementTint == null) throw new FileSyntaxException(json+" is missing element 'tint'");
        if (!elementTint.isJsonPrimitive())  throw new FileSyntaxException("'tint' must be primitive");

        int id = deserializeBlock(elementBlock.getAsJsonPrimitive());
        byte data = deserializeData(elementData.getAsJsonPrimitive());
        String type = deserializeType(elementType.getAsJsonPrimitive());
        String value = deserializeValue(elementValue.getAsJsonPrimitive());
        int voxels = deserializeVoxels(elementVoxels.getAsJsonPrimitive());
        boolean tint = deserializeTint(elementTint.getAsJsonPrimitive());
        BlockKey block = new BlockKey(id, data);

        switch (type) {
            case "texture": return new ExtractableColor(block, value, voxels, tint);
            case "rgb": return new ExtractableColor(block, parseHex(value), voxels, tint);
            default: throw new FileSyntaxException("unknown 'type': "+type);
        }
    }

    public static int parseHex(String hexadecimal) {
        if (hexadecimal.startsWith("0x")) hexadecimal = hexadecimal.substring(2);
        if (hexadecimal.startsWith("#")) hexadecimal = hexadecimal.substring(1);
        return (int) Long.parseLong(hexadecimal, 16);
    }

    @SuppressWarnings("deprecation")
    private static int deserializeBlock(JsonPrimitive primitive) throws FileSyntaxException {
        if (primitive.isString())
            try {return org.bukkit.Material.valueOf(primitive.getAsString()).getId();}
            catch (IllegalArgumentException ex) {throw new FileSyntaxException(ex);}
        else if (primitive.isNumber())
            return primitive.getAsNumber().intValue();
        else throw new FileSyntaxException("'block' must be string or number");
    }

    private static byte deserializeData(JsonPrimitive primitive) throws FileSyntaxException {
        if (!primitive.isNumber()) throw new FileSyntaxException("'data' must be a number");
        return primitive.getAsByte();
    }

    private static String deserializeType(JsonPrimitive primitive) throws FileSyntaxException {
        if (!primitive.isString()) throw new FileSyntaxException("'type' must be a string");
        return primitive.getAsString();
    }

    private static String deserializeValue(JsonPrimitive primitive) throws FileSyntaxException {
        if (!primitive.isString()) throw new FileSyntaxException("'value' must be a string");
        return primitive.getAsString();
    }

    private static int deserializeVoxels(JsonPrimitive primitive) throws FileSyntaxException {
        if (!primitive.isNumber()) throw new FileSyntaxException("'voxels' must be a number");
        return primitive.getAsInt();
    }

    private static boolean deserializeTint(JsonPrimitive primitive) throws FileSyntaxException {
        if (!primitive.isBoolean()) throw new FileSyntaxException("'tint' must be a boolean");
        return primitive.getAsBoolean();
    }

}
