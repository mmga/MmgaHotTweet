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
import com.mmga.mmgahottweet.data.model.Status;
import com.mmga.mmgahottweet.provider.DataProvider;
import com.mmga.mmgahottweet.provider.DataProviderCallback;
import com.mmga.mmgahottweet.ui.transformer.InsetViewTransformer;
import com.mmga.mmgahottweet.utils.EncodeUtil;
import com.mmga.mmgahottweet.utils.GeoUtil;
import com.mmga.mmgahottweet.utils.SharedPrefsUtil;
import com.mmga.mmgahottweet.utils.StatusBarCompat;
import com.mmga.mmgahottweet.utils.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DataProviderCallback {
    private static final int REQUEST_CODE = 1;
    private MainActivityAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeLayout;
    private DrawerLayout mDrawerLayout;
    private BottomSheetLayout bottomSheet;
    private Toolbar toolbar;
    private LinearLayout internetError;
    private TextView retryButton;
    private FloatingActionButton fab;
    private View sheetView;
    private boolean isLoadingMore;
    DataProvider dataProvider = DataProvider.getInstance();


    private boolean mNeedGeo;
    private String mCurrentSearchText;
    private String maxId;
    private int mLangPos;
    private String mResultType;
    private String mGeoCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        ToastUtil.register(this);

        initConfigs();//读取初始设置
        initViews();
        authAndLoadData();

    }

    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeLayout.setEnabled(false);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(mCurrentSearchText,Constant.LOAD_TYPE_NEW);
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
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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
                                startSettingsActivity();
                            default:
                                break;

                        }
                        return true;
                    }
                }

        );
    }

    private void initConfigs() {
        mGeoCode = GeoUtil.getGeocode(this);
        mCurrentSearchText = SharedPrefsUtil.getValue(this, "config", "lastSearchedText", Constant.DEFAULT_CONTENT);
        mLangPos = SharedPrefsUtil.getValue(this, "config", "langPos", Constant.LANG_DEFAULT);
        mResultType = SharedPrefsUtil.getValue(this, "config", "resultType", Constant.TYPE_MIX);
        mNeedGeo = SharedPrefsUtil.getValue(this, "config", "needGeo", false);
    }


    private void authAndLoadData() {
        initDataProvider(mCurrentSearchText);
        dataProvider.authAndLoadData(this);
    }

    private void loadData(String content,int loadType) {
        initDataProvider(content);
        dataProvider.loadData(this, loadType);
    }

    private void initDataProvider(String content) {
        dataProvider.setGeoCode(mNeedGeo ? mGeoCode : Constant.DO_NOT_GEO);
        dataProvider.setContent(EncodeUtil.urlEncodeString(content));
        dataProvider.setLangPos(mLangPos);
        dataProvider.setResultType(mResultType);
        dataProvider.setMaxId(maxId);
    }


    @Override
    public void OnAuthComplete() {
        mSwipeLayout.setEnabled(true);//正确获取token之后才可以下拉刷新
        fab.show();//获取token之后才显示搜索按钮
    }

    @Override
    public void OnAuthError(Throwable e) {
        Log.d("mmga", "getTokenError : " + e.getMessage());
        mSwipeLayout.setRefreshing(false);
        internetError.setVisibility(View.VISIBLE);
        retryButton.setOnClickListener(MainActivity.this);
        ToastUtil.showLong(getString(R.string.error_not_get_token));
    }

    @Override
    public void OnDataSuccess(List<Status> status,int loadType) {
        if (loadType == Constant.LOAD_TYPE_NEW) {
            mAdapter.refreshAdapterData(status);
        } else {
            mAdapter.addAdapterData(status);
        }
        String lastId = status.get(status.size() - 1).getLastId();
        //lastId是最后一个的id，之后请求的最大id要比它小
        maxId = String.valueOf(Long.valueOf(lastId) - 1);
    }

    @Override
    public void OnDataComplete() {
        mSwipeLayout.setRefreshing(false);
        isLoadingMore = false;
        Log.d("mmga", "load completed");
    }


    @Override
    public void OnDataError(Throwable e) {
        mSwipeLayout.setRefreshing(false);
        isLoadingMore = false;
        if (e.getMessage().equals("timeout")) {
            ToastUtil.showLong(getString(R.string.error_timeout));
        } else {
            ToastUtil.showLong(getString(R.string.error_no_more_data));
        }
        Log.d("mmga", "load error = " + e.getMessage());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.fab):
                fab.hide();
                showSearchDialog();
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
                authAndLoadData();
                mSwipeLayout.setRefreshing(true);
                break;
            case R.id.my_connection:
                makeACall();
                break;
            case R.id.my_github:
                linkToGithub("");
                break;
            case R.id.litedo_url:
                linkToGithub("Litedo");
                break;
            case R.id.cloudcover_url:
                linkToGithub("cloudcover");
                break;
            case R.id.upclock_url:
                linkToGithub("Upclock");
                break;
            case R.id.metroloading_url:
                linkToGithub("MetroLoading");
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
                loadData(mCurrentSearchText, Constant.LOAD_TYPE_MORE);
                mSwipeLayout.setRefreshing(true);
                isLoadingMore = true;
            }
        }
    };


    private void showSearchDialog() {
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
                            loadData(input,Constant.LOAD_TYPE_NEW);
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
                startSettingsActivity();
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void startSettingsActivity() {
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        i.putExtra("langPos", mLangPos);
        i.putExtra("resultType", mResultType);
        i.putExtra("needGeo", mNeedGeo);
        startActivityForResult(i, REQUEST_CODE);
    }

    private void linkToGithub(String project) {
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


}