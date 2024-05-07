package ch.fhnw.prp.amrs.logic;

import java.util.Map;

import static java.util.Map.entry;

public class UnitConverter {
    public static final Map<String, Double> conversionMap =
            Map.ofEntries(
                    entry("km", 1000000.0),
                    entry("m", 1000.0),
                    entry("cm", 10.0),
                    entry("mm", 1.0), //standard for distance
                    entry("µm", 0.001),
                    entry("miles", 1.609e+6),
                    entry("yard", 914.4),
                    entry("feet", 304.8),
                    entry("inch", 25.4),
                    entry("degree", 1.0), //standard for angle
                    entry("radian",  180 / Math.PI));


    public static double convertUnit(String fromUnit, String toUnit, double value){
        double conversionFactor = conversionMap.get(fromUnit) / conversionMap.get(toUnit);
        return value * conversionFactor;
    }
}
