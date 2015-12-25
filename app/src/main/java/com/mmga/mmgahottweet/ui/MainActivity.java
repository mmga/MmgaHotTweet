package com.mmga.mmgahottweet.ui;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mmga.mmgahottweet.R;
import com.mmga.mmgahottweet.data.Constant;
import com.mmga.mmgahottweet.data.LoadDataTransFormer;
import com.mmga.mmgahottweet.data.SearchFactory;
import com.mmga.mmgahottweet.data.ServiceGenerator;
import com.mmga.mmgahottweet.data.TweetApi;
import com.mmga.mmgahottweet.data.model.Status;
import com.mmga.mmgahottweet.data.model.Token;
import com.mmga.mmgahottweet.data.model.Twitter;
import com.mmga.mmgahottweet.utils.LogUtil;
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
    RecyclerViewAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    Toolbar toolbar;
    //    Location mLocation;
    RecyclerView mRecyclerView;
    Observable<Twitter> observable;
    FloatingActionButton fab;
    private String mCurrentSearchText;
    private String maxId;
    private int mLangPos;
    boolean isLoadingMore;
    private String mResultType;


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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeLayout.setEnabled(false);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(mCurrentSearchText);
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
        mAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(scrollListener);


    }

    private void setupDrawerContent(NavigationView mNavigationView) {

        SimpleDraweeView myAvatar = (SimpleDraweeView) mNavigationView.getHeaderView(0).findViewById(R.id.my_avatar);
        Uri uri = Uri.parse("res://com.mmga.mmgahottweet/" + R.drawable.my_avatar);
        myAvatar.setImageURI(uri);
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                ToastUtil.showShort("╮(╯▽╰)╭");
                                break;
                            case R.id.nav_messages:
                                ToastUtil.showShort("╮(╯▽╰)╭");
                                break;
                            case R.id.nav_notification:
                                ToastUtil.showShort("╮(╯▽╰)╭");
                                break;
                            case R.id.nav_friends:
                                ToastUtil.showShort("╮(╯▽╰)╭");
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
        mCurrentSearchText = SharedPrefsUtil.getValue(this, "config", "lastSearchedText", Constant.DEFAULT_CONTENT);
        mLangPos = SharedPrefsUtil.getValue(this, "config", "langPos", Constant.LANG_DEFAULT);
        mResultType = SharedPrefsUtil.getValue(this, "config", "resultType", Constant.TYPE_MIX);
        LogUtil.d("initPrefs : mCurrentSearchText =" + mCurrentSearchText);
        LogUtil.d("initPrefs : resultType =" + mResultType);
    }


    private void loadData(String content) {
        String encodedString = urlEncodeString(content);
        Observable.Transformer<Twitter, Twitter> loadDataTransFormer = new LoadDataTransFormer();
        observable = SearchFactory.search(encodedString, mLangPos, mResultType);
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
                        ToastUtil.showLong("没有搜索结果");
                        Log.d("mmga", "loadData error");
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
        String encodedString = urlEncodeString(content);
        Observable.Transformer<Twitter, Twitter> loadDataTransFormer = new LoadDataTransFormer();
        observable = SearchFactory.search(encodedString, maxId, mLangPos, mResultType);
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
                        ToastUtil.showLong("没有更多了内容了");
                        Log.d("mmga", "loadMore error = " + e.getMessage());
                    }

                    @Override
                    public void onNext(Twitter t) {
                        List<Status> status = t.getStatuses();
                        String lastId = status.get(status.size() - 1).getLastId();
                        //lastId是最后一个的id，之后请求的最大id要比它小
                        maxId = String.valueOf(Long.valueOf(lastId) - 1);
                        mAdapter.addAdapterData(status);
                        LogUtil.d(mAdapter.getItemCount());
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
                fab.setClickable(false);//防连点
                fab.hide();
                openSearchDialog();
                break;
        }
    }


    //监听RecyclerView滑动事件，用来判断是否滑动到底部，用来加载更多信息
    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

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
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && (mLayoutManager.findLastCompletelyVisibleItemPosition() == mAdapter.getItemCount() - 1)
                    && !isLoadingMore) {
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
                        fab.setClickable(true);
                    }
                })
                .show();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPrefsUtil.putValue(MainActivity.this, "config", "lastSearchedText", mCurrentSearchText);
        SharedPrefsUtil.putValue(MainActivity.this, "config", "langPos", mLangPos);
        SharedPrefsUtil.putValue(MainActivity.this, "config", "resultType", mResultType);
        LogUtil.d("onStop : langPos =" + mLangPos);
        LogUtil.d("onStop : resultType =" + mResultType);
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
        startActivityForResult(i, REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mLangPos = data.getIntExtra("langPos", mLangPos);
            mResultType = data.getStringExtra("resultType");
            LogUtil.d("result = " + data.getIntExtra("langPos", mLangPos));
        }
    }

}