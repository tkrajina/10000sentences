package info.puzz.a10000sentences.api;

import info.puzz.a10000sentences.apimodels.InfoVO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SentencesService {
    @GET("info.json")
    Call<InfoVO> info(@Query("random") int random);
}
