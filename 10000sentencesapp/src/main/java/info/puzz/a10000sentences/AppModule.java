package info.puzz.a10000sentences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.puzz.a10000sentences.dao.Dao;

@Module
public class AppModule {
    @Provides @Singleton
    public Dao provideDao() {
        return new Dao();
    }
}
