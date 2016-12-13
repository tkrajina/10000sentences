package info.puzz.a10000sentences.activities.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.activeandroid.query.From;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.BaseActivity;
import info.puzz.a10000sentences.databinding.AnnotationBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.models.Annotation;

public class AnnotationsAdapter extends ArrayAdapter<Annotation> {

    public static final int PAGE_SIZE = 100;

    @Inject
    AnnotationService annotationService;

    public <T extends BaseActivity> AnnotationsAdapter(T activity, From select) {
        super(activity, R.layout.sentence_collection, select.limit(PAGE_SIZE).<Annotation>execute());
        Application.COMPONENT.inject(this);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        AnnotationBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.annotation, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        final Annotation annotation = getItem(position);
        binding.setAnnotation(annotation);

        return binding.getRoot();
    }

    public void reload(From select) {
        clear();
        addAll(select.limit(PAGE_SIZE).<Annotation>execute());
        notifyDataSetChanged();
    }
}
