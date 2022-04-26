package com.skronawi.dlr_school_lab.navigation_english.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class DegreeFormatter {

    private static final DecimalFormat coordFormat;

    static {
        coordFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
        coordFormat.applyPattern("00.000000");
    }

    public static String toLat(float latitude) {
        return coordFormat.format(latitude) + "° N";
    }

    public static String toLon(float longitude) {
        return coordFormat.format(longitude) + "° O";
    }

    private static float fromDegrees(String degrees) {
        String degreeValue = degrees;
        if (degrees.contains("°")) {
            degreeValue = degrees.substring(0, degrees.indexOf("°"));
        }
        return Float.valueOf(degreeValue);
    }

    public static float fromLat(String latitude) {
        return fromDegrees(latitude);
    }

    public static float fromLon(String longitude) {
        return fromDegrees(longitude);
    }

    public static String withoutDegree(String degrees) {
        return degrees.substring(0, degrees.indexOf("°"));
    }
}
