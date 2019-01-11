package eisenwave.vv.ui.user;

import eisenwave.torrens.error.FileFormatException;
import eisenwave.torrens.img.ARGBSerializerBMP;
import eisenwave.torrens.img.ARGBSerializerWBMP;
import eisenwave.torrens.schematic.BlockStructureStream;
import eisenwave.torrens.schematic.DeserializerStructureBlocks;
import eisenwave.torrens.schematic.legacy.LegacyBlockStructure;
import eisenwave.torrens.voxel.*;
import eisenwave.torrens.wavefront.*;
import eisenwave.vv.io.DeserializerBCT;
import eisenwave.vv.io.SerializerBCT;
import eisenwave.vv.ui.fmtvert.Format;
import eisenwave.torrens.schematic.legacy.DeserializerSchematicBlocks;
import eisenwave.torrens.schematic.legacy.SerializerSchematicBlocks;
import eisenwave.torrens.stl.DeserializerSTL;
import eisenwave.vv.io.SerializerMCModelZip;
import eisenwave.vv.rp.BlockColorTable;
import eisenwave.torrens.img.DeserializerImage;
import eisenwave.torrens.img.Texture;
import eisenwave.vv.object.MCModel;
import eisenwave.torrens.stl.STLModel;
import eisenwave.torrens.stl.SerializerSTL;
import org.jetbrains.annotations.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipFile;

public class VVInventoryImpl implements VVInventory {
    
    @Nullable
    private static Class<?> getPreferredClass(Format format) {
        switch (format.getId()) {
            case "block_array":
                return LegacyBlockStructure.class;
        
            case "block_stream":
                return BlockStructureStream.class;
        
            case "voxel_array":
                return VoxelArray.class;
        
            case "voxel_mesh":
                return VoxelMesh.class;
        
            default:
                return null;
        }
    }
    
    protected final Map<String, Object> storage = new HashMap<>();
    protected final Map<String, VVInventoryVariable<?>> variables = new HashMap<>();
    
    private final VVUser owner;
    private final File dir;
    
    public VVInventoryImpl(@NotNull VVUser owner, @NotNull File directory) {
        this.owner = owner;
        this.dir = directory;
        
        if (!directory.exists() && !directory.mkdirs())
            throw new IllegalArgumentException(directory + "doesn't exist and couldn't be created");
    }
    
    @NotNull
    @Override
    public VVUser getOwner() {
        return owner;
    }
    
    @NotNull
    @Override
    public File getDirectory() {
        return dir;
    }
    
    @Override
    public Set<String> getVariableNames() {
        return Collections.unmodifiableSet(variables.keySet());
    }
    
    @Override
    public boolean hasVariable(String id) {
        return variables.containsKey(id);
    }
    
    @Nullable
    @Override
    public VVInventoryVariable<?> getVariable(String id) {
        return variables.get(id);
    }
    
    public boolean contains(@Nullable Format format, @NotNull String name) {
        if (hasVariable(name)) {
            VVInventoryVariable var = getVariable(name);
            assert var != null;
            return var.isSet();
        }
        if (format == null)
            return storage.containsKey(name) || new File(dir, name).exists();
        if (format.isInternal())
            return storage.containsKey(name);
        else
            return new File(dir, name).exists();
    }
    
    @Override
    public boolean contains(@NotNull String name) {
        if (hasVariable(name)) {
            VVInventoryVariable var = getVariable(name);
            assert var != null;
            return var.isSet();
        }
        return storage.containsKey(name) || new File(dir, name).exists();
    }
    
    @Override
    public BasicFileAttributes getBasicAttributes(String path) {
        File file = new File(dir, path);
        if (!file.exists()) return null;
        try {
            return Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            return null;
        }
    }
    
    // OPERATIONS
    
    @Nullable
    @Override
    public Object load(@NotNull Format format, @NotNull String name) throws IOException {
        //System.out.println("load: " + format + "<" + name + ">");
        if (hasVariable(name)) {
            VVInventoryVariable var = getVariable(name);
            assert var != null;
            return var.getFormat().equals(format)? var.get() : null;
        }
        
        if (format.isFile()) {
            File file = new File(dir, name);
            if (!file.exists())
                return null;
            
            switch (format.getId()) {
                case "colors":
                    return new DeserializerBCT().fromFile(file);
                
                case "qef":
                    return new DeserializerQEF().fromFile(file);
                
                case "qb":
                    return new DeserializerQB().fromFile(file);
                
                case "image":
                    return Texture.wrapOrCopy(new DeserializerImage().fromFile(file));
                
                case "resource_pack":
                    return new ZipFile(file);
                
                case "schematic":
                    return new DeserializerSchematicBlocks().fromFile(file);
                
                case "stl":
                    return new DeserializerSTL().fromFile(file);
                    
                case "structure":
                    return new DeserializerStructureBlocks().fromFile(file);
                
                case "wavefront":
                    return new DeserializerOBJ(new OBJModel(), file.getParentFile()).fromFile(file);
            }
            
            throw new FileFormatException("Unknown file format \"" + format + '"');
        }
        
        if (!storage.containsKey(name)) return null;
        
        Object obj = storage.get(name);
        
        Class<?> clazz = getPreferredClass(format);
        if (clazz == null)
            throw new FileFormatException("Unknown format \"" + format + '"');
        
        if (clazz.isInstance(obj))
            return obj;
        else
            throw new IOException("Stored object \"" + name + "\" must be a " + clazz.getSimpleName());
    }
    
    @Override
    public boolean save(@NotNull Format format, @NotNull Object object, @NotNull String name) throws IOException {
        //System.out.println("save: " + format + "<" + name + ">");
        if (hasVariable(name)) {
            VVInventoryVariable var = getVariable(name);
            assert var != null;
            
            if (var.isWritable() && var.getFormat().equals(format)) {
                //noinspection unchecked
                var.set(object);
                return true;
            }
            return false;
        }
        
        if (format.isFile()) {
            File file = new File(dir, name);
            
            switch (format.getId()) {
                
                case "colors": {
                    if (object instanceof BlockColorTable) {
                        new SerializerBCT().toFile((BlockColorTable) object, file);
                        return true;
                    }
                    else throw new IOException("object must be a " + BlockColorTable.class.getSimpleName());
                }
                
                case "model": {
                    if (object instanceof MCModel) {
                        new SerializerMCModelZip().toFile((MCModel) object, file);
                        return true;
                    }
                    else throw new IOException("object must be a " + MCModel.class.getSimpleName());
                }
                
                case "qef": {
                    if (object instanceof VoxelArray) {
                        new SerializerQEF().toFile((VoxelArray) object, file);
                        return true;
                    }
                    else throw new IOException("object must be a " + VoxelArray.class.getSimpleName());
                }
                
                case "qb": {
                    if (object instanceof QBModel) {
                        new SerializerQB().toFile((QBModel) object, file);
                        return true;
                    }
                    else throw new IOException("object must be a " + VoxelArray.class.getSimpleName());
                }
                
                case "schematic": {
                    if (object instanceof LegacyBlockStructure) {
                        new SerializerSchematicBlocks().toFile((LegacyBlockStructure) object, file);
                        return true;
                    }
                    else throw new IOException("object must be a " + LegacyBlockStructure.class.getSimpleName());
                }
                
                case "stl": {
                    if (object instanceof STLModel) {
                        new SerializerSTL().toFile((STLModel) object, file);
                        return true;
                    }
                    else throw new IOException("object must be a " + STLModel.class.getSimpleName());
                }
                
                case "wavefront": {
                    if (object instanceof OBJModel) {
                        File objFile = new File(dir, name);
                        OBJModel obj = (OBJModel) object;
                        new SerializerOBJ().toFile(obj, objFile);
                        
                        if (obj.hasMaterials()) {
                            File objDir = objFile.getParentFile();
                            MTLLibrary mtllib = obj.getMaterials();
                            assert mtllib != null;
                            new SerializerMTL().toFile(mtllib, new File(objDir, mtllib.getName()));
                            
                            for (MTLMaterial material : mtllib) {
                                String diffuseMap = material.getDiffuseMap();
                                if (diffuseMap != null) {
                                    Texture diff = mtllib.getMap(diffuseMap);
                                    assert diff != null;
                                    writeImageByExtension(diff.getImageWrapper(), new File(objDir, diffuseMap));
                                }
                            }
                        }
                        
                        return true;
                    }
                    else throw new IOException("object must be a " + OBJModel.class.getSimpleName());
                }
                
                case "image": {
                    if (object instanceof Texture) {
                        writeImageByExtension(((Texture) object).getImageWrapper(), file);
                        return true;
                    }
                    else if (object instanceof BufferedImage) {
                        writeImageByExtension((BufferedImage) object, file);
                        return true;
                    }
                    else throw new IOException("object must be a " + Texture.class.getSimpleName());
                }
                
                default: throw new IOException("Can't store unknown file format \"" + format + "\"");
            }
            
        }
    
        Class<?> clazz = getPreferredClass(format);
        if (clazz == null)
            throw new IOException("Can't store unknown format \"" + format + '"');
    
        if (clazz.isInstance(object)) {
            storage.put(name, object);
            return true;
        }
        else throw new IOException("Stored object \"" + name + "\" must be a " + clazz.getSimpleName());
    }
    
    @Override
    public boolean copy(@NotNull String source, @NotNull String target, boolean replace) throws IOException {
        if (storage.containsKey(source)) {
            storage.put(target, storage.get(source));
            return true;
        }
        Path
            sourcePath = new File(dir, source).toPath(),
            targetPath = new File(dir, target).toPath();
        if (!Files.exists(sourcePath)) {
            return false;
        }
        if (replace) {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        else {
            Files.copy(sourcePath, targetPath);
        }
        return true;
    }
    
    @Override
    public boolean move(@NotNull String source, @NotNull String target, boolean replace) throws IOException {
        if (storage.containsKey(source)) {
            storage.put(target, storage.remove(source));
            return true;
        }
        Path
            sourcePath = new File(dir, source).toPath(),
            targetPath = new File(dir, target).toPath();
        if (!Files.exists(sourcePath)) {
            return false;
        }
        if (replace) {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        else {
            Files.move(sourcePath, targetPath);
        }
        return true;
    }
    
    @Override
    public boolean delete(String name) {
        return storage.remove(name) != null || new File(dir, name).delete();
    }
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void clear() {
        storage.clear();
        dir.delete();
    }
    
    // UTIL
    
    private static void writeImageByExtension(BufferedImage image, File file) throws IOException {
        String ext = file.getName();
        int index = ext.lastIndexOf('.');
        if (index < 0) throw new IOException("can't recognize file extension of " + file);
        
        ext = ext.substring(index + 1).toLowerCase();
        switch (ext) {
            case "bmp":
                new ARGBSerializerBMP(image.getTransparency() != Transparency.OPAQUE).toFile(image, file);
                break;
            
            case "wbmp":
                new ARGBSerializerWBMP().toFile(image, file);
                break;
            
            default:
                if (!ImageIO.write(image, ext, file))
                    throw new IOException("no writer could be found for \"" + ext + "\"");
        }
    }
    
}
