package info.puzz.a10000sentences.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.activeandroid.query.Select;

import java.util.List;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.databinding.ActivitySentenceQuizBinding;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceStatus;
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

    public static <T extends BaseActivity> void startRandom(T activity, String collectionId) {
        Sentence randomSentence = new Select()
                .from(Sentence.class)
                .where("collection_id = ?", collectionId)
                .orderBy("random()")
                .executeSingle();

        start(activity, randomSentence.getSentenceId());
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

        SentenceCollection collection = new Select()
                .from(SentenceCollection.class)
                .where("collection_id = ?", sentence.collectionId)
                .executeSingle();

        List<Sentence> randomSentences = new Select()
                .from(Sentence.class)
                .where("collection_id = ?", collection.collectionID)
                .orderBy("random()")
                .limit(100)
                .execute();

        binding.setQuiz(new SentenceQuiz(sentence, 4, randomSentences));

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
        if (binding.getQuiz().canBeMarkedAsDone()) {
            binding.finalMessage.setText(R.string.correct);
            binding.next.setVisibility(View.VISIBLE);
        } else {
            binding.finalMessage.setText(R.string.too_many_errors);
            binding.next.setVisibility(View.GONE);
        }
        binding.quizButtons.setVisibility(View.GONE);
        binding.finalButtons.setVisibility(View.VISIBLE);

        binding.repeatLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatSentenceLater();
            }
        });
        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSentence();
            }
        });
    }

    private void nextSentence() {
        Sentence sentence = binding.getQuiz().getSentence();
        sentence.status = SentenceStatus.TODO.getStatus();
        sentence.save();
        startRandom(this, binding.getQuiz().getSentence().getCollectionId());
    }

    private void repeatSentenceLater() {
        Sentence sentence = binding.getQuiz().getSentence();
        sentence.status = SentenceStatus.AGAIN.getStatus();
        sentence.save();
        startRandom(this, binding.getQuiz().getSentence().getCollectionId());
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
