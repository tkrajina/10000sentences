package info.puzz.a10000sentences.activities;

import android.databinding.BaseObservable;
import android.os.AsyncTask;

import javax.inject.Inject;

import info.puzz.a10000sentences.logic.StatsService;

public class StatsModel extends BaseObservable {

    private static final int MAX_STREAK_DAYS = 365;

    private int daysStreak = -1;

    @Inject
    StatsService statsService;

    public StatsModel() {
    }

    public int getDaysStreak() {
        return daysStreak;
    }

    void init() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                daysStreak = statsService.getStreakDays(MAX_STREAK_DAYS);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                StatsModel.this.notifyChange();
            }
        }.execute();
    }

    public String getDaysStreakDesc() {
        if (daysStreak < 0) {
            return "???";
        }
        if (daysStreak < 30) {
            return String.format("%d days", daysStreak);
        }
        if (daysStreak < MAX_STREAK_DAYS) {
            return String.format("More than %d days", MAX_STREAK_DAYS);
        }
        return String.format("More than %d months", daysStreak / 30);
    }

}
