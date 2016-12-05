package info.puzz.a10000sentences.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.security.SecureRandom;
import java.util.Random;

import info.puzz.a10000sentences.BuildConfig;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.api.Api;
import info.puzz.a10000sentences.apimodels.InfoVO;
import info.puzz.a10000sentences.apimodels.LanguageVO;
import info.puzz.a10000sentences.apimodels.SentenceCollectionVO;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.utils.DebugUtils;
import info.puzz.a10000sentences.utils.DialogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final Random RANDOM = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes());

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) {
            // Test db locally with:
            //adb pull /sdcard/debug_10000sentences.db && sqlite3 debug_10000sentences.db
            DebugUtils.backupDatabase(this, "10000sentences.db");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNavigation();
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void reloadLanguages() {
        Call<InfoVO> info = Api.instance().info(RANDOM.nextInt());
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
                    Dao.importLanguage(language);
                }
                for (SentenceCollectionVO collectionVO : info.getSentenceCollections()) {
                    SentenceCollection col = new SentenceCollection()
                            .setCollectionID(String.format("%s-%s", collectionVO.getKnownLanguage(), collectionVO.getTargetLanguage()))
                            .setKnownLanguage(collectionVO.getKnownLanguage())
                            .setTargetLanguage(collectionVO.getTargetLanguage())
                            .setFilename(collectionVO.getFilename());
                    Dao.importCollection(col);
                }
            }

            @Override
            public void onFailure(Call<InfoVO> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        });
    }

    private void initNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_collections) {
            CollectionsActivity.start(this);
        } else if (id == R.id.nav_reload) {
            DialogUtils.showWarningDialog(this, "TODO", "");
        } else if (id == R.id.nav_stats) {
            DialogUtils.showWarningDialog(this, "TODO", "");
        } else if (id == R.id.nav_about) {
            DialogUtils.showWarningDialog(this, "TODO", "");
        } else if (id == R.id.nav_help) {
            DialogUtils.showWarningDialog(this, "TODO", "");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
