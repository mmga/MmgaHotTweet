package com.mmga.mmgahottweet.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.mmga.mmgahottweet.R;
import com.mmga.mmgahottweet.data.LoadDataTransFormer;
import com.mmga.mmgahottweet.data.SearchFactory;
import com.mmga.mmgahottweet.data.ServiceGenerator;
import com.mmga.mmgahottweet.data.TweetApi;
import com.mmga.mmgahottweet.data.model.Status;
import com.mmga.mmgahottweet.data.model.Token;
import com.mmga.mmgahottweet.data.model.Twitter;
import com.mmga.mmgahottweet.utils.ToastUtil;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    RecyclerViewAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    //    Location mLocation;
    Observable<Twitter> observable;
    private String mContent = "avatar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        ToastUtil.register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLayoutManager = new LinearLayoutManager(this);
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
//        initLocation();

        getInitToken();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadData("忒会玩");
            }
        });
    }


    private void loadData(String content) {
        Observable.Transformer<Twitter, List<Status>> loadDataTransFormer = new LoadDataTransFormer();
        observable = SearchFactory.search(content);
        observable.compose(loadDataTransFormer)
                .subscribe(new Subscriber<List<Status>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("mmga", "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("mmga", "error");
                    }

                    @Override
                    public void onNext(List<Status> status) {
                        mAdapter.refreshAdapterData(status);
                    }
                });
    }

    private void loadMoreData(String content) {
        Observable.Transformer<Twitter, List<Status>> loadDataTransFormer = new LoadDataTransFormer();
        observable = SearchFactory.search(content);
        observable.compose(loadDataTransFormer)
                .subscribe(new Subscriber<List<Status>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("mmga", "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("mmga", "error");
                    }

                    @Override
                    public void onNext(List<Status> status) {
                        mAdapter.addAdapterData(status);
                    }
                });
    }


    private void getInitToken() {
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
                        loadData(mContent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("mmga", "getTokenError");
//                        Toast.makeText(MainActivity.this, "同学，要记得科学上网啊", Toast.LENGTH_SHORT).show();
                        ToastUtil.showLong("同学，要记得科学上网啊");
                    }

                    @Override
                    public void onNext(Token token) {
                        SearchFactory.prepareToSearch(token.getAccessToken());
                    }
                });

    }

//    private void initLocation() {
//        Double latitude = 0.0;
//        Double longitude = 0.0;
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                    1000 * 60, 100, new LocationListener() {
//                        @Override
//                        public void onLocationChanged(Location location) {
//                            if (location != null) { //如果获取到location 就把经纬度信息存到SharedPreferences中
//                                mLocation = location;
//                                Log.d("mmga", "location = " + mLocation.getLatitude() + " + " + mLocation.getLongitude());
//                                SharedPrefsUtil.putValue(MainActivity.this,
//                                        "config", "location_latitude",
//                                        Double.doubleToRawLongBits(mLocation.getLatitude()));//转换为long再存，不会损失精度
//                                SharedPrefsUtil.putValue(MainActivity.this,
//                                        "config", "location_longitude",
//                                        Double.doubleToRawLongBits(mLocation.getLongitude()));
//                            }
//                        }
//
//                        @Override
//                        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                        }
//
//                        @Override
//                        public void onProviderEnabled(String provider) {
//
//                        }
//
//                        @Override
//                        public void onProviderDisabled(String provider) {
//
//                        }
//                    });
//
//            //如果不能直接获取到location，尝试从SharedPreferences读取，即上次成功获取的坐标
//            if (mLocation == null) {
//                latitude = Double.longBitsToDouble(
//                        SharedPrefsUtil.getValue(MainActivity.this,
//                                "config", "location_latitude", 0));
//                longitude = Double.longBitsToDouble(
//                        SharedPrefsUtil.getValue(MainActivity.this,
//                                "config", "location_longitude", 0));
//                if (latitude == 0 && longitude == 0) {
//                    //如果从来没成功获取过坐标，弹出提示。
//                    ToastUtil.showLong("位置获取失败");
//                }
//            }
//            Log.d("mmga", "location = " + latitude + " + " + longitude);
//        }
//    }
}