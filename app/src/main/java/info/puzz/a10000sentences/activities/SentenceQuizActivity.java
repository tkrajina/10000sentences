package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.activeandroid.query.Select;

import info.puzz.a10000sentences.CollectionActivity;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.api.Api;
import info.puzz.a10000sentences.apimodels.InfoVO;
import info.puzz.a10000sentences.apimodels.LanguageVO;
import info.puzz.a10000sentences.apimodels.SentenceCollectionVO;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivitySentenceQuizBinding;
import info.puzz.a10000sentences.databinding.SentenceFinalizeOptionsBinding;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.utils.DialogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import temp.DBG;

public class SentenceQuizActivity extends BaseActivity {

    private static final String TAG = SentenceQuizActivity.class.getSimpleName();

    private static final String ARG_SENTENCE_ID = "arg_sentence_id";

    ActivitySentenceQuizBinding binding;

    private SentenceQuiz quiz;
    private Button[] answerButtons;

    public static <T extends BaseActivity> void start(T activity, String sentenceId) {
        Intent intent = new Intent(activity, SentenceQuizActivity.class)
                .putExtra(ARG_SENTENCE_ID, sentenceId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sentence_quiz);
        //FontAwesomeIcons.fa_volume_up

        String sentenceId = getIntent().getStringExtra(ARG_SENTENCE_ID);
        Sentence sentence = new Select()
                .from(Sentence.class)
                .where("sentence_id = ?", sentenceId)
                .executeSingle();
        if (sentence == null) {
            DBG.todo();
        }

        binding.setQuiz(new SentenceQuiz(sentence, 4));

        answerButtons = new Button[] { binding.answer1, binding.answer2, binding.answer3, binding.answer4, };
        for (final Button answerButton : answerButtons) {
            answerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitResponse(answerButton.getText().toString());
                }
            });
        }
    }

    private void submitResponse(String text) {
        binding.getQuiz().guessWord(text);
        if (binding.getQuiz().isFinished()) {
            finalizeSentence();
        }
    }

    private void finalizeSentence() {
        SentenceFinalizeOptionsBinding finalizeBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.sentence_finalize_options, null, false);
        new AlertDialog.Builder(this)
                .setTitle(R.string.finalize_sentence_title)
                .setView(finalizeBinding.getRoot())
                .show();
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
