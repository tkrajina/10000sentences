package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.logic.StatsService;
import info.puzz.a10000sentences.databinding.ActivityStatsBinding;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.utils.TimeUtils;

public class StatsActivity extends BaseActivity {

    private static final String TAG = CollectionActivity.class.getSimpleName();

    @Inject
    StatsService statsService;

    @Inject
    Dao dao;

    ActivityStatsBinding binding;

    private Float graphFontSize = null;

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

        setupGraphs(null);
    }

    private void setupGraphs(final String collectionId) {
        final int daysAgo = 7;

        if (collectionId == null) {
            setTitle(getString(R.string.stats_title, daysAgo));
        } else {
            SentenceCollection collection = dao.getCollection(collectionId);
            Language language = dao.getLanguage(collection.targetLanguage);
            setTitle(getString(R.string.stats_title, daysAgo) + ": " + language.name);
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
        if (graphFontSize == null) {
            graphFontSize = graph.getGridLabelRenderer().getTextSize();
        }

        graph.removeAllSeries();

        double minX = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(daysAgo) - TimeUnit.HOURS.toMillis(12);
        double maxX = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12);
        double minY = 0;
        double maxY = 0;

        for (String collectionId : dataPointsByCollectionId.keySet()) {
            List<DataPointInterface> points = dataPointsByCollectionId.get(collectionId);

            LineGraphSeries series = new LineGraphSeries<>(points.toArray(new DataPointInterface[points.size()]));
            graph.addSeries(series);

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
        graph.getGridLabelRenderer().setGridColor(Color.GRAY);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        Map<String, Language> langs = dao.getLanguagesByLanguageID();

        List<SentenceCollection> collections = dao.getCollections();
        Collections.sort(collections, new Comparator<SentenceCollection>() {
            @Override
            public int compare(SentenceCollection c1, SentenceCollection c2) {
                return - Integer.compare(c1.doneCount, c2.doneCount);
            }
        });

        for (final SentenceCollection collection : collections) {
            if (collection.doneCount > 0) {
                MenuItem item = menu.add(langs.get(collection.targetLanguage).name);
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        StatsActivity.this.setupGraphs(collection.collectionID);
                        return true;
                    }
                });
            }
        }

        if (menu.size() > 0) {
            menu.add(R.string.all).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    StatsActivity.this.setupGraphs(null);
                    return false;
                }
            });
        }

        return true;
    }
}
