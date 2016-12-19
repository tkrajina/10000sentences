package info.puzz.a10000sentences.utils;

import android.util.Log;

import info.puzz.a10000sentences.BuildConfig;

public final class LogUtils {

    public LogUtils() throws Exception {
        throw new Exception();
    }

    public static void exception(Object o, String msg, Throwable e) {
        Log.e(getSimpleName(o), msg, e);
    }
    public static void e(Object o, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.e(getSimpleName(o), String.format(format, args));
        }
    }
    public static void i(Object o, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.i(getSimpleName(o), String.format(format, args));
        }
    }
    public static void d(Object o, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.d(getSimpleName(0), String.format(format, args));
        }
    }
    public static void v(Object o, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.v(getSimpleName(o), String.format(format, args));
        }
    }
    public static void w(Object o, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.w(getSimpleName(o), String.format(format, args));
        }
    }
    public static void wtf(Object o, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.wtf(getSimpleName(o), String.format(format, args));
        }
    }

    private static String getSimpleName(Object o) {
        if (o == null) {
            return "null";
        }
        if (o instanceof Class) {
            return ((Class) o).getSimpleName();
        }
        return o.getClass().getSimpleName();
    }

    public static void main(String[] args) {
        System.out.println(Class.class.equals(LogUtils.class));
    }

}
