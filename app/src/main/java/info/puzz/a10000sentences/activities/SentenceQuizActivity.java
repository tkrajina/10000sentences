package info.puzz.a10000sentences.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import info.puzz.a10000sentences.R;
import info.puzz.a10000sentences.api.Api;
import info.puzz.a10000sentences.apimodels.InfoVO;
import info.puzz.a10000sentences.apimodels.LanguageVO;
import info.puzz.a10000sentences.apimodels.SentenceCollectionVO;
import info.puzz.a10000sentences.dao.Dao;
import info.puzz.a10000sentences.databinding.ActivitySentenceQuizBinding;
import info.puzz.a10000sentences.models.Language;
import info.puzz.a10000sentences.models.SentenceCollection;
import info.puzz.a10000sentences.utils.DialogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SentenceQuizActivity extends BaseActivity {

    private static final String TAG = SentenceQuizActivity.class.getSimpleName();

    ActivitySentenceQuizBinding binding;

    private SentenceQuiz quiz;
    private Button[] answerButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sentence_quiz);
        //FontAwesomeIcons.fa_volume_up

        reloadLanguages();

        binding.setQuiz(new SentenceQuiz("Sonst noch irgendwelche schlauen Einfälle?", 4));

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

    private void reloadLanguages() {
        Call<InfoVO> info = Api.instance().info();
        info.enqueue(new Callback<InfoVO>() {
            @Override
            public void onResponse(Call<InfoVO> call, Response<InfoVO> response) {
                Log.i(TAG, String.valueOf(response.body()));
                InfoVO info = response.body();
                for (LanguageVO languageVO : info.getLanguages()) {
                    Language language = new Language()
                            .setLanguageId(languageVO.getAbbrev())
                            .setFamily(languageVO.getFamily())
                            .setName(languageVO.getName())
                            .setNativeName(languageVO.getNativeName())
                            .setRightToLeft(languageVO.isRightToLeft());
                    Dao.saveLanguage(language);
                }
                for (SentenceCollectionVO collectionVO : info.getSentenceCollections()) {
                    SentenceCollection col = new SentenceCollection()
                            .setCollectionID(String.format("%s-%s", collectionVO.getKnownLanguage(), collectionVO.getTargetLanguage()))
                            .setKnownLanguage(collectionVO.getKnownLanguage())
                            .setTargetLanguage(collectionVO.getTargetLanguage())
                            .setFilename(collectionVO.getFilename());
                    Dao.saveCollection(col);
                }
            }

            @Override
            public void onFailure(Call<InfoVO> call, Throwable t) {
                Log.i(TAG, t.getMessage(), t);
            }
        });
    }

    private void submitResponse(String text) {
        binding.getQuiz().guessWord(text);
        if (binding.getQuiz().isFinished()) {
            DialogUtils.showYesNoButton(this, "Yes!", null);
        }
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
