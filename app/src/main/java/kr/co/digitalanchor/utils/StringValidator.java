package kr.co.digitalanchor.utils;

/**
 * Created by Thomas on 2015-06-15.
 */
public class StringValidator {

    public static boolean isEmail(String email) {

        boolean result;

        try {

            String reg = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

            result = email.matches(reg);

        } catch (Exception e) {

            result = false;

        }

        return result;
    }

    public static boolean isPassword(String password) {

        boolean result;

        try {

            String reg = "^[A-Za-z0-9]{6,}$";

            result = password.matches(reg);

        } catch (Exception e) {

            result = false;

        }

        return result;
    }

    public static boolean isBirthDay(String date) {

        boolean result;

        try {

            String reg = "\\d{8}";

            result = date.matches(reg);

        } catch (Exception e) {

            result = false;

        }

        return result;
    }

}
