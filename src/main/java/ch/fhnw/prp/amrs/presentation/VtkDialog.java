package ch.fhnw.prp.amrs.presentation;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class VtkDialog {
    private final Map<String, Integer> rangePerViewMap;
    private int range;

    public VtkDialog(Map<String, Integer> rangePerViewMap) {
        this.rangePerViewMap = rangePerViewMap;
        range = rangePerViewMap.get("axial");
    }

    public Optional<Map<String, String>> showDialog(StateModel stateModel) {

        Dialog<Map<String, String>> vtkDialog = new Dialog<>();
        vtkDialog.setTitle("VTK-File Reader");
        vtkDialog.setHeaderText("Please choose a slice");

        ButtonType applyButtonTyp = new ButtonType("Apply", ButtonBar.ButtonData.APPLY);
        ButtonType cancelButtonTyp = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        vtkDialog.getDialogPane().getButtonTypes().addAll(applyButtonTyp, cancelButtonTyp);
        vtkDialog.getDialogPane().getStyleClass().add("dialog");
        vtkDialog.getDialogPane().getStylesheets().add(stateModel.getStyleSheetOfColorMode());

        Label sliceNumberLabel = new Label("Slice-Number");
        TextField sliceNumberTextField = new TextField();
        Label rangeLabel = new Label("(range 1 - " + range + ")");

        Label viewOptoinsLabel = new Label("View");
        List<String> viewOptions = List.of("axial", "sagittal", "coronal");
        ComboBox<String> viewOptionsCombobox = new ComboBox<>(FXCollections.observableArrayList(viewOptions));
        viewOptionsCombobox.getSelectionModel().selectFirst();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        gridPane.add(sliceNumberLabel, 0, 0);
        gridPane.add(sliceNumberTextField, 1, 0);
        gridPane.add(rangeLabel, 2, 0);
        gridPane.add(viewOptoinsLabel, 0, 1);
        gridPane.add(viewOptionsCombobox, 1, 1);

        vtkDialog.getDialogPane().setContent(gridPane);

        AtomicReference<Button> cancelButton = new AtomicReference<>((Button) vtkDialog.getDialogPane().lookupButton(cancelButtonTyp));
        AtomicReference<Button> applyButton = new AtomicReference<>((Button) vtkDialog.getDialogPane().lookupButton(applyButtonTyp));
        applyButton.get().setDisable(true);

        //look if the input is valid
        sliceNumberTextField.textProperty().addListener(observable -> {
            checkApplyButton(sliceNumberTextField, applyButton, cancelButton);
        });

        //change the range according to the selected view
        viewOptionsCombobox.setOnAction(event -> {
            range = rangePerViewMap.get(String.valueOf(viewOptionsCombobox.getValue()));
            rangeLabel.setText("(range 1 - " + range + ")");
            checkApplyButton(sliceNumberTextField, applyButton, cancelButton);
        });

        AtomicReference<String> tempImageSliceNumber = new AtomicReference<>("");

        sliceNumberTextField.setOnKeyPressed(event -> {
            addDigit(tempImageSliceNumber, event, sliceNumberTextField);
            if (event.getCode() == KeyCode.BACK_SPACE) {
                deleteLastDigit(tempImageSliceNumber, sliceNumberTextField);
            }
        });

        vtkDialog.getDialogPane().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!applyButton.get().isDisabled()) {
                    applyButton.get().fire();
                }
            } else if (event.getCode() == KeyCode.A) {
                viewOptionsCombobox.setValue("axial");
            } else if (event.getCode() == KeyCode.S) {
                viewOptionsCombobox.setValue("sagittal");
            } else if (event.getCode() == KeyCode.C) {
                viewOptionsCombobox.setValue("coronal");
            } else if (event.getCode().isDigitKey()) {
                addDigit(tempImageSliceNumber, event, sliceNumberTextField);
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                deleteLastDigit(tempImageSliceNumber, sliceNumberTextField);
            }
        });

        //define the result if apply is pressed
        vtkDialog.setResultConverter(button -> {
            if (button == applyButtonTyp) {
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("sliceNumber", sliceNumberTextField.getText());
                resultMap.put("view", String.valueOf(viewOptionsCombobox.getValue()));
                return resultMap;
            }
            return null;
        });

        // return the result
        return vtkDialog.showAndWait();
    }

    private void checkApplyButton(TextField sliceNumberTextField, AtomicReference<Button> applyButton,
                                  AtomicReference<Button> cancelButton) {
        try {
            int value = Integer.parseInt(sliceNumberTextField.getText().trim());
            if (!(value <= 0 || value > range)) {
                applyButton.get().setDisable(false);
                applyButton.get().requestFocus();
            } else {
                cancelButton.get().requestFocus();
                applyButton.get().setDisable(true);
            }
        } catch (Exception e) {
            applyButton.get().setDisable(true);
        }
    }

    private void addDigit(AtomicReference<String> tempImageSliceNumber, KeyEvent event, TextField sliceNumberTextField) {
        tempImageSliceNumber.updateAndGet(v -> v + event.getText());
        sliceNumberTextField.setText(tempImageSliceNumber.get());
    }

    private void deleteLastDigit(AtomicReference<String> tempImageSliceNumber, TextField sliceNumberTextField) {
        if (!sliceNumberTextField.getText().isEmpty()) {
            tempImageSliceNumber.updateAndGet(v -> v.substring(0, v.length() - 1));
            sliceNumberTextField.setText(tempImageSliceNumber.get());
        }
    }
}
