package info.puzz.a10000sentences.activities.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.From;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.BaseActivity;
import info.puzz.a10000sentences.databinding.AnnotationBinding;
import info.puzz.a10000sentences.services.AnnotationService;
import info.puzz.a10000sentences.models.Annotation;

public class AnnotationsAdapter extends LoadMoreAdapter<Annotation> {

    public interface OnClickListener {
        void onClick(Annotation annotation);
    }

    private final OnClickListener listener;

    @Inject
    AnnotationService annotationService;

    public <T extends BaseActivity> AnnotationsAdapter(T activity, From select, OnClickListener listener) {
        super(activity, R.layout.sentence_collection, select);
        Application.COMPONENT.inject(this);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        AnnotationBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.annotation, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        final Annotation annotation = getItemAndLoadMoreIfNeeded(position);
        binding.setAnnotation(annotation);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(annotation);
                }
            }
        });

        return binding.getRoot();
    }

    public int reloadAndGetSize(From select) {
        return reset(select);
    }
}
