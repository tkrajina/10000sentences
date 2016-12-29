package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.activeandroid.query.From;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.adapters.SentencesAdapter;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivitySentencesBinding;
import info.puzz.a10000sentences.models.SentenceStatus;

public class SentencesActivity extends BaseActivity {

    private static final String ARG_COLLECTION_ID = "arg_collection_id";
    private static final String ARG_SENTENCE_STATUS = "arg_sentence_status";
    public static final int STATUS_ALL = -1;

    @Inject Dao dao;

    private String collectionId;
    private int sentenceStatus;

    private SentencesAdapter adapter;
    ActivitySentencesBinding binding;

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


        From select = getSql("");

        adapter = new SentencesAdapter(this, select);
        binding.sentencesList.setAdapter(adapter);

        binding.filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                final String filter = binding.filter.getText().toString();
                new AsyncTask<Void, Void, From>() {

                    @Override
                    protected From doInBackground(Void... voids) {
                        return getSql(filter);
                    }

                    @Override
                    protected void onPostExecute(From from) {
                        adapter.reset(from);
                    }
                }.execute();
            }
        });
    }

    private From getSql(String filter) {
        String[] filterParts = filter.split("\\s+");
        if (sentenceStatus == STATUS_ALL) {
            return dao.getSentencesByCollection(collectionId, filterParts[filterParts.length - 1]);
        }
        return dao.getSentencesByCollectionAndStatus(collectionId, sentenceStatus, filterParts[filterParts.length - 1]);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
