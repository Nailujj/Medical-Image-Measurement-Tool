package ch.fhnw.prp.amrs.presentation;

import ch.fhnw.prp.amrs.data.DataReader;
import ch.fhnw.prp.amrs.data.JsonReader;
import ch.fhnw.prp.amrs.data.TxtReader;
import ch.fhnw.prp.amrs.data.VtkReader;
import ch.fhnw.prp.amrs.logic.CallbackFunction;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class ControlPane extends VBox {

    StateModel stateModel;
    Stage secondaryStage = new Stage();

    CheckBox displayIntensityProfile;

    public ControlPane(ImagePane imagePane, StateModel stateModel, CallbackFunction imageSelectedCallback) {
        this.stateModel = stateModel;

        //Control Pane design
        setSpacing(20);
        setPadding(new Insets(10));

        //Titel: Controls
        Label controlTitle = new Label("Controls");
        controlTitle.setFont(Font.font("", 24));
        controlTitle.getStyleClass().add("text");

        //Button: Select Picture
        Button buttonSelectImage = new Button("Select Picture");

        buttonSelectImage.setOnAction(event -> {
            imageSelected(imagePane, imageSelectedCallback);
        });

        //CheckBox: Display Distance
        CheckBox displayDistance = new CheckBox("Display Distance");
        displayDistance.getStyleClass().add("text");

        displayDistance.setOnAction(event -> {
            stateModel.setDisplayDistance(displayDistance.isSelected());
        });

        //ComboBox: Display Distances
        List<String> units = List.of("km", "m", "cm", "mm", "µm", "miles", "yard", "feet", "inch", "pixels");
        ComboBox<String> distancesComboBox = new ComboBox<>(FXCollections.observableArrayList(units));
        distancesComboBox.setValue("pixels");

        distancesComboBox.setOnAction(event -> {
            String unitValue = distancesComboBox.getValue();
            stateModel.setDistanceUnit(unitValue);
        });

        //CheckBox: Display Angles
        CheckBox displayAngles = new CheckBox("Display Angles");
        displayAngles.getStyleClass().add("text");

        displayAngles.setOnAction(event -> stateModel.setDisplayAngles(displayAngles.isSelected()));

        //ComboBox: Display Angles
        List<String> angles = List.of("degree", "radian");
        ComboBox<String> anglesComboBox = new ComboBox<>(FXCollections.observableArrayList(angles));
        anglesComboBox.setValue("degree");

        anglesComboBox.setOnAction(event -> {
            String unitValue = anglesComboBox.getValue();
            stateModel.setAngleUnit(unitValue);
        });

        //Slider: Stroke Width
        String strokeWidthText = "Stroke width: ";
        Label strokeWidthLabel = new Label(strokeWidthText + stateModel.getStrokeWidth());
        strokeWidthLabel.getStyleClass().add("text");
        Slider strokeWidthSlider = new Slider(1, 10, stateModel.getStrokeWidth());
        strokeWidthSlider.maxWidth(30);

        strokeWidthSlider.setOnMouseDragged(event -> {
            int value = (int) Math.round(strokeWidthSlider.getValue());
            strokeWidthLabel.setText(strokeWidthText + value);
            stateModel.setStrokeWidth(value);
            stateModel.setDistanceFontSize(13 + value);
            stateModel.setAngleFontSize(11 + value);
        });

        //Button: Color Picker
        ColorPicker lineColorPicker = new ColorPicker(stateModel.getLineColor());

        lineColorPicker.setOnAction(event -> {
            stateModel.setLineColor(lineColorPicker.getValue());
        });

        //HBox: Stroke Width and Line color
        HBox linePropertiesBox = new HBox();
        linePropertiesBox.setAlignment(Pos.CENTER_LEFT);
        linePropertiesBox.setSpacing(20);
        VBox strokeWidthBox = new VBox();
        strokeWidthBox.setSpacing(10);
        strokeWidthBox.getChildren().addAll(strokeWidthLabel, strokeWidthSlider);
        linePropertiesBox.getChildren().addAll(strokeWidthBox, lineColorPicker);

        HBox distanceHBox = new HBox();
        distanceHBox.setAlignment(Pos.CENTER_LEFT);
        distanceHBox.setSpacing(20);
        distanceHBox.getChildren().addAll(displayDistance, distancesComboBox);

        HBox angleHBox = new HBox();
        angleHBox.setAlignment(Pos.CENTER_LEFT);
        angleHBox.setSpacing(20);
        angleHBox.getChildren().addAll(displayAngles, anglesComboBox);

        VBox displayOptionVBox = new VBox();
        displayOptionVBox.setSpacing(10);
        displayOptionVBox.getChildren().addAll(distanceHBox, angleHBox);

        //CheckBox: Intensity Profile
        displayIntensityProfile = new CheckBox("Display Intensity Profile");
        displayIntensityProfile.getStyleClass().add("text");

        displayIntensityProfile.setOnAction(event -> {
            try {
                if (displayIntensityProfile.isSelected() && stateModel.getImageData() != null) {
                    openHeightProfilePane(stateModel);
                } else if (!displayIntensityProfile.isSelected() && stateModel.getImageData() != null) {
                    closeHeightProfilePane();
                } else {
                    displayIntensityProfile.setSelected(false);
                }
            } catch (Exception e) {
                displayIntensityProfile.setSelected(false);
            }
        });

        //Button: Clear
        Button clearDrawingButton = new Button("clear");
        clearDrawingButton.setOnAction(event -> {
            stateModel.clearMeasurements();
        });

        //CheckBox: Dark Mode
        CheckBox darkMode = new CheckBox("Dark Mode");
        darkMode.setSelected(stateModel.isDarkMode());
        darkMode.getStyleClass().add("text");

        darkMode.setOnMouseClicked((mouseEvent) -> {
            if (darkMode.isSelected() != stateModel.isDarkMode()) {
                stateModel.setDarkMode(darkMode.isSelected());
            }
        });

        getChildren().addAll(controlTitle, buttonSelectImage, displayOptionVBox, linePropertiesBox, displayIntensityProfile, clearDrawingButton, darkMode);
    }

    private void imageSelected(ImagePane imagePane, CallbackFunction imageSelectedCallback) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        FileChooser.ExtensionFilter extFilterTXT = new FileChooser.ExtensionFilter("Text, JSON and VTK files (*.txt, *.json, *.vtk)", "*.txt", "*.json", "*.vtk");
        fileChooser.getExtensionFilters().add(extFilterTXT);
        File dataFile = fileChooser.showOpenDialog(null);

        if (dataFile != null) {
            String fileName = dataFile.getPath();

            Optional<DataReader> dataReader = Optional.empty();
            if (fileName.endsWith(".txt")) {
                dataReader = Optional.of(new TxtReader(fileName));
            } else if (fileName.endsWith(".json")) {
                dataReader = Optional.of(new JsonReader(fileName));
            } else if (fileName.endsWith(".vtk")) {
                VtkReader vtkReader = new VtkReader(fileName);
                stateModel.setVtkReader(vtkReader);


                /*This thread starts the reading of the scalars in a background thread.
                 The image is shown in near real time due to the background thread.
                 */
                new Thread(() -> {
                    try {
                        vtkReader.readScalars();
                    } catch (Exception e) {
                        System.out.println("Error" + e.getMessage());
                    }
                }).start();

                VtkDialog vtkDialog = new VtkDialog(vtkReader.getRangePerView());
                Optional<Map<String, String>> resultMap = vtkDialog.showDialog(stateModel);

                if (resultMap.isPresent()) {
                    vtkReader.setSliceNumber(Integer.parseInt(resultMap.get().get("sliceNumber")));
                    vtkReader.setView(resultMap.get().get("view"));

                    dataReader = Optional.of(vtkReader);

                } else {
                    dataReader = Optional.empty();
                }
            }

            if (dataReader.isPresent()) {
                try {
                    stateModel.setImageData(dataReader.get().read());
                    imagePane.showImage();

                    // Callback to add image- and metadatapane
                    imageSelectedCallback.apply();
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }


    private void openHeightProfilePane(StateModel stateModel) {
        IntensityProfilePane intensityProfilePane = new IntensityProfilePane(stateModel);
        Scene scene = new Scene(intensityProfilePane, 400, 300);
        secondaryStage.setScene(scene);
        secondaryStage.setTitle("Intensity Profile");
        secondaryStage.show();
        IntensityProfilePane.setCalculateAndDisplay(true);
        stateModel.update();
        secondaryStage.setOnCloseRequest(event -> {
            event.consume();
            closeHeightProfilePane();
            displayIntensityProfile.setSelected(false);
        });

    }

    private void closeHeightProfilePane() {
        secondaryStage.close();
        IntensityProfilePane.setCalculateAndDisplay(false);
    }
}