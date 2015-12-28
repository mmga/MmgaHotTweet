package com.mmga.mmgahottweet.utils;

import com.mmga.mmgahottweet.Constant;


public class LangCodeUtil {
    static final String LANG_DEFAULT_CODE= "default";
    static final String LANG_CHINESE_CODE= "zh";
    static final String LANG_ENGLISH_CODE= "en";
    static final String LANG_GERMAN_CODE= "de";
    static final String LANG_JAPANESE_CODE = "ja";
    static final String LANG_FRENCH_CODE= "fr";
    static final String LANG_KOREAN_CODE= "ko";


    public static String getLangCode(int langPos) {
        switch (langPos) {
            case Constant.LANG_DEFAULT:
                return LANG_DEFAULT_CODE;
            case Constant.LANG_CHINESE:
                return LANG_CHINESE_CODE;
            case Constant.LANG_ENGLISH:
                return LANG_ENGLISH_CODE;
            case Constant.LANG_JAPANESE:
                return LANG_JAPANESE_CODE;
            case Constant.LANG_KOREAN:
                return LANG_KOREAN_CODE;
            case Constant.LANG_GERMAN:
                return LANG_GERMAN_CODE;
            case Constant.LANG_FRENCH:
                return LANG_FRENCH_CODE;
        }
        return LANG_DEFAULT_CODE;
    }
}
