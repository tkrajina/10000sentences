package info.puzz.a10000sentences.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.adapters.AnnotationsAdapter;
import info.puzz.a10000sentences.databinding.ActivityAnnotationBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.models.Annotation;
import info.puzz.a10000sentences.utils.DialogUtils;
import info.puzz.a10000sentences.utils.ShareUtils;
import temp.DBG;

public class AnnotationActivity extends BaseActivity {

    private static final String TAG = AnnotationActivity.class.getSimpleName();

    private static final String ARG_WORD = "arg_word";
    private static final String ARG_COLLECTION_ID = "arg_collection_id";

    @Inject
    AnnotationService annotationService;

    ActivityAnnotationBinding binding;

    private AnnotationsAdapter annotationsAdapter;
    private String word;
    private String collectionId;
    private AsyncTask<Void, Void, From> reloadingAsyncTask;

    public static <T extends BaseActivity> void start(T activity, String word, String collectionId) {
        Intent intent = new Intent(activity, AnnotationActivity.class)
                .putExtra(ARG_WORD, word)
                .putExtra(ARG_COLLECTION_ID, collectionId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        word = getIntent().getStringExtra(ARG_WORD);
        collectionId = getIntent().getStringExtra(ARG_COLLECTION_ID);

        Application.COMPONENT.injectActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_annotation);
        binding.setWord(word);
        setTitle(R.string.annotation);

        From sql = new Select().from(Annotation.class).where("collection_id=?", collectionId);
        annotationsAdapter = new AnnotationsAdapter(this, sql, new AnnotationsAdapter.OnClickListener() {
            @Override
            public void onClick(Annotation annotation) {
                onAnnotationSelected(annotation);
            }
        });
        binding.annotationsList.setAdapter(annotationsAdapter);

        binding.annotation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = binding.annotation.getText().toString();
                reloadAnnotations(text);
            }
        });
    }

    private void onAnnotationSelected(final Annotation annotation) {
        DialogUtils.showYesNoButton(this, getString(R.string.add_word_to_this_annotation), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == Dialog.BUTTON_POSITIVE) {
                    save(annotation);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadAnnotations("");
        ShareUtils.copyToClipboard(this, word);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.annotation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DBG.todo("Share/copy to clipboard");
        switch (item.getItemId()) {
            case R.id.action_save:
                Annotation annotation = new Annotation();
                annotation.annotation = binding.annotation.getText().toString();
                annotation.collectionId = collectionId;
                save(annotation);
                break;
        }
        return true;
    }

    private void save(Annotation annotation) {
        annotation.annotation = StringUtils.trim(annotation.annotation);
        if (StringUtils.isEmpty(annotation.annotation)) {
            Toast.makeText(this, R.string.annotation_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        annotationService.addWordToAnnotation(annotation, binding.getWord());
        Toast.makeText(this, R.string.annotation_saved, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void reloadAnnotations(final String text) {
        if (reloadingAsyncTask != null) {
            reloadingAsyncTask.cancel(true);
        }

        reloadingAsyncTask = new AsyncTask<Void, Void, From>() {
            @Override
            protected From doInBackground(Void... voids) {
                StringBuilder likeFilter = new StringBuilder();
                for (char c : text.toCharArray()) {
                    if (Character.isLetter(c) || Character.isDigit(c) || Character.isSpaceChar(c)) {
                        likeFilter.append(c);
                    } else {
                        likeFilter.append(' ');
                    }
                }
                return new Select()
                        .from(Annotation.class)
                        .where("collection_id=? and (annotation like ? or annotation like ?)", collectionId, likeFilter.toString() + "%", "% " + likeFilter + "%");
            }

            @Override
            protected void onPostExecute(From from) {
                int size = annotationsAdapter.reloadAndGetSize(from);
                binding.existingAnnotations.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
            }
        };
        reloadingAsyncTask.execute();
    }

}
