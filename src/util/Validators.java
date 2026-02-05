package util;

public class Validators {
    public static void requireNonBlank(String value, String fileName) {
        if ((value == null || value.trim().isEmpty()))
            throw new IllegalArgumentException(fileName + " CANNOT BE EMPTY");
    }

    public static void requireMinLen(String value, int min, String fieldName) {
        if (value == null || value.length() < min)
            throw new IllegalArgumentException(fieldName + " MUST BE AT LEAST " + min + "CHARACTERS");
        
    }
}