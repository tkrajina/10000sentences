package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.adapters.AnnotationsAdapter;
import info.puzz.a10000sentences.databinding.ActivityAnnotationsBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.models.Annotation;

public class AnnotationsActivity extends BaseActivity {

    private static final String TAG = AnnotationsActivity.class.getSimpleName();

    private static final String ARG_COLLECTION_ID = "arg_collection_id";

    @Inject
    AnnotationService annotationService;

    ActivityAnnotationsBinding binding;
    private String collectionId;
    private AnnotationsAdapter annotationsAdapter;

    private AsyncTask<Void, Void, From> reloadingAsyncTask;

    public static <T extends BaseActivity> void start(T activity) {
        start(activity, null);
    }

    public static <T extends BaseActivity> void start(T activity, String collectionId) {
        Intent intent = new Intent(activity, AnnotationsActivity.class)
                .putExtra(ARG_COLLECTION_ID, collectionId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application.COMPONENT.injectActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_annotations);
        setTitle(R.string.word_annotations);

        collectionId = getIntent().getStringExtra(ARG_COLLECTION_ID);

        From sql = new Select()
                .from(Annotation.class)
                .orderBy("created desc");
        annotationsAdapter = new AnnotationsAdapter(this, sql, new AnnotationsAdapter.OnClickListener() {
            @Override
            public void onClick(Annotation annotation) {
                onAnnotationSelected(annotation);
            }
        });
        binding.annotationsList.setAdapter(annotationsAdapter);

        binding.filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                reloadAnnotations(binding.filter.getText().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        From sql = new Select()
                .from(Annotation.class);
        if (!StringUtils.isEmpty(collectionId)) {
            sql.where("collection_id=?", collectionId);
        }
        sql.orderBy("created desc");
    }

    private void onAnnotationSelected(final Annotation annotation) {
        EditAnnotationActivity.start(this, annotation.getId());
    }

    private void reloadAnnotations(final String text) {
        if (reloadingAsyncTask != null) {
            reloadingAsyncTask.cancel(true);
        }

        reloadingAsyncTask = new AsyncTask<Void, Void, From>() {
            @Override
            protected From doInBackground(Void... voids) {
                return annotationService.getAnnotationsSelectBydFilter(text);
            }

            @Override
            protected void onPostExecute(From from) {
                annotationsAdapter.reloadAndGetSize(from);
            }
        };
        reloadingAsyncTask.execute();
    }
}
