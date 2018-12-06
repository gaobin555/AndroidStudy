package com.example.gavin.gavintest;

import android.content.Intent;

public class MyFunction {

    private String name;
    private int id;
    private Intent intent;

    public MyFunction(String name, int id, Intent intent) {
        this.name = name;
        this.id = id;
        this.intent =intent;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Intent getIntent() {
        return intent;
    }
}
