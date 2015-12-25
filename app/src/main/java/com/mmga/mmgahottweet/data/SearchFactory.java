package com.mmga.mmgahottweet.data;


import com.mmga.mmgahottweet.data.model.Twitter;
import com.mmga.mmgahottweet.utils.LanguageCodeUtil;

import rx.Observable;

public class SearchFactory {

    static TweetApi twitterService;

    public static void prepareToSearch(String accessToken) {
        twitterService = ServiceGenerator.createBearerTokenService(TweetApi.class, accessToken);
    }

    // TODO: 2015/12/25 扩展性太差，每加一个可选的参数都要大改
    public static Observable<Twitter> search(String content, int langPos) {
        if (langPos != 0) {
            String langCode = LanguageCodeUtil.getLangCode(langPos);
            return twitterService.getTwitter(content, Constant.DEFAULT_COUNT, langCode);
        } else {
            return twitterService.getTwitter(content, Constant.DEFAULT_COUNT);
        }
    }


    public static Observable<Twitter> search(String content, String maxId, int langPos) {
        if (langPos != 0) {
            String langCode = LanguageCodeUtil.getLangCode(langPos);
            return twitterService.getMoreTwitter(content, Constant.DEFAULT_COUNT, maxId, langCode);
        } else {
            return twitterService.getTwitter(content, Constant.DEFAULT_COUNT, maxId);
        }
    }

}
