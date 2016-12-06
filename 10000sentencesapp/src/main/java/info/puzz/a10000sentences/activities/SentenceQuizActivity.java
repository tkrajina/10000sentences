package info.puzz.a10000sentences.activities;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.List;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.SentenceCollectionsService;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivitySentenceQuizBinding;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceStatus;
import info.puzz.a10000sentences.utils.ShareUtils;
import info.puzz.a10000sentences.utils.StringUtils;
import info.puzz.a10000sentences.utils.WordChunk;
import temp.DBG;

public class SentenceQuizActivity extends BaseActivity {

    private static final String TAG = SentenceQuizActivity.class.getSimpleName();

    private static final String ARG_SENTENCE_ID = "arg_sentence_id";

    ActivitySentenceQuizBinding binding;

    private SentenceQuiz quiz;
    private Button[] answerButtons;
    private Integer originalButtonColor;

    public static <T extends BaseActivity> void startSentence(T activity, String sentenceId) {
        Intent intent = new Intent(activity, SentenceQuizActivity.class)
                .putExtra(ARG_SENTENCE_ID, sentenceId);
        activity.startActivity(intent);
    }

    public static <T extends BaseActivity> void startRandom(T activity, String collectionId, String exceptSentenceId) {
        SentenceCollection collection = new Select()
                .from(SentenceCollection.class)
                .where("collection_id=?", collectionId)
                .executeSingle();

        Sentence sentence = SentenceCollectionsService.nextSentence(collection, exceptSentenceId);
        if (sentence == null) {
            Toast.makeText(activity, activity.getString(R.string.no_sentence_found), Toast.LENGTH_SHORT).show();
            return;
        }
        startSentence(activity, sentence.sentenceId);
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

        Language targetLanguage = Dao.getLanguage(collection.targetLanguage);

        binding.setQuiz(new SentenceQuiz(sentence, 4, randomSentences));

        if (targetLanguage.isRightToLeft()) {
            binding.targetSentence.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
        }

        answerButtons = new Button[] { binding.answer1, binding.answer2, binding.answer3, binding.answer4, };
        for (final Button answerButton : answerButtons) {
            answerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitResponse(answerButton, answerButton.getText().toString());
                }
            });
        }

        binding.startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.startQuizGroup.setVisibility(View.GONE);
                binding.quizSentenceGroup.setVisibility(View.VISIBLE);
                binding.quizButtons.setVisibility(View.VISIBLE);
            }
        });
    }

    private void submitResponse(Button answerButton, String text) {
        if (originalButtonColor == null) {
            originalButtonColor = answerButton.getCurrentTextColor();
        }

        boolean guessed = binding.getQuiz().guessWord(text);
        if (guessed) {
            for (Button b : answerButtons) {
                b.setTextColor(originalButtonColor);
            }
        } else {
            answerButton.setTextColor(ContextCompat.getColor(this, R.color.error));
        }

        if (binding.getQuiz().isFinished()) {
            finalizeSentence();
        }
    }

    private void finalizeSentence() {
        if (binding.getQuiz().canBeMarkedAsDone()) {
            binding.finalMessage.setText(R.string.correct);
            binding.markAsDone.setVisibility(View.VISIBLE);
        } else {
            binding.finalMessage.setText(R.string.too_many_errors);
            binding.markAsDone.setVisibility(View.GONE);
        }
        binding.quizButtons.setVisibility(View.GONE);
        binding.finalButtons.setVisibility(View.VISIBLE);

        binding.repeatLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSentenceStatusAndGotoNext(SentenceStatus.AGAIN);
            }
        });
        binding.markAsDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSentenceStatusAndGotoNext(SentenceStatus.DONE);
            }
        });
        binding.ignoreSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSentenceStatusAndGotoNext(SentenceStatus.IGNORE);
            }
        });
        binding.copyToClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] strings = getStringsToTranslate();
                showAlertDialog(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShareUtils.copyToClipboard(SentenceQuizActivity.this, strings[which]);
                    }
                });
            }
        });
        binding.shareTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] strings = getStringsToTranslate();
                showAlertDialog(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShareUtils.shareWithTranslate(SentenceQuizActivity.this, strings[which]);
                    }
                });
            }
        });
    }

    private void showAlertDialog(String[] strings, DialogInterface.OnClickListener listener) {
        if (strings.length == 0) {
            Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show();
        } else if (strings.length == 1) {
            listener.onClick(null, 0);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(SentenceQuizActivity.this);
            builder.setTitle(R.string.select_text);
            builder.setItems(strings, listener);
            builder.show();
        }
    }

    private String[] getStringsToTranslate() {
        String targetSentence = binding.getQuiz().getSentence().targetSentence;
        List<WordChunk> chunks = StringUtils.getWordChunks(targetSentence);
        if (chunks.size() <= 1) {
            return new String[] {targetSentence};
        }
        String[] res = new String[chunks.size() + 1];
        res[0] = targetSentence;
        for (int i = 0; i < chunks.size(); i++) {
            res[i+1] = chunks.get(i).word;
        }
        return res;
    }

    private void updateSentenceStatusAndGotoNext(SentenceStatus status) {
        Sentence sentence = binding.getQuiz().getSentence();
        SentenceCollectionsService.updateStatus(sentence, status);
        startRandom(this, sentence.collectionId, sentence.sentenceId);
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
