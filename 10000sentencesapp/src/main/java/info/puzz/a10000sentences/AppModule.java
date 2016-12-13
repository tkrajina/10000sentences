package info.puzz.a10000sentences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.logic.SentenceCollectionsService;

@Module
public class AppModule {

    @Provides @Singleton
    public Dao provideDao() {
        return new Dao();
    }

    @Provides @Singleton
    public SentenceCollectionsService providesSentenceCollectionsService(Dao dao) {
        return new SentenceCollectionsService(dao);
    }

    @Provides @Singleton
    public StatsService providesStatsService() {
        return new StatsService();
    }

}
