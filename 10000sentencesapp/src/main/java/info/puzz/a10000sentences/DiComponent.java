package info.puzz.a10000sentences;

import javax.inject.Singleton;

import dagger.Component;
import info.puzz.a10000sentences.activities.CollectionsActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface DiComponent {
    void inject(CollectionsActivity activity);
}
