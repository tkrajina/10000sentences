package info.puzz.a10000sentences.activities.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import javax.inject.Inject;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.BaseActivity;
import info.puzz.a10000sentences.databinding.AnnotationWordBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.models.WordAnnotation;

public class WordsAdapter extends ArrayAdapter<WordAnnotation> {

    public interface OnClickListener {
        void onClick(WordAnnotation annotation);
    }

    private final OnClickListener listener;

    @Inject
    AnnotationService annotationService;

    public <T extends BaseActivity> WordsAdapter(T activity, List<WordAnnotation> words, OnClickListener listener) {
        super(activity, R.layout.sentence_collection, words);
        //Application.COMPONENT.inject(this);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        AnnotationWordBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.annotation_word, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        final WordAnnotation word = getItem(position);
        binding.setAnnotation(word);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(word);
                }
            }
        });

        return binding.getRoot();
    }

}
