package com.mmga.mmgahottweet.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncodeUtil {

    static public String urlEncodeString(String input) {
        String encodedStr = "";
        try {
            encodedStr = URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedStr;
    }
}
