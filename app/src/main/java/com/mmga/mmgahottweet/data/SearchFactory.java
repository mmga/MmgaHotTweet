package com.mmga.mmgahottweet.data;


import com.mmga.mmgahottweet.Constant;
import com.mmga.mmgahottweet.data.model.Twitter;
import com.mmga.mmgahottweet.utils.LangCodeUtil;
import com.mmga.mmgahottweet.utils.LogUtil;

import rx.Observable;

public class SearchFactory {

    private static TweetApi twitterService;

    public static void prepareToSearch(String accessToken) {
        twitterService = ServiceGenerator.createBearerTokenService(TweetApi.class, accessToken);
    }

    public static Observable<Twitter> search(String content, int langPos, String resultType, String geoCode) {
        String lang = null;
        if (langPos != 0) {
            lang = LangCodeUtil.getLangCode(langPos);
        }

        String geo = null;
        if (!geoCode.equals(Constant.DO_NOT_GEO)) {
            geo = geoCode;
        }
        LogUtil.d(content + " + " + Constant.DEFAULT_COUNT + " + " + lang + " + " + resultType + " + " + geo);
        return twitterService.getTwitter(content, Constant.DEFAULT_COUNT, lang, resultType, geo);
    }


    public static Observable<Twitter> search(String content, String maxId, int langPos, String resultType, String geoCode) {
        String lang = null;
        if (langPos != 0) {
            lang = LangCodeUtil.getLangCode(langPos);
        }

        String geo = null;
        if (!geoCode.equals(Constant.DO_NOT_GEO)) {
            geo = geoCode;
        }

        LogUtil.d(content + " + " + Constant.DEFAULT_COUNT + " + " + maxId + " + " + lang + " + " + resultType + " + " + geo);
        return twitterService.getMoreTwitter(content, Constant.DEFAULT_COUNT, maxId, lang, resultType, geo);

    }

}
