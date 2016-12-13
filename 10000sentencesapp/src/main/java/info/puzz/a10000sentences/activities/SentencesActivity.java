package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivitySentencesBinding;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceStatus;

public class SentencesActivity extends BaseActivity implements BaseActivity.OnCollectionsReloaded {

    private static final String ARG_COLLECTION_ID = "arg_collection_id";
    private static final String ARG_SENTENCE_STATUS = "arg_sentence_status";
    public static final int STATUS_ALL = -1;

    @Inject Dao dao;

    ActivitySentencesBinding binding;

    private String collectionId;
    private int sentenceStatus;

    public static <T extends BaseActivity> void start(T activity, String collectionID, SentenceStatus status) {
        Intent intent = new Intent(activity, SentencesActivity.class)
                .putExtra(ARG_COLLECTION_ID, collectionID)
                .putExtra(ARG_SENTENCE_STATUS, status == null ? STATUS_ALL : status.getStatus());
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.COMPONENT.injectActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sentences);

        collectionId = getIntent().getStringExtra(ARG_COLLECTION_ID);
        sentenceStatus = getIntent().getIntExtra(ARG_SENTENCE_STATUS, SentenceStatus.TODO.getStatus());

        setTitle(R.string.sentences);

        if (dao.getLanguages().size() == 0) {
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
        From select = new Select()
                .from(Sentence.class);
        if (sentenceStatus == STATUS_ALL) {
            select.where("collection_id=?", collectionId);
        } else {
            select.where("collection_id=? and status=?", collectionId, sentenceStatus);
        }
        select.orderBy("complexity");
        binding.sentencesList.setAdapter(new SentencesAdapter(this, select));
    }

}
