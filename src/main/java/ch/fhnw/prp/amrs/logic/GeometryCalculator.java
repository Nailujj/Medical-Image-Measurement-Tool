package ch.fhnw.prp.amrs.logic;

import ch.fhnw.prp.amrs.presentation.StateModel;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class GeometryCalculator {

    private static double getDistanceInPixel(StateModel stateModel) {
        double distance = 0;

        ArrayList<Point> points = stateModel.getPointSet().getPoints();

        if (!points.isEmpty() && points.size() > 1) {
            double distanceTemp = 0;
            for (int i = 0; i < points.size() - 1; i++) {
                double differenceX = Math.abs(points.get(i).getRealX(stateModel) - points.get(i + 1).getRealX(stateModel));
                double differenceY = Math.abs(points.get(i).getRealY(stateModel) - points.get(i + 1).getRealY(stateModel));
                distanceTemp += Math.sqrt(Math.pow(differenceX, 2) + Math.pow(differenceY, 2));
            }
            distance = distanceTemp;

        }
        return distance;
    }

    private static ArrayList<Double> getAnglesInDegree(ArrayList<Point> points) {
        ArrayList<Double> angles = new ArrayList<>();

        if (!points.isEmpty() && points.size() >= 3) {
            for (int i = 0; i < points.size() - 2; i++) {

                //angle between P1P2 and P2P3
                Point2D.Double p1 = new Point2D.Double(points.get(i).getX(), points.get(i).getY());
                Point2D.Double p2 = new Point2D.Double(points.get(i + 1).getX(), points.get(i + 1).getY());
                Point2D.Double p3 = new Point2D.Double(points.get(i + 2).getX(), points.get(i + 2).getY());

                //vector P1P2 and P2P3
                Point2D.Double vec1 = new Point2D.Double(p1.getX() - p2.getX(), p1.getY() - p2.getY());
                Point2D.Double vec2 = new Point2D.Double(p3.getX() - p2.getX(), p3.getY() - p2.getY());

                //length of vek1 and vek2
                double length1 = Math.sqrt(vec1.getX() * vec1.getX() + vec1.getY() * vec1.getY());
                double length2 = Math.sqrt(vec2.getX() * vec2.getX() + vec2.getY() * vec2.getY());

                //scalar product from vek1 and vek2
                double scalarProduct = vec1.getX() * vec2.getX() + vec1.getY() * vec2.getY();

                //vec1*vec2 = norm1*norm2*cos(alpha)
                double alpha = Math.acos(scalarProduct / (length1 * length2));

                double angle = Math.toDegrees(alpha);

                angles.add(angle);
            }
        }

        return angles;
    }

    public static String getDistanceInUnit(StateModel stateModel) {
        String distanceInUnit;

        if (!stateModel.getDistanceUnit().equals("pixels")) {
            double distanceInPixel = getDistanceInPixel(stateModel);

            // Remove commas from the resolution string
            String resolution = stateModel.getImageData().getResolution().replace(",", "");

            // Explicitly set the locale to use the dot as the decimal separator
            Locale.setDefault(Locale.US);

            double distanceInDefaultUnit = distanceInPixel * Double.parseDouble(resolution);
            String selectedUnit = stateModel.getDistanceUnit();
            double distanceInSelectedUnit = UnitConverter.convertUnit(stateModel.getImageData().getResolutionUnit(), selectedUnit, distanceInDefaultUnit);

            if (distanceInSelectedUnit <= 0.001) {
                BigDecimal bd = new BigDecimal(distanceInSelectedUnit);
                DecimalFormat decimalFormat = new DecimalFormat("0.00E0");
                distanceInUnit = decimalFormat.format(bd) + selectedUnit;
            } else if (distanceInSelectedUnit >= 10000) {
                BigDecimal bd = new BigDecimal(distanceInSelectedUnit);
                DecimalFormat decimalFormat = new DecimalFormat("0000.00E0");
                distanceInUnit = decimalFormat.format(bd) + selectedUnit;
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                double formatted = Double.parseDouble(decimalFormat.format(distanceInSelectedUnit));
                distanceInUnit = formatted + selectedUnit;
            }
        } else {
            distanceInUnit = String.valueOf((double) Math.round(getDistanceInPixel(stateModel) * 100) / 100);
        }
        return distanceInUnit;
    }

    public static ArrayList<String> getAngleInUnit(StateModel stateModel) {
        ArrayList<String> angles = new ArrayList<>();

        ArrayList<Point> points = stateModel.getPointSet().getPoints();

        for (int i = 0; i < points.size() - 2; i++) {
            double angleInDegree = getAnglesInDegree(points).get(i);
            double angleInUnit = UnitConverter.convertUnit("degree", stateModel.getAngleUnit(), angleInDegree);
            String angleInUnitString;

            if (!stateModel.getAngleUnit().equals("degree")) {
                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                double formatted = Double.parseDouble(decimalFormat.format(angleInUnit));
                angleInUnitString = String.valueOf(formatted);
            } else {
                angleInUnitString = String.valueOf(Math.round(angleInUnit));
            }
            angles.add(angleInUnitString);

        }
        return angles;
    }
}
