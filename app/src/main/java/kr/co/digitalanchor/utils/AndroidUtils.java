package kr.co.digitalanchor.utils;

import android.content.Context;

import com.android.volley.toolbox.SimpleXmlRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AndroidUtils {
    // convert InputStream to String
    public static String inputStreamToString(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    // convert from UTF-8 -> internal Java String format
    public static String convertFromUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    // convert from internal Java String format -> UTF-8
    public static String convertToUTF8(String s) {

        String out = null;

        try {

            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");

        } catch (java.io.UnsupportedEncodingException e) {

            return null;
        }

        return out;
    }

    public static boolean containsIgnoreCase(String src, String what) {
        final int length = what.length();
        if (length == 0)
            return true; // Empty string is contained

        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);

            if (ch != firstLo && ch != firstUp)
                continue;

            if (src.regionMatches(true, i, what, 0, length))
                return true;
        }

        return false;
    }

    public static String getCurrentTimeIncludeMs() {

        Calendar cal = Calendar.getInstance();

        cal.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        return format.format(cal.getTime());
    }

    public static String getCurrentTime4Chat() {

        Calendar calendar = Calendar.getInstance();

        calendar.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return format.format(calendar.getTime());
    }

}
