package kr.co.digitalanchor.utils;

/**
 * Created by Thomas on 2015-06-15.
 */
public class StringValidator {

    public static boolean isEmail(String email) {

        boolean result = true;

        try {

            String reg = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

            result = email.matches(reg);

        } catch (Exception e) {

            result = false;

        }

        return result;
    }

}
