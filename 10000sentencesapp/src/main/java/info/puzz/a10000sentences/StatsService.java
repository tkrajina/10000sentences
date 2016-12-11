package info.puzz.a10000sentences;

import android.content.Intent;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.puzz.a10000sentences.models.SentenceHistory;

public final class StatsService {
    public StatsService() throws Exception {
        throw new Exception();
    }

    public static DataPoint[] getStats(int daysAgo) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -daysAgo);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        List<SentenceHistory> history = new Select()
                .from(SentenceHistory.class)
                .where("created>?", cal.getTime().getTime())
                .orderBy("created")
                .execute();


        Map<Long, List<Integer>> timeByDay = new HashMap<>();
        for (SentenceHistory model : history) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(model.created);
            c.set(Calendar.HOUR_OF_DAY, 12);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.DST_OFFSET, 0);
            c.set(Calendar.ZONE_OFFSET, 0);
            long time = c.getTimeInMillis();
            if (!timeByDay.containsKey(time)) {
                timeByDay.put(time, new ArrayList<Integer>());
            }
            timeByDay.get(time).add(model.time);
        }

        List<DataPoint> data = new ArrayList<>();
        for (Map.Entry<Long, List<Integer>> e : timeByDay.entrySet()) {
            data.add(new DataPoint(e.getKey(), sum(e.getValue())));
        }

        return data.toArray(new DataPoint[data.size()]);
    }

    private static final float avg(List<Integer> l) {
        if (l.size() == 0) {
            return 0;
        }

        return sum(l) + ((float) l.size());
    }

    private static float sum(List<Integer> l) {
        int s = 0;
        for (Integer i : l) {
            s += i.intValue();
        }
        return s;
    }

}
