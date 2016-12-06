package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.activeandroid.query.Select;

import java.util.List;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivityCollectionsBinding;
import info.puzz.a10000sentences.databinding.ActivitySentencesBinding;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;

public class SentencesActivity extends BaseActivity implements BaseActivity.OnCollectionsReloaded {

    ActivitySentencesBinding binding;

    public static <T extends BaseActivity> void start(T activity) {
        Intent intent = new Intent(activity, SentencesActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sentences);

        if (Dao.getLanguages().size() == 0) {
            reloadLanguages();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        reloadCollections();
    }

    @Override
    public void onCollectionsReloaded() {
        reloadCollections();
    }

    private void reloadCollections() {
        binding.sentencesList.setAdapter(new SentencesAdapter(this, new Select().from(Sentence.class)));
    }

}
