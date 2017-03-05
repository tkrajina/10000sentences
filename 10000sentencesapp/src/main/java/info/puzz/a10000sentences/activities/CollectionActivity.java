package info.puzz.a10000sentences.activities;

import android.app.AlertDialog;
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

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.api.Api;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivityCollectionBinding;
import info.puzz.a10000sentences.logic.SentenceCollectionsService;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceStatus;
import info.puzz.a10000sentences.tasks.ImporterAsyncTask;
import info.puzz.a10000sentences.utils.DialogUtils;
import info.puzz.a10000sentences.utils.SleepUtils;

public class CollectionActivity extends BaseActivity implements ImporterAsyncTask.CollectionReloadedListener {

    private static final String TAG = CollectionActivity.class.getSimpleName();

    private static final String ARG_COLLECTION_ID = "arg_collection_id";

    @Inject
    Dao dao;

    @Inject
    SentenceCollectionsService sentenceCollectionsService;

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

        Application.COMPONENT.injectActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_collection);

        collectionId = getIntent().getStringExtra(ARG_COLLECTION_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SentenceCollection collection = dao.getCollection(collectionId);
        if (collection == null) {
            Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
            CollectionsActivity.startDefaultCollections(this);
            return;
        }

        Language knownLanguage = sentenceCollectionsService.getLanguageOrUnknown(collection.getKnownLanguage());
        Language unknownLanguage = sentenceCollectionsService.getLanguageOrUnknown(collection.getTargetLanguage());

        binding.setSentenceCollection(collection);
        binding.setKnownLanguage(knownLanguage);
        binding.setTargetLanguage(unknownLanguage);

        setTitle(binding.getTargetLanguage().name);

        binding.randomKnownSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SentenceQuizActivity.startRandom(CollectionActivity.this, dao, sentenceCollectionsService, binding.getSentenceCollection().getCollectionID(), SentenceQuizActivity.Type.ONLY_KNOWN, null);
            }
        });
        binding.randomSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SentenceQuizActivity.startRandom(CollectionActivity.this, dao, sentenceCollectionsService, binding.getSentenceCollection().getCollectionID(), SentenceQuizActivity.Type.KNOWN_AND_UNKNOWN, null);
            }
        });
        binding.annotations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnnotationsActivity.start(CollectionActivity.this, collectionId);
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
                dao.reloadCollectionCounter(collection);
                return null;
            }
            @Override
            protected void onPostExecute(Void _) {
                binding.randomSentence.setVisibility(collection.count > 0 ? View.VISIBLE : View.GONE);
                binding.randomKnownSentence.setVisibility(collection.doneCount > 0 ? View.VISIBLE : View.GONE);
                binding.allSentences.setVisibility(collection.count > 0 ? View.VISIBLE : View.GONE);
                binding.annotations.setVisibility(collection.annotationCount > 0 ? View.VISIBLE : View.GONE);
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
    public void onCollectionReloaded(SentenceCollection collection) {
        binding.notifyChange();
        binding.randomSentence.setVisibility(View.VISIBLE);
        binding.randomKnownSentence.setVisibility(binding.getSentenceCollection().getDoneCount() > 0 ? View.VISIBLE : View.GONE);
        binding.allSentences.setVisibility(View.VISIBLE);
        binding.setSentenceCollection(collection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection, menu);

        menu.findItem(R.id.action_remove_collecition).setVisible(binding.getSentenceCollection().count > 0);
        menu.findItem(R.id.action_redownload).setVisible(binding.getSentenceCollection().count > 0);

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
            case R.id.action_skip_unskip:
                skipUnskipSentences();
                break;
            case R.id.action_redownload:
                downloadSentences();
                break;
        }
        return true;
    }

    private void skipUnskipSentences() {

        String[] options = new String[]{
                getString(R.string.skip_n_sentences, 50),
                getString(R.string.skip_n_sentences, 100),
                getString(R.string.unskip_n_sentences, 50),
                getString(R.string.unskip_n_sentences, 100),
                getString(R.string.cancel),
        };
        final int[] optionsSkipNo = new int[]{50, 100, -50, -100, 0};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_text);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int skipNo = optionsSkipNo[i];
                if (skipNo > 0) {
                    sentenceCollectionsService.skipSentences(binding.getSentenceCollection().collectionID, skipNo);
                } else if (skipNo < 0) {
                    sentenceCollectionsService.unskipSentences(binding.getSentenceCollection().collectionID, skipNo);
                } else {
                    return;
                }
                new AsyncTask<Void, Void, SentenceCollection>() {
                    @Override
                    protected SentenceCollection doInBackground(Void... voids) {
                        return dao.reloadCollectionCounter(binding.getSentenceCollection());
                    }

                    @Override
                    protected void onPostExecute(SentenceCollection sentenceCollection) {
                        binding.setSentenceCollection(sentenceCollection);
                    }
                }.execute();
            }
        });
        builder.show();
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
                                dao.removeCollectionSentences(binding.getSentenceCollection());
                                Toast.makeText(CollectionActivity.this, R.string.collection_deleted, Toast.LENGTH_SHORT).show();
                                CollectionsActivity.startDefaultCollections(CollectionActivity.this);
                            } else {
                                Toast.makeText(CollectionActivity.this, R.string.not_deleted, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
