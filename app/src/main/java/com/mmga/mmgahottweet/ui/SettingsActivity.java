package com.mmga.mmgahottweet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.mmga.mmgahottweet.R;
import com.mmga.mmgahottweet.data.Constant;
import com.mmga.mmgahottweet.utils.LogUtil;
import com.mmga.mmgahottweet.utils.StatusBarCompat;
import com.mmga.mmgahottweet.utils.ToastUtil;


public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner langSpinner, geoSpinner;
    Toolbar toolbar;
    String[] lang;
    String[] geo;
    String resultType;
    int langPos, geoPos;
    CheckBox recentCheckBox, popularCheckBox;

    private ArrayAdapter<String> langAdapter;
    private ArrayAdapter<String> geoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        ToastUtil.register(this);
        init();

    }


    private void init() {

        Intent intent = getIntent();
        langPos = intent.getIntExtra("langPos", Constant.LANG_DEFAULT);
        resultType = intent.getStringExtra("resultType");
        LogUtil.d("getIntExtra = " + langPos);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettingsAndFinish();
            }
        });
        langSpinner = (Spinner) findViewById(R.id.lang_spinner);
        geoSpinner = (Spinner) findViewById(R.id.geo_spinner);
        setupSpinner();


        recentCheckBox = (CheckBox) findViewById(R.id.recent_checkbox);
        popularCheckBox = (CheckBox) findViewById(R.id.popular_checkbox);
        setCheckBoxStatus();
        recentCheckBox.setOnClickListener(this);
        popularCheckBox.setOnClickListener(this);

    }

    private void setCheckBoxStatus() {//初始化checkbox的状态
        switch (resultType) {
            case Constant.TYPE_MIX:
                recentCheckBox.setChecked(true);
                popularCheckBox.setChecked(true);
                break;
            case Constant.TYPE_POPULAR:
                popularCheckBox.setChecked(true);
                break;
            case Constant.TYPE_RECENT:
                recentCheckBox.setChecked(true);
                break;
        }
    }

    private void setupSpinner() {
        //// TODO: 2015/12/25 spinner 会自动点一下第一个item，等自己写个代替
        lang = getResources().getStringArray(R.array.languages);
        langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lang);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(langAdapter);
        langSpinner.setOnItemSelectedListener(this);
        langSpinner.setSelection(langPos);

        geo = getResources().getStringArray(R.array.geo);
        geoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, geo);
        geoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        geoSpinner.setAdapter(geoAdapter);
        geoSpinner.setOnItemSelectedListener(this);
        geoSpinner.setSelection(2);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lang_spinner:
                langPos = position;
                LogUtil.d("click " + position);
                break;
            case R.id.geo_spinner:
//                geoPos = position;
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        LogUtil.d("onNothingSelected");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recent_checkbox:
                if (!recentCheckBox.isChecked() && !popularCheckBox.isChecked()) {
                    popularCheckBox.setChecked(true);
                }
                break;
            case R.id.popular_checkbox:
                if (!popularCheckBox.isChecked() && !recentCheckBox.isChecked()) {
                    recentCheckBox.setChecked(true);
                }
                break;
        }
    }


    private void saveSettingsAndFinish() {

        Intent i = new Intent();
        i.putExtra("langPos", langPos);
        i.putExtra("resultType", getResultType());
//        i.putExtra("geo", geoPos);
        setResult(RESULT_OK, i);
        finish();
    }

    private String getResultType() {
        if (recentCheckBox.isChecked() && popularCheckBox.isChecked()) {
            return Constant.TYPE_MIX;
        } else if (recentCheckBox.isChecked()) {
            return Constant.TYPE_RECENT;
        } else {
            return Constant.TYPE_POPULAR;
        }

    }

    @Override
    public void onBackPressed() {
        saveSettingsAndFinish();
        super.onBackPressed();
    }


}
