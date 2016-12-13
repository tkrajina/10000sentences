package info.puzz.a10000sentences.tasks;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.activeandroid.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.BaseActivity;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImporterAsyncTask extends AsyncTask<String, Integer, Void> {

    private static final java.lang.String TAG = ImporterAsyncTask.class.getSimpleName();
    public static final int PROGRESS_EVERY = 50;

    @Inject
    Dao dao;

    private final BaseActivity activity;
    private final ProgressDialog progressDialog;
    private final SentenceCollection collection;
    private final CollectionReloadedListener listener;

    public interface CollectionReloadedListener {
        void onCollectionReloaded(SentenceCollection collection);
    }

    public ImporterAsyncTask(BaseActivity activity, SentenceCollection collection, CollectionReloadedListener listener) {
        Application.COMPONENT.inject(this);
        this.activity = activity;
        this.collection = collection;
        this.listener = listener;

        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(String... strings) {
        String url = strings[0];
        publishProgress(0);

        OkHttpClient httpClient = new OkHttpClient();
        Call call = httpClient.newCall(new Request.Builder().url(url).get().build());
        try {
            Response response = call.execute();

            InputStream stream = response.body().byteStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            List<Sentence> sentences = new ArrayList<>();

            int order = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                order += 1;
                sentences.add(parseSentence(order, line));
                if (sentences.size() == PROGRESS_EVERY) {
                    importSentences(sentences);
                    publishProgress(order);
                }
            }
            importSentences(sentences);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(activity, R.string.error_retrieving, Toast.LENGTH_SHORT).show();
            return null;
        }

        dao.reloadCollectionCounter(collection);

        return null;
    }

    private Sentence parseSentence(int order, String line) {
        if (StringUtils.isEmpty(line)) {
            return null;
        }
        String[] parts = line.split("\\t");
        String sentenceId = parts[0];
        String knownSentence = parts[1];
        String targetSentence = parts[2];
        return new Sentence()
                .setCollectionId(collection.getCollectionID())
                .setSentenceId(sentenceId)
                .setKnownSentence(knownSentence)
                .setTargetSentence(targetSentence)
                // Complexity === order because the sentences are ordered in the collection file
                .setComplexity(order);
    }

    private void importSentences(List<Sentence> sentences) {
        dao.importSentences(sentences);
        sentences.clear();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Integer lines = values[0];
        progressDialog.setMessage(String.format("Imported %d sentences", lines.intValue()));
        if (lines.intValue() > 0 && lines.intValue() % (PROGRESS_EVERY * 4) == 0 && listener != null) {
            listener.onCollectionReloaded(dao.reloadCollectionCounter(collection));
        }
    }

    @Override
    protected void onPostExecute(Void bytes) {
        progressDialog.hide();
        // Reenable orientation change
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        if (listener != null) {
            listener.onCollectionReloaded(dao.reloadCollectionCounter(collection));
        }
    }

}
