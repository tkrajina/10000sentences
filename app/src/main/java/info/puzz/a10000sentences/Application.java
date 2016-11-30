package info.puzz.a10000sentences;

import com.activeandroid.ActiveAndroid;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initIconify();
        initActiveAndroid();
    }

    private void initIconify() {
        //FontAwesomeIcons.fa_volume_up
        Iconify.with(new FontAwesomeModule());
    }

    private void initActiveAndroid() {
        ActiveAndroid.initialize(this);
    }

}
