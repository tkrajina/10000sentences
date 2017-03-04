package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivityImportTextBinding;
import info.puzz.a10000sentences.models.Language;

public class ImportTextActivity extends BaseActivity {

    private static final String TAG = ImportTextActivity.class.getSimpleName();

    @Inject
    Dao dao;

    private ActivityImportTextBinding binding;

    public static <T extends BaseActivity> void start(T activity) {
        Intent intent = new Intent(activity, ImportTextActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.COMPONENT.injectActivity(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_import_text);

        List<String> languages = new ArrayList<String>();
        for (Language language : dao.getLanguages()) {
            languages.add(language.getName());
        }

        binding.setLangsAdapter(new ArrayAdapter<>(ImportTextActivity.this, android.R.layout.simple_spinner_item, languages));
        binding.setImportText(new ImportText());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
        }
        return true;
    }

    private void save() {
        // TODO
    }

}
