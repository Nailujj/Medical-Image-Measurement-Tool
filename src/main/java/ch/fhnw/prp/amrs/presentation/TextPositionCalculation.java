package ch.fhnw.prp.amrs.presentation;

import ch.fhnw.prp.amrs.logic.Point;

import java.util.ArrayList;

public class TextPositionCalculation {

    private final double textHeight;
    private final double textWidth;
    private final double drawPaneHeight;
    private final double drawPaneWidth;

    public TextPositionCalculation(double textHeight, double textWidth, double drawPaneHeight, double drawPaneWidth) {
        this.textHeight = textHeight;
        this.textWidth = textWidth;
        this.drawPaneHeight = drawPaneHeight;
        this.drawPaneWidth = drawPaneWidth;
    }

    public Point getDistanceTextPosition(ArrayList<Point> points) {
        double lastPointX = points.get(points.size() - 1).getX();
        double lastPointY = points.get(points.size() - 1).getY();
        double secondLastPointY = points.get(points.size() - 2).getY();

        double x = setXAtPoint(lastPointX);
        double y;

        //if the line goes from bottom to top -> set y above the line
        //if the line goes from top to bottom -> set y under the line
        if (lastPointY > secondLastPointY) {
            y = setYBelowPoint(lastPointY);
        } else {
            y = setYAbovePoint(lastPointY);
        }
        return new Point(x, y);
    }

    public Point getAngleTextPosition(Point p1, Point p2, Point p3) {
        double y = yAtPoint(p2.getY());
        double x = xAtPoint(p2.getX());

        double deltaY1 = p3.getY() - p2.getY();
        double deltaX1 = p3.getX() - p2.getY();
        double angle1 = Math.atan2(deltaY1, deltaX1);

        double deltaY2 = p1.getY() - p2.getY();
        double deltaX2 = p1.getX() - p2.getY();
        double angle2 = Math.atan2(deltaY2, deltaX2);

        //set the text to the obtuse angle to make sure that it can be read
        //if x1 and x3 are < x2, the obtuse angel must be right
        if (p1.getX() <= p2.getX() && p3.getX() <= p2.getX()) {
            y = setYAtPoint(p2.getY());
            x = setXRightFromPoint(p2.getX());
        }
        //if x1 and x3 are > x2, the obtuse angle must be left
        else if (p1.getX() > p2.getX() && p3.getX() > p2.getX()) {
            y = setYAtPoint(p2.getY());
            x = setXLeftFromPoint(p2.getX());
        }
        //if y1 and y3 are < y2, the obtuse angle must be below (0 is at the top)
        else if (p1.getY() <= p2.getY() && p3.getY() <= p2.getY()) {
            y = setYBelowPoint(p2.getY());
            x = setXAtPoint(p2.getX());
        }
        //if y1 and y3 are > y2, the obtuse angle must be above (0 is at the top)
        else if (p1.getY() > p2.getY() && p3.getY() > p2.getY()) {
            y = setYAbovePoint(p2.getY());
            x = setXAtPoint(p2.getX());
        }
        //detects orientation of the obtuse angle of most cases
        else if (Math.abs(p1.getY() - p3.getY()) > Math.abs(p1.getX() - p3.getX())) {
            if (angle1 < angle2) {
                y = setYAtPoint(p2.getY());
                x = setXRightFromPoint(p2.getX());
            } else if (angle1 >= angle2) {
                y = setYAtPoint(p2.getY());
                x = setXLeftFromPoint(p2.getX());
            }
        } else if (Math.abs(p1.getY() - p3.getY()) <= Math.abs(p1.getX() - p3.getX())) {
            if (angle1 < angle2) {
                y = setYBelowPoint(p2.getY());
                x = setXAtPoint(p2.getX());
            } else if (angle1 >= angle2) {
                y = setYAbovePoint(p2.getY());
                x = setXAtPoint(p2.getX());
            }
        }
        return new Point(x, y);
    }

    private double yBelowPoint(double pointY) {
        return pointY + textHeight;
    }

    private double yAbovePoint(double pointY) {
        return pointY - textHeight + 10;
    }

    private double yAtPoint(double pointY) {
        return pointY + textHeight / 3;
    }

    private double xLeftFromPoint(double pointX) {
        return pointX - textWidth - 10;
    }

    private double xRightFromPoint(double pointX) {
        return pointX + 10;
    }

    private double xAtPoint(double pointX) {
        return pointX - textWidth / 2;
    }

    private boolean xOutOfBoundsRight(double pointX) {
        return pointX + textWidth > drawPaneWidth;
    }

    private boolean xOutOfBoundsLeft(double pointX) {
        return pointX - textWidth < 0;
    }

    private boolean yOutOfBoundsBottom(double pointY) {
        return pointY + textHeight > drawPaneHeight;
    }

    private boolean yOutOfBoundsTop(double pointY) {
        return pointY - textHeight < 0;
    }


    private double setYBelowPoint(double pointY) {
        if (yOutOfBoundsBottom(pointY)) {
            return yAbovePoint(pointY);
        }
        return yBelowPoint(pointY);
    }

    private double setYAbovePoint(double pointY) {
        if (yOutOfBoundsTop(pointY)) {
            return yBelowPoint(pointY);
        }
        return yAbovePoint(pointY);
    }

    private double setYAtPoint(double pointY) {
        if (yOutOfBoundsTop(pointY)) {
            return yBelowPoint(pointY);
        } else if (yOutOfBoundsBottom(pointY)) {
            return yAbovePoint(pointY);
        }
        return yAtPoint(pointY);
    }

    private double setXLeftFromPoint(double pointX) {
        if (xOutOfBoundsLeft(pointX)) {
            return xRightFromPoint(pointX);
        }
        return xLeftFromPoint(pointX);
    }

    private double setXRightFromPoint(double pointX) {
        if (xOutOfBoundsRight(pointX)) {
            return xLeftFromPoint(pointX);
        }
        return xRightFromPoint(pointX);
    }

    private double setXAtPoint(double pointX) {
        if (xOutOfBoundsRight(pointX)) {
            return xLeftFromPoint(pointX);
        } else if (xOutOfBoundsLeft(pointX)) {
            return xRightFromPoint(pointX);
        }
        return xAtPoint(pointX);
    }
}