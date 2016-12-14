package info.puzz.a10000sentences.activities.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.activeandroid.Model;
import com.activeandroid.query.From;

import java.util.ArrayList;
import java.util.List;

public abstract class LoadMoreAdapter<T extends Model> extends ArrayAdapter<T> {

    private static final int PAGE_SIZE = 100;
    private final From select;
    private int offset;

    public LoadMoreAdapter(Context context, int resource, From sql) {
        super(context, resource, new ArrayList<T>());
        this.select = sql;
        this.offset = 0;
        loadMore();
    }

    private void loadMore() {
        List<T> rows = select
                .offset(offset)
                .limit(PAGE_SIZE)
                .execute();
        this.offset = offset + rows.size();
        this.addAll(rows);
        this.notifyDataSetChanged();
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
