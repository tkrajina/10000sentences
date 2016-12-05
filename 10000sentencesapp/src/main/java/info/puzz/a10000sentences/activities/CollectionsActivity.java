package info.puzz.a10000sentences.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import java.util.List;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivityCollectionsBinding;
import info.puzz.a10000sentences.models.SentenceCollection;
import temp.DBG;

public class CollectionsActivity extends BaseActivity {

    ActivityCollectionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_collections);

        DBG.todo("Remove this");
        reloadLanguages();
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<SentenceCollection> cols = Dao.getCollections();
        binding.collectionsList.setAdapter(new CollectionsAdapter(this, cols));
    }
}
