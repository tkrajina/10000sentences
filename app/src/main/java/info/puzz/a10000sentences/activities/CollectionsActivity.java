package info.puzz.a10000sentences.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import info.puzz.a10000sentences.CollectionActivity;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivityCollectionsBinding;
import info.puzz.a10000sentences.models.SentenceCollection;
import temp.DBG;

public class CollectionsActivity extends BaseActivity {

    private static final String TAG = CollectionActivity.class.getSimpleName();

    ActivityCollectionsBinding binding;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_collections);

        List<SentenceCollection> cols = Dao.getCollections();

        reloadLanguages();

        DBG.todo("Check and possibli download the TTS data, more on http://android-developers.blogspot.hr/2009/09/introduction-to-text-to-speech-in.html");
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                Log.i(TAG, "TTS initialized");
                speech();
            }
        });

        binding.collectionsList.setAdapter(new CollectionsAdapter(this, cols));
    }

    private void speech() {
        tts.setLanguage(Locale.US);
        tts.speak("Text to say aloud", TextToSpeech.QUEUE_ADD, null);
    }
}
