package com.mmga.mmgahottweet.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kogitune.activity_transition.ActivityTransition;
import com.mmga.mmgahottweet.R;


public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ActivityTransition.with(getIntent()).to(findViewById(R.id.my_avatar)).start(savedInstanceState);
    }
}
