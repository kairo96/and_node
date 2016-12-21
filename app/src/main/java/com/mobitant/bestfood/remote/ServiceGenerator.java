package com.mobitant.bestfood.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobitant.bestfood.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 서버와 통신하기 위한 리트로핏을 사용하기 위한 클래스
 */
public class ServiceGenerator {
    /**
     * 원격 호출을 정의한 인터페이스 메소드를 호출할 수 있는 서비스를 생성
     * @param serviceClass 원격 호출 메소드를 정의한 인터페이스
     * @return 인터페이스 구현체
     */
    public static <S> S createService(Class<S> serviceClass) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retorfit = new Retrofit.Builder()
                .baseUrl(RemoteService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        return retorfit.create(serviceClass);
    }
}
