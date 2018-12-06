package com.example.gavin.gavintest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.gavin.gavintest.AudioPlayTest.AudioPlayMainActivity;
import com.example.gavin.gavintest.LBSTest.LBSTestActivity;
import com.example.gavin.gavintest.NetworkTest.NetworkActivity;
import com.example.gavin.gavintest.PlayVideoTest.PlayVideo;
import com.example.gavin.gavintest.WebViewTest.WebViewActivity;
import com.example.gavin.gavintest.ServiceBestPractice.DownloadActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String TAG2 = "GavinTest";

    private List<MyFunction> myFunctionList = new ArrayList<>();
    private static String TAG = "gavintest.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initMyFunction();
        MyFunctionAdapter adapter = new MyFunctionAdapter(MainActivity.this,
                R.layout.myfanction_item, myFunctionList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyFunction myFunction = myFunctionList.get(i);
                int id = myFunction.getId();
                Log.d(TAG, "function id = " + id);
                MainActivity.this.startActivity(myFunction.getIntent());
            }
        });

    }

    private void initMyFunction() {
        Intent intent = new Intent(MainActivity.this, AudioPlayMainActivity.class);
        MyFunction audioplay = new MyFunction("Audio Play Test", 1, intent);
        myFunctionList.add(audioplay);

        Intent vp = new Intent(MainActivity.this, PlayVideo.class);
        MyFunction videoplay = new MyFunction("Play Video Test", 2, vp);
        myFunctionList.add(videoplay);

        Intent webviewintent = new Intent(MainActivity.this, WebViewActivity.class);
        MyFunction webview = new MyFunction("Web View Test", 3, webviewintent);
        myFunctionList.add(webview);

        Intent networkintent = new Intent(MainActivity.this, NetworkActivity.class);
        MyFunction network = new MyFunction("Network Test", 4, networkintent);
        myFunctionList.add(network);

        Intent servicebestintent = new Intent(MainActivity.this, DownloadActivity.class);
        MyFunction servicebest = new MyFunction("Download Test", 5, servicebestintent);
        myFunctionList.add(servicebest);

        Intent lbstestintent = new Intent(MainActivity.this, LBSTestActivity.class);
        MyFunction lbstest = new MyFunction("LBS Test", 6, lbstestintent);
        myFunctionList.add(lbstest);
    }
}
