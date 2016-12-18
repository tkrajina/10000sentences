package info.puzz.a10000sentences.activities.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.activeandroid.Model;
import com.activeandroid.query.From;

import java.util.ArrayList;
import java.util.List;

public abstract class LoadMoreAdapter<T extends Model> extends ArrayAdapter<T> {

    private static final int PAGE_SIZE = 100;
    private final From originalSql;
    private From select;
    private int offset;

    public LoadMoreAdapter(Context context, int resource, From sql) {
        super(context, resource, new ArrayList<T>());
        reset(sql);
        originalSql = sql;
    }

    public int reset() {
        this.select = originalSql;
        this.offset = 0;
        return loadMore();
    }

    public int reset(From sql) {
        this.select = sql;
        this.offset = 0;
        return loadMore();
    }

    private int loadMore() {
        List<T> rows = select
                .offset(offset)
                .limit(PAGE_SIZE)
                .execute();
        this.offset = offset + rows.size();
        this.addAll(rows);
        this.notifyDataSetChanged();
        return rows.size();
    }

    protected T getItemAndLoadMoreIfNeeded(int position) {
        loadMoreIfNeeded(position);
        return getItem(position);
    }

    protected void loadMoreIfNeeded(int position) {
        if (position == offset - 2) {
            loadMore();
        }
    }

}
