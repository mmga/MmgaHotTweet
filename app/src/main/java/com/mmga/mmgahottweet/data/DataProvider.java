package com.mmga.mmgahottweet.data;

public class DataProvider {



    private static DataProvider instance = null;

    private DataProvider() {
    }

    public static synchronized DataProvider getInstance() {
        if (instance == null) {
            instance = new DataProvider();
        }
        return instance;
    }


    public void authAndLoad() {

    }
}
