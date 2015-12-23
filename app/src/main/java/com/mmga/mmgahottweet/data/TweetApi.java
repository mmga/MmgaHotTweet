package com.mmga.mmgahottweet.data;

import com.mmga.mmgahottweet.data.model.Token;
import com.mmga.mmgahottweet.data.model.Twitter;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;


public interface TweetApi {

    @POST("oauth2/token")
    Observable<Token> getToken(@Query("grant_type") String string);


    @GET("search/tweets.json")
    Observable<Twitter> getTwitter(@Query("q") String content,
                                   @Query("count") int count);

    @GET("search/tweets.json")
    Observable<Twitter> getTwitter(@Query("q") String content,
                                   @Query("count") int count,
                                   @Query("geocode") String geocode,
                                   @Query("lang") String language);
}
