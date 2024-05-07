package ch.fhnw.prp.amrs.data;

import ch.fhnw.prp.amrs.logic.ImageData;

import ch.fhnw.prp.amrs.logic.ImageLoader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class JsonReader implements DataReader {


    /**
     * @param fileName Path to the JSON file
     * @return ImageData object
     * @throws IllegalArgumentException if the file is not a JSON file
     * @throws IOException if there is an error reading or parsing the JSON file
     * the ImageData object contains the following attributs:
     * - description
     * - image_resolution
     * - image_resolution_unit
     * - image_file
     */

    private final File dataFile;

    public JsonReader(String fileName) {
        dataFile = new File(fileName);
    }

    public ImageData read() {
        if (!dataFile.toString().endsWith(".json")) {
            throw new IllegalArgumentException("File must be a .json file");
        }

        String description = "";
        String resolution = "";
        String resolutionUnit = "";
        String imageFile = "";

        try {
            String jsonString = new String(Files.readAllBytes(Path.of(dataFile.toString()))).trim();
            JSONObject jsonObject = new JSONObject(jsonString);

            // Extract values for specific keys
            //.optString() can throw a JSONException. This is caught in the catch block.
            description = jsonObject.optString("description", "");
            resolution = jsonObject.optString("image_resolution", "");
            resolutionUnit = jsonObject.optString("image_resolution_unit", "");
            imageFile = jsonObject.optString("image_file", "");



        } catch (IOException | JSONException e) {
            System.err.println("Error reading or parsing JSON file: " + e.getMessage());
        }

        String absoluteFilePath = dataFile.getParentFile().toURI().resolve(imageFile).getPath();

        return new ImageData(description, resolution, resolutionUnit, imageFile, new ImageLoader(absoluteFilePath));
    }
}

