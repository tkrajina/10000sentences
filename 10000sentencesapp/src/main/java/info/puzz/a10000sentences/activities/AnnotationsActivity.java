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
import android.widget.Toast;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.adapters.AnnotationsAdapter;
import info.puzz.a10000sentences.databinding.ActivityAnnotationBinding;
import info.puzz.a10000sentences.databinding.ActivityAnnotationsBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.models.Annotation;
import info.puzz.a10000sentences.utils.DialogUtils;
import temp.DBG;

public class AnnotationsActivity extends BaseActivity {

    private static final String TAG = AnnotationsActivity.class.getSimpleName();

    private static final String ARG_COLLECTION_ID = "arg_collection_id";

    @Inject
    AnnotationService annotationService;

    ActivityAnnotationsBinding binding;
    private String collectionId;
    private AnnotationsAdapter annotationsAdapter;

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
        setTitle(R.string.annotations);

        collectionId = getIntent().getStringExtra(ARG_COLLECTION_ID);

    }

    @Override
    protected void onResume() {
        super.onResume();

        From sql = new Select()
                .from(Annotation.class);
        if (!StringUtils.isEmpty(collectionId)) {
            sql.where("collection_id=?", collectionId);
        }

        annotationsAdapter = new AnnotationsAdapter(this, sql, new AnnotationsAdapter.OnClickListener() {
            @Override
            public void onClick(Annotation annotation) {
                onAnnotationSelected(annotation);
            }
        });
        binding.annotationsList.setAdapter(annotationsAdapter);
    }

    private void onAnnotationSelected(final Annotation annotation) {
        EditAnnotationActivity.start(this, annotation.getId());
    }

}
