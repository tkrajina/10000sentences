package info.puzz.a10000sentences;

import android.content.Context;
import android.preference.PreferenceManager;

import info.puzz.a10000sentences.utils.NumberUtils;

public final class Preferences {
    public static final String USE_TTS = "use_tts";
    public static final String GUESSES_TO_CLIPBOARD = "guesses_to_clipboard";
    public static final String MAX_REPEAT = "max_repeat";
    public static final String MIN_CORRECT_WORDS = "min_correct_words";

    public static boolean isUseTTS(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(USE_TTS, true);
    }

    public static boolean isWordToClipboard(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(GUESSES_TO_CLIPBOARD, true);
    }

    public static int getMaxRepeat(Context context) {
        int dflt = 10;
        int repeat = NumberUtils.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(MAX_REPEAT, String.valueOf(dflt)), dflt);
        if (repeat < 1) {
            return 3;
        }
        return repeat;
    }

    public static int getMinCorrectWords(Context context) {
        int dflt = 90;
        int mcw = NumberUtils.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(MIN_CORRECT_WORDS, String.valueOf(dflt)), dflt);
        if (mcw < 0) {
            return 0;
        }
        if (mcw > 100) {
            return 100;
        }
        return mcw;
    }
}
