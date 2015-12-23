package com.mmga.mmgahottweet.data;

import com.mmga.mmgahottweet.data.model.Status;
import com.mmga.mmgahottweet.data.model.Twitter;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class LoadDataTransFormer implements Observable.Transformer<Twitter, List<Status>> {

    @Override
    public Observable<List<Status>> call(Observable<Twitter> twitterObservable) {
        return twitterObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Twitter, List<Status>>() {
                    @Override
                    public List<Status> call(Twitter twitter) {
                        return twitter.getStatuses();
                    }
                });
    }
}
