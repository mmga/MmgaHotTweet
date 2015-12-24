package com.mmga.mmgahottweet.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.mmga.mmgahottweet.R;
import com.mmga.mmgahottweet.data.Constant;
import com.mmga.mmgahottweet.data.LoadDataTransFormer;
import com.mmga.mmgahottweet.data.SearchFactory;
import com.mmga.mmgahottweet.data.ServiceGenerator;
import com.mmga.mmgahottweet.data.TweetApi;
import com.mmga.mmgahottweet.data.model.Status;
import com.mmga.mmgahottweet.data.model.Token;
import com.mmga.mmgahottweet.data.model.Twitter;
import com.mmga.mmgahottweet.utils.SharedPrefsUtil;
import com.mmga.mmgahottweet.utils.StatusBarCompat;
import com.mmga.mmgahottweet.utils.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerViewAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeLayout;
    //    Location mLocation;
    RecyclerView mRecyclerView;
    Observable<Twitter> observable;
    FloatingActionButton fab;
    private String currentSearchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        ToastUtil.register(this);

        init();

        initPrefs();//读取初始设置

//        initLocation();
        getInitToken();

    }

    private void init() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeLayout.setEnabled(false);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(currentSearchText);
            }
        });
        mSwipeLayout.post(new Runnable() {//显示loading动画
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initPrefs() {
        currentSearchText = SharedPrefsUtil.getValue(this, "config", "lastSearchedText", Constant.DEFAULT_CONTENT);
    }


    private void loadData(String content) {
        String encodedString = urlEncodeString(content);
        Observable.Transformer<Twitter, List<Status>> loadDataTransFormer = new LoadDataTransFormer();
        observable = SearchFactory.search(encodedString);
        observable.compose(loadDataTransFormer)
                .subscribe(new Subscriber<List<Status>>() {
                    @Override
                    public void onCompleted() {
                        mSwipeLayout.setRefreshing(false);
                        Log.d("mmga", "loadData completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSwipeLayout.setRefreshing(false);
                        Log.d("mmga", "loadData error");
                    }

                    @Override
                    public void onNext(List<Status> status) {
                        mAdapter.refreshAdapterData(status);
                    }
                });
    }

    private void loadMoreData(String content) {
        String encodedString = urlEncodeString(content);
        Observable.Transformer<Twitter, List<Status>> loadDataTransFormer = new LoadDataTransFormer();
        observable = SearchFactory.search(encodedString);
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
                        mSwipeLayout.setEnabled(true);//正确获取token之后才可以下拉刷新
                        fab.show();//获取token之后才能搜索
                        loadData(currentSearchText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("mmga", "getTokenError");
                        mSwipeLayout.setRefreshing(false);
                        ToastUtil.showLong("同学，要记得科学上网啊");
                    }

                    @Override
                    public void onNext(Token token) {
                        SearchFactory.prepareToSearch(token.getAccessToken());
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.fab):
                openSearchDialog();
                fab.hide();
                break;
        }
    }

    private void openSearchDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        FrameLayout searchView = (FrameLayout) getLayoutInflater().inflate(R.layout.search_dialog, null);
        final MaterialEditText editText = (MaterialEditText) searchView.findViewById(R.id.edit_text);
        builder.setView(searchView)
                .setTitle(R.string.search_title)
                .setPositiveButton(R.string.search_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = editText.getText().toString();

                        if (!input.equals("")) {
                            loadData(input);
                            mSwipeLayout.setRefreshing(true);
                            currentSearchText = input;
                        } else {
                            dialog.dismiss();
                        }
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        fab.show();
                    }
                })
                .show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPrefsUtil.putValue(this, "config", "lastSearchedText", currentSearchText);
    }

    private String urlEncodeString(String input) {
        String encodedStr = "";
        try {
            encodedStr = URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedStr;
    }


//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case Constant.REFRESH:
//                    Log.d("mmga", "refresh");
//                    loadData(currentSearchText);
//                    mSwipeLayout.setRefreshing(false);
//                    break;
//            }
//            return false;
//        }
//    });

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