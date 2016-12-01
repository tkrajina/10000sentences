package info.puzz.a10000sentences;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;

import com.activeandroid.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import info.puzz.a10000sentences.activities.BaseActivity;
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

        OkHttpClient httpClient = new OkHttpClient();
        Call call = httpClient.newCall(new Request.Builder().url(url).get().build());
        try {
            Response response = call.execute();

            InputStream stream = response.body().byteStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            int n = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                n += 1;
                if (n % 1000 == 0) {
                    publishProgress(n);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            DBG.todo(e);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Integer lines = values[0];
        progressDialog.setMessage(String.valueOf(lines));
    }

    @Override
    protected void onPostExecute(Void bytes) {
        progressDialog.hide();
        // Reenable orientation change
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

}
