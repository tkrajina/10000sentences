package info.puzz.a10000sentences;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class Application extends android.app.Application {

    public static DiComponent COMPONENT;

    public static Tracker GA_TRACKER;

    @Override
    public void onCreate() {
        super.onCreate();
        initIconify();
        initActiveAndroid();
        initDagger();
        initTracker();
    }

    private void initDagger() {
        COMPONENT = DaggerDiComponent.builder()
                .appModule(new AppModule())
                .build();
    }

    private void initIconify() {
        //FontAwesomeIcons.fa_volume_up
        Iconify.with(new FontAwesomeModule());
    }

    private void initActiveAndroid() {
        ActiveAndroid.initialize(this);
    }

    synchronized public void initTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        GA_TRACKER = analytics.newTracker(R.xml.global_tracker);
    }
}
