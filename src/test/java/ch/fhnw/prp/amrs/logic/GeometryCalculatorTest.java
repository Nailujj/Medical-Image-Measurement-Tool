package ch.fhnw.prp.amrs.logic;

import ch.fhnw.prp.amrs.presentation.StateModel;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class GeometryCalculatorTest {

    @org.junit.jupiter.api.Test
    public void getDistanceInUnit() {
        StateModel stateModel = new StateModel();

        Point p1 = new Point(1, 1);
        Point p2 = new Point(2, 2);
        Point p3 = new Point(3, 3);
        Point p4 = new Point(3, 3);

        ArrayList<Point> points = new ArrayList<>();
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);

        stateModel.setPointSet(new PointSet(points));

        stateModel.setRealImageHeight(400);
        stateModel.setRealImageWidth(400);
        stateModel.setImageViewHeight(200);
        stateModel.setImageViewWidth(200);

        stateModel.setImageData(new ImageData("", "0.02", "m", null, null));

        Map<String, String> expectedValuePerUnit = new HashMap<>();
        expectedValuePerUnit.put("pixels", "5.66");
        expectedValuePerUnit.put("km", "1.13E-4km");
        expectedValuePerUnit.put("m", "0.113m");
        expectedValuePerUnit.put("cm", "11.314cm");
        expectedValuePerUnit.put("mm", "113.137mm");
        expectedValuePerUnit.put("µm", "1131.37E2µm");
        expectedValuePerUnit.put("miles", "7.03E-5miles");
        expectedValuePerUnit.put("yard", "0.124yard");
        expectedValuePerUnit.put("feet", "0.371feet");
        expectedValuePerUnit.put("inch", "4.454inch");

        for (String unit : expectedValuePerUnit.keySet()) {
            stateModel.setDistanceUnit(unit);
            String calculatedDistance = GeometryCalculator.getDistanceInUnit(stateModel);
            Assertions.assertEquals(expectedValuePerUnit.get(unit), calculatedDistance, "Expected distance does not match");
        }
    }

    @org.junit.jupiter.api.Test
    public void getAngleInUnit() {
        StateModel stateModel = new StateModel();

        Point p1 = new Point(1, 1);
        Point p2 = new Point(2, 2);
        Point p3 = new Point(1, 3);
        Point p4 = new Point(0, 4);
        Point p5 = new Point(1, 3);
        Point p6 = new Point(1, 4);

        ArrayList<Point> points = new ArrayList<>();
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);
        points.add(p5);
        points.add(p6);

        stateModel.setPointSet(new PointSet(points));

        Map<String, String[]> expectedValuePerUnit = new HashMap<>();

        String[] expectedAnglesInDegree = {"90", "180", "0", "45", "0"};
        String[] expectedAnglesInRadian = {"1.571", "3.142", "0.0", "0.785", "0"};

        expectedValuePerUnit.put("degree", expectedAnglesInDegree);
        expectedValuePerUnit.put("radian", expectedAnglesInRadian);


        for (String unit : expectedValuePerUnit.keySet()) {
            stateModel.setAngleUnit(unit);

            ArrayList<String> calculatedAngle = GeometryCalculator.getAngleInUnit(stateModel);

            for (int i = 0; i < calculatedAngle.size(); i++) {
                Assertions.assertEquals(expectedValuePerUnit.get(unit)[i], calculatedAngle.get(i), "Expected angle does not match");
            }
        }
    }
}