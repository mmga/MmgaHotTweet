package com.mmga.mmgahottweet.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.mmga.mmgahottweet.Constant;
import com.mmga.mmgahottweet.R;
import com.mmga.mmgahottweet.data.SearchFactory;
import com.mmga.mmgahottweet.data.ServiceGenerator;
import com.mmga.mmgahottweet.data.TweetApi;
import com.mmga.mmgahottweet.data.model.Status;
import com.mmga.mmgahottweet.data.model.Token;
import com.mmga.mmgahottweet.data.model.Twitter;
import com.mmga.mmgahottweet.data.transformer.LoadDataTransFormer;
import com.mmga.mmgahottweet.ui.transformer.InsetViewTransformer;
import com.mmga.mmgahottweet.utils.GeoUtil;
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
    private static final int REQUEST_CODE = 1;
    private MainActivityAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeLayout;
    private DrawerLayout mDrawerLayout;
    private BottomSheetLayout bottomSheet;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private LinearLayout internetError;
    private TextView retryButton;
    private FloatingActionButton fab;
    private Observable<Twitter> observable;
    private View sheetView;


    private String mCurrentSearchText;
    private String maxId;
    private int mLangPos;
    private boolean isLoadingMore;
    private String mResultType;
    private boolean mNeedGeo;
    private String mGeoCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        ToastUtil.register(this);

        initPrefs();//读取初始设置
        init();
        getInitToken();

    }

    private void init() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeLayout.setEnabled(false);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(mCurrentSearchText);
            }
        });
        mSwipeLayout.post(new Runnable() {//把setRefreshing(true)排队，确保能获得位置信息
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setupDrawerContent(mNavigationView);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainActivityAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(scrollListener);

        bottomSheet = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        internetError = (LinearLayout) findViewById(R.id.internet_error_layout);
        retryButton = (TextView) findViewById(R.id.retry);
        sheetView = LayoutInflater.from(MainActivity.this).
                inflate(R.layout.my_resume, bottomSheet, false);
        sheetView.findViewById(R.id.my_connection).setOnClickListener(this);
        sheetView.findViewById(R.id.my_github).setOnClickListener(this);
        sheetView.findViewById(R.id.litedo_url).setOnClickListener(this);
        sheetView.findViewById(R.id.cloudcover_url).setOnClickListener(this);
        sheetView.findViewById(R.id.upclock_url).setOnClickListener(this);
        sheetView.findViewById(R.id.metroloading_url).setOnClickListener(this);
    }

    private void setupDrawerContent(final NavigationView mNavigationView) {

        TextView myResume = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.my_resume);
        myResume.setOnClickListener(this);
        SimpleDraweeView myAvatar = (SimpleDraweeView) mNavigationView.getHeaderView(0).findViewById(R.id.my_avatar);
        Uri uri = Uri.parse("res://com.mmga.mmgahottweet/" + R.drawable.my_avatar);
        myAvatar.setImageURI(uri);
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                ToastUtil.showShort(getString(R.string.orz));
                                break;
                            case R.id.nav_messages:
                                ToastUtil.showShort(getString(R.string.orz));
                                break;
                            case R.id.nav_notification:
                                ToastUtil.showShort(getString(R.string.orz));
                                break;
                            case R.id.nav_friends:
                                ToastUtil.showShort(getString(R.string.orz));
                                break;
                            case R.id.nav_settings:
                                openSettingsActivity();
                            default:
                                break;

                        }
                        return true;
                    }
                }

        );
    }

    private void initPrefs() {
        mGeoCode = GeoUtil.getGeocode(this);
        mCurrentSearchText = SharedPrefsUtil.getValue(this, "config", "lastSearchedText", Constant.DEFAULT_CONTENT);
        mLangPos = SharedPrefsUtil.getValue(this, "config", "langPos", Constant.LANG_DEFAULT);
        mResultType = SharedPrefsUtil.getValue(this, "config", "resultType", Constant.TYPE_MIX);
        mNeedGeo = SharedPrefsUtil.getValue(this, "config", "needGeo", false);
    }


    private void loadData(String content) {
        String geoString = mNeedGeo ? mGeoCode : Constant.DO_NOT_GEO;
        String encodedString = urlEncodeString(content);
        Observable.Transformer<Twitter, Twitter> loadDataTransFormer = new LoadDataTransFormer();
        observable = SearchFactory.search(encodedString, mLangPos, mResultType, geoString);
        observable.compose(loadDataTransFormer)
                .map(new Func1<Twitter, List<Status>>() {
                    @Override
                    public List<Status> call(Twitter twitter) {
                        return twitter.getStatuses();
                    }
                })
                .subscribe(new Subscriber<List<Status>>() {
                    @Override
                    public void onCompleted() {
                        mSwipeLayout.setRefreshing(false);
                        isLoadingMore = false;
                        Log.d("mmga", "loadData completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSwipeLayout.setRefreshing(false);
                        isLoadingMore = false;
                        if (e.getMessage().equals("timeout")) {
                            ToastUtil.showLong(getString(R.string.error_timeout));
                        } else {
                            ToastUtil.showLong(getString(R.string.error_data_not_found));
                        }
                        Log.d("mmga", "loadData error :" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<Status> status) {
                        mAdapter.refreshAdapterData(status);
                        String lastId = status.get(status.size() - 1).getLastId();
                        maxId = String.valueOf(Long.valueOf(lastId) - 1); //lastId是最后一个的id，之后请求的最大id要比它小
                    }
                });
    }

    private void loadMoreData(String content) {
        String geoString = mNeedGeo ? mGeoCode : Constant.DO_NOT_GEO;
        String encodedString = urlEncodeString(content);
        Observable.Transformer<Twitter, Twitter> loadDataTransFormer = new LoadDataTransFormer();
        observable = SearchFactory.search(encodedString, maxId, mLangPos, mResultType, geoString);
        observable.compose(loadDataTransFormer)
                .subscribe(new Subscriber<Twitter>() {
                    @Override
                    public void onCompleted() {
                        mSwipeLayout.setRefreshing(false);
                        isLoadingMore = false;
                        Log.d("mmga", "loadMore completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSwipeLayout.setRefreshing(false);
                        isLoadingMore = false;
                        if (e.getMessage().equals("timeout")) {
                            ToastUtil.showLong(getString(R.string.error_timeout));
                        } else {
                            ToastUtil.showLong(getString(R.string.error_no_more_data));
                        }
                        Log.d("mmga", "loadMore error = " + e.getMessage());
                    }

                    @Override
                    public void onNext(Twitter t) {
                        List<Status> status = t.getStatuses();
                        String lastId = status.get(status.size() - 1).getLastId();
                        //lastId是最后一个的id，之后请求的最大id要比它小
                        maxId = String.valueOf(Long.valueOf(lastId) - 1);
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
                        loadData(mCurrentSearchText);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("mmga", "getTokenError : " + e.getMessage());
                        mSwipeLayout.setRefreshing(false);
                        internetError.setVisibility(View.VISIBLE);
                        retryButton.setOnClickListener(MainActivity.this);
                        ToastUtil.showLong(getString(R.string.error_not_get_token));
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
                fab.hide();
                openSearchDialog();
                break;
            case (R.id.my_resume):
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                bottomSheet.postDelayed(new Runnable() { //设一小段延时，要不屏幕短时间内变亮又变暗，晃瞎眼
                    @Override
                    public void run() {
                        bottomSheet.showWithSheetView(sheetView, new InsetViewTransformer());
                    }
                }, 200);
                break;
            case (R.id.retry):
                internetError.setVisibility(View.GONE);
                getInitToken();
                mSwipeLayout.setRefreshing(true);
                break;
            case R.id.my_connection:
                makeACall();
                break;
            case R.id.my_github:
                openGithub("");
                break;
            case R.id.litedo_url:
                openGithub("Litedo");
                break;
            case R.id.cloudcover_url:
                openGithub("cloudcover");
                break;
            case R.id.upclock_url:
                openGithub("Upclock");
                break;
            case R.id.metroloading_url:
                openGithub("MetroLoading");
                break;
        }
    }




    //监听RecyclerView滑动事件，用来判断是否滑动到底部，用来加载更多信息
    private final RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 20) {
                fab.hide();
            } else if (dy < -10) {
                fab.show();
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //isShortList用来过滤掉没有获得token时的滑动手势，以及在结果数量不能充满整个页面时频繁触发loadMoreData的问题
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && (mLayoutManager.findLastCompletelyVisibleItemPosition() == mAdapter.getItemCount() - 1)
                    && !isLoadingMore
                    && !mAdapter.isShortList()) {
                loadMoreData(mCurrentSearchText);
                mSwipeLayout.setRefreshing(true);
                isLoadingMore = true;
            }
        }
    };


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
                            mCurrentSearchText = input;
                            toolbar.setTitle(input);
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
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
        editor.putString("lastSearchedText", mCurrentSearchText);
        editor.putString("resultType", mResultType);
        editor.putInt("langPos", mLangPos);
        editor.putBoolean("needGeo", mNeedGeo);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettingsActivity();
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSettingsActivity() {
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        i.putExtra("langPos", mLangPos);
        i.putExtra("resultType", mResultType);
        i.putExtra("needGeo", mNeedGeo);
        startActivityForResult(i, REQUEST_CODE);
    }


    private void openGithub(String project) {
        String url = "http://www.github.com/mmga/" + project;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void makeACall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + "18641199236");
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mLangPos = data.getIntExtra("langPos", mLangPos);
            mResultType = data.getStringExtra("resultType");
            mNeedGeo = data.getBooleanExtra("needGeo", mNeedGeo);
        }
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

}