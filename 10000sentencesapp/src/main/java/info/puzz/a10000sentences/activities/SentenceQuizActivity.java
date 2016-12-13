package info.puzz.a10000sentences.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import info.puzz.a10000sentences.Constants;
import info.puzz.a10000sentences.Preferences;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.SentenceCollectionsService;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivitySentenceQuizBinding;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceStatus;
import info.puzz.a10000sentences.utils.ShareUtils;
import info.puzz.a10000sentences.utils.SleepUtils;
import info.puzz.a10000sentences.utils.Speech;
import info.puzz.a10000sentences.utils.StringUtils;
import info.puzz.a10000sentences.utils.TatoebaUtils;
import info.puzz.a10000sentences.utils.WordChunk;
import temp.DBG;

public class SentenceQuizActivity extends BaseActivity {

    private static final String TAG = SentenceQuizActivity.class.getSimpleName();
    private long started;

    public static enum Type {
        ONLY_KNOWN,
        KNOWN_AND_UNKNOWN,
        BACK_TO_COLLECTION,
    }

    private static final String ARG_SENTENCE_ID = "arg_sentence_id";
    private static final String ARG_TYPE = "arg_type";

    ActivitySentenceQuizBinding binding;

    private Button[] answerButtons;
    private Integer originalButtonColor;
    private Speech speech;
    private Language targetLanguage;

    private Type type;
    private String sentenceId;

    public static <T extends BaseActivity> void startSentence(T activity, String sentenceId, Type type) {
        if (activity.getClass().equals(SentenceQuizActivity.class)) {
            // if already in a quiz, just replace the current activity on the stack:
            activity.finish();
        }
        Intent intent = new Intent(activity, SentenceQuizActivity.class)
                .putExtra(ARG_SENTENCE_ID, sentenceId)
                .putExtra(ARG_TYPE, type);
        activity.startActivity(intent);
    }

    public static <T extends BaseActivity> void startRandom(T activity, String collectionId, Type type, String exceptSentenceId) {
        SentenceCollection collection = Dao.getCollection(collectionId);

        Sentence sentence;
        if (type == Type.ONLY_KNOWN) {
            sentence = SentenceCollectionsService.getRandomKnownSentence(activity, collection, exceptSentenceId);
        } else {
            sentence = SentenceCollectionsService.nextSentence(activity, collection, exceptSentenceId);
        }

        if (sentence == null) {
            Toast.makeText(activity, activity.getString(R.string.no_sentence_found), Toast.LENGTH_SHORT).show();
            return;
        }

        startSentence(activity, sentence.sentenceId, type);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sentence_quiz);
        //FontAwesomeIcons.fa_volume_up

        type = (Type) getIntent().getSerializableExtra(ARG_TYPE);
        sentenceId = getIntent().getStringExtra(ARG_SENTENCE_ID);

        Sentence sentence = Dao.getSentenceBySentenceId(sentenceId);
        if (sentence == null) {
            Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
            CollectionsActivity.start(this);
            return;
        }

        SentenceCollection collection = Dao.getCollection(sentence.collectionId);
        List<Sentence> randomSentences = Dao.getRandomSentences(collection);

        targetLanguage = Dao.getLanguage(collection.targetLanguage);
        setTitle(targetLanguage.name);

        binding.setQuiz(new SentenceQuiz(sentence, 4, randomSentences));
        binding.setCollection(collection);

        if (targetLanguage.isRightToLeft()) {
            binding.targetSentence.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
        }

        binding.sentenceStatus.setTextColor(ContextCompat.getColor(this, binding.getQuiz().getSentence().getSentenceStatus().getColor()));

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
        binding.knownSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.getQuiz().nextKnownSentenceAlternative();
            }
        });

        adjustFontSize();
    }

    private void adjustFontSize() {
        for (String lang : Constants.LANGS_WITH_LARGER_FONTS) {
            if (targetLanguage.languageId.equals(lang)) {
                for (Button answerButton : answerButtons) {
                    answerButton.setTextSize(answerButton.getTextSize() * 1.2F);
                }
                binding.targetSentence.setTextSize(binding.targetSentence.getTextSize() * 1.2F);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        speech = new Speech(this, targetLanguage);
        // TODO: the time will be reset on orientation change!!!
        started = System.currentTimeMillis();
        SleepUtils.disableSleep(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        speech.shutdown();
        SleepUtils.enableSleep(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sentence, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_in_tatoteba:
                openLink();
                break;
            case R.id.action_read_sentence:
                speech.speech(binding.getQuiz().getSentence().targetSentence);
                break;
            case R.id.action_done_sentence:
                SentenceCollectionsService.updateStatus(binding.getQuiz().getSentence(), SentenceStatus.DONE, started);
                CollectionActivity.start(this, binding.getQuiz().getSentence().collectionId);
                break;
            case R.id.action_todo_sentence:
                SentenceCollectionsService.updateStatus(binding.getQuiz().getSentence(), SentenceStatus.TODO, started);
                CollectionActivity.start(this, binding.getQuiz().getSentence().collectionId);
                break;
            case R.id.action_ignored_sentence:
                SentenceCollectionsService.updateStatus(binding.getQuiz().getSentence(), SentenceStatus.IGNORE, started);
                CollectionActivity.start(this, binding.getQuiz().getSentence().collectionId);
                break;
            case R.id.action_repeat_sentence:
                SentenceCollectionsService.updateStatus(binding.getQuiz().getSentence(), SentenceStatus.REPEAT, started);
                CollectionActivity.start(this, binding.getQuiz().getSentence().collectionId);
                break;
        }
        return true;
    }

    private void openLink() {
        try {
            String url = TatoebaUtils.getTatotebaUrl(binding.getQuiz().getSentence());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(this, R.string.error_opening_link, Toast.LENGTH_SHORT);
        }
    }

    private void submitResponse(Button answerButton, String text) {
        if (originalButtonColor == null) {
            originalButtonColor = answerButton.getCurrentTextColor();
        }

        if (Preferences.isWordToClipboard(this)) {
            ShareUtils.copyToClipboard(this, text);
        }

        speech.speech(text);

        boolean guessed = binding.getQuiz().guessWord(text);
        if (guessed) {
            for (Button b : answerButtons) {
                b.setTextColor(originalButtonColor);
            }
        } else {
            answerButton.setTextColor(ContextCompat.getColor(this, R.color.error));
        }

        if (binding.getQuiz().isFinished()) {
            speech.speech(binding.getQuiz().getSentence().targetSentence);
            finalizeSentence();
        }
    }

    private void finalizeSentence() {
        if (binding.getQuiz().canBeMarkedAsDone(this)) {
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
                updateSentenceStatusAndGotoNext(SentenceStatus.REPEAT);
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
        if (type == Type.BACK_TO_COLLECTION) {
            CollectionActivity.start(this, binding.getQuiz().getSentence().getCollectionId());
        } else {
            Sentence sentence = binding.getQuiz().getSentence();
            SentenceCollectionsService.updateStatus(sentence, status, started);
            startRandom(this, sentence.collectionId, type, sentence.sentenceId);
        }
    }

}
