package ch.fhnw.prp.amrs.data;

import ch.fhnw.prp.amrs.logic.ImageData;

import java.io.File;
import java.util.Optional;

public interface DataReader {

    ImageData read();

    static Optional<DataReader> getReader(String fileName) {
        if(fileName.endsWith(".txt")) {
            return Optional.of(new TxtReader(fileName));
        } else if(fileName.endsWith(".json")) {
            return Optional.of(new JsonReader(fileName));
        } else if (fileName.endsWith(".vtk")) {
            return Optional.of(new VtkReader(fileName));
        }
        return Optional.empty();
    }

}
