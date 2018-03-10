package com.edge.coin;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by user1 on 2018-03-09.
 */

public class SetRetrofit {
    public static ApiService setRefrofit(){
        /*HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();*/
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ApiService.BASE_URL)
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        return apiService;
    }
}
