package info.puzz.a10000sentences;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import info.puzz.a10000sentences.activities.BaseActivity;
import info.puzz.a10000sentences.activities.SentenceQuizActivity;
import info.puzz.a10000sentences.api.Api;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivityCollectionBinding;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.utils.DialogUtils;
import temp.DBG;

public class CollectionActivity extends BaseActivity {

    private static final String TAG = CollectionActivity.class.getSimpleName();

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

        DBG.todo("Move this to a better place");
        int rows = SQLiteUtils.intQuery("select count(*) from sentence", null);
        collection.count = rows;
        collection.save();

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
    }

    private void randomSentence() {
        Sentence randomSentence = new Select()
                .from(Sentence.class)
                .where("collection_id = ?", binding.getSentenceCollection().getCollectionID())
                .orderBy("random()")
                .executeSingle();
        SentenceQuizActivity.start(this, randomSentence.getSentenceId());
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

                            new DownloaderAsyncTask(CollectionActivity.this, binding.getSentenceCollection()).execute(url);
                        }
                    }
                });
    }
}
