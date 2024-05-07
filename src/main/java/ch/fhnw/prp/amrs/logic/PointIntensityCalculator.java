package ch.fhnw.prp.amrs.logic;

import ch.fhnw.prp.amrs.presentation.StateModel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PointIntensityCalculator {

    public static double getIntensity(Point point, StateModel stateModel) {
        double x = point.getRealX(stateModel);
        double y = point.getRealY(stateModel);

        BufferedImage image = stateModel.getImageData().getImageLoader().loadBufferedImage();

        int rgb = image.getRGB((int) x, (int) y);
        Color color = new Color(rgb, true);

        int grayscaleValue = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

        if (stateModel.getImageData().getImageFile().equals("dem-switzerland.png")) {
            // Map the grayscale value (0 to 255) to the height range (0 to 4556)
            double maxHeight = 4556.0;
            return (grayscaleValue / 255.0) * maxHeight;
        } else {
            //Get intensity in % for all other images
            return (grayscaleValue / 255.0) * 100;
        }
    }

    /**
     * Bresenham's Line Algorithm: The algorithm uses integer arithmetic to efficiently determine the set of pixels that approximate a straight line between the two given points.
     * x1, y1: Coordinates of the first point.
     * x2, y2: Coordinates of the second point.
     * dx, dy: Differences between the x and y coordinates of the two points.
     * sx, sy: Sign of the change in x and y (either 1 or -1).
     * err: The current error in the y coordinate.
     * Loop: The algorithm uses a while loop to iterate through the points along the line.
     * Inside the loop, the current point (x1, y1) is added to the list of points (ArrayList<Point> points).
     * The loop continues until it reaches the endpoint (x2, y2).
     * Bresenham's Algorithm Logic: The core logic inside the loop updates the current coordinates based on the error and increments either the x or y coordinate.
     * e2 = 2 * err is calculated.
     * If e2 is greater than -dy, the error is adjusted, and the x-coordinate is incremented (x1 += sx).
     * If e2 is less than dx, the error is adjusted, and the y-coordinate is incremented (y1 += sy).
     */
    public static ArrayList<Point> getAllPointsBetween(Point point1, Point point2) {
        //Bresenham's line algorithm

        int x1 = (int) point1.getX();
        int y1 = (int) point1.getY();
        int x2 = (int) point2.getX();
        int y2 = (int) point2.getY();

        ArrayList<Point> points = new ArrayList<>();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;

        int err = dx - dy;

        while (true) {
            points.add(new Point(x1, y1));

            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }

        return points;
    }
}
