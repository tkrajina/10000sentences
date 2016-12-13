package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.databinding.ActivityAnnotationBinding;
import info.puzz.a10000sentences.logic.AnnotationService;

public class AnnotationActivity extends BaseActivity {

    private static final String TAG = AnnotationActivity.class.getSimpleName();

    ActivityAnnotationBinding binding;

    @Inject
    AnnotationService annotationService;

    public static <T extends BaseActivity> void start(T activity) {
        Intent intent = new Intent(activity, AnnotationActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application.COMPONENT.injectActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_annotation);
        setTitle(R.string.annotation);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
