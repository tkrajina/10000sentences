package info.puzz.a10000sentences.activities;

import android.app.Dialog;
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
import info.puzz.a10000sentences.utils.SleepUtils;

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

        binding.randomKnownSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SentenceQuizActivity.startRandom(CollectionActivity.this, binding.getSentenceCollection().getCollectionID(), SentenceQuizActivity.Type.ONLY_KNOWN, null);
            }
        });
        binding.randomSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SentenceQuizActivity.startRandom(CollectionActivity.this, binding.getSentenceCollection().getCollectionID(), SentenceQuizActivity.Type.KNOWN_AND_UNKNOWN, null);
            }
        });
        binding.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadSentences();
            }
        });
        binding.allSentences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allSentences();
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
                binding.randomKnownSentence.setVisibility(collection.doneCount > 0 ? View.VISIBLE : View.GONE);
                binding.allSentences.setVisibility(collection.getCount() > 0 ? View.VISIBLE : View.GONE);
            }
        }.execute();

    }

    private void allSentences() {
        SentencesActivity.start(this, binding.getSentenceCollection().collectionID, null);
    }

    private void downloadSentences() {
        DialogUtils.showYesNoButton(
                this,
                getString(R.string.download_senteces),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == DialogInterface.BUTTON_POSITIVE) {
                            SleepUtils.disableSleep(CollectionActivity.this);
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
        binding.randomSentence.setVisibility(View.VISIBLE);
        binding.randomKnownSentence.setVisibility(binding.getSentenceCollection().getDoneCount() > 0 ? View.VISIBLE : View.GONE);
        binding.allSentences.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection, menu);

        menu.findItem(R.id.action_remove_collecition).setVisible(binding.getSentenceCollection().count > 0);

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
            case R.id.action_remove_collecition:
                removeCollection();
                break;
        }
        return true;
    }

    private void removeCollection() {
        DialogUtils.showInputDialog(
                this,
                getString(R.string.really_delete_collection, binding.getSentenceCollection().getCollectionID()),
                new DialogUtils.OnInputDialogClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which, String value) {
                        if (which == Dialog.BUTTON_POSITIVE) {
                            if (binding.getSentenceCollection().getCollectionID().equalsIgnoreCase(value)) {
                                Dao.removeCollectionSentences(binding.getSentenceCollection());
                                Toast.makeText(CollectionActivity.this, R.string.collection_deleted, Toast.LENGTH_SHORT).show();
                                CollectionsActivity.start(CollectionActivity.this);
                            } else {
                                Toast.makeText(CollectionActivity.this, R.string.not_deleted, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
