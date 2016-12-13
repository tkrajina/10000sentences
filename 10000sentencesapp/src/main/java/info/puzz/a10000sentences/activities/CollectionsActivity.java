package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import java.util.List;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.activities.adapters.CollectionsAdapter;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivityCollectionsBinding;
import info.puzz.a10000sentences.models.SentenceCollection;

public class CollectionsActivity extends BaseActivity implements BaseActivity.OnCollectionsReloaded {

    private static final String TAG = CollectionActivity.class.getSimpleName();

    ActivityCollectionsBinding binding;

    @Inject Dao dao;

    public static <T extends BaseActivity> void start(T activity) {
        Intent intent = new Intent(activity, CollectionsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application.COMPONENT.injectActivity(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_collections);

        if (dao.getLanguages().size() == 0) {
            reloadLanguages();
        }

        setTitle(R.string.collections);
    }

    @Override
    protected void onResume() {
        super.onResume();

        reloadCollections();
    }

    @Override
    public void onCollectionsReloaded() {
        reloadCollections();
    }

    private void reloadCollections() {
        List<SentenceCollection> cols = dao.getCollections();
        binding.collectionsList.setAdapter(new CollectionsAdapter(this, cols));
    }

}
