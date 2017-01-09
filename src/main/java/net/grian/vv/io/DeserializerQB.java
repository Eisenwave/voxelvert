package net.grian.vv.io;

import net.grian.spatium.util.IOMath;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.util.Colors;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class DeserializerQB implements Deserializer<VoxelMesh> {

    private boolean compressed, visibilityMaskEncoded, zLeft;
    private int colorFormat, numMatrices;
    private VoxelMesh mesh;

    private final Logger logger;

    public DeserializerQB(Logger logger) {
        this.logger = logger;
    }

    public DeserializerQB() {
        this(Logger.getGlobal());
    }

    @Override
    public VoxelMesh deserialize(InputStream stream) throws IOException {
        logger.info("deserializing qb...");
        DataInputStream dataStream = new DataInputStream(stream);

        deserializeHeader(dataStream);
        logger.info("deserializing "+numMatrices+" matrices with"+
                ": compression="+compressed+
                ", colorFormat="+colorFormat+
                ", visMaskEncoded="+visibilityMaskEncoded+
                ", zLeft="+zLeft);

        mesh = new VoxelMesh();
        for (int i = 0; i < numMatrices; i++)
            deserializeMatrix(dataStream);

        logger.info("deserialized matrices");
        return mesh;
    }

    private void deserializeHeader(DataInputStream stream) throws IOException {
        int version = stream.readInt(); //big endian
        if (version != SerializerQB.CURRENT_VERSION)
            throw new FileVersionException(version+" != current ("+SerializerQB.CURRENT_VERSION+")");

        this.colorFormat = stream.readInt();
        if (colorFormat != SerializerQB.COLOR_FORMAT_RGBA && colorFormat != SerializerQB.COLOR_FORMAT_BGRA)
            throw new FileSyntaxException("unknown color format: "+colorFormat);

        int zAxisOrientation = stream.readInt();
        if (zAxisOrientation != SerializerQB.Z_ORIENT_LEFT && zAxisOrientation != SerializerQB.Z_ORIENT_RIGHT)
            throw new FileSyntaxException("unknown z axis orientation: "+zAxisOrientation);
        this.zLeft = zAxisOrientation==SerializerQB.Z_ORIENT_LEFT;

        int compressedInt = readLittleInt(stream);
        if (compressedInt != SerializerQB.UNCOMPRESSED && compressedInt != SerializerQB.COMPRESSED)
            throw new FileSyntaxException("unknown compression: "+compressedInt);
        this.compressed = compressedInt == SerializerQB.COMPRESSED;

        int visEncodedInt = readLittleInt(stream);
        if (visEncodedInt != SerializerQB.VIS_MASK_UNENCODED && visEncodedInt != SerializerQB.VIS_MASK_ENCODED)
            throw new FileSyntaxException("unknown vis mask encoding: "+visEncodedInt);
        this.visibilityMaskEncoded = visEncodedInt == SerializerQB.VIS_MASK_ENCODED;

        this.numMatrices = readLittleInt(stream);
    }

    private void deserializeMatrix(DataInputStream stream) throws IOException {
        // read matrix name
        byte nameLength = stream.readByte();
        stream.skipBytes(nameLength);

        final int
                sizeX = readLittleInt(stream),
                sizeY = readLittleInt(stream),
                sizeZ = readLittleInt(stream),
                posX  = readLittleInt(stream),
                posY  = readLittleInt(stream),
                posZ  = readLittleInt(stream);

        logger.info("deserializing matrix: "+sizeX+" x "+sizeY+" x "+sizeZ+" at "+posX+", "+posY+", "+posZ);

        VoxelArray matrix = compressed?
                readCompressed(sizeX, sizeY, sizeZ, stream) :
                readUncompressed(sizeX, sizeY, sizeZ, stream);

        mesh.add(posX, posY, posZ, matrix);
    }

    private VoxelArray readUncompressed(int sizeX, int sizeY, int sizeZ, DataInputStream stream) throws IOException {
        VoxelArray matrix = new VoxelArray(sizeX, sizeY, sizeZ);
        final int maxZ = sizeZ-1;

        for(int z = 0; z < sizeX; z++)
            for(int y = 0; y < sizeY; y++)
                for(int x = 0; x < sizeZ; x++)
                    matrix.setRGB(x, y, (zLeft? z : maxZ-z), asARGB(stream.readInt()));

        return matrix;
    }

    private VoxelArray readCompressed(int sizeX, int sizeY, int sizeZ, DataInputStream stream) throws IOException {
        VoxelArray matrix = new VoxelArray(sizeX, sizeY, sizeZ);
        final int maxZ = sizeZ-1;

        for (int z = 0; z<sizeZ; z++) {
            int index = 0;

            while (true) {
                int data = readLittleInt(stream);

                if (data == SerializerQB.NEXTSLICEFLAG) break;
                else if (data == SerializerQB.CODEFLAG) {
                    int count = readLittleInt(stream);
                    data = stream.readInt();

                    for(int i = 0; i < count; i++) {
                        int x = index%sizeX, y = index/sizeX;
                        matrix.setRGB(x, y, (zLeft? z : maxZ-z), asARGB(data));
                        index++;
                    }
                }
                else {
                    int x = index%sizeX, y = index/sizeX;
                    matrix.setRGB(x, y, (zLeft? z : maxZ-z), asARGB(IOMath.invertBytes(data)));
                    index++;
                }
            }
        }

        return matrix;
    }

    private static int readLittleInt(InputStream stream) throws IOException {
        byte[] bytes = new byte[4];
        if (stream.read(bytes) != 4) throw new IOException("incomplete int read");
        return ((bytes[3]&0xFF)<<24) | ((bytes[2]&0xFF)<<16) | ((bytes[1]&0xFF)<<8) | (bytes[0]&0xFF);
    }

    /**
     * Converts a color integer using the qb's color format.
     *
     * @param color the color int
     * @return an ARGB int
     */
    private int asARGB(int color) {
        int argb;

        switch (colorFormat) {
            case 0: argb = Colors.fromRGB( //RGBA
                    (color >> 24) & 0xFF,
                    (color >> 16) & 0xFF,
                    (color >> 8) & 0xFF,
                    color & 0xFF);
                break;
            case 1: argb = Colors.fromRGB( //BGRA
                    color & 0xFF,
                    (color >> 24) & 0xFF,
                    (color >> 16) & 0xFF,
                    (color >> 8) & 0xFF);
                break;
            default: throw new AssertionError(colorFormat);
        }

        //if any side is visible, make color solid
        if (visibilityMaskEncoded) {
            if ((Colors.alpha(argb) != 0)) argb |= 0xFF_000000;
            else argb &= 0x00_FFFFFF;
        }

        return argb;
    }

}
