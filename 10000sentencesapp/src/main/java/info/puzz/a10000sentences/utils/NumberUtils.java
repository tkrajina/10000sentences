package info.puzz.a10000sentences.utils;

/**
 * Created by puzz on 06/12/2016.
 */
public class NumberUtils {
    private NumberUtils() throws Exception {
        throw new Exception();
    }

    public static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
}
