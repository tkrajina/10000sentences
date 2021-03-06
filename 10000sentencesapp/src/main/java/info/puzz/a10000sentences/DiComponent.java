package info.puzz.a10000sentences;

import javax.inject.Singleton;

import dagger.Component;
import info.puzz.a10000sentences.activities.AnnotationActivity;
import info.puzz.a10000sentences.activities.AnnotationsActivity;
import info.puzz.a10000sentences.activities.BaseActivity;
import info.puzz.a10000sentences.activities.CollectionActivity;
import info.puzz.a10000sentences.activities.CollectionsActivity;
import info.puzz.a10000sentences.activities.EditAnnotationActivity;
import info.puzz.a10000sentences.activities.StatsModel;
import info.puzz.a10000sentences.activities.adapters.AnnotationsAdapter;
import info.puzz.a10000sentences.activities.adapters.CollectionsAdapter;
import info.puzz.a10000sentences.activities.SentenceQuizActivity;
import info.puzz.a10000sentences.activities.SentencesActivity;
import info.puzz.a10000sentences.activities.StatsActivity;
import info.puzz.a10000sentences.activities.adapters.WordsAdapter;
import info.puzz.a10000sentences.tasks.ImporterAsyncTask;

@Singleton
@Component(modules = {AppModule.class})
public interface DiComponent {

    void inject(BaseActivity baseActivity);

    void injectActivity(CollectionsActivity activity);
    void injectActivity(CollectionActivity activity);
    void injectActivity(SentenceQuizActivity activity);
    void injectActivity(SentencesActivity activity);
    void injectActivity(StatsActivity statsActivity);
    void injectActivity(AnnotationActivity annotationActivity);
    void injectActivity(AnnotationsActivity annotationsActivity);
    void injectActivity(EditAnnotationActivity editAnnotationActivity);

    void inject(ImporterAsyncTask importerAsyncTask);

    void inject(CollectionsAdapter collectionsAdapter);
    void inject(AnnotationsAdapter annotationsAdapter);
    void inject(WordsAdapter wordsAdapter);
    void inject(StatsModel stats);
}
