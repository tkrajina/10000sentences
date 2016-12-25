package info.puzz.a10000sentences.logic;

import com.activeandroid.query.Select;
import com.jjoe64.graphview.series.DataPoint;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import info.puzz.a10000sentences.models.SentenceHistory;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

public final class StatsService {

    private static final Comparator<DataPoint> DATAPOINT_COMPARATOR = new Comparator<DataPoint>() {
        @Override
        public int compare(DataPoint dp1, DataPoint dp2) {
            return Double.compare(dp1.getX(), dp2.getX());
        }
    };

    @Data
    @Accessors(chain = true)
    @ToString
    public class Stats {
        DataPoint[] timePerDay;
        DataPoint[] donePerDay;
    }

    public StatsService() {
    }

    public Stats getStats(int daysAgo, String collectionId) {
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
        Map<Long, Map<String, Integer>> doneByDay = new HashMap<>();

        long now = System.currentTimeMillis();
        for (int i = 0; i < daysAgo + 1; i++) {
            long time = getMiddayTime(now + TimeUnit.DAYS.toMillis(i));

            timeByDay.put(time, new ArrayList<Integer>());
            doneByDay.put(time, new HashMap<String, Integer>());

            timeByDay.get(time).add(0);
            doneByDay.get(time).put(collectionId, 0);
        }

        for (SentenceHistory sh : history) {
            if (collectionId == null || StringUtils.equals(sh.collectionId, collectionId)) {
                long time = getMiddayTime(sh.created);
                if (!timeByDay.containsKey(time)) {
                    timeByDay.put(time, new ArrayList<Integer>());
                    doneByDay.put(time, new HashMap<String, Integer>());
                }
                timeByDay.get(time).add(sh.time);
                doneByDay.get(time).put(sh.collectionId, sh.doneCount);
            }
        }

        List<DataPoint> timeDailyData = new ArrayList<>();
        for (Map.Entry<Long, List<Integer>> e : timeByDay.entrySet()) {
            DataPoint point = new DataPoint(e.getKey(), sum(e.getValue()));
            timeDailyData.add(point);
        }
        
        List<DataPoint> doneDailyData = new ArrayList<>();
        for (Map.Entry<Long, Map<String, Integer>> e : doneByDay.entrySet()) {
            int sum = 0;
            for (Integer integer : e.getValue().values()) {
                sum += integer.intValue();
            }
            doneDailyData.add(new DataPoint(e.getKey(), sum));
        }

        Collections.sort(timeDailyData, DATAPOINT_COMPARATOR);
        Collections.sort(doneDailyData, DATAPOINT_COMPARATOR);

        return new Stats()
                .setTimePerDay(timeDailyData.toArray(new DataPoint[timeDailyData.size()]))
                .setDonePerDay(doneDailyData.toArray(new DataPoint[doneDailyData.size()]));
    }

    private long getMiddayTime(long created) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(created);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DST_OFFSET, 0);
        c.set(Calendar.ZONE_OFFSET, 0);
        return c.getTimeInMillis();
    }

    private final float avg(List<Integer> l) {
        if (l.size() == 0) {
            return 0;
        }

        return sum(l) + ((float) l.size());
    }

    private float sum(List<Integer> l) {
        int s = 0;
        for (Integer i : l) {
            s += i.intValue();
        }
        return s;
    }

}
