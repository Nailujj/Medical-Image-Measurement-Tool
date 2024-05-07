package ch.fhnw.prp.amrs;

import ch.fhnw.prp.amrs.data.DataReader;
import ch.fhnw.prp.amrs.data.VtkReader;
import ch.fhnw.prp.amrs.presentation.StateModel;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CLIImageDataHandler {
    StateModel stateModel = new StateModel();
    Set<String> output = new HashSet<>();

    public void showImageData(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getPath();

                    Optional<DataReader> dataReader = DataReader.getReader(fileName);

                    if (dataReader.isPresent()) {
                        try {
                            if (fileName.endsWith(".vtk")){
                                String vtkFile = new VtkReader(fileName).getDataFile().replace(folderPath + "\\","");
                                output.add("\nName: " + vtkFile);
                            }
                            else {
                                stateModel.setImageData(dataReader.get().read());
                                StringBuilder tabs = new StringBuilder();
                                int length = (50-stateModel.getImageData().getImageFile().length());
                                for (int i = 0; i < length; i++) {
                                    tabs.append(" ");
                                }
                                output.add("Name: " + stateModel.getImageData().getImageFile() + tabs+
                                        "Resolution: " + stateModel.getImageData().getResolution() +
                                        stateModel.getImageData().getResolutionUnit());
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error" + e.getMessage());
                        }
                    }

                }
            }
        }
        for (String line : output) {
            System.out.println(line);
        }
        System.exit(0);
    }
}



