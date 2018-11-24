package com.example.gaobi.activitylifecycletest;

import android.app.Activity;
import android.os.Bundle;

public class DialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
    }
}
