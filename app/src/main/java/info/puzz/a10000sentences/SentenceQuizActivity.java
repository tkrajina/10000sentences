package info.puzz.a10000sentences;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;

import info.puzz.a10000sentences.databinding.ActivitySentenceQuizBinding;

public class SentenceQuizActivity extends AppCompatActivity {

    ActivitySentenceQuizBinding binding;
    private List<WordChunk> chunks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sentence_quiz);
        //FontAwesomeIcons.fa_volume_up

        String sentence = "Sonst noch irgendwelche schlauen Einfälle?";
        chunks = StringUtils.getWordChunks(sentence);

        Button[] answerButtons = new Button[] { binding.answer1, binding.answer2, binding.answer3, binding.answer4, };

        // This isn't working, why?:
/*        binding.iconSomething.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_share)
                        .colorRes(R.color.colorAccent)
                        .actionBarSize());*/
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem articlesItem = menu.findItem(R.id.action_settings);
        articlesItem.setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_share)
                        .colorRes(R.color.colorAccent)
                        .actionBarSize());

        return true;
    }*/
}
