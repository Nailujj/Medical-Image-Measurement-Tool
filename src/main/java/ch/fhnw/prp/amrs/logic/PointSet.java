package ch.fhnw.prp.amrs.logic;

import java.util.ArrayList;

public class PointSet {
    private final ArrayList<Point> points;
    private Point dragPoint = null;
    private String resolution = "";

    public PointSet(ArrayList<Point> points) {
        this.points = points;
    }
    public void setDragPoint(Point dragPoint) {
        this.dragPoint = dragPoint;
    }
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    public String getResolution() {
        return resolution;
    }

    public ArrayList<Point> getPoints() {
        if (dragPoint != null) {
            ArrayList<Point> pointsCopy = new ArrayList<>(points);
            pointsCopy.add(dragPoint);
            return pointsCopy;
        }
        return this.points;
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public void clearSet() {
        this.points.clear();
    }
}



