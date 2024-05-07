package ch.fhnw.prp.amrs.data;

import ch.fhnw.prp.amrs.logic.ImageData;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.*;

public class VtkReaderTest {

    @org.junit.jupiter.api.Test
    public void read() throws IOException {
        File f = new File("test.vtk");
        PrintWriter writer = getPrintWriter(f);
        writer.close();

        VtkReader vtkReader = new VtkReader(f.getName());
        vtkReader.readScalars();
        vtkReader.setView("axial");
        vtkReader.setSliceNumber(2);
        ImageData imageData = vtkReader.read();

        //test for setting dimension
        Map<String, Integer> expectedDimensionMap = new HashMap<>();
        expectedDimensionMap.put("dimX", 2);
        expectedDimensionMap.put("dimY", 3);
        expectedDimensionMap.put("dimZ", 4);
        Map<String, Integer> actualDimensionMap = vtkReader.getDimensions();
        Assertions.assertEquals(expectedDimensionMap, actualDimensionMap, "Dimensions aren't set correctly");

        //test for reading LOOKUP_TABLE
        float[][][] expectedScalars = new float[][][]{
                {{0.0f, 0.0f}, {0.0f, 0.0f}, {0.0f, 0.0f}},
                {{0.0f, 5.0f}, {15.0f, 20.0f}, {25.0f, 20.0f}},
                {{0.0f, 10.0f}, {30.0f, 40.0f}, {50.0f, 40.0f}},
                {{0.0f, 10.0f}, {30.0f, 40.0f}, {50.0f, 40.0f}}
        };
        float[][][] actualScalars = vtkReader.getScalars();
        Assertions.assertEquals(Arrays.deepToString(expectedScalars), Arrays.deepToString(actualScalars),
                "LOOKUP_Table isn't read correctly");


        //test for setting ImageData
        Assertions.assertEquals("slice 2, axial view", imageData.getDescription(),
               "wrong description");
        Assertions.assertEquals("test.vtk", imageData.getImageFile(), "wrong file name");



        //test for getting values for different views
        float[][] expectedAxialScalars = new float[][]
                {{0.0f, 5.0f}, {15.0f, 20.0f}, {25.0f, 20.0f}};
        float[][] actualAxialScalars = vtkReader.getViewScalars();
        Assertions.assertEquals(Arrays.deepToString(expectedAxialScalars), Arrays.deepToString(actualAxialScalars),
                "wrong values for axial view");

        vtkReader.setView("coronal");
        float[][] expectedCoronalScalars = new float[][]
                {{30.0f, 30.0f, 15.0f, 0.0f}, {40.0f, 40.0f, 20.0f, 0.0f}};
        float[][] actualCoronalScalars = vtkReader.getViewScalars();
        Assertions.assertEquals(Arrays.deepToString(expectedCoronalScalars), Arrays.deepToString(actualCoronalScalars),
                "wrong values for coronal view");

        vtkReader.setView("sagittal");
        float[][] expectedSagittalScalars = new float[][]
                {{0.0f, 5.0f, 10.0f, 10.0f},{0.0f, 20.0f, 40.0f, 40.0f}, {0.0f, 20.0f, 40.0f, 40.0f}};

        float[][] actualSagittalScalars = vtkReader.getViewScalars();
        Assertions.assertEquals(Arrays.deepToString(expectedSagittalScalars), Arrays.deepToString(actualSagittalScalars),
                "wrong values for sagittal view");

        Files.delete(Path.of("test.vtk"));
    }

    private static PrintWriter getPrintWriter(File f) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(f);
        writer.write("""
                vtk DataFile Version 2.0
                grid
                ASCII
                DATASET STRUCTURED_POINTS
                DIMENSIONS 2 3 4
                ORIGIN 0.0 0.0 0.0
                SPACING 1.0 1.0 1.0
                POINT_DATA 24
                SCALARS scalars float
                LOOKUP_TABLE default
                0.0 0.0
                0.0 0.0
                0.0 0.0
                0.0 5.0
                15.0 20.0
                25.0 20.0
                0.0 10.0
                30.0 40.0
                50.0 40.0
                0.0 10.0
                30.0 40.0
                50.0 40.0
                """);
        return writer;
    }
}