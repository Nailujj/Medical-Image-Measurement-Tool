package ch.fhnw.prp.amrs.logic;

import ch.fhnw.prp.amrs.presentation.StateModel;

public class Point {

    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getRealX(StateModel stateModel) {
        double imageViewWidth = stateModel.getImageViewWidth();
        return (getX() / imageViewWidth) * stateModel.getRealImageWidth();
    }

    public double getRealY(StateModel stateModel) {
        double imageHeight = stateModel.getRealImageHeight();
        double imageViewHeight = stateModel.getImageViewHeight();
        return (getY() / imageViewHeight) * imageHeight;
    }

}
