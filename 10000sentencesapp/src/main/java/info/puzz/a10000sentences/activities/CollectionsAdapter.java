package info.puzz.a10000sentences.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

    Map<String, Language> languages = new HashMap<>();

    public <T extends BaseActivity> CollectionsAdapter(T activity, List<SentenceCollection> cols) {
        super(activity, R.layout.sentence_collection, cols);
        for (Language language : Dao.getLanguages()) {
            languages.put(language.getLanguageId(), language);
        }
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SentenceCollectionBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.sentence_collection, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        final SentenceCollection collection = getItem(position);

        if (collection.isDownloaded()) {
            binding.progress.setTextColor(ContextCompat.getColor(getContext(), R.color.active));
        } else {
            binding.progress.setTextColor(ContextCompat.getColor(getContext(), R.color.inactive));
        }

        binding.setKnownLanguage(languages.get(collection.getKnownLanguage()));
        binding.setTargetLanguage(languages.get(collection.getTargetLanguage()));
        binding.setCollection(getItem(position));
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectionActivity.start((BaseActivity) getContext(), collection.getCollectionID());
            }

        });

        return binding.getRoot();
    }

}
