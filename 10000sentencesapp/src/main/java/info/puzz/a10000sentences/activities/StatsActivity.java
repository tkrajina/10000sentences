package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.databinding.ActivityStatsBinding;

public class StatsActivity extends BaseActivity {

    private static final String TAG = CollectionActivity.class.getSimpleName();

    ActivityStatsBinding binding;

    public static <T extends BaseActivity> void start(T activity) {
        Intent intent = new Intent(activity, StatsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stats);
        setTitle(R.string.stats);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
