package net.grian.vv.io;

import net.grian.spatium.util.IOMath;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.core.VoxelMesh;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SerializerQB implements Serializer<VoxelMesh> {

    public final static int
            CURRENT_VERSION = 0x01_01_00_00,
            COLOR_FORMAT_RGBA = 0,
            COLOR_FORMAT_BGRA = 1,
            Z_ORIENT_LEFT = 0,
            Z_ORIENT_RIGHT = 1,
            UNCOMPRESSED = 0,
            COMPRESSED = 1,
            VIS_MASK_ENCODED = 0,
            VIS_MASK_UNENCODED = 1,
            CODEFLAG = 2,
            NEXTSLICEFLAG = 6;

    private VoxelMesh mesh;

    private int colorFormat;

    @Override
    public void serialize(VoxelMesh mesh, OutputStream stream) throws IOException {
        DataOutputStream dataStream = new DataOutputStream(stream);
        serialize(mesh, dataStream);
    }

    public void serialize(VoxelMesh mesh, DataOutputStream stream) throws IOException {
        this.mesh = mesh;
        serializeHeader(stream);
        for (VoxelMesh.Element matrix : mesh)
            serializeMatrix(matrix, stream);
    }

    private void serializeHeader(DataOutputStream stream) throws IOException {
        stream.writeInt(CURRENT_VERSION);
        stream.writeInt(colorFormat = COLOR_FORMAT_RGBA);
        stream.writeInt(Z_ORIENT_LEFT);
        stream.writeInt(UNCOMPRESSED);
        writeLittleInt(stream, VIS_MASK_UNENCODED);
        writeLittleInt(stream, mesh.size());
    }

    private void serializeMatrix(VoxelMesh.Element matrix, DataOutputStream stream) throws IOException {
        stream.write(new byte[1]); //one byte for name = 0

        VoxelArray array = matrix.getArray();
        writeLittleInt(stream, array.getSizeX());
        writeLittleInt(stream, array.getSizeY());
        writeLittleInt(stream, array.getSizeZ());
        writeLittleInt(stream, matrix.getMinX());
        writeLittleInt(stream, matrix.getMinY());
        writeLittleInt(stream, matrix.getMinZ());

        serializeUncompressed(array, stream);
    }

    private void serializeUncompressed(VoxelArray array, DataOutputStream stream) throws IOException {
        final int
                limX = array.getSizeX(),
                limY = array.getSizeY(),
                limZ = array.getSizeZ();

        for (int z = 0; z<limZ; z++)
            for (int y = 0; y<limY; y++)
                for (int x = 0; x<limX; x++)
                    stream.writeInt(asColor(array.getRGB(x, y, z)));
    }

    private int asColor(int argb) {
        byte[] bytes = IOMath.asBytes(argb);
        if (colorFormat == COLOR_FORMAT_RGBA) {
            return
                    ((bytes[1]&0xFF)<<24) | ((bytes[2]&0xFF)<<16) | ((bytes[3]&0xFF)<<8) | (bytes[0]&0xFF);
        }
        else if (colorFormat == COLOR_FORMAT_BGRA) {
            return
                    ((bytes[3]&0xFF)<<24) | ((bytes[2]&0xFF)<<16) | ((bytes[1]&0xFF)<<8) | (bytes[0]&0xFF);
        }
        else throw new IllegalStateException("unknown color format");
    }

    private static void writeLittleInt(OutputStream stream, int number) throws IOException {
        stream.write(new byte [] {
                (byte) (number &0xFF),
                (byte) ((number>>8) &0xFF),
                (byte) ((number>>16) &0xFF),
                (byte) ((number>>24) &0xFF)});
    }

}
