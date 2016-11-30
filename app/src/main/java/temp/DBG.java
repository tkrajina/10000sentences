package temp;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DBG {
    public static final boolean TRUE = true;
    private static final String JAVA_HOME = System.getProperty("java.home");
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String CLASS_PATH = System.getProperty("java.class.path");

    static class TimerData {
        private double time;
        private String name;

        private TimerData(double time, String name) {
            this.time = time;
            this.name = name;
        }

        double getTime() {
            return time;
        }

        String getName() {
            return name;
        }
    }

    private static ThreadLocal<ArrayList<TimerData>> THREAD_LOCAL = new ThreadLocal<ArrayList<TimerData>>() {
        protected synchronized ArrayList<TimerData> initialValue() {
            return new ArrayList<TimerData>();
        }
    };

    public static final void printWithLocation(Object... objects) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        print(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(), line:" + stackTraceElement.getLineNumber());
        String result = joinObjects("", objects);
        if (result != null && result.trim().length() > 0) {
            printLine("TEMPDEBUG>	", objects);
        }
    }

    public static final void print(Object... objects) {
        printLine("TEMPDEBUG>", objects);
    }

    public static final void printBinaryRepresentation(byte[] bytes) {
        print(getBinaryRepresentation(bytes));
    }

    public static final String getBinaryRepresentation(byte[] bytes) {
        if (bytes == null) {
            return "null";
        }
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            String binary = Integer.toBinaryString((256 + (int) b) % 256);
            for (int i = 0; i < 8 - binary.length(); i++) {
                result.append("0");
            }
            result.append(binary);
            result.append(" ");
        }
        return result.toString();
    }

    public static final void todo(Object... objects) {
    }

    public static final void comment(Object... objects) {
    }

    public static final <T extends Object> T get(T object) {
        return object;
    }

    private static final void printLine(String prefix, Object[] objects) {
        String result = joinObjects(prefix, objects);
        System.out.println(result);
    }

    private static String joinObjects(String prefix, Object[] objects) {
        if (objects == null || objects.length == 0) return "";
        StringBuilder result = new StringBuilder(prefix);
        for (Object object : objects) {
            result.append(objectToString(object));
        }
        String string = result.toString();
        if (prefix != null && prefix.length() > 0) {
            return string.replace("\n", "\n                                         ".substring(0, prefix.length() + 1));
        }
        return string;
    }

    public static final StackTraceElement getCurrentStackTraceElement() {
        return Thread.currentThread().getStackTrace()[2];
    }

    public static final void printStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            print(" at " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" + stackTraceElement.getLineNumber() + ")");
        }
    }

    private static String objectToString(Object object) {
        if (object == null) {
            return "null";
        } else if (object.getClass().isArray()) {
            StringBuilder result = new StringBuilder();
            result.append("[");
            for (int i = 0; i < Array.getLength(object); i++) {
                result.append(objectToString(Array.get(object, i)));
                result.append(", ");
            }
            result.append("]");
            return result.toString();
        } else if (object instanceof Throwable) {
            Throwable throwable = (Throwable) object;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            throwable.printStackTrace(new PrintStream(out));
            try {
                out.close();
            } catch (Exception ignore) {
            }
            return new String(out.toByteArray());
        }
        return String.valueOf(object);
    }

    public static final String getCommandLine(Class<?> classWithMainMethod, String... additionalArguments) {
        String javaCommand = "java";
        if (FILE_SEPARATOR.equals("\\")) {
            javaCommand += ".exe";
        }
        String arguments = " ";
        if (additionalArguments != null) {
            for (String arg : additionalArguments) {
                if (arg != null) {
                    arguments += arg + " ";
                }
            }
        }
        String commandLine = JAVA_HOME + FILE_SEPARATOR + "bin" + FILE_SEPARATOR + javaCommand + CLASS_PATH + " " + classWithMainMethod.getName() + " " + arguments;
        return commandLine;
    }

    public static final String saveTime(Object... name) {
        ArrayList<TimerData> times = THREAD_LOCAL.get();
        times.add(new TimerData(System.nanoTime() / 1000000., joinObjects("", name)));
        return getTimerTimes(1);
    }

    public static final void resetTimer() {
        ArrayList<TimerData> times = THREAD_LOCAL.get();
        times.clear();
    }

    public static final void resetTimer(Object... name) {
        resetTimer();
        saveTime(name);
    }

    public static final String getTimerTimes() {
        return getTimerTimes(1000);
    }

    public static final String getTimerTimes(int n) {
        StringBuilder result = new StringBuilder();
        ArrayList<TimerData> times = THREAD_LOCAL.get();
        int startIndex = Math.max(0, times.size() - n);
        if (times.size() == 0) {
            return "No timer data";
        }
        double previous = -1;
        double start = times.get(0).getTime();
        for (int i = startIndex; i < times.size(); i++) {
            TimerData timerData = times.get(i);
            if (i > 0) {
                previous = times.get(i - 1).getTime();
            } else {
                previous = timerData.getTime();
            }
            String fromPrevious = String.format("%10.4f", timerData.getTime() - previous);
            String fromStart = String.format("%10.4f", timerData.getTime() - start);
            result.append(fromPrevious).append("ms").append(fromStart).append("ms").append(" - ").append(timerData.getName()).append('\n');
        }
        return result.toString();
    }

    public static final void printTimer() {
        print(getTimerTimes());
    }

    public static final void printTimer(int n) {
        print(getTimerTimes(n));
    }

    public static final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Throwable ignore) {
        }
    }
}