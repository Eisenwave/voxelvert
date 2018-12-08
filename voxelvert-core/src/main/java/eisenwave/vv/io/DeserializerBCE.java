package eisenwave.vv.io;

import com.google.gson.*;
import eisenwave.vv.rp.*;
import eisenwave.torrens.error.FileSyntaxException;
import eisenwave.torrens.io.TextDeserializer;
import eisenwave.torrens.object.Rectangle4i;
import eisenwave.torrens.schematic.legacy.BlockKey;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

public class DeserializerBCE implements TextDeserializer<BlockColorExtractor> {
    
    public final static short
        DEFAULT_VOXELS = 4096;
    
    private BlockColorExtractor result;
    
    @NotNull
    @Override
    public BlockColorExtractor fromReader(Reader reader) throws FileSyntaxException {
        JsonObject root = new Gson().fromJson(reader, JsonObject.class);
        BlockColorExtractor extractor = deserializeBlocks(root.getAsJsonArray("blocks"));
        
        String[] maps = deserializeMaps(root.getAsJsonObject("colormap"));
        extractor.setGrassMap(maps[0]);
        extractor.setFoliageMap(maps[1]);
        
        return extractor;
    }
    
    public BlockColorExtractor deserializeBlocks(JsonArray root) throws FileSyntaxException {
        final int length = root.size();
        result = new BlockColorExtractor();
        
        for (int i = 0; i < length; i++) {
            JsonElement obj = root.get(i);
            if (!(obj instanceof JsonObject))
                throw new FileSyntaxException("root must be array of objects");
            addColor((JsonObject) obj);
        }
        
        return result;
    }
    
    private void addColor(JsonObject json) throws FileSyntaxException {
        
        final String type;
        {
            JsonElement elementType = json.get("type");
            if (elementType == null) throw new FileSyntaxException(json + " is missing element 'type'");
            if (!elementType.isJsonPrimitive()) throw new FileSyntaxException("'type' must be primitive");
            
            type = deserializeString(elementType.getAsJsonPrimitive(), "type");
        }
        
        BlockKey[] blocks;
        blocks:
        {
            JsonPrimitive primitiveId = getPrimitive(json, "id");
            int id = deserializeInt(primitiveId, "id");
            
            JsonPrimitive primitiveData = getPrimitive(json, "data");
            if (primitiveData.isNumber()) {
                byte data = deserializeData(primitiveData);
                blocks = new BlockKey[] {new BlockKey(id, data)};
            }
            
            else if (primitiveData.isString()) {
                String dataStr = primitiveData.getAsString();
                //noinspection EmptyCatchBlock
                try {
                    byte data = Byte.parseByte(dataStr);
                    blocks = new BlockKey[] {new BlockKey(id, data)};
                    break blocks;
                } catch (NumberFormatException ex) {}
                
                String[] split = dataStr.split("-", 2);
                if (split.length < 2)
                    throw new FileSyntaxException("'data' range must be of format '<from>-<to>'");
                
                try {
                    byte from = Byte.parseByte(split[0]), to = Byte.parseByte(split[1]);
                    
                    if (from > to) throw new NumberFormatException("'data' range out of order");
                    if (from < 0) throw new NumberFormatException("start of range must be in range(0,15)");
                    if (to > 15) throw new NumberFormatException("end of range must be in range(0,15)");
                    
                    blocks = new BlockKey[to - from + 1];
                    for (byte i = 0, d = from; d <= to; d++, i++)
                        blocks[i] = new BlockKey(id, d);
                    
                } catch (NumberFormatException ex) {
                    throw new FileSyntaxException("failed to parse 'data' \"" + dataStr + "\"", ex);
                }
            }
            
            else throw new FileSyntaxException("'data' must be a number or a string");
        }
        
        BlockColorMeta meta = deserializeMeta(json);
        
        switch (type) {
            
            case "texture": {
                JsonPrimitive primitiveTexture = getPrimitive(json, "texture");
                String texture = deserializeString(primitiveTexture, "texture");
                
                for (BlockKey block : blocks)
                    result.put(block, meta, texture);
                break;
            }
            
            case "texture_area": {
                JsonPrimitive primitiveTexture = getPrimitive(json, "texture");
                String texture = deserializeString(primitiveTexture, "texture");
                
                JsonArray arrayArea = getArray(json, "area");
                Rectangle4i area = deserializeArea(arrayArea);
                
                for (BlockKey block : blocks)
                    result.put(block, meta, texture, area);
                break;
            }
            
            case "rgb": {
                JsonPrimitive primitiveValue = getPrimitive(json, "rgb");
                String value = deserializeString(primitiveValue, "rgb");
                
                for (BlockKey block : blocks)
                    result.put(block, meta, parseHex(value));
                break;
            }
            
            
            default: throw new AssertionError(type);
        }
    }
    
    // UTIL
    
    public static int parseHex(String hexadecimal) {
        if (hexadecimal.startsWith("0x")) hexadecimal = hexadecimal.substring(2);
        if (hexadecimal.startsWith("#")) hexadecimal = hexadecimal.substring(1);
        return (int) Long.parseLong(hexadecimal, 16);
    }
    
    private static JsonPrimitive getPrimitive(JsonObject json, String name) throws FileSyntaxException {
        JsonElement elementValue = json.get(name);
        if (elementValue == null)
            throw new FileSyntaxException(String.format("%s is missing element '%s'", json, name));
        if (!elementValue.isJsonPrimitive())
            throw new FileSyntaxException(String.format("'%s' must be a primitive", name));
        
        return elementValue.getAsJsonPrimitive();
    }
    
    private static JsonArray getArray(JsonObject json, String name) throws FileSyntaxException {
        JsonElement elementValue = json.get(name);
        if (elementValue == null)
            throw new FileSyntaxException(String.format("%s is missing element '%s'", json, name));
        if (!elementValue.isJsonArray())
            throw new FileSyntaxException(String.format("'%s' must be an array", name));
        
        return elementValue.getAsJsonArray();
    }
    
    private static int getInt(JsonArray array, int index) throws FileSyntaxException {
        JsonElement elementValue = array.get(index);
        if (!elementValue.isJsonPrimitive())
            throw new FileSyntaxException(String.format("%s is not an int-array", array));
        
        return elementValue.getAsInt();
    }
    
    // DESERIALIZE UTIL
    
    private static int deserializeInt(JsonPrimitive primitive, String name) throws FileSyntaxException {
        if (!primitive.isNumber()) throw new FileSyntaxException(String.format("'%s' must be a number", name));
        return primitive.getAsInt();
    }
    
    private static short deserializeShort(JsonPrimitive primitive, String name) throws FileSyntaxException {
        if (!primitive.isNumber()) throw new FileSyntaxException(String.format("'%s' must be a number", name));
        return primitive.getAsShort();
    }
    
    private static String deserializeString(JsonPrimitive primitive, String name) throws FileSyntaxException {
        if (!primitive.isString()) throw new FileSyntaxException(String.format("'%s' must be a string", name));
        return primitive.getAsString();
    }
    
    private static String[] deserializeMaps(JsonObject json) {
        String[] result = new String[2];
        JsonPrimitive grass = json.getAsJsonPrimitive("grass");
        if (grass != null) result[0] = grass.getAsString();
        
        JsonPrimitive foliage = json.getAsJsonPrimitive("foliage");
        if (foliage != null) result[1] = foliage.getAsString();
        
        return result;
    }
    
    @NotNull
    private static BlockColorMeta deserializeMeta(JsonObject json) throws FileSyntaxException {
        final Tint tint;
        final int tintRGB;
        tint: {
            JsonElement elementTint = json.get("tint");
            if (elementTint == null) {
                tint = Tint.NONE;
                tintRGB = 0;
                break tint;
            }
            else if (!elementTint.isJsonPrimitive())
                throw new FileSyntaxException("'tint' must be primitive");
            
            JsonPrimitive primitive = elementTint.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                tint = Tint.CONSTANT;
                tintRGB = primitive.getAsInt();
            }
            else if (primitive.isString()) {
                tintRGB = 0;
                switch (primitive.getAsString()) {
                    case "none": tint = Tint.NONE; break tint;
                    case "grass": tint = Tint.GRASS; break tint;
                    case "foliage": tint = Tint.FOLIAGE; break tint;
                    default: throw new FileSyntaxException("invalid tint '" + primitive.getAsString() + "'");
                }
            }
            else throw new FileSyntaxException("'tint' must be an int or string");
        }
    
        final int flags;
        flags:
        {
            JsonElement element = json.get("flags");
            if (element == null) {
                flags = 0;
                break flags;
            }
            else if (!element.isJsonPrimitive())
                throw new FileSyntaxException("'flags' must be primitive");
        
            JsonPrimitive primitive = (JsonPrimitive) element;
            if (!primitive.isString())
                throw new FileSyntaxException("'flags' must be a string");
            flags = BlockColorMeta.parseFlags(primitive.getAsString());
        }
        
        final short voxels;
        {
            JsonElement elementVoxels = json.get("voxels");
            if (elementVoxels == null)
                voxels = DEFAULT_VOXELS;
            else if (!elementVoxels.isJsonPrimitive())
                throw new FileSyntaxException("'voxels' must be primitive");
            else
                voxels = deserializeShort(elementVoxels.getAsJsonPrimitive(), "voxels");
        }
    
        return new BlockColorMeta(flags, voxels, tint, tintRGB);
    }
    
    @NotNull
    private static Rectangle4i deserializeArea(JsonArray array) throws FileSyntaxException {
        if (array.size() != 4)
            throw new FileSyntaxException("'area' array must have a length of 4");
        
        final int
            minX = getInt(array, 0),
            minY = getInt(array, 1),
            maxX = getInt(array, 2),
            maxY = getInt(array, 3);
        
        return new Rectangle4i(minX, minY, maxX, maxY);
    }
    
    private static byte deserializeData(JsonPrimitive primitive) throws FileSyntaxException {
        if (!primitive.isNumber()) throw new FileSyntaxException("'data' must be a number");
        return primitive.getAsByte();
    }
    
}
