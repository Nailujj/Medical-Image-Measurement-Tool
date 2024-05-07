package ch.fhnw.prp.amrs.data;

import ch.fhnw.prp.amrs.logic.ImageData;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class JsonReaderTest {

    @org.junit.jupiter.api.Test
    public void read() {
        String jsonPath = System.getProperty("user.dir");
        jsonPath += "/data/test-image-01.json";

        System.out.println("Testing JsonReader:");
        JsonReader jsonReader = new JsonReader(jsonPath);
        ImageData imageData = jsonReader.read();

        assertEquals("Expected description does not match", "Blutausstrich (Mensch)", imageData.getDescription());
        assertEquals("Expected image_resolution_unit does not match", "mm", imageData.getResolutionUnit());
        assertEquals("Expected image_resolution does not match", "0.0002", imageData.getResolution());
        assertEquals("Expected image-file does not match", "image01.jpg", imageData.getImageFile());
    }
}