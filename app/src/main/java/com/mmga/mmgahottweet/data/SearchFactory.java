package com.mmga.mmgahottweet.data;


import com.mmga.mmgahottweet.data.model.Twitter;
import com.mmga.mmgahottweet.utils.LanguageCodeUtil;

import rx.Observable;

public class SearchFactory {

    static TweetApi twitterService;

    public static void prepareToSearch(String accessToken) {
        twitterService = ServiceGenerator.createBearerTokenService(TweetApi.class, accessToken);
    }

    public static Observable<Twitter> search(String content, int langPos, String resultType) {
        String lang = null;
        if (langPos != 0) {
            lang = LanguageCodeUtil.getLangCode(langPos);
        }

        return twitterService.getTwitter(content, Constant.DEFAULT_COUNT, lang, resultType);
    }


    public static Observable<Twitter> search(String content, String maxId, int langPos, String resultType) {
        String lang = null;
        if (langPos != 0) {
            lang = LanguageCodeUtil.getLangCode(langPos);
        }

        return twitterService.getMoreTwitter(content, Constant.DEFAULT_COUNT, maxId, lang, resultType);

    }

}
