package info.puzz.a10000sentences.logic;

import com.activeandroid.query.Select;
import com.jjoe64.graphview.series.DataPointInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import info.puzz.a10000sentences.models.SentenceHistory;

public final class StatsService {

    public class DataPoint implements DataPointInterface {
        private double x;
        private double y;
        public DataPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public double getX() {
            return x;
        }

        public DataPoint setX(double x) {
            this.x = x;
            return this;
        }

        @Override
        public double getY() {
            return y;
        }

        public DataPoint setY(double y) {
            this.y = y;
            return this;
        }
    }

    public class Stats {
        /**
         * List of active collections in that period of time.
         */
        private Set<String> collections = new HashSet<>();
        /**
         * Time per day by collection id.
         */
        private Map<String, List<DataPoint>> timePerDay = new HashMap<>();
        /**
         * Total done sentences per day by collection id.
         * @see info.puzz.a10000sentences.models.SentenceStatus
         */
        private Map<String, List<DataPoint>> donePerDay = new HashMap<>();

        public void addTime(String collectionId, long middayTime, long time) {
            addCollectionIfNeeded(collectionId);
            if (!timePerDay.containsKey(collectionId)) {
                timePerDay.put(collectionId, new ArrayList<DataPoint>());
            }

            List<DataPoint> dataPoints = timePerDay.get(collectionId);
            if (dataPoints.size() == 0) {
                dataPoints.add(new DataPoint(middayTime, time));
                return;
            }

            DataPointInterface lastDataPoint = dataPoints.get(dataPoints.size() - 1);
            if (lastDataPoint.getX() == middayTime) {
                dataPoints.set(dataPoints.size() - 1, new DataPoint(middayTime, lastDataPoint.getY() + time));
            } else {
                dataPoints.add(new DataPoint(middayTime, time));
            }
        }

        public void addDone(String collectionId, long middayTime, int count) {
            addCollectionIfNeeded(collectionId);
            if (!donePerDay.containsKey(collectionId)) {
                donePerDay.put(collectionId, new ArrayList<DataPoint>());
            }

            List<DataPoint> dataPoints = donePerDay.get(collectionId);
            if (dataPoints.size() == 0) {
                dataPoints.add(new DataPoint(middayTime, count));
                return;
            }

            DataPointInterface lastDataPoint = dataPoints.get(dataPoints.size() - 1);
            if (lastDataPoint.getX() == middayTime) {
                dataPoints.set(dataPoints.size() - 1, new DataPoint(middayTime, count));
            } else {
                dataPoints.add(new DataPoint(middayTime, count));
            }
        }

        private void addCollectionIfNeeded(String collectionId) {
            if (!collections.contains(collectionId)) {
                collections.add(collectionId);
            }
        }

        public Map<String,List<DataPoint>> getTimePerDay() {
            return timePerDay;
        }

        public Map<String,List<DataPoint>> getDonePerDay() {
            Iterator<List<DataPoint>> i = donePerDay.values().iterator();
            while (i.hasNext()) {
                List<DataPoint> values = i.next();
                double minValue = Double.MAX_VALUE;
                for (DataPoint value : values) {
                    if (value.y < minValue) {
                        minValue = value.y;
                    }
                }

                if (minValue == Double.MAX_VALUE) {
                    minValue = 0;
                }

                for (DataPoint value : values) {
                    value.y -= minValue;
                }
            }
            return donePerDay;
        }
    }

    public StatsService() {
    }

    public Stats getStats(int daysAgo) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -daysAgo);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        List<SentenceHistory> history = new Select()
                .from(SentenceHistory.class)
                .where("created>?", cal.getTime().getTime())
                .orderBy("created")
                .execute();

        Stats stats = new Stats();

        for (SentenceHistory sh : history) {
            long middayTime = getMiddayTime(sh.created);
            stats.addDone(sh.collectionId, middayTime, sh.doneCount);
            stats.addTime(sh.collectionId, middayTime, sh.time);
        }

        return stats;
    }

    public int getStreakDays(int maxDays) {
        long now = System.currentTimeMillis();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DST_OFFSET, 0);
        c.set(Calendar.ZONE_OFFSET, 0);
        long startFrom = c.getTimeInMillis();
        // Do not start from 0 here because the current date can still change:
        for (int i = 0; i < maxDays; i++) {
            long from = startFrom - TimeUnit.DAYS.toMillis(i + 1);
            long to = from + TimeUnit.DAYS.toMillis(1);
            int count = new Select()
                    .from(SentenceHistory.class)
                    .where("created>?", from)
                    .where("created<?", to)
                    .orderBy("created")
                    .limit(1)
                    .count();
            if (count == 0) {
                return i;
            }
        }
        return maxDays;
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
