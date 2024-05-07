package ch.fhnw.prp.amrs.presentation;

import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Objects;

public class MainPane extends BorderPane implements StateObserver {

    StateModel stateModel = new StateModel();

    public MainPane(Stage stage) {

        stateModel.addObserver(this);

        ImagePane imagePane = new ImagePane(stateModel);
        MetadataPane metadataPane = new MetadataPane(stateModel);
        ControlPane controlPane = new ControlPane(imagePane, stateModel, () -> {
            if (this.getCenter() == null) {
                setCenter(imagePane);
                setRight(metadataPane);
            }
            stage.sizeToScene();
        });
        setLeft(controlPane);
    }

    public void setTheme() {
        if (getScene() != null) {
            String styleSheet = stateModel.getStyleSheetOfColorMode();
            if (getScene().getStylesheets().isEmpty() || !getScene().getStylesheets().get(0).endsWith(styleSheet)) {
                getScene().getStylesheets().clear();
                getScene().getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().
                        getResource(styleSheet)).toExternalForm());
            }
        }
    }

    @Override
    public void stateChanged() {
        setTheme();
    }
}