package net.grian.vv.io;

import net.grian.vv.core.VoxelArray;

import java.awt.*;
import java.io.*;
import java.util.logging.Logger;

/**
 * Deserializer designed for parsing Qubicle Exchange Format files (version 0.2). These files are text files for storing
 * voxels which begin with a constant header, size and color definition and end with a series of voxels.
 */
public class DeserializerQEF implements Parser<VoxelArray> {

    private final Logger logger;

    private VoxelArray voxels;
    private int[] colors;

    public DeserializerQEF(Logger logger) {
        this.logger = logger;
    }

    public DeserializerQEF() {
        this(Logger.getGlobal());
    }

    @Override
    public VoxelArray deserialize(Reader reader) throws IOException {
        BufferedReader buffReader = new BufferedReader(reader);
        logger.info("parsing qef ...");

        String line;

        int num = 0;
        while ((line = buffReader.readLine()) != null) parseLine(++num, line);
        logger.info("completed parsing qef ("+voxels.size()+"/"+voxels.getVolume()+" voxels)");

        buffReader.close();
        return voxels;
    }

    private void parseLine(int num, String line) throws IOException {
        if (num == 1 || num == 3) return; //header lines

        if (num == 2) {
            if (line.equals("Version 0.2"))
                logger.info("parsing file of version '"+line+"'");
            else
                throw new FileVersionException("version '"+line+"' not supported");
        }

        else if (num == 4) {
            parseDimensions(line);
            logger.info("parsing qef of dimensions "+voxels.getSizeX()+"x"+voxels.getSizeY()+"x"+voxels.getSizeZ());
        }

        else if (num == 5) {
            parseColorCount(line);
            logger.info("parsing "+colors.length+" colors ...");
        }

        else if (num < 6 + colors.length) {
            parseColorDefinition(num, line);
        }

        else {
            parseVoxelDefinition(line);
        }
    }

    private void parseDimensions(String line) throws FileSyntaxException {
        int[] dims = lineToInts(line);
        if (dims.length < 3)
            throw new FileSyntaxException("less than 3 dimensions");
        voxels = new VoxelArray(dims[0], dims[1], dims[2]);
    }

    private void parseColorCount(String line) throws FileSyntaxException {
        try {
            int colorCount = Integer.parseInt(line);
            colors = new int[colorCount];
        } catch (NumberFormatException ex) {
            throw new FileSyntaxException(ex);
        }
    }

    private void parseColorDefinition(int num, String line) throws FileSyntaxException {
        float[] rgb = lineToFloats(line);
        if (rgb.length < 3) throw new FileSyntaxException();
        colors[num - 6] = new Color(rgb[0], rgb[1], rgb[2]).getRGB();
    }

    private void parseVoxelDefinition(String line) throws FileSyntaxException {
        int[] ints = lineToInts(line);
        voxels.setRGB(ints[0], ints[1], ints[2], colors[ints[3]]);
    }

    private static int[] lineToInts(String line) throws FileSyntaxException {
        String[] splits = line.split(" ");
        int[] result = new int[splits.length];
        for (int i = 0; i<splits.length; i++) {
            try {
                result[i] = Integer.parseInt(splits[i]);
            } catch (NumberFormatException ex) {
                throw new FileSyntaxException(ex);
            }
        }
        return result;
    }

    private static float[] lineToFloats(String line) throws FileSyntaxException {
        String[] splits = line.split(" ");
        float[] result = new float[splits.length];
        for (int i = 0; i<splits.length; i++) {
            try {
                result[i] = Float.parseFloat(splits[i]);
            } catch (NumberFormatException ex) {
                throw new FileSyntaxException(ex);
            }
        }
        return result;
    }

}
