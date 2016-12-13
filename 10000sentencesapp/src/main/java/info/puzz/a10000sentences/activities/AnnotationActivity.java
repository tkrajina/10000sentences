package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Select;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.adapters.AnnotationsAdapter;
import info.puzz.a10000sentences.databinding.ActivityAnnotationBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.models.Annotation;
import temp.DBG;

public class AnnotationActivity extends BaseActivity {

    private static final String TAG = AnnotationActivity.class.getSimpleName();

    private static final String ARG_WORD = "arg_word";
    private static final String ARG_COLLECTION_ID = "arg_collection_id";

    @Inject
    AnnotationService annotationService;

    ActivityAnnotationBinding binding;

    private AnnotationsAdapter annotationsAdapter;

    public static <T extends BaseActivity> void start(T activity, String word, String collectionId) {
        Intent intent = new Intent(activity, AnnotationActivity.class)
                .putExtra(ARG_WORD, word)
                .putExtra(ARG_COLLECTION_ID, collectionId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String word = getIntent().getStringExtra(ARG_WORD);
        String collectionId = getIntent().getStringExtra(ARG_COLLECTION_ID);

        Application.COMPONENT.injectActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_annotation);
        binding.setWord(word);
        setTitle(R.string.annotation);

        annotationsAdapter = new AnnotationsAdapter(this, new Select().from(Annotation.class).where("collection_id=?", collectionId));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.annotation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
        }
        return true;
    }

    private void save() {
        DBG.todo();
        annotationService.addWordToAnnotation(null, binding.getWord());
        Toast.makeText(this, R.string.annotation_saved, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void reloadAnnotations(String text) {
        DBG.todo();
        //annotationsAdapter.reload(text);
    }

}
