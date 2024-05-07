package ch.fhnw.prp.amrs.presentation;

import ch.fhnw.prp.amrs.data.VtkReader;
import ch.fhnw.prp.amrs.logic.GeometryCalculator;
import ch.fhnw.prp.amrs.logic.Point;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.ArrayList;


public class ImagePane extends VBox implements StateObserver {
    private final StateModel stateModel;

    private final StackPane imageViewPane = new StackPane();
    private final Pane drawPane = new Pane();

    public ImagePane(StateModel stateModel) {
        this.stateModel = stateModel;
        stateModel.addObserver(this);

        setAlignment(Pos.CENTER);
        imageViewPane.setAlignment(Pos.CENTER);

        getChildren().add(imageViewPane);
    }

    public void showImage() {
        imageViewPane.getChildren().clear();
        drawPane.getChildren().clear();
        stateModel.clearMeasurements();

        // Image
        Image img = stateModel.getImageData().getImageLoader().loadImage();
        ImageView imageView = new ImageView(img);

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        imageViewPane.getChildren().add(imageView);
        imageViewPane.getChildren().add(drawPane);
        imageViewPane.setOnScroll(this::onScroll);

        drawPane.setOnMouseClicked(this::handleMouseClick);
        drawPane.setOnMouseDragged(this::handleMouseDragged);

        widthProperty().addListener((obs, oldV, newV) -> sizeImageOnPane(img, imageView));
        heightProperty().addListener((obs, oldV, newV) -> sizeImageOnPane(img, imageView));
        sizeImageOnPane(img, imageView);
    }

    private void sizeImageOnPane(Image img, ImageView imageView) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double maxHeight = screenSize.getHeight() / 5 * 4;

        Pane parent = ((Pane) getParent());

        if (img.getWidth() > img.getHeight()) { // assuring that imageview and drawpane are sized the same
            double width = parent != null ? parent.getWidth() - 500 : 500;
            double ratio = img.getWidth() / img.getHeight();
            drawPane.setMaxWidth(width);
            imageView.setFitWidth(width);
            drawPane.setMaxHeight(width / ratio);
            imageView.setFitHeight(width / ratio);
        } else {
            double height = parent != null ? parent.getHeight() : screenSize.getHeight() / 2;
            double ratio = img.getHeight() / img.getWidth();
            height = Math.min(height, maxHeight);
            drawPane.setMaxHeight(height);
            imageView.setFitHeight(height);
            drawPane.setMaxWidth(height / ratio);
            imageView.setFitWidth(height / ratio);
        }
        stateModel.clearMeasurements();
        stateModel.setImageViewHeight(imageView.getBoundsInParent().getHeight());
        stateModel.setImageViewWidth(imageView.getBoundsInParent().getWidth());
    }

    private void handleMouseDragged(MouseEvent event) {
        double xCoord = event.getX();
        double yCoord = event.getY();
        Point point = new Point(xCoord, yCoord);
        stateModel.setDragPoint(point);
    }

    private void handleMouseClick(MouseEvent event) {
        stateModel.setDragPoint(null);
        double xCoord = event.getX();
        double yCoord = event.getY();

        if(xCoord > 0 && yCoord > 0 && xCoord < drawPane.getWidth() && yCoord < drawPane.getHeight()){
            Point point = new Point(xCoord, yCoord);
            stateModel.addPoint(point);
        }
    }

    @Override
    public void stateChanged() {
        drawLines();
    }

    private void drawLines() {
        drawPane.getChildren().clear();

        Point lastPoint = null;

        for (Point p : stateModel.getPointSet().getPoints()) {
            if (lastPoint != null) {
                Line line = new Line(lastPoint.getX(), lastPoint.getY(), p.getX(), p.getY());
                line.setFill(null);
                line.setStroke(stateModel.getLineColor());
                line.setStrokeWidth(stateModel.getStrokeWidth());
                drawPane.getChildren().add(line);
            }
            Circle circle = new Circle(p.getX(), p.getY(), stateModel.getStrokeWidth());
            circle.setStroke(stateModel.getLineColor());
            circle.setFill(stateModel.getLineColor());
            drawPane.getChildren().add(circle);

            lastPoint = p;
        }

        if (stateModel.getDisplayDistance() && stateModel.getPointSet().getPoints().size() >= 2) {
            displayDistanceInUnit();
        }

        if (stateModel.getDisplayAngles() && stateModel.getPointSet().getPoints().size() >= 3) {
            displayAngleInUnit();
        }
    }

    private void displayDistanceInUnit() {
        String distanceInUnit = GeometryCalculator.getDistanceInUnit(stateModel);

        Text text = new Text(distanceInUnit);
        text.setFill(stateModel.getLineColor());
        text.setFont(Font.font("", stateModel.getDistanceFontSize()));

        double textHeight = text.getLayoutBounds().getHeight();
        double textWidth = text.getLayoutBounds().getWidth();

        double drawPaneHeight = drawPane.getHeight();
        double drawPaneWidth = drawPane.getWidth();

        Point point = new TextPositionCalculation(textHeight, textWidth, drawPaneHeight, drawPaneWidth)
                .getDistanceTextPosition(stateModel.getPointSet().getPoints());
        text.setY(point.getY());
        text.setX(point.getX());

        drawPane.getChildren().add(text);
    }

    private void displayAngleInUnit() {

        ArrayList<Point> points = stateModel.getPointSet().getPoints();
        for (int i = 0; i < points.size() - 2; i++) {
            String angleInUnitString = GeometryCalculator.getAngleInUnit(stateModel).get(i);

            Text text = new Text(angleInUnitString);
            text.setFill(stateModel.getLineColor());
            text.setFont(Font.font("", stateModel.getAngleFontSize()));

            double textHeight = text.getLayoutBounds().getHeight();
            double textWidth = text.getLayoutBounds().getWidth();

            double drawPaneHeight = drawPane.getHeight();
            double drawPaneWidth = drawPane.getWidth();

            Point firstPoint = points.get(i);
            Point secondPoint = points.get(i + 1);
            Point thirdPoint = points.get(i + 2);

            Point point = new TextPositionCalculation(textHeight, textWidth, drawPaneHeight, drawPaneWidth).getAngleTextPosition(firstPoint, secondPoint, thirdPoint);

            text.setX(point.getX());
            text.setY(point.getY());

            drawPane.getChildren().add(text);
        }
    }

    private void onScroll(ScrollEvent event) {
        if (stateModel.getImageData().getImageFile().endsWith(".vtk")) {
            double deltaY = event.getDeltaY();

            VtkReader vtkReader = stateModel.getVtkReader();

            if (deltaY > 0 && vtkReader.getSliceNumber() < vtkReader.getRangePerView().get(vtkReader.getView())) {
                // handleScrollUp
                vtkReader.setSliceNumber(vtkReader.getSliceNumber() + 1);

            } else if (deltaY < 0 && (vtkReader.getSliceNumber() - 1 > 0)) {
                // handleScrollUp
                vtkReader.setSliceNumber(vtkReader.getSliceNumber() - 1);
            }
            stateModel.setImageData(vtkReader.read());
            showImage();
        }
    }
}