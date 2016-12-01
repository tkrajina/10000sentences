package info.puzz.a10000sentences.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import info.puzz.a10000sentences.api.Api;
import info.puzz.a10000sentences.apimodels.InfoVO;
import info.puzz.a10000sentences.apimodels.LanguageVO;
import info.puzz.a10000sentences.apimodels.SentenceCollectionVO;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.SentenceCollection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void reloadLanguages() {
        Call<InfoVO> info = Api.instance().info();
        info.enqueue(new Callback<InfoVO>() {
            @Override
            public void onResponse(Call<InfoVO> call, Response<InfoVO> response) {
                Log.i(TAG, String.valueOf(response.body()));
                InfoVO info = response.body();
                for (LanguageVO languageVO : info.getLanguages()) {
                    Language language = new Language()
                            .setLanguageId(languageVO.getAbbrev())
                            .setFamily(languageVO.getFamily())
                            .setName(languageVO.getName())
                            .setNativeName(languageVO.getNativeName())
                            .setRightToLeft(languageVO.isRightToLeft());
                    Dao.saveLanguage(language);
                }
                for (SentenceCollectionVO collectionVO : info.getSentenceCollections()) {
                    SentenceCollection col = new SentenceCollection()
                            .setCollectionID(String.format("%s-%s", collectionVO.getKnownLanguage(), collectionVO.getTargetLanguage()))
                            .setKnownLanguage(collectionVO.getKnownLanguage())
                            .setTargetLanguage(collectionVO.getTargetLanguage())
                            .setFilename(collectionVO.getFilename());
                    Dao.saveCollection(col);
                }
            }

            @Override
            public void onFailure(Call<InfoVO> call, Throwable t) {
                Log.i(TAG, t.getMessage(), t);
            }
        });
    }
}
