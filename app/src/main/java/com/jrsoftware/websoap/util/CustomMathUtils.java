package com.jrsoftware.websoap.util;

/**
 * Custom Utility class for common Math Functions.
 */
public class CustomMathUtils {

    /**
     * Returns the entered float value to a standard percent string
     * @param value - Float value in the range [0,1]
     * @return - String percent in the range [0-100]
     */
    public static String asPercent(float value){
        if(value < 0f)
            return "0.00%";

        return String.format("%3.2f", value * 100) + "%";
    }

    /**
     * Returns a safe int value. Intended for use with int values in the range [0,inf)
     * @param value - String value to be converted to int
     * @return The int value of the string input if possible, -1 otherwise.
     */
    public static int safeInt(String value){
        if(value == null)
            return -1;

        Integer obj = tryParseInt(value);
        if(obj == null)
            return -1;

        return obj;
    }

    /**
     * Returns a safe float value. Intended for use with float values in the range [0,inf)
     * @param value - String value to be converted to float
     * @return The float value of the string input if possible, -1 otherwise.
     */
    public static float safeFloat(String value){
        if(value == null)
            return -1f;

        Float obj = tryParseFloat(value);
        if(obj == null)
            return -1f;

        return obj;
    }

    /**
     * Returns a safe int value. Intended for use with int values in the range [0,inf)
     * @param value - String value to be converted to int
     * @return The int value of the string input if possible, -1 otherwise.
     */
    public static long safeLong(String value){
        if(value == null)
            return -1;

        Long obj = tryParseLong(value);
        if(obj == null)
            return -1;

        return obj;
    }

    /**
     * Attempts to parse the float value safely.
     * @param value - String value to parse to a Float Object
     * @return The parsed float value if successful, null otherwise.
     */
    public static Float tryParseFloat(String value){
        try{
            return Float.parseFloat(value);
        }
        catch(NumberFormatException nfe){
            return null;
        }
    }

    /**
     * Attempts to parse the int value safely.
     * @param value - String value to parse to an Integer Object
     * @return The parsed int value if successful, null otherwise.
     */
    public static Integer tryParseInt(String value){
        try{
            return Integer.parseInt(value);
        }
        catch(NumberFormatException nfe){
            return null;
        }
    }

    /**
     * Attempts to parse the long value safely.
     * @param value - String value to parse to a Long Object
     * @return The parsed long value if successful, null otherwise.
     */
    public static Long tryParseLong(String value){
        try{
            return Long.parseLong(value);
        }
        catch(NumberFormatException nfe){
            return null;
        }
    }
}
