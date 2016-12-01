package info.puzz.a10000sentences.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.SentenceCollectionBinding;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.SentenceCollection;

public class CollectionsAdapter extends ArrayAdapter<SentenceCollection> {

    private final SentenceCollection[] collections;
    Map<String, Language> languages = new HashMap<>();

    public <T extends BaseActivity> CollectionsAdapter(T activity, List<SentenceCollection> cols) {
        this(activity, cols.toArray(new SentenceCollection[cols.size()]));
    }

    public <T extends BaseActivity> CollectionsAdapter(T activity, SentenceCollection[] cols) {
        super(activity, R.layout.sentence_collection, cols);
        for (Language language : Dao.getLanguages()) {
            languages.put(language.getLanguageId(), language);
        }
        collections = cols;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SentenceCollectionBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.sentence_collection, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        binding.setKnownLanguage(languages.get(collections[position].getKnownLanguage()));
        binding.setTargetLanguage(languages.get(collections[position].getTargetLanguage()));

        return binding.getRoot();
    }
}
