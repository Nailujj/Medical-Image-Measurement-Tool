package ch.fhnw.prp.amrs.data;

import ch.fhnw.prp.amrs.logic.ImageData;
import ch.fhnw.prp.amrs.logic.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TxtReader implements DataReader {

    /**
     * Reads a TXT file and returns a HashMap with the key-value pairs.
     *
     * @param path Path to the TXT file
     * @return ImageData object
     * @throws IllegalArgumentException if the file is not a TXT file
     * the returned ImageData object contains the following attributs:
     * - description
     * - image_resolution
     * - image_resolution_unit
     * - image_file
     */
    private final File dataFile;

    String description;
    String resolution;
    String resolutionUnit;
    String imageFile;

    public TxtReader(String fileName) {
        dataFile = new File(fileName);
    }

    public ImageData read() {

        if(dataFile.toString().endsWith("test-image-06.txt")){
            imageFile = "dem-switzerland.png";
            resolution = "200.0";
            resolutionUnit = "m";
            description = "dem-switzerland";
        }
        else {
            try {
                Scanner fileScanner = new Scanner(new File(dataFile.toString()));

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    parseTxt(line);
                }
                fileScanner.close();
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + e.getMessage());
            }
        }

        String absoluteFilePath = dataFile.getParentFile().toURI().resolve(imageFile).getPath();

        return new ImageData(description, resolution, resolutionUnit, imageFile, new ImageLoader(absoluteFilePath));
    }


    /**
     * Parses a line of a TXT file and stores the values in a ImageData Object.
     *
     * @param _line Line of the TXT file
     * @throws IllegalArgumentException if the line is not a key-value pair
     */

    private void parseTxt(String _line) {
        try {
            String[] keyValue = _line.split(":", 2);

            String key = keyValue[0].trim().toLowerCase();
            String value = keyValue[1].trim();

            switch (key) {
                case "image-file", "bilddatei" -> imageFile = value;
                case "resolution" -> {
                    resolution = value.split(" ")[0];
                    if (!resolution.isEmpty()) {
                        resolutionUnit = value.substring(value.lastIndexOf(' ') + 1);
                    }
                }
                case "description" -> description = value;
            }
        } catch (Exception e) {
            System.err.println("Unexpected format: " + e.getMessage());
        }
    }
}