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

    Spinner langSpinner;
    Toolbar toolbar;
    String[] lang;
    String resultType;
    int langPos;
    CheckBox recentCheckBox, popularCheckBox,geoCheckBox;

    private ArrayAdapter<String> langAdapter;
    private boolean needGeo;


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
        needGeo = intent.getBooleanExtra("needGeo", false);
        LogUtil.d("getBooleanExtra = " + needGeo);

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
        setupSpinner();

        geoCheckBox = (CheckBox) findViewById(R.id.geo_checkbox);
        recentCheckBox = (CheckBox) findViewById(R.id.recent_checkbox);
        popularCheckBox = (CheckBox) findViewById(R.id.popular_checkbox);
        setCheckBoxStatus();
        recentCheckBox.setOnClickListener(this);
        popularCheckBox.setOnClickListener(this);
        geoCheckBox.setOnClickListener(this);

    }


    private void setupSpinner() {
        lang = getResources().getStringArray(R.array.languages);
        langAdapter = new ArrayAdapter<>(this, R.layout.my_spinner_text, lang);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(langAdapter);
        langSpinner.setOnItemSelectedListener(this);
        langSpinner.setSelection(langPos);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lang_spinner:
                langPos = position;
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        LogUtil.d("onNothingSelected");
    }


    private void setCheckBoxStatus() {//初始化checkbox的状态
        if (needGeo) {
            geoCheckBox.setChecked(true);
        } else {
            geoCheckBox.setChecked(false);
        }

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
            case R.id.geo_checkbox:
                needGeo = geoCheckBox.isChecked();
        }
    }


    private void saveSettingsAndFinish() {

        Intent i = new Intent();
        i.putExtra("langPos", langPos);
        i.putExtra("resultType", getResultType());
        i.putExtra("needGeo", needGeo);
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
