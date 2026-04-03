package helpers;

/**
 * Utility class for common string operations.
 */
public class StringHelper {
    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String repeat(String s, int times) {
        if (times <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) sb.append(s);
        return sb.toString();
    }

    public static String truncate(String s, int maxLength) {
        if (s == null || s.length() <= maxLength) return s;
        return s.substring(0, maxLength) + "...";
    }
}
