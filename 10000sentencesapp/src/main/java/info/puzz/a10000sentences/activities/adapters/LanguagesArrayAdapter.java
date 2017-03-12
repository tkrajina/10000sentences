package info.puzz.a10000sentences.activities.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import info.puzz.a10000sentences.models.Language;

public class LanguagesArrayAdapter extends ArrayAdapter<Language> {
    public LanguagesArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Language> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return this.getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Language language = getItem(position);

        TextView view = (TextView) (convertView == null ? inflater.inflate(android.R.layout.simple_spinner_item, parent, false) : convertView);
        view.setText(String.format("%s / %s", language.family, language.name));

        return view;
    }
}
