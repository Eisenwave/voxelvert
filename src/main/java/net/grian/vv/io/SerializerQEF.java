package net.grian.vv.io;

import net.grian.vv.core.VoxelArray.Voxel;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.io.Serializer;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("SpellCheckingInspection")
public class SerializerQEF implements Serializer<VoxelArray> {

    private Color[] colors;

    /** An array in which instead of an rgb value each visible voxel's rgb integer is the index of the color in the
     * color array + 1. */
    private VoxelArray compressedArray;

    private final Logger logger;

    public SerializerQEF(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void serialize(VoxelArray array, OutputStream stream) throws IOException {
        Writer streamWriter = new OutputStreamWriter(stream);
        BufferedWriter writer = new BufferedWriter(streamWriter);

        logger.info("serializing voxel array ("+array.size()+" voxels) as QEF ...");

        writeMeta(writer);
        writeDimensions(array, writer);

        rewriteArray(array);

        writeColors(writer);
        writeVoxels(writer);

        writer.close();
    }

    private void writeMeta(BufferedWriter writer) throws IOException {
        writer.write("Qubicle Exchange Format"); writer.newLine();
        writer.write("Version 0.2"); writer.newLine();
        writer.write("www.minddesk.com"); writer.newLine();
    }

    private void writeDimensions(VoxelArray array, BufferedWriter writer) throws IOException {
        String dimensions = array.getSizeX()+" "+array.getSizeY()+" "+array.getSizeZ();
        logger.info("dimensions = "+dimensions);
        writer.write(dimensions); writer.newLine();
    }

    private void writeColors(BufferedWriter writer) throws IOException {
        logger.info("writing "+colors.length+" colors ...");
        writer.write(String.valueOf(colors.length));
        writer.newLine();

        for (Color color : colors) {
            writer.write(color.getRed()   / 255D+" ");
            writer.write(color.getGreen() / 255D+" ");
            writer.write(String.valueOf(color.getBlue() / 255D));
            writer.newLine();
        }
    }

    private void writeVoxels(BufferedWriter writer) throws IOException {
        logger.info("writing "+compressedArray.size()+" voxels to file ...");
        for (Voxel voxel : compressedArray) {
            final int color = (voxel.getRGB() & 0xFFFFFF) -1;
            writer.write(voxel.getX()+" "+voxel.getY()+" "+voxel.getZ()+" "+color);
            writer.newLine();
        }
    }

    private void rewriteArray(VoxelArray array) {
        Map<Color, Integer> colors = new HashMap<>();
        int colorIndex = 0;
        compressedArray = new VoxelArray(array.getSizeX(), array.getSizeY(), array.getSizeZ());

        for (Voxel v : array) {
            Color color = v.getColor();
            if (colors.containsKey(color)) {
                compressedArray.setRGB(v.getX(), v.getY(), v.getZ(), 0xFF_000000 | colors.get(color));
            }
            else {
                colors.put(color, ++colorIndex);
                compressedArray.setRGB(v.getX(), v.getY(), v.getZ(), 0xFF_000000 | colorIndex);
            }
        }

        this.colors = new Color[colors.size()];
        for (Map.Entry<Color, Integer> entry : colors.entrySet())
            this.colors[entry.getValue() - 1] = entry.getKey();
    }

}
