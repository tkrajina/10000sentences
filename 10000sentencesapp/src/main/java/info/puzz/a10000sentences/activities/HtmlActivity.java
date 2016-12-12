package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.databinding.ActivityHtmlBinding;

public class HtmlActivity extends BaseActivity {

    private static final Markdown4jProcessor MARKDOWN_PROCESSOR = new Markdown4jProcessor();

    private static final String TAG = HtmlActivity.class.getSimpleName();

    private static final String ARG_TITLE = "argtitle";
    private static final String ARG_HTML = "arghtml";
    private static final String ARG_MARKDOWN = "arg_markdown";

    ActivityHtmlBinding binding;

    public static final void start(BaseActivity activity, String title, String html, boolean markdown) {
        Intent intent = new Intent(activity, HtmlActivity.class);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_HTML, html);
        intent.putExtra(ARG_MARKDOWN, markdown);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_html);

        String title = getIntent().getStringExtra(ARG_TITLE);
        String html = getIntent().getStringExtra(ARG_HTML);
        boolean markdown = getIntent().getBooleanExtra(ARG_MARKDOWN, false);

        if (markdown) {
            try {
                html = MARKDOWN_PROCESSOR.process(html);
            } catch (IOException e) {
                Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage(), e);
            }
        }

        setTitle(title);
        binding.helpViewer.loadData(html, "text/html; charset=UTF-8", "UTF-8");
    }

}
