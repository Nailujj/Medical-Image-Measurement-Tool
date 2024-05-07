package ch.fhnw.prp.amrs;

import ch.fhnw.prp.amrs.presentation.MainPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        if (args.length != 0) {
            CLIImageDataHandler CLIImageDataHandler = new CLIImageDataHandler();
            CLIImageDataHandler.showImageData(args[0]);
        } else {
            launch(args);
        }
    }

    @Override
    public void start(Stage stage) {
        stage.show();
        stage.setTitle("Image Measurement Tool");

        MainPane rootPane = new MainPane(stage);
        rootPane.setCenterShape(true);

        Image icon = new Image("file:src/main/resources/logo.png");
        stage.getIcons().add(icon);

        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        rootPane.setTheme();
        stage.sizeToScene();
    }
}