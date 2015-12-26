package com.mmga.mmgahottweet.data;

import com.mmga.mmgahottweet.data.model.Token;
import com.mmga.mmgahottweet.data.model.Twitter;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;


public interface TweetApi {
    //@Query("q")
    //@Query("count")
    //@Query("max_id")
    //@Query("result_type")
    //@Query("lang")
    //@Query("geocode")
    //@Query("since_id")
    //@Query("until")


    //获取token
    @POST("oauth2/token")
    Observable<Token> getToken(@Query("grant_type") String string);


    //搜索、刷新
    @GET("search/tweets.json")
    Observable<Twitter> getTwitter(@Query("q") String content,
                                   @Query("count") int count,
                                   @Query("lang") String lang,
                                   @Query("result_type") String resultType,
                                   @Query("geocode") String geoCode);

    //加载更多
    @GET("search/tweets.json")
    Observable<Twitter> getMoreTwitter(@Query("q") String content,
                                       @Query("count") int count,
                                       @Query("max_id") String maxId,
                                       @Query("lang") String lang,
                                       @Query("result_type") String resultType,
                                       @Query("geocode") String geoCode);


}
