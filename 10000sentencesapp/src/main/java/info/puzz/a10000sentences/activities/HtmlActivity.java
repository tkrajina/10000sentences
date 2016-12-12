package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import org.markdown4j.Markdown4jProcessor;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.databinding.ActivityHtmlBinding;

public class HtmlActivity extends BaseActivity {

    public static final Markdown4jProcessor MARKDOWN_PROCESSOR = new Markdown4jProcessor();

    private static final String TAG = HtmlActivity.class.getSimpleName();

    private static final String ARG_TITLE = "argtitle";
    private static final String ARG_HTML = "arghtml";

    ActivityHtmlBinding binding;

    public static final void start(BaseActivity activity, String title, String html) {
        Intent intent = new Intent(activity, HtmlActivity.class);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_HTML, html);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_html);

        String title = getIntent().getStringExtra(ARG_TITLE);
        String html = getIntent().getStringExtra(ARG_HTML);

        setTitle(title);
        binding.helpViewer.loadData(html, "text/html; charset=UTF-8", "UTF-8");
    }

}
