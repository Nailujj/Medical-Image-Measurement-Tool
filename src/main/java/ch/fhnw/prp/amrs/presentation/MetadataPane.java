package ch.fhnw.prp.amrs.presentation;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Objects;


public class MetadataPane extends VBox implements StateObserver {

    private final StateModel stateModel;
    private final TextArea textArea;

    public MetadataPane(StateModel stateModel) {
        this.stateModel = stateModel;
        stateModel.addObserver(this);

        setPadding(new Insets(10));
        setSpacing(20);

        //Title: Metadata
        Label metadataTitle = new Label("Metadata");
        metadataTitle.setFont(Font.font("", 24));
        metadataTitle.getStyleClass().add("text");
        getChildren().add(metadataTitle);

        //TextArea: ShowMetadata
        textArea = new TextArea();
        textArea.setMaxWidth(200);
        textArea.setWrapText(true);
        getChildren().add(textArea);

        BackgroundFill backgroundFill = new BackgroundFill(Color.gray(0), null, null);
        Background background = new Background(backgroundFill);
        textArea.setBackground(background);
        textArea.setEditable(false);
    }

    public void showText() {
        if (stateModel.getImageData() != null) {
            textArea.setText(
                    "description: " + Objects.requireNonNullElse(stateModel.getImageData().getDescription(), "Not available") + "\n" +
                            "image-file: " + Objects.requireNonNullElse(stateModel.getImageData().getImageFile(), "Not available") + "\n" +
                            "resolution: " + Objects.requireNonNullElse(stateModel.getImageData().getResolution(), "Not available") +
                            Objects.requireNonNullElse(stateModel.getImageData().getResolutionUnit(), "Not available") + "\n");
        }
    }

    @Override
    public void stateChanged() {
        showText();
    }
}
