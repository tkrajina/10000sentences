package info.puzz.a10000sentences;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;

import com.activeandroid.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import info.puzz.a10000sentences.activities.BaseActivity;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.models.Sentence;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import temp.DBG;

public class DownloaderAsyncTask extends AsyncTask<String, Integer, Void> {

    private static final java.lang.String TAG = DownloaderAsyncTask.class.getSimpleName();

    private final BaseActivity activity;
    private final ProgressDialog progressDialog;

    public DownloaderAsyncTask(BaseActivity activity) {
        this.activity = activity;

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

            int n = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                n += 1;
                sentences.add(parseSentence(line));
                if (sentences.size() == 50) {
                    importSentences(sentences);
                    publishProgress(n);
                }
            }
            importSentences(sentences);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            DBG.todo(e);
        }

        return null;
    }

    private Sentence parseSentence(String line) {
        if (StringUtils.isEmpty(line)) {
            return null;
        }
        String[] parts = line.split("\\t");
        String sentenceId = parts[0];
        String knownSentence = parts[1];
        String targetSentence = parts[2];
        return new Sentence()
                .setSentenceId(sentenceId)
                .setKnownSentence(knownSentence)
                .setTargetSentence(targetSentence);
    }

    private void importSentences(List<Sentence> sentences) {
        Dao.saveSentences(sentences);
        sentences.clear();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Integer lines = values[0];
        progressDialog.setMessage(String.format("Imported %d sentences", lines.intValue()));
    }

    @Override
    protected void onPostExecute(Void bytes) {
        progressDialog.hide();
        // Reenable orientation change
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

}
