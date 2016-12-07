package info.puzz.a10000sentences.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.models.Language;
import lombok.Getter;

public class Speech {

    private static final String TAG = Speech.class.getSimpleName();

    private final Context context;
    private final TextToSpeech tts;
    private final Locale locale;
    private final boolean languageFound;

    @Getter
    private boolean initialized = false;

    public Speech(Context context, Language language) {
        this.context = context;
        this.locale = findLocale(language);
        this.languageFound = locale != null;
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                Speech.this.initialized = true;
            }
        });
        tts.setLanguage(locale);
    }

    private Locale findLocale(Language language) {
        for (Locale locale : Locale.getAvailableLocales()) {
            if (org.apache.commons.lang3.StringUtils.isEmpty(locale.getCountry()) && StringUtils.equals(locale.getLanguage(), language.getLanguageId())) {
                return locale;
            }
        }
        for (Locale locale : Locale.getAvailableLocales()) {
            if (StringUtils.equals(locale.getLanguage(), language.getLanguageId())) {
                return locale;
            }
        }
        return null;
    }

    public void speech(String speech) {
        if (!languageFound) {
            Toast.makeText(context, R.string.tts_language_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!initialized) {
            Toast.makeText(context, R.string.tts_not_inititialized, Toast.LENGTH_SHORT).show();
            return;
        }

        tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    public static void main(String[] args) {
        for (Locale locale : Locale.getAvailableLocales()) {
            System.out.println(locale.getLanguage());
            System.out.println(locale.getCountry());
            System.out.println();
        }
    }

}
