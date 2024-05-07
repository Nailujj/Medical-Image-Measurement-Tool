package ch.fhnw.prp.amrs.data;

import ch.fhnw.prp.amrs.logic.ImageData;
import ch.fhnw.prp.amrs.logic.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/*
Reader for vtk-files with the following format:
# vtk DataFile Version 2.0
grid
ASCII
DATASET STRUCTURED_POINTS
DIMENSIONS dimX dimY dimZ
ORIGIN 0.0 0.0 0.0
SPACING 1.0 1.0 1.0
POINT_DATA dimX*dimY*dimZ
SCALARS scalars float
LOOKUP_TABLE default
 */

public class VtkReader implements DataReader {
    private int dimX;
    private int dimY;
    private int dimZ;
    private int sliceNumber;
    private float minScalar;
    private float maxScalar;
    private final File dataFile;
    private BufferedImage bufferedImage;
    private Scanner scanner;
    private String view;
    private float[][][] scalars;

    public String getDataFile() {
        return String.valueOf(dataFile);
    }

    public VtkReader(String fileName) {
        dataFile = new File(fileName);
        try {
            scanner = new Scanner(dataFile);
            readHeader();
        } catch (IOException e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    public ImageData read() {
        try {
            //readScalars(); Use this if you don't want a background thread
            bufferedImage = createBufferedImage();
        } catch (
                IOException e) {
            System.out.println("Error" + e.getMessage());
        }

        //resolution and resolutionUnit is set specifically for the test-file
        return new ImageData("slice " + sliceNumber + ", " + view + " view",
                "0.58", "mm", dataFile.getName(), new ImageLoader(bufferedImage));
    }

    //set the dimensions, skip all other lines from the header
    private void readHeader() throws IOException {
        String line;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains("DIMENSIONS")) {
                String[] dimensions = line.split(" ");
                dimX = Integer.parseInt(dimensions[1]);
                dimY = Integer.parseInt(dimensions[2]);
                dimZ = Integer.parseInt(dimensions[3]);

            } else if (line.contains("LOOKUP_TABLE default")) {
                break;
            }
        }
    }

    //read the values in the LOOKUP_TABLE of the vtk file
    //create a 3-dimensional array and set the values
    public void readScalars() throws IOException {
        scalars = new float[dimZ][dimY][dimX];

        while (scanner.hasNextLine()) {
            for (int z = 0; z < dimZ; z++) {
                for (int y = 0; y < dimY; y++) {
                    String[] values = scanner.nextLine().trim().split(" ");
                    for (int x = 0; x < dimX; x++) {
                        float pixelValue = Float.parseFloat(values[x]);
                        scalars[z][y][x] = pixelValue;

                        if (pixelValue < minScalar) {
                            minScalar = pixelValue;
                        } else if (pixelValue > maxScalar) {
                            maxScalar = pixelValue;
                        }
                    }
                }
            }
        }
        scanner.close();
    }

    public BufferedImage createBufferedImage() throws IOException {
        float[][] viewScalars = getViewScalars();

        BufferedImage bufferedImage;
        if (view.equals("axial")) {
            bufferedImage = new BufferedImage(viewScalars[0].length,
                    viewScalars[1].length, BufferedImage.TYPE_INT_RGB);
        } else {
            bufferedImage = new BufferedImage(
                    viewScalars[1].length, viewScalars[0].length, BufferedImage.TYPE_INT_RGB);
        }

        for (int i = 0; i < viewScalars[1].length; i++) {
            for (int j = 0; j < viewScalars[0].length; j++) {
                float pixelValue = viewScalars[i][j];
                //value between 0 and 1
                float normalizedValue = ((maxScalar - pixelValue) / (maxScalar - minScalar));

                int rgb = getRgb(normalizedValue);

                if (view.equals("axial")) {
                    bufferedImage.setRGB(j, i, rgb);
                } else {
                    bufferedImage.setRGB(i, j, rgb);
                }
            }
        }
        return bufferedImage;
    }

    //map the normalizedValue to a color of a specific color scale
    private int getRgb(float normalizedValue) {
        //define the color scale
        int[] colorScale = {0x9f1731, 0xd54b3a, 0xf0ccb9, 0x82a6fa, 0x42509f};
        int numColors = colorScale.length;
        int numIntervals = numColors - 1;
        float intervalWidth = 1f / numIntervals;

        //result is the index of the first colorValue for the current intervall
        int index1 = (int) (normalizedValue * numIntervals);
        //result is the index of the second colorValue for the current intervall
        // (index2 = index1+1 but make sure that it isn't out of range)
        int index2 = Math.min(index1 + 1, numIntervals);

        int color1 = colorScale[index1];
        int color2 = colorScale[index2];

        //result = value between 0 and 1, shows the position of the normalizedValue in the current interval
        float factor = (normalizedValue - index1 * intervalWidth) / intervalWidth;

        //interpolate between two colors
        //factor = 0 -> colorValue = color1
        //factor = 1 -> colorValue = color2
        //colorValue = color1 + factor*differenceC1C2
        int red = (int) (red(color1) + factor * (red(color2) - red(color1)));
        int green = (int) (green(color1) + factor * (green(color2) - green(color1)));
        int blue = (int) (blue(color1) + factor * (blue(color2) - blue(color1)));

        return new Color(red, green, blue).getRGB();
    }


    //extract the values from a rgb-color
    //>>: right shift
    //&: bitwise and operator
    private int red(int color) {
        return color >> 16 & 0xFF;
    }

    private int green(int color) {
        return color >> 8 & 0xFF;
    }

    private int blue(int color) {
        return color & 0xFF;
    }

    //public for testing
    public float[][] getViewScalars() {
        return switch (view) {
            case "sagittal" -> getSagittalScalars(sliceNumber);
            case "coronal" -> getCoronalScalars(sliceNumber);
            default -> getAxialScalars(sliceNumber);
        };
    }

    private float[][] getAxialScalars(int imageSlice) {
        float[][] axialScalars = new float[dimY][dimX];
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                axialScalars[y][x] = scalars[imageSlice - 1][y][x];
            }
        }
        return axialScalars;
    }

    private float[][] getSagittalScalars(int imageSlice) {
        float[][] sagittalScalars = new float[dimY][dimZ];
        for (int y = 0; y < dimY; y++) {
            for (int z = 0; z < dimZ; z++) {
                sagittalScalars[y][z] = scalars[z][y][imageSlice - 1];
            }
        }
        return sagittalScalars;
    }

    private float[][] getCoronalScalars(int imageSlice) {
        float[][] coronalScalars = new float[dimX][dimZ];
        for (int x = 0; x < dimX; x++) {
            for (int z = 0; z < dimZ; z++) {
                coronalScalars[x][z] = scalars[dimZ - z - 1][imageSlice - 1][x];
            }
        }
        return coronalScalars;
    }

    public void setSliceNumber(int sliceNumber) {
        this.sliceNumber = sliceNumber;
    }

    public int getSliceNumber() {
        return sliceNumber;
    }

    public void setView(String view) {
        this.view = view;
    }

    //for testing
    public float[][][] getScalars() {
        return scalars;
    }

    public Map<String, Integer> getDimensions() {
        Map<String, Integer> dimensionMap = new HashMap<>();
        dimensionMap.put("dimX", dimX);
        dimensionMap.put("dimY", dimY);
        dimensionMap.put("dimZ", dimZ);
        return dimensionMap;
    }

    public Map<String, Integer> getRangePerView() {
        Map<String, Integer> rangeMap = new HashMap<>();
        rangeMap.put("axial", dimZ);
        rangeMap.put("sagittal", dimX);
        rangeMap.put("coronal", dimY);
        return rangeMap;
    }

    public String getView() {
        return view;
    }
}