package info.puzz.a10000sentences.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import info.puzz.a10000sentences.ImporterAsyncTask;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.api.Api;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivityCollectionBinding;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceStatus;
import info.puzz.a10000sentences.utils.DialogUtils;

public class CollectionActivity extends BaseActivity implements ImporterAsyncTask.CollectionReloadedListener {

    private static final String TAG = CollectionActivity.class.getSimpleName();

    private static final String ARG_COLLECTION_ID = "arg_collection_id";

    ActivityCollectionBinding binding;
    private String collectionId;

    public static <T extends BaseActivity> void start(T activity, String collectionId) {
        Intent intent = new Intent(activity, CollectionActivity.class);
        intent.putExtra(ARG_COLLECTION_ID, collectionId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_collection);
    }

    @Override
    protected void onResume() {
        super.onResume();

        collectionId = getIntent().getStringExtra(ARG_COLLECTION_ID);
        final SentenceCollection collection = Dao.getCollection(collectionId);
        if (collection == null) {
            Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
            CollectionsActivity.start(this);
            return;
        }

        binding.setSentenceCollection(collection);
        binding.setKnownLanguage(Dao.getLanguage(collection.getKnownLanguage()));
        binding.setTargetLanguage(Dao.getLanguage(collection.getTargetLanguage()));

        binding.randomSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomSentence();
            }
        });
        binding.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadSentences();
            }
        });

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... _) {
                Dao.reloadCollectionCounter(collection);
                return null;
            }
            @Override
            protected void onPostExecute(Void _) {
                binding.randomSentence.setVisibility(collection.getCount() > 0 ? View.VISIBLE : View.GONE);
            }
        }.execute();

    }

    private void randomSentence() {
        SentenceQuizActivity.startRandom(this, binding.getSentenceCollection().getCollectionID(), null);
    }

    private void downloadSentences() {
        DialogUtils.showYesNoButton(
                this,
                getString(R.string.download_senteces),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == DialogInterface.BUTTON_POSITIVE) {
                            String filename = binding.getSentenceCollection().getFilename();

                            if (filename.indexOf("/") > 0) {
                                String[] parts = filename.split("\\/");
                                filename = parts[parts.length - 1];
                            }

                            String url = Api.BASE_URL + filename;

                            new ImporterAsyncTask(CollectionActivity.this, binding.getSentenceCollection(), CollectionActivity.this)
                                    .execute(url);
                        }
                    }
                });
    }

    @Override
    public void onCollectionReloaded() {
        Dao.reloadCollectionCounter(binding.getSentenceCollection());
        binding.notifyChange();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done_sentences:
                SentencesActivity.start(this, collectionId, SentenceStatus.DONE);
                break;
            case R.id.action_todo_sentences:
                SentencesActivity.start(this, collectionId, SentenceStatus.TODO);
                break;
            case R.id.action_ignored_sentences:
                SentencesActivity.start(this, collectionId, SentenceStatus.IGNORE);
                break;
            case R.id.action_repeat_sentences:
                SentencesActivity.start(this, collectionId, SentenceStatus.REPEAT);
                break;
        }
        return true;
    }
}
