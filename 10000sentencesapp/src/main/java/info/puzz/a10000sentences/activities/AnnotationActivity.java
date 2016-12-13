package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.activeandroid.query.Select;

import java.util.ArrayList;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.adapters.AnnotationsAdapter;
import info.puzz.a10000sentences.databinding.ActivityAnnotationBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.models.Annotation;
import info.puzz.a10000sentences.models.SentenceCollection;
import temp.DBG;

public class AnnotationActivity extends BaseActivity {

    private static final String TAG = AnnotationActivity.class.getSimpleName();

    private static final String ARG_WORD = "arg_word";
    private static final String ARG_COLLECTION_ID = "arg_collection_id";

    @Inject
    AnnotationService annotationService;

    ActivityAnnotationBinding binding;

    private AnnotationsAdapter annotationsAdapter;

    public static <T extends BaseActivity> void start(T activity, String word, SentenceCollection collection) {
        Intent intent = new Intent(activity, AnnotationActivity.class)
                .putExtra(ARG_WORD, word)
                .putExtra(ARG_COLLECTION_ID, collection.getCollectionID());
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String word = getIntent().getStringExtra(ARG_WORD);
        String sentenceId = getIntent().getStringExtra(ARG_COLLECTION_ID);

        Application.COMPONENT.injectActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_annotation);
        setTitle(R.string.annotation);

        annotationsAdapter = new AnnotationsAdapter(this, new Select().from(Annotation.class).where("collection_id=?", sentenceId));
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void reloadAnnotations(String text) {
        DBG.todo();
        //annotationsAdapter.reload(text);
    }

}
