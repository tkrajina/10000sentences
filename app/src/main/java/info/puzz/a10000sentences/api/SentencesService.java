package info.puzz.a10000sentences.api;

import info.puzz.a10000sentences.apimodels.InfoVO;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SentencesService {
    @GET("info")
    Call<InfoVO> info();
}
