package info.puzz.a10000sentences.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Select;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.adapters.WordsAdapter;
import info.puzz.a10000sentences.databinding.ActivityEditAnnotationBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.models.Annotation;
import info.puzz.a10000sentences.models.WordAnnotation;
import info.puzz.a10000sentences.utils.DialogUtils;

public class EditAnnotationActivity extends BaseActivity {

    private static final String TAG = EditAnnotationActivity.class.getSimpleName();

    private static final String ARG_ANNOTATION_ID = "arg_annotation_id";

    @Inject
    AnnotationService annotationService;

    ActivityEditAnnotationBinding binding;
    private long annotationId;

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

        annotationId = getIntent().getLongExtra(ARG_ANNOTATION_ID, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Annotation annotation = Annotation.load(Annotation.class, annotationId);
        if (annotation == null) {
            Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
            CollectionsActivity.start(this);
            return;
        }

        binding.setAnnotation(annotation);

        reloadWords();
    }

    private void reloadWords() {
        List<WordAnnotation> wordAnnotations = new Select()
                .from(WordAnnotation.class)
                .where("annotation_id=?", annotationId)
                .execute();

        WordsAdapter adapter = new WordsAdapter(this, binding.getAnnotation(), wordAnnotations);
        binding.annotationsList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_annotation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
            case R.id.action_add_word:
                addWord();
                break;
            case R.id.action_delete:
                delete();
                break;
        }
        return true;
    }

    private void addWord() {
        DialogUtils.showInputDialog(this, getString(R.string.add_word), new DialogUtils.OnInputDialogClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, String value) {
                value = StringUtils.trim(value);
                if (which == Dialog.BUTTON_POSITIVE && !StringUtils.isEmpty(value)) {
                    annotationService.addWordToAnnotation(binding.getAnnotation(), value);
                    reloadWords();
                }
            }
        });
    }

    private void delete() {
        annotationService.delete(this.binding.getAnnotation());
        Toast.makeText(this, R.string.annotation_deleted, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void save() {
        Annotation annotation = Annotation.load(Annotation.class, binding.getAnnotation().getId());
        annotation.annotation = StringUtils.trim(binding.annotationText.getText().toString());
        if (StringUtils.isEmpty(annotation.annotation)) {
            Toast.makeText(this, R.string.annotation_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        annotation.save();
        Toast.makeText(this, R.string.annotation_saved, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
}
