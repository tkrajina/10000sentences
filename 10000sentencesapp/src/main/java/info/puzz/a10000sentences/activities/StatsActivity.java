package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPointInterface;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivityStatsBinding;
import info.puzz.a10000sentences.services.StatsService;
import info.puzz.a10000sentences.utils.TimeUtils;

public class StatsActivity extends BaseActivity {

    private static final String TAG = CollectionActivity.class.getSimpleName();

    @Inject
    StatsService statsService;

    @Inject
    Dao dao;

    ActivityStatsBinding binding;

    private Float graphFontSize = null;
    private int[] graphColors;

    private interface Formatter {
        String format(double value);
    }

    public static <T extends BaseActivity> void start(T activity) {
        Intent intent = new Intent(activity, StatsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.COMPONENT.injectActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stats);
        setTitle(R.string.stats);

        graphColors = new int[] {
                R.color.graph_1,
                R.color.graph_2,
                R.color.graph_3,
                R.color.graph_4,
                R.color.graph_5,
        };

        setupGraphs();
    }

    private void setupGraphs() {
        final int daysAgo = 7;

        setTitle(getString(R.string.stats_title, daysAgo));

        if (graphFontSize == null) {
            graphFontSize = (float) (binding.timeGraph.getGridLabelRenderer().getTextSize() * 0.6);
        }

        for (GraphView graph : new GraphView[]{binding.timeGraph, binding.doneCounterGraph,}) {
            graph.removeAllSeries();
            graph.getGridLabelRenderer().setTextSize(graphFontSize);
        }

        new AsyncTask<Void, Void, StatsService.Stats>() {
            @Override
            protected StatsService.Stats doInBackground(Void... voids) {
                return statsService.getStats(daysAgo);
            }

            @Override
            protected void onPostExecute(StatsService.Stats stats) {
                setupGraph(stats.getTimePerDay(), binding.timeGraph, daysAgo, new Formatter() {
                    @Override
                    public String format(double value) {
                        return TimeUtils.formatDurationToHHMMSS((long) value, false);
                    }
                });
                setupGraph(stats.getDonePerDay(), binding.doneCounterGraph, daysAgo, new Formatter() {
                    @Override
                    public String format(double value) {
                        return String.format("%d", (int) value);
                    }
                });
            }
        }.execute();
    }

    private void setupGraph(Map<String, List<DataPointInterface>> dataPointsByCollectionId, GraphView graph, int daysAgo, final Formatter yAxisFormatter) {

        double minX = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(daysAgo) - TimeUnit.HOURS.toMillis(12);
        double maxX = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12);
        double minY = 0;
        double maxY = 0;

        int colorNo = 0;
        for (String collectionId : dataPointsByCollectionId.keySet()) {
            List<DataPointInterface> points = dataPointsByCollectionId.get(collectionId);

            BarGraphSeries series = new BarGraphSeries<>(points.toArray(new DataPointInterface[points.size()]));
            series.setColor(ContextCompat.getColor(this, graphColors[(colorNo ++) % graphColors.length]));
            series.setTitle(collectionId);

            graph.addSeries(series);

            maxY = Math.max(maxY, series.getHighestValueY());
        }

        if (minY == maxY) {
            minY = 0;
            maxY = 1 + maxY * 1.5;
        }

        graph.getViewport().setMinX(minX);
        graph.getViewport().setMaxX(maxX);
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setMinY(minY);
        graph.getViewport().setMaxY(maxY);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getLegendRenderer().setFixedPosition(0, 0);
        graph.getLegendRenderer().setVisible(true);

        graph.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
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
                return yAxisFormatter.format(value);
            }

            @Override
            public void setViewport(Viewport viewport) {}
        });
        graph.getGridLabelRenderer().setGridColor(Color.GRAY);
        graph.getGridLabelRenderer().setNumHorizontalLabels(daysAgo);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
