package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;

import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.StatsService;
import info.puzz.a10000sentences.databinding.ActivityStatsBinding;
import info.puzz.a10000sentences.utils.TimeUtils;

public class StatsActivity extends BaseActivity {

    private static final String TAG = CollectionActivity.class.getSimpleName();

    ActivityStatsBinding binding;

    private Float graphFontSize = null;

    public static <T extends BaseActivity> void start(T activity) {
        Intent intent = new Intent(activity, StatsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stats);
        setTitle(R.string.stats);

        if (graphFontSize == null) {
            graphFontSize = binding.doneCounterGraph.getGridLabelRenderer().getTextSize();
        }


        binding.doneCounterGraph.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
            public String lattestFormatted;
            Calendar cal = Calendar.getInstance();
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    cal.setTimeInMillis((long) value);
                    String formatted = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
                    if (formatted.equals(lattestFormatted)) {
                        this.lattestFormatted = formatted;
                        return "";
                    }
                    this.lattestFormatted = formatted;
                    return formatted;
                }
                return TimeUtils.formatDurationToHHMMSS((long) value, false);
            }

            @Override
            public void setViewport(Viewport viewport) {}
        });

/*        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.doneCounterGraph.getGridLabelRenderer().setNumHorizontalLabels(20);
        } else {
            binding.doneCounterGraph.getGridLabelRenderer().setNumHorizontalLabels(10);
        }*/

        DataPoint[] dataPoints = StatsService.getStats(7);
        LineGraphSeries series = new LineGraphSeries<>(dataPoints);

        double minX = series.getLowestValueX();
        double maxX = series.getHighestValueX();
        double minY = 0; series.getLowestValueY();
        double maxY = series.getHighestValueY();

        if (minX == maxX) {
            minX -= TimeUnit.DAYS.toMillis(1);
            maxX += TimeUnit.DAYS.toMillis(1);
        }
        if (minY == maxY) {
            minY = 0;
            maxY = maxY * 1.5;
        }

        binding.doneCounterGraph.getViewport().setMinX(minX);
        binding.doneCounterGraph.getViewport().setMaxX(maxX);
        binding.doneCounterGraph.getViewport().setXAxisBoundsManual(true);

        binding.doneCounterGraph.getViewport().setMinY(minY);
        binding.doneCounterGraph.getViewport().setMaxY(maxY);
        binding.doneCounterGraph.getViewport().setYAxisBoundsManual(true);
        binding.doneCounterGraph.getGridLabelRenderer().setGridColor(Color.GRAY);

        if (dataPoints.length < 6) {
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(graphFontSize / dataPoints.length);
        }

        binding.doneCounterGraph.addSeries(series);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
