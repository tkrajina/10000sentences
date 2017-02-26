package info.puzz.a10000sentences.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.Constants;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivityImportTextBinding;
import info.puzz.a10000sentences.databinding.ActivitySentenceQuizBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.logic.SentenceCollectionsService;
import info.puzz.a10000sentences.models.Annotation;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceStatus;
import info.puzz.a10000sentences.models.WordAnnotation;
import info.puzz.a10000sentences.utils.ShareUtils;
import info.puzz.a10000sentences.utils.SleepUtils;
import info.puzz.a10000sentences.utils.Speech;
import info.puzz.a10000sentences.utils.TatoebaUtils;
import info.puzz.a10000sentences.utils.TranslateUtils;
import info.puzz.a10000sentences.utils.WordChunk;
import info.puzz.a10000sentences.utils.WordChunkUtils;

public class ImportTextActivity extends BaseActivity {

    private static final String TAG = ImportTextActivity.class.getSimpleName();

    private ActivityImportTextBinding binding;

    public static <T extends BaseActivity> void startSentence(T activity) {
        Intent intent = new Intent(activity, ImportTextActivity.class)
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.COMPONENT.injectActivity(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_import_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.sentence, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    }

}
