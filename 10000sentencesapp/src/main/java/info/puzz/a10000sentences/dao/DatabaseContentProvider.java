package info.puzz.a10000sentences.dao;

import com.activeandroid.Configuration;
import com.activeandroid.content.ContentProvider;

public class DatabaseContentProvider extends ContentProvider {
    @Override
    protected Configuration getConfiguration() {
        Configuration.Builder builder = new Configuration.Builder(getContext());
        builder.addModelClass(info.puzz.a10000sentences.models.Language.class);
        builder.addModelClass(info.puzz.a10000sentences.models.Sentence.class);
        builder.addModelClass(info.puzz.a10000sentences.models.SentenceCollection.class);
        builder.addModelClass(info.puzz.a10000sentences.models.SentenceHistory.class);
        builder.addModelClass(info.puzz.a10000sentences.models.Annotation.class);
        builder.addModelClass(info.puzz.a10000sentences.models.WordAnnotation.class);
        return builder.create();
    }
}
