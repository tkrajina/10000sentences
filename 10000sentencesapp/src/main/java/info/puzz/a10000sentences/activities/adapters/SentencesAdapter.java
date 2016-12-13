package info.puzz.a10000sentences.activities.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.activeandroid.query.From;

import java.util.ArrayList;
import java.util.List;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.BaseActivity;
import info.puzz.a10000sentences.activities.SentenceQuizActivity;
import info.puzz.a10000sentences.databinding.SentenceBinding;
import info.puzz.a10000sentences.models.Sentence;

public class SentencesAdapter extends ArrayAdapter<Sentence> {

    private static final int PAGE_SIZE = 100;
    private final From select;
    private int offset;

    public <T extends BaseActivity> SentencesAdapter(T activity, From select) {
        super(activity, R.layout.sentence_collection, new ArrayList<Sentence>());
        this.select = select;
        this.offset = 0;
        loadMore();
    }

    private void loadMore() {
        List<Sentence> rows = select.offset(offset).limit(PAGE_SIZE).execute();
        this.offset = offset + rows.size();
        this.addAll(rows);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SentenceBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.sentence, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        final Sentence sentence = getItem(position);
        binding.setSentence(sentence);

        if (position == offset - 2) {
            loadMore();
        }

        binding.targetSentence.setTextColor(ContextCompat.getColor(getContext(), sentence.getSentenceStatus().getColor()));

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SentenceQuizActivity.startSentence((BaseActivity) getContext(), sentence.sentenceId, SentenceQuizActivity.Type.BACK_TO_COLLECTION);
            }
        });

        return binding.getRoot();
    }

}
