package util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+0-9\\s-]{9,15}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isPositiveDouble(String text) {
        if (!isNotEmpty(text)) return false;
        try {
            double val = Double.parseDouble(text.trim());
            return val >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInt(String text) {
        if (!isNotEmpty(text)) return false;
        try {
            int val = Integer.parseInt(text.trim());
            return val >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
