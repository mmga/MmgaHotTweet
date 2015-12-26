package com.mmga.mmgahottweet.utils;

import com.mmga.mmgahottweet.data.Constant;

/**
 * Created by mmga on 2015/12/25.
 */
public class LangCodeUtil {
    public static String LANG_DEFAULT_CODE= "default";
    public static String LANG_CHINESE_CODE= "zh";
    public static String LANG_ENGLISH_CODE= "en";
    public static String LANG_GERMAN_CODE= "de";
    public static String LANG_JAPANESE_CODE = "ja";
    public static String LANG_FRENCH_CODE= "fr";
    public static String LANG_KOREAN_CODE= "ko";


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
