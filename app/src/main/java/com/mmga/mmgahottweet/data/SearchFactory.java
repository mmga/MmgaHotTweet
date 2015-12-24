package com.mmga.mmgahottweet.data;


import com.mmga.mmgahottweet.data.model.Twitter;

import rx.Observable;

public class SearchFactory {

    static TweetApi twitterService;

    public static void prepareToSearch(String accessToken) {
        twitterService = ServiceGenerator.createBearerTokenService(TweetApi.class, accessToken);
    }

    public static Observable<Twitter> search(String content) {
//        return search(content, DEFAULT_COUNT, DEFAULT_GEO_CODE, DEFAULT_LANGUAGE);
        return twitterService.getTwitter(content, Constant.DEFAULT_COUNT);
    }


    public static Observable<Twitter> search(String content, String maxId) {
        return twitterService.getTwitter(content, Constant.DEFAULT_COUNT, maxId);
    }


    public static Observable<Twitter> search(String content, int count) {
        return search(content, count, Constant.DEFAULT_GEO_CODE, Constant.DEFAULT_LANGUAGE);
    }


    public static Observable<Twitter> search( String content, int count,
                                                 String geocode, String language) {
        return twitterService.getTwitter(content, count, geocode, language);
    }
}
