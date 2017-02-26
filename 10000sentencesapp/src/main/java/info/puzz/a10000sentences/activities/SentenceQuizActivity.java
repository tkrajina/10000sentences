package info.puzz.a10000sentences.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import info.puzz.a10000sentences.Application;
import info.puzz.a10000sentences.Constants;
import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivitySentenceQuizBinding;
import info.puzz.a10000sentences.logic.AnnotationService;
import info.puzz.a10000sentences.logic.SentenceCollectionsService;
import info.puzz.a10000sentences.models.Annotation;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.Sentence;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.models.SentenceStatus;
import info.puzz.a10000sentences.models.WordAnnotation;
import info.puzz.a10000sentences.utils.ShareUtils;
import info.puzz.a10000sentences.utils.SleepUtils;
import info.puzz.a10000sentences.utils.Speech;
import info.puzz.a10000sentences.utils.TatoebaUtils;
import info.puzz.a10000sentences.utils.TranslateUtils;
import info.puzz.a10000sentences.utils.WordChunk;
import info.puzz.a10000sentences.utils.WordChunkUtils;

public class SentenceQuizActivity extends BaseActivity {

    private static final String TAG = SentenceQuizActivity.class.getSimpleName();

    public static enum Type {
        ONLY_KNOWN,
        KNOWN_AND_UNKNOWN,
        RETURN_BACK,
    }

    private static final String ARG_SENTENCE_ID = "arg_sentence_id";
    private static final String ARG_TYPE = "arg_type";

    @Inject Dao dao;
    @Inject SentenceCollectionsService sentenceCollectionsService;
    @Inject AnnotationService annotationService;

    private long started;
    //private boolean skipSentenceCandidate;

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

    public static <T extends BaseActivity> void startRandom(T activity, Dao dao, SentenceCollectionsService sentenceCollectionsService, String collectionId, Type type, String exceptSentenceId) {
        SentenceCollection collection = dao.getCollection(collectionId);

        Sentence sentence;
        if (type == Type.ONLY_KNOWN) {
            sentence = sentenceCollectionsService.getRandomKnownSentence(activity, collection, exceptSentenceId);
        } else {
            sentence = sentenceCollectionsService.nextSentence(activity, collection, exceptSentenceId);
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
        Application.COMPONENT.injectActivity(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sentence_quiz);
        //FontAwesomeIcons.fa_volume_up

        type = (Type) getIntent().getSerializableExtra(ARG_TYPE);
        sentenceId = getIntent().getStringExtra(ARG_SENTENCE_ID);

        Sentence sentence = dao.getSentenceBySentenceId(sentenceId);
        if (sentence == null) {
            Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
            CollectionsActivity.startDefaultCollections(this);
            return;
        }

        SentenceCollection collection = dao.getCollection(sentence.collectionId);
        List<Sentence> randomSentences = dao.getRandomSentences(collection);

        targetLanguage = dao.getLanguage(collection.targetLanguage);
        setTitle(targetLanguage.name);

        binding.setQuiz(new SentenceQuiz(sentence, 4, randomSentences));
        binding.setCollection(collection);

        if (targetLanguage.isRightToLeft()) {
            binding.targetSentence.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
        }

        binding.sentenceStatus.setTextColor(ContextCompat.getColor(this, binding.getQuiz().getSentence().getSentenceStatus().getColor()));

        answerButtons = new Button[] { binding.answer1, binding.answer2, binding.answer3, binding.answer4, };
        setupAnswerButtons();

        binding.startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.startQuizGroup.setVisibility(View.GONE);
                binding.quizSentenceGroup.setVisibility(View.VISIBLE);
                binding.quizButtons.setVisibility(View.VISIBLE);
                binding.knownSentence.setTextColor(R.color.inactive);
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

    private void setupAnswerButtons() {
        for (final Button answerButton : answerButtons) {
            answerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitResponse(answerButton, answerButton.getText().toString());
                }
            });
            answerButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String text = answerButton.getText().toString();
                    TranslateUtils.translate(SentenceQuizActivity.this, text);
                    return true;
                }
            });
        }
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

        //reloadSkipSentencesStatus();
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
            case R.id.action_previous:
                gotoPreviousSentence();
                break;
            case R.id.action_share_with:
                final String[] strings = getStringsFromSentence(true);
                showAlertDialog(strings, new DialogInterface.OnClickListener() {
                    @Override
                   public void onClick(DialogInterface dialog, int which) {
                        ShareUtils.shareWithTranslate(SentenceQuizActivity.this, strings[which]);
                    }
                });                break;
            case R.id.action_done_sentence:
                updateSentenceStatusAndGotoNext(SentenceStatus.DONE);
                break;
            case R.id.action_todo_sentence:
                updateSentenceStatusAndGotoNext(SentenceStatus.TODO);
                break;
            case R.id.action_ignored_sentence:
                updateSentenceStatusAndGotoNext(SentenceStatus.IGNORE);
                break;
            case R.id.action_repeat_sentence:
                updateSentenceStatusAndGotoNext(SentenceStatus.REPEAT);
                break;
        }
        return true;
    }

/*    private void reloadSkipSentencesStatus() {
        skipSentenceCandidate = false;
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                return sentenceCollectionsService.isCandidateForSkipping(binding.getCollection().collectionID);
            }

            @Override
            protected void onPostExecute(Boolean isCandidate) {
                skipSentenceCandidate = isCandidate.booleanValue();
            }
        }.execute();
    }*/

    private void gotoPreviousSentence() {
        Sentence sentence = sentenceCollectionsService.findPreviousSentence(binding.getCollection().collectionID);
        if (sentence == null) {
            Toast.makeText(this, R.string.cannot_find_sentence, Toast.LENGTH_SHORT).show();
        } else {
            startSentence(this, sentence.sentenceId, type);
        }
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

    private void submitResponse(Button answerButton, final String text) {
        if (originalButtonColor == null) {
            originalButtonColor = answerButton.getCurrentTextColor();
        }

        binding.translateWord.setText(getString(R.string.translate) + ":" + text);
        binding.translateWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslateUtils.translate(SentenceQuizActivity.this, text);
            }
        });
        binding.translateWord.setVisibility(View.VISIBLE);

        speech.speech(text);
        showAnnotation(text);

        boolean guessed = binding.getQuiz().guessWord(text);
        if (guessed) {
            resetButtons();
        } else {
            answerButton.setTextColor(ContextCompat.getColor(this, R.color.error));
        }

        if (binding.getQuiz().isFinished()) {
            speech.speech(binding.getQuiz().getSentence().targetSentence);
            finalizeSentence();
        }
    }

    private void resetButtons() {
        for (Button b : answerButtons) {
            b.setTextColor(originalButtonColor);
            b.setMaxWidth(0);
        }
    }

    private void showAnnotation(final String text) {
        new AsyncTask<Void, Void, Annotation>() {

            @Override
            protected Annotation doInBackground(Void... voids) {
                WordAnnotation wordAnnotation = new Select()
                        .from(WordAnnotation.class)
                        .where("word=? and collection_id=?", text, binding.getQuiz().getSentence().collectionId)
                        .executeSingle();
                if (wordAnnotation != null) {
                    return Annotation.load(Annotation.class, wordAnnotation.annotationId);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Annotation annotation) {
                if (annotation == null) {
                    binding.annotationGroup.setVisibility(View.GONE);
                } else {
                    binding.annotationWord.setText(text);
                    binding.annotation.setText(": " + annotation.annotation);
                    binding.annotationGroup.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
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
        binding.sentenceStatus.setVisibility(View.GONE);
        binding.readSentence.setVisibility(View.VISIBLE);

        binding.readSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speech.speech(binding.getQuiz().getSentence().targetSentence);
            }
        });
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
        binding.translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] strings = getStringsFromSentence(true);
                showAlertDialog(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TranslateUtils.translate(SentenceQuizActivity.this, strings[which]);
                    }
                });
            }
        });
        binding.annotateWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] strings = getStringsFromSentence(false);
                showAlertDialog(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String word = strings[which];
                        gotoAnnotation(word);
                    }
                });
            }
        });
    }

/*    private void askToSkipSentences() {
        final int skipSentences = 100;
        DialogUtils.showYesNoButton(this, getString(R.string.skip_sentences, skipSentences), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (Dialog.BUTTON_POSITIVE == which) {
                    sentenceCollectionsService.skipSentences(binding.getCollection().collectionID, skipSentences);
                    DBG.todo();
                }
            }
        });
    }*/

    private void gotoAnnotation(String word) {
        String collectionId = binding.getQuiz().getSentence().collectionId;
        final List<Annotation> annodations = annotationService.findAnnotations(collectionId, word);
        if (annodations == null || annodations.size() == 0) {
            AnnotationActivity.start(SentenceQuizActivity.this, word, collectionId);
        } else if (annodations.size() == 1) {
            EditAnnotationActivity.start(SentenceQuizActivity.this, annodations.get(0).getId());
        } else {
            String[] annotationStrings = new String[annodations.size()];
            for (int i = 0; i < annodations.size(); i++) {
                annotationStrings[i] = annodations.get(i).annotation;
            }

            new AlertDialog.Builder(this)
                    .setTitle(R.string.select_annotation)
                    .setItems(annotationStrings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            EditAnnotationActivity.start(SentenceQuizActivity.this, annodations.get(which).getId());
                        }
                    })
                    .show();
        }
    }

    private void showAlertDialog(String[] strings, DialogInterface.OnClickListener listener) {
        if (strings.length == 0) {
            Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show();
        } else if (strings.length == 1) {
            listener.onClick(null, 0);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.select_text);
            builder.setItems(strings, listener);
            builder.show();
        }
    }

    private String[] getStringsFromSentence(boolean includingSentence) {
        String targetSentence = binding.getQuiz().getSentence().targetSentence;
        List<WordChunk> chunks = WordChunkUtils.getWordChunks(targetSentence);
        if (chunks.size() <= 1) {
            return new String[] {targetSentence};
        }
        List<String> strings = new ArrayList<>();
        if (includingSentence) {
            strings.add(targetSentence);
        }
        for (WordChunk chunk : chunks) {
            strings.add(chunk.word);
        }
        return strings.toArray(new String[strings.size()]);
    }

    private void updateSentenceStatusAndGotoNext(SentenceStatus status) {

/*        if (SentenceStatus.DONE == status && skipSentenceCandidate) {
            askToSkipSentences();
        }*/

        if (type == Type.RETURN_BACK) {
            onBackPressed();
        } else {
            Sentence sentence = binding.getQuiz().getSentence();
            sentenceCollectionsService.updateStatus(sentence, status, started, System.currentTimeMillis());
            startRandom(this, dao, sentenceCollectionsService, sentence.collectionId, type, sentence.sentenceId);
        }
    }

}
