package org.eisenwave.vv.io;

import net.grian.torrens.img.Texture;
import net.grian.torrens.io.DeserializerByteArray;
import net.grian.torrens.io.Serializer;
import org.eisenwave.vv.object.MCModel;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * A serializer for <b>Minecraft Model (.zip)</b> files.
 * <p>
 * This serializer packs the Minecraft model and its textures into a ready-to-use Minecraft resource pack.
 */
@SuppressWarnings("ConstantConditions")
public class SerializerMCModelZip implements Serializer<MCModel> {
    
    private final static int
        COMPRESSION_GEOMETRY = ZipEntry.STORED,
        COMPRESSION_MCMETA = ZipEntry.STORED,
        COMPRESSION_TEXTURES = ZipEntry.STORED;
    
    private final static String
        PATH_MODELS_ITEMS = "assets/minecraft/models/item/",
        PATH_TEXTURES = "assets/minecraft/textures/",
        IMAGE_FORMAT = "png",
        RESOURCE_PACK_MCMETA = "resourcepacks/pack.mcmeta";
    
    private final static ImageWriter IMAGE_WRITER = ImageIO.getImageWritersByFormatName(IMAGE_FORMAT).next();
    
    @Override
    public void toStream(MCModel model, OutputStream stream) throws IOException {
        toStream(model, new ZipOutputStream(stream));
    }
    
    public void toStream(MCModel model, ZipOutputStream stream) throws IOException {
        writeMCMeta(stream);
        writeGeometry(model, stream);
        writeTextures(model, stream);
    }
    
    private void writeMCMeta(ZipOutputStream stream) throws IOException {
        byte[] bytes = new DeserializerByteArray().fromResource(getClass(), RESOURCE_PACK_MCMETA);
        
        ZipEntry entry = new ZipEntry("pack.mcmeta");
        entry.setMethod(COMPRESSION_MCMETA);
        entry.setSize(bytes.length);
        if (COMPRESSION_MCMETA == ZipEntry.STORED) {
            entry.setCompressedSize(bytes.length);
            entry.setCrc(crc32(bytes));
        }
        
        stream.putNextEntry(entry);
        stream.write(bytes);
    }
    
    private void writeGeometry(MCModel model, ZipOutputStream stream) throws IOException {
        byte[] bytes = new SerializerMCModelGeometry().toBytes(model);
        
        ZipEntry entry = new ZipEntry(PATH_MODELS_ITEMS + "stick.json");
        entry.setMethod(COMPRESSION_GEOMETRY);
        entry.setSize(bytes.length);
        if (COMPRESSION_GEOMETRY == ZipEntry.STORED) {
            entry.setCompressedSize(bytes.length);
            entry.setCrc(crc32(bytes));
        }
        
        stream.putNextEntry(entry);
        stream.write(bytes);
    }
    
    private void writeTextures(MCModel model, ZipOutputStream stream) throws IOException {
        for (Map.Entry<String, byte[]> e : serializeTextures(model).entrySet()) {
            byte[] bytes = e.getValue();
            
            ZipEntry entry = new ZipEntry(PATH_TEXTURES + e.getKey());
            entry.setMethod(COMPRESSION_TEXTURES);
            entry.setSize(bytes.length);
            if (COMPRESSION_TEXTURES == ZipEntry.STORED) {
                entry.setCompressedSize(bytes.length);
                entry.setCrc(crc32(bytes));
            }
            
            stream.putNextEntry(entry);
            stream.write(bytes);
            stream.closeEntry();
        }
        
        stream.finish();
    }
    
    private static long crc32(byte[] bytes) {
        CRC32 crc = new CRC32();
        crc.update(bytes);
        return crc.getValue();
    }
    
    private static Map<String, byte[]> serializeTextures(MCModel model) throws IOException {
        Map<String, byte[]> result = new HashMap<>();
        
        for (String name : model.getTextures()) {
            Texture texture = model.getTexture(name);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ImageOutputStream imageStream = ImageIO.createImageOutputStream(byteStream);
            
            IMAGE_WRITER.setOutput(imageStream);
            IMAGE_WRITER.write(texture.getImageWrapper());
            
            result.put(name + "." + IMAGE_FORMAT, byteStream.toByteArray());
        }
        
        return result;
    }
    
}
