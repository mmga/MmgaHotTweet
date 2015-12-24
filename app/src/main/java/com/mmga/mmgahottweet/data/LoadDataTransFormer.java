package com.mmga.mmgahottweet.data;

import com.mmga.mmgahottweet.data.model.Twitter;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class LoadDataTransFormer implements Observable.Transformer<Twitter, Twitter> {

    @Override
    public Observable<Twitter> call(Observable<Twitter> twitterObservable) {
        return twitterObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
