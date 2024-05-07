package ch.fhnw.prp.amrs.logic;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {
    private Image image;
    private BufferedImage bufferedImage;

    public ImageLoader(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        image = SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public ImageLoader(String absoluteFilePath) {
        try {
            image = new Image("file:" + absoluteFilePath);
        } catch (RuntimeException rex) {
            System.out.println("Internal graphics not initialized yet");
        }

        try {
            bufferedImage = ImageIO.read(new File(absoluteFilePath));
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
            bufferedImage = null;
        }

    }

    public Image loadImage() {
        return image;
    }

    BufferedImage loadBufferedImage() {
        return bufferedImage;
    }
}
