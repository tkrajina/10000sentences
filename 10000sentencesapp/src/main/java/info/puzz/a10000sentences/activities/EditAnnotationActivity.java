package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.List;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.adapters.AnnotationsAdapter;
import info.puzz.a10000sentences.databinding.ActivityAnnotationsBinding;
import info.puzz.a10000sentences.databinding.ActivityEditAnnotationBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.models.Annotation;
import info.puzz.a10000sentences.models.WordAnnotation;
import temp.DBG;

public class EditAnnotationActivity extends BaseActivity {

    private static final String TAG = EditAnnotationActivity.class.getSimpleName();

    private static final String ARG_ANNOTATION_ID = "arg_annotation_id";

    @Inject
    AnnotationService annotationService;

    ActivityEditAnnotationBinding binding;

    public static <T extends BaseActivity> void start(T activity, long annotationId) {
        Intent intent = new Intent(activity, EditAnnotationActivity.class)
                .putExtra(ARG_ANNOTATION_ID, annotationId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application.COMPONENT.injectActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_annotation);
        setTitle(R.string.annotations);

        long annotationId = getIntent().getLongExtra(ARG_ANNOTATION_ID, -1);
        if (annotationId < 0) {
            DBG.todo();
        }

        Annotation annotation = Annotation.load(Annotation.class, annotationId);

        binding.setAnnotation(annotation);

        List<WordAnnotation> wordAnnotations = new Select()
                .from(WordAnnotation.class)
                .where("annotation_id=?", annotationId)
                .execute();
        String[] words = new String[wordAnnotations.size()];
        for (int i = 0; i < wordAnnotations.size(); i++) {
            words[i] = wordAnnotations.get(i).word;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, words);
        binding.annotationsList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
