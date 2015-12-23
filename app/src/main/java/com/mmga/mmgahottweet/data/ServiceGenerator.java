package com.mmga.mmgahottweet.data;

import android.util.Base64;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class ServiceGenerator {


    private static Retrofit.Builder builder = new Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());



    public static <T> T createService(Class<T> serviceClass) {
        String credentials = ApiKey.CONSUMER_KEY + ":" + ApiKey.CONSUMER_SECRET;
        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().clear();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", basic)
                        .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = builder.client(httpClient)
                .baseUrl("https://api.twitter.com/")
                .build();
        return retrofit.create(serviceClass);
    }


    public static <T> T createBearerTokenService(Class<T> serviceClazz, String accessToken) {
       // final String bearer = "Bearer " + Base64.encodeToString(accessToken.getBytes(), Base64.NO_WRAP);
        final String bearer = "Bearer " + accessToken;
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().clear();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Authorization", bearer);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = builder.client(httpClient)
                .baseUrl("https://api.twitter.com/1.1/")
                .build();
        return retrofit.create(serviceClazz);
    }


}
