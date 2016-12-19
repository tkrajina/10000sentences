package info.puzz.a10000sentences.utils;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import info.puzz.a10000sentences.Preferences;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.models.Language;
import lombok.Getter;

public class Speech {

    private static final String TAG = Speech.class.getSimpleName();

    private TextToSpeech tts;

    private final Context context;
    private final Locale locale;
    private final boolean languageFound;
    private final boolean enabled;

    private final Set<Integer> toastMessagesShown = new HashSet<>();

    @Getter
    private boolean initialized = false;

    public Speech(Context context, Language language) {
        this.context = context;
        this.locale = findLocale(language);
        this.languageFound = locale != null;
        this.enabled = Preferences.isUseTTS(context);
        if (!this.enabled) {
            return;
        }
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                Speech.this.initialized = true;
            }
        });
        tts.setPitch(1);
        tts.setSpeechRate(0.75F);
        tts.setLanguage(locale);
    }

    private static Locale findLocale(Language language) {
        return findLocale(language.languageId);
    }

    private static Locale findLocale(String languageID) {
        for (Locale locale : Locale.getAvailableLocales()) {
            if (org.apache.commons.lang3.StringUtils.isEmpty(locale.getCountry()) && StringUtils.equals(locale.getLanguage(), languageID)) {
                return locale;
            }
        }
        for (Locale locale : Locale.getAvailableLocales()) {
            if (StringUtils.equals(locale.getLanguage(), languageID)) {
                return locale;
            }
        }
        return null;
    }

    public void speech(String speech) {
        if (!enabled) {
            return;
        }
        if (!languageFound) {
            showToastOnce(R.string.tts_language_not_available);
            return;
        }
        if (!initialized) {
            showToastOnce(R.string.tts_not_inititialized);
            return;
        }

/*        Bundle bundle = new Bundle();
        bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1F);
        tts.speak(speech, TextToSpeech.QUEUE_FLUSH, bundle, null);*/
        tts.setLanguage(locale);
        tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void showToastOnce(int resId) {
        if (toastMessagesShown.contains(resId)) {
            return;
        }
        toastMessagesShown.add(resId);
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public void shutdown() {
        if (tts != null) {
            tts.shutdown();
        }
    }

    public static void main(String[] args) {
        System.out.println(findLocale("de"));
    }

}
