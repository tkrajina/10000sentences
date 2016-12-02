package info.puzz.a10000sentences;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import info.puzz.a10000sentences.activities.BaseActivity;
import info.puzz.a10000sentences.api.Api;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivityCollectionBinding;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.utils.DialogUtils;
import temp.DBG;

public class CollectionActivity extends BaseActivity {

    private static final String ARG_COLLECTION_ID = "arg_collection_id";

    ActivityCollectionBinding binding;

    public static <T extends BaseActivity> void start(T activity, String collectionId) {
        Intent intent = new Intent(activity, CollectionActivity.class);
        intent.putExtra(ARG_COLLECTION_ID, collectionId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_collection);

        String collectionId = getIntent().getStringExtra(ARG_COLLECTION_ID);
        SentenceCollection collection = Dao.getCollection(collectionId);
        if (collection == null) {
            DBG.todo();
        }

        binding.setCollection(collection);
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
    }

    private void randomSentence() {
        DialogUtils.showWarningDialog(this, "bu", "be");
    }

    private void downloadSentences() {
        DialogUtils.showYesNoButton(
                this,
                getString(R.string.download_senteces),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == DialogInterface.BUTTON_POSITIVE) {
                            String filename = binding.getCollection().getFilename();

                            if (filename.indexOf("/") > 0) {
                                String[] parts = filename.split("\\/");
                                filename = parts[parts.length - 1];
                            }

                            String url = Api.BASE_URL + filename;

                            new DownloaderAsyncTask(CollectionActivity.this).execute(url);
                        }
                    }
                });
    }
}
