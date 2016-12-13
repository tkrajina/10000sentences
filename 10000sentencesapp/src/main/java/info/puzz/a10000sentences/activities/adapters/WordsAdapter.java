package info.puzz.a10000sentences.activities.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.BaseActivity;
import info.puzz.a10000sentences.databinding.AnnotationWordBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.models.Annotation;
import info.puzz.a10000sentences.models.WordAnnotation;
import info.puzz.a10000sentences.utils.DialogUtils;

public class WordsAdapter extends ArrayAdapter<WordAnnotation> {

    private final Annotation annotation;

    @Inject
    AnnotationService annotationService;

    public <T extends BaseActivity> WordsAdapter(T activity, Annotation annotation, List<WordAnnotation> words) {
        super(activity, R.layout.sentence_collection, words);
        Application.COMPONENT.inject(this);
        this.annotation = annotation;
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

        binding.getRoot().setLongClickable(true);
        binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removeWord(word);
                return true;
            }
        });

        return binding.getRoot();
    }

    private void removeWord(final WordAnnotation word) {
        DialogUtils.showYesNoButton(
                (Activity) this.getContext(),
                getContext().getString(R.string.remove_word_from_annotation),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == Dialog.BUTTON_POSITIVE) {
                            annotationService.removeWordToAnnotation(annotation, word);
                            remove(word);
                            notifyDataSetChanged();
                        }
                    }
                });
    }

}
