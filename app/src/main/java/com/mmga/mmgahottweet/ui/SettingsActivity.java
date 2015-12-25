package com.mmga.mmgahottweet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mmga.mmgahottweet.R;
import com.mmga.mmgahottweet.data.Constant;
import com.mmga.mmgahottweet.utils.LogUtil;
import com.mmga.mmgahottweet.utils.StatusBarCompat;
import com.mmga.mmgahottweet.utils.ToastUtil;


public class SettingsActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {

    Spinner langSpinner, geoSpinner;
    Toolbar toolbar;
    String[] lang;
    String[] geo;
    String langSelected, geoSelected;
    int langPos, geoPos;
    private ArrayAdapter<String> langAdapter;
    private ArrayAdapter<String> geoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        ToastUtil.register(this);
        init();
        Intent intent = getIntent();
        langPos = intent.getIntExtra("langPos", Constant.LANG_DEFAULT);
        LogUtil.d("getIntExtra = " + langPos);
    }


    private void init() {
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
                LogUtil.d("click " +position);
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


    private void saveSettingsAndFinish() {
        Intent i = new Intent();
        i.putExtra("langPos", langPos);
//        i.putExtra("geo", geoPos);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onBackPressed() {
        saveSettingsAndFinish();
        super.onBackPressed();
    }
}
