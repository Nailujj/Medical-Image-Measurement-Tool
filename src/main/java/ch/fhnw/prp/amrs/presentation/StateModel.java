package ch.fhnw.prp.amrs.presentation;

import ch.fhnw.prp.amrs.data.VtkReader;
import ch.fhnw.prp.amrs.logic.ImageData;
import ch.fhnw.prp.amrs.logic.Point;
import ch.fhnw.prp.amrs.logic.PointSet;
import javafx.scene.paint.Color;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.util.ArrayList;
import java.util.List;

public class StateModel {

    private final List<StateObserver> observers;

    private ImageData imageData;
    private PointSet pointSet = new PointSet(new ArrayList<>());

    // Drawing options
    private int strokeWidth = 3;
    private int distanceFontSize = getStrokeWidth() + 13;
    private int angleFontSize = getStrokeWidth() + 11;
    private Color lineColor = Color.BLACK;

    private VtkReader vtkReader;

    // Geometry
    private boolean displayDistance;
    private boolean displayAngles;
    private String angleUnit = "degree";
    private String distanceUnit = "pixels";
    private double imageViewWidth;
    private double imageViewHeight;
    private double realImageWidth;
    private double realImageHeight;
    private boolean isDarkMode = false;

    private void sendStateChangedEvent() {
        for (StateObserver observer : observers) {
            observer.stateChanged();
        }
    }

    public void update() {
        sendStateChangedEvent();
    }

    public void setDistanceFontSize(int distanceFontSize) {
        this.distanceFontSize = distanceFontSize;
        sendStateChangedEvent();
    }

    public void setAngleFontSize(int angleFontSize) {
        this.angleFontSize = angleFontSize;
        sendStateChangedEvent();
    }

    public int getDistanceFontSize() {
        return distanceFontSize;
    }

    public int getAngleFontSize() {
        return angleFontSize;
    }


    public void setPointSet(PointSet pointSet) {
        this.pointSet = pointSet;
        sendStateChangedEvent();
    }

    public void setImageViewWidth(double imageViewWidth) {
        this.imageViewWidth = imageViewWidth;
        sendStateChangedEvent();
    }

    public void setImageViewHeight(double imageViewHeight) {
        this.imageViewHeight = imageViewHeight;
        sendStateChangedEvent();
    }

    public double getImageViewWidth() {
        return imageViewWidth;
    }

    public double getImageViewHeight() {
        return imageViewHeight;
    }

    public double getRealImageHeight() {
        return realImageHeight;
    }

    public double getRealImageWidth() {
        return realImageWidth;
    }

    //for JUnitTest
    public void setRealImageHeight(double realImageHeight) {
        this.realImageHeight = realImageHeight;
    }

    //for JUnit Test
    public void setRealImageWidth(double realImageWidth) {
        this.realImageWidth = realImageWidth;
    }

    public StateModel() {
        observers = new ArrayList<>();
    }

    public void addObserver(StateObserver observer) {
        observers.add(observer);
    }

    public void clearMeasurements() {
        pointSet.clearSet();
        pointSet.setDragPoint(null);
        sendStateChangedEvent();
    }

    public void addPoint(Point _point) {
        pointSet.addPoint(_point);
        sendStateChangedEvent();
    }

    public PointSet getPointSet() {
        return pointSet;
    }


    public void setDragPoint(Point dragPoint) {
        pointSet.setDragPoint(dragPoint);
        sendStateChangedEvent();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        sendStateChangedEvent();
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
        sendStateChangedEvent();
    }

    public void setImageData(ImageData imageData) {
        try {
            realImageWidth = imageData.getImageLoader().loadImage().getWidth();
            realImageHeight = imageData.getImageLoader().loadImage().getHeight();
        } catch (NullPointerException nex) {
            System.out.println("Graphics are not set");
        }

        this.imageData = imageData;
        sendStateChangedEvent();
    }

    public ImageData getImageData() {
        return imageData;
    }

    public boolean getDisplayDistance() {
        return displayDistance;
    }

    public void setDisplayDistance(boolean displayDistance) {
        this.displayDistance = displayDistance;
        sendStateChangedEvent();
    }

    public boolean getDisplayAngles() {
        return displayAngles;
    }

    public void setDisplayAngles(boolean displayAngles) {
        this.displayAngles = displayAngles;
        sendStateChangedEvent();
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
        sendStateChangedEvent();
    }

    public String getAngleUnit() {
        return angleUnit;
    }

    public void setAngleUnit(String angle) {
        this.angleUnit = angle;
        sendStateChangedEvent();
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public String getStyleSheetOfColorMode() {
        if (isDarkMode)
            return "style_dark.css";
        else
            return "style_light.css";
    }

    public void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
        sendStateChangedEvent();
    }

    public void setVtkReader(VtkReader _vtkReader) {
        this.vtkReader = _vtkReader;
    }

    public VtkReader getVtkReader() {
        return vtkReader;
    }
}