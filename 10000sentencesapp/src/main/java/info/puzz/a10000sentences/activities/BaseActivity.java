package info.puzz.a10000sentences.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
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
import android.view.SubMenu;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final Random RANDOM = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes());

    private static final String TAG = BaseActivity.class.getSimpleName();

    private boolean collectionNavigationSet;

    @Inject
    Dao dao;

    public interface OnCollectionsReloaded {
        public void onCollectionsReloaded();
    }

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
        Application.COMPONENT.inject(this);
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
        if (!isNetworkAvailable()) {
            Toast.makeText(this, getString(R.string.no_newtork), Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        progressDialog.show();

        Call<InfoVO> info = Api.instance().info(RANDOM.nextInt());
        info.enqueue(new Callback<InfoVO>() {
            @Override
            public void onResponse(Call<InfoVO> call, Response<InfoVO> response) {
                try {
                    _onResponse(response);
                    Toast.makeText(BaseActivity.this, getString(R.string.imported), Toast.LENGTH_SHORT).show();
                } finally {
                    progressDialog.hide();
                }
            }

            private void _onResponse(Response<InfoVO> response) {
                Log.i(TAG, String.valueOf(response.body()));
                InfoVO info = response.body();
                for (LanguageVO languageVO : info.getLanguages()) {
                    Language language = new Language()
                            .setLanguageId(languageVO.getAbbrev())
                            .setFamily(languageVO.getFamily())
                            .setName(languageVO.getName())
                            .setNativeName(languageVO.getNativeName())
                            .setRightToLeft(languageVO.isRightToLeft());
                    dao.importLanguage(language);
                }
                for (SentenceCollectionVO collectionVO : info.getSentenceCollections()) {
                    SentenceCollection col = new SentenceCollection()
                            .setCollectionID(String.format("%s-%s", collectionVO.getKnownLanguage(), collectionVO.getTargetLanguage()))
                            .setKnownLanguage(collectionVO.getKnownLanguage())
                            .setTargetLanguage(collectionVO.getTargetLanguage())
                            .setFilename(collectionVO.getFilename());
                    dao.importCollection(col);
                }

                if (BaseActivity.this instanceof OnCollectionsReloaded) {
                    ((OnCollectionsReloaded) BaseActivity.this).onCollectionsReloaded();
                }
            }

            @Override
            public void onFailure(Call<InfoVO> call, Throwable t) {
                progressDialog.hide();
                Log.e(TAG, t.getMessage(), t);
                Toast.makeText(BaseActivity.this, getString(R.string.error_retrieving), Toast.LENGTH_SHORT).show();
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

        setupMenuIcon(navigationView, R.id.nav_collections, FontAwesomeIcons.fa_list);
        setupMenuIcon(navigationView, R.id.nav_reload, FontAwesomeIcons.fa_refresh);
        setupMenuIcon(navigationView, R.id.nav_stats, FontAwesomeIcons.fa_line_chart);
        setupMenuIcon(navigationView, R.id.nav_settings, FontAwesomeIcons.fa_toggle_on);
        setupMenuIcon(navigationView, R.id.nav_about, FontAwesomeIcons.fa_info);
        setupMenuIcon(navigationView, R.id.nav_help, FontAwesomeIcons.fa_question);

        setupCollectionsNavigation(navigationView);
    }

    private void setupCollectionsNavigation(NavigationView navigationView) {
        if (collectionNavigationSet) {
            return;
        }
        collectionNavigationSet = true;

        SubMenu submenu = navigationView.getMenu().addSubMenu(R.string.downloaded_colections);
        Map<String, Language> languages = dao.getLanguagesByLanguageID();

        for (final SentenceCollection collection : dao.getCollections()) {
            Language language = languages.get(collection.targetLanguage);
            if (language == null) {
                continue;
            }
            if (collection.todoCount == 0) {
                continue;
            }
            MenuItem menu = submenu.add(language.name).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    CollectionActivity.start(BaseActivity.this, collection.collectionID);
                    return true;
                }
            });
            setupMenuIcon(menu, FontAwesomeIcons.fa_language);
        }
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
            reloadLanguages();
        } else if (id == R.id.nav_stats) {
            StatsActivity.start(this);
        } else if (id == R.id.nav_settings) {
            SettingsActivity.start(this);
        } else if (id == R.id.nav_about) {
            try {
                PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), 0);
                HtmlActivity.start(this, getString(R.string.about), getString(R.string.info_contents, info.versionName, String.valueOf(info.versionCode)), false);
            } catch (Exception e) {
                Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage(), e);
            }
        } else if (id == R.id.nav_help) {
            HtmlActivity.start(this, getString(R.string.help), getString(R.string.help_contents), true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupMenuIcon(NavigationView navigationView, int menuResId, FontAwesomeIcons icon) {
        MenuItem menuItem = navigationView.getMenu().findItem(menuResId);
        setupMenuIcon(menuItem, icon);
    }

    private void setupMenuIcon(MenuItem menuItem, FontAwesomeIcons icon) {
        menuItem.setIcon(new IconDrawable(this, icon).colorRes(R.color.colorPrimary).actionBarSize());
    }
}
