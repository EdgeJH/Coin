package com.edge.coin;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by user1 on 2018-03-09.
 */

public interface ApiService {
    static final String BASE_URL ="https://crix-api-endpoint.upbit.com/v1/crix/candles/";

    @GET("minutes/1")
    Call<List<DataModel>> get1MinuteCandle (@Query("code") String code, @Query("count")int count, @Query("to")String to);
}
