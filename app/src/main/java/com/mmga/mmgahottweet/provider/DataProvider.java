package com.mmga.mmgahottweet.provider;

import com.mmga.mmgahottweet.Constant;
import com.mmga.mmgahottweet.data.SearchFactory;
import com.mmga.mmgahottweet.data.ServiceGenerator;
import com.mmga.mmgahottweet.data.TweetApi;
import com.mmga.mmgahottweet.data.model.Status;
import com.mmga.mmgahottweet.data.model.Token;
import com.mmga.mmgahottweet.data.model.Twitter;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DataProvider {

    private String content;
    private String maxId;
    private int langPos;
    private String resultType;
    private String geoCode;


    private static DataProvider instance = null;

    private DataProvider() {
    }

    public static synchronized DataProvider getInstance() {
        if (instance == null) {
            instance = new DataProvider();
        }
        return instance;
    }


    public void loadData(final DataProviderCallback callback, final int loadType) {
        Observable<Twitter> observable;
        if (loadType == Constant.LOAD_TYPE_NEW) {
            observable = SearchFactory.search(content, langPos, resultType, geoCode);
        } else {
            observable = SearchFactory.search(content, maxId, langPos, resultType, geoCode);
        }
        observable.compose(new LoadDataTransFormer())
                .map(new Func1<Twitter, List<Status>>() {
                    @Override
                    public List<Status> call(Twitter twitter) {
                        return twitter.getStatuses();
                    }
                })
                .subscribe(new Subscriber<List<Status>>() {
                               @Override
                               public void onCompleted() {
                                   callback.OnDataComplete();
                               }

                               @Override
                               public void onError(Throwable e) {
                                   callback.OnDataError(e);
                               }

                               @Override
                               public void onNext(List<Status> status) {
                                   callback.OnDataSuccess(status,loadType);
                               }
                           }

                );
    }


    public void authAndLoadData(final DataProviderCallback callback) {
        TweetApi tokenService = ServiceGenerator.createService(TweetApi.class);
        tokenService.getToken("client_credentials")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<Token, Boolean>() {
                    @Override
                    public Boolean call(Token token) {
                        return token.getTokenType().equals("bearer");
                    }
                })
                .subscribe(new Subscriber<Token>() {
                    @Override
                    public void onCompleted() {
                        loadData(callback, Constant.LOAD_TYPE_NEW);
                        callback.OnAuthComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.OnAuthError(e);
                    }

                    @Override
                    public void onNext(Token token) {
                        SearchFactory.prepareToSearch(token.getAccessToken());
                    }
                });
    }



    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMaxId(String maxId) {
        this.maxId = maxId;
    }

    public void setLangPos(int langPos) {
        this.langPos = langPos;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
