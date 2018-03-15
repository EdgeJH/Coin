package com.edge.coin.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by user1 on 2018-03-09.
 */

public interface ApiService {
    static final String BASE_URL ="https://crix-api-endpoint.upbit.com/v1/crix/candles/";
    @GET("minutes/{time}")
    Call<List<Candle>> get1MinuteCandle (@Path("time")int chatTime, @Query("code") String code, @Query("count")int count, @Query("ciqrandom")long time);

    @GET("days")
    Call<List<Candle>> getDayCandle (@Query("code") String code, @Query("count")int count, @Query("ciqrandom")long time);
}
