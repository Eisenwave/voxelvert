package eisenwave.vv.ui.user;

import eisenwave.vv.io.SerializerBCT;
import eisenwave.vv.ui.fmtvert.Format;
import net.grian.torrens.schematic.BlockStructure;
import net.grian.torrens.schematic.DeserializerSchematicBlocks;
import net.grian.torrens.schematic.SerializerSchematicBlocks;
import net.grian.torrens.stl.DeserializerSTL;
import net.grian.torrens.voxel.*;
import net.grian.torrens.wavefront.*;
import eisenwave.vv.io.SerializerMCModelZip;
import eisenwave.vv.rp.BlockColorTable;
import net.grian.torrens.img.DeserializerImage;
import net.grian.torrens.img.Texture;
import eisenwave.vv.object.MCModel;
import net.grian.torrens.stl.STLModel;
import net.grian.torrens.stl.SerializerSTL;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipFile;

public class VVInventoryImpl implements VVInventory {
    
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
        if (hasVariable(name)) {
            VVInventoryVariable var = getVariable(name);
            assert var != null;
            return var.getFormat().equals(format)? var.get() : null;
        }
        
        switch (format.getId()) {
            
            case "qef": {
                File file = new File(dir, name);
                return file.exists()? new DeserializerQEF().fromFile(file) : null;
            }
            
            case "qb": {
                File file = new File(dir, name);
                return file.exists()? new DeserializerQB().fromFile(file) : null;
            }
            
            case "image": {
                File file = new File(dir, name);
                return file.exists()? Texture.wrapOrCopy(new DeserializerImage().fromFile(file)) : null;
            }
            
            case "resource_pack": {
                File file = new File(dir, name);
                return file.exists()? new ZipFile(file) : null;
            }
            
            case "schematic": {
                File file = new File(dir, name);
                return file.exists()? new DeserializerSchematicBlocks().fromFile(file) : null;
            }
    
            case "stl": {
                File file = new File(dir, name);
                return file.exists()? new DeserializerSTL().fromFile(file) : null;
            }
    
            case "wavefront": {
                OBJModel model = new OBJModel();
                File file = new File(dir, name);
                File objDir = file.getParentFile();
                
                return file.exists()? new DeserializerOBJ(model, objDir).fromFile(file) : null;
            }
            
            case "block_array": {
                if (!storage.containsKey(name)) return null;
                Object obj = storage.get(name);
                if (obj instanceof BlockStructure) return obj;
                else
                    throw new IOException("stored object \"" + name + "\" must be a " + BlockStructure.class.getSimpleName());
            }
    
            case "voxel_array": {
                if (!storage.containsKey(name)) return null;
                Object obj = storage.get(name);
                if (obj instanceof VoxelArray) return obj;
                else
                    throw new IOException("stored object \"" + name + "\" must be a " + VoxelArray.class.getSimpleName());
            }
    
            case "voxel_mesh": {
                if (!storage.containsKey(name)) return null;
                Object obj = storage.get(name);
                if (obj instanceof VoxelMesh) return obj;
                else
                    throw new IOException("stored object \"" + name + "\" must be a " + VoxelMesh.class.getSimpleName());
            }
            
            default: return null;
        }
    }
    
    @Override
    public boolean save(@NotNull Format format, @NotNull Object object, @NotNull String name) throws IOException {
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
        
        switch (format.getId()) {
    
            case "colors": {
                if (object instanceof BlockColorTable) {
                    File file = new File(dir, name);
                    new SerializerBCT().toFile((BlockColorTable) object, file);
                    return true;
                }
                else throw new IOException("object must be a " + BlockColorTable.class.getSimpleName());
            }
    
            case "model": {
                if (object instanceof MCModel) {
                    File file = new File(dir, name);
                    new SerializerMCModelZip().toFile((MCModel) object, file);
                    return true;
                }
                else throw new IOException("object must be a " + MCModel.class.getSimpleName());
            }
            
            case "qef": {
                if (object instanceof VoxelArray) {
                    File file = new File(dir, name);
                    new SerializerQEF().toFile((VoxelArray) object, file);
                    return true;
                }
                else throw new IOException("object must be a " + VoxelArray.class.getSimpleName());
            }
    
            case "qb": {
                if (object instanceof QBModel) {
                    File file = new File(dir, name);
                    new SerializerQB().toFile((QBModel) object, file);
                    return true;
                }
                else throw new IOException("object must be a " + VoxelArray.class.getSimpleName());
            }
    
            case "schematic": {
                if (object instanceof BlockStructure) {
                    File file = new File(dir, name);
                    new SerializerSchematicBlocks().toFile((BlockStructure) object, file);
                    return true;
                }
                else throw new IOException("object must be a " + BlockStructure.class.getSimpleName());
            }
            
            case "stl": {
                if (object instanceof STLModel) {
                    File file = new File(dir, name);
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
                                writeImageByExtension(mtllib.getMap(diffuseMap), new File(objDir, diffuseMap));
                            }
                        }
                    }
                    
                    return true;
                }
                else throw new IOException("object must be a " + OBJModel.class.getSimpleName());
            }
    
            case "image": {
                if (object instanceof Texture) {
                    File file = new File(dir, name);
                    writeImageByExtension((Texture) object, file);
                    return true;
                }
                else if (object instanceof BufferedImage) {
                    File file = new File(dir, name);
                    writeImageByExtension(Texture.wrapOrCopy((BufferedImage) object), file);
                    return true;
                }
                else throw new IOException("object must be a " + Texture.class.getSimpleName());
            }
            
            case "block_array": {
                if (object instanceof BlockStructure) {
                    storage.put(name, object);
                    return true;
                }
                else throw new IOException("object must be a " + BlockStructure.class.getSimpleName());
            }
            
            case "voxel_array": {
                if (object instanceof VoxelArray) {
                    storage.put(name, object);
                    return true;
                }
                else throw new IOException("object must be a " + VoxelArray.class.getSimpleName());
            }
    
            case "voxel_mesh": {
                if (object instanceof VoxelMesh) {
                    storage.put(name, object);
                    return true;
                }
                else throw new IOException("object must be a " + VoxelMesh.class.getSimpleName());
            }
            
            default: return false;
        }
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
    
    private static void writeImageByExtension(Texture image, File file) throws IOException {
        String ext = file.getName();
        int index = ext.lastIndexOf('.');
        if (index < 0) throw new IOException("can't recognize file extension of " + file);
    
        ext = ext.substring(index + 1);
        ImageIO.write(image.getImageWrapper(), ext, file);
    }
    
}
