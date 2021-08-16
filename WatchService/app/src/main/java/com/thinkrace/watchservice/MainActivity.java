package com.thinkrace.watchservice;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.thinkrace.watchservice.orderlibrary.GlobalSettings;
import com.thinkrace.watchservice.orderlibrary.LocationService;
import com.thinkrace.watchservice.orderlibrary.data.MsgType;
import com.thinkrace.watchservice.orderlibrary.data.TcpMsg;
import com.thinkrace.watchservice.orderlibrary.utils.GSMCellLocationUtils;
import com.thinkrace.watchservice.orderlibrary.utils.OrderUtil;
import com.thinkrace.watchservice.orderlibrary.utils.UnicodeUtils;
import com.thinkrace.watchservice.orderlibrary.utils.Utils;
import com.thinkrace.watchservice.parser.MsgSender;
import com.xuhao.android.common.constant.OrderConstans;
import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import rx.functions.Action1;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.CHANGE_NETWORK_STATE;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends Activity {

    private static final String INIT_TAG = "InitializeTask";
    private static final String[] PERMISSION = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            WRITE_EXTERNAL_STORAGE,
            READ_EXTERNAL_STORAGE,
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            RECORD_AUDIO,
            CALL_PHONE,
            MODIFY_AUDIO_SETTINGS,
            CAMERA,
            READ_PHONE_STATE,
            CHANGE_NETWORK_STATE,
            ACCESS_NETWORK_STATE
    };
    OrderUtil.OrderListener orderListener = new OrderUtil.OrderListener() {
        @Override
        public void sendOrderSuccess(String result) {

        }

        @Override
        public void sendOrderFail(String result) {

        }

        @Override
        public void receiverOrder(TcpMsg tcpMsg) {

        }
    };
    private boolean initialized = false;
    private Context context;
    private TextView XinTiao_Tv;
    private TextView DengLu_Tv;
    private TextView Location_Tv;
    private OrderUtil orderUtil;
    private ArrayList<String> bpGlList = new ArrayList<>();
    private DynamicReceiver dynamicReceiver;
    private ListView bpGL_listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        testButton();
        getPermission();
    }

    private void getPermission() {
        RxPermissions.getInstance(this)
                .request(PERMISSION).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean granted) {
                if (granted) {// 已经获取权限
                    GlobalSettings.instance().saveImei(Utils.getContext());
                    GlobalSettings.instance().saveImsi(Utils.getContext());
                    LocationService.pull(Utils.getContext());
                    orderUtil = OrderUtil.getInstance();
                    orderUtil.setOnOrderListener(orderListener);
                    orderUtil.setServiceListener(new OrderUtil.ServiceListener() {
                        @Override
                        public void serviceStart(OrderUtil instance) {
                            orderUtil = instance;
                        }
                    });
                } else {
                    // 未获取权限
                    Toast.makeText(KApplication.sContext, "您没有授权该权限，请在设置中打开授权", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void testButton() {
        bpGL_listView = findViewById(R.id.bpGL_listView);
        bpGL_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ReceiverConstant.ACTION_APCL_ID);
                intent.putExtra(ReceiverConstant.EXTRA_BPCL_ID,bpGlList.get(i).split("\n")[2]);
                context.sendBroadcast(intent);
            }
        });
        bpGL_listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return bpGlList.size();
            }

            @Override
            public Object getItem(int i) {
                return bpGlList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                TextView textView = new TextView(context);
                textView.setText(bpGlList.get(i));
                return textView;
            }
        });
        findViewById(R.id.request_weather_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestWeather();
            }
        });
        findViewById(R.id.request_bpGL_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestBPGL();
            }
        });
        findViewById(R.id.upload_voice_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent voiceIntent = new Intent(ReceiverConstant.ACTION_AP07);
                voiceIntent.putExtra(ReceiverConstant.EXTRA_BP07_Path, Environment.getExternalStorageDirectory() + "/BeiBeiAnRecord/22010/3797.amr");
                voiceIntent.putExtra(ReceiverConstant.EXTRA_BP07_Long, "10");
                voiceIntent.putExtra(ReceiverConstant.EXTRA_BP07_Type, "1");
                voiceIntent.putExtra(ReceiverConstant.EXTRA_BP07_Filetype, "1");
                sendBroadcast(voiceIntent);
            }
        });
        findViewById(R.id.upload_pic_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent voiceIntent = new Intent(ReceiverConstant.ACTION_AP42);
                voiceIntent.putExtra(ReceiverConstant.EXTRA_BP42_Path, Environment.getExternalStorageDirectory() + "/BeiBeiAnRecord/22010/123.amr");
                voiceIntent.putExtra(ReceiverConstant.EXTRA_BP42_Type, "1");
                voiceIntent.putExtra(ReceiverConstant.EXTRA_BP42_Filetype, "0");
                sendBroadcast(voiceIntent);
            }
        });
        findViewById(R.id.voice_list_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent voiceIntent = new Intent(ReceiverConstant.ACTION_REQUEST_VOICE_LIST);
                voiceIntent.putExtra("pageNo", String.valueOf(1));
                voiceIntent.putExtra("pageCount",String.valueOf(5));
                sendBroadcast(voiceIntent);
            }
        });
        findViewById(R.id.add_friend_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent voiceIntent = new Intent(ReceiverConstant.ACTION_APT4);
                sendBroadcast(voiceIntent);
            }
        });

        final TextView socketState_Tv = (TextView) findViewById(R.id.socketState_Tv);
        Button socketStateBtn = (Button) findViewById(R.id.socketState_btn);
        socketStateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionInfo connectionInfo = orderUtil.getmManager().getConnectionInfo();
                socketState_Tv.setText(connectionInfo.getIp()
                        + "   " + connectionInfo.getPort()
                        + "   " + orderUtil.getmManager().isConnect());
            }
        });
        Button socket_close_btn = (Button) findViewById(R.id.socket_close_btn);
        socket_close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderUtil.getmManager().disconnect();
            }
        });
        Button socket_reconnect_btn = (Button) findViewById(R.id.socket_reconnect_btn);
        socket_reconnect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderUtil.getmManager().connect();
            }
        });

        XinTiao_Tv = (TextView) findViewById(R.id.XinTiao_Tv);
        XinTiao_Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "XinTiao_Tv", Toast.LENGTH_SHORT).show();
                orderUtil.heartbeatPacket("012", "354076070336278", "086", "00", "00", "0000", "00");
                socketState_Tv.setText("");
            }
        });

        DengLu_Tv = (TextView) findViewById(R.id.DengLu_Tv);
        DengLu_Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderUtil.loginPkg("354076070336278");
                socketState_Tv.setText("");
            }
        });

        Location_Tv = (TextView) findViewById(R.id.Location_Tv);
        Location_Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketState_Tv.setText("");
                //格林尼治时间
                String nowTime = "000000";
                String GreenwichTime = "000000";

                SimpleDateFormat simpleDateFormatyy = new SimpleDateFormat("yyyyMMdd");
                Date yyyyMMdd = new Date();
                nowTime = simpleDateFormatyy.format(yyyyMMdd);
                nowTime = nowTime.substring(2, nowTime.length());

                orderUtil.sendLocationOrder("IWAP01", "180350", "A", "2239.0123N", "11400.0633E",
                        "", "100350", "323.87", "", "");

            }
        });

        dynamicReceiver = new DynamicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OrderConstans.BP00);
        intentFilter.addAction(OrderConstans.BP03);
        intentFilter.addAction(OrderConstans.BP01);
        intentFilter.addAction(OrderConstans.BP12);
        intentFilter.addAction(OrderConstans.BP46);
        intentFilter.addAction(OrderConstans.BP31);
        intentFilter.addAction(OrderConstans.BP13);
        intentFilter.addAction(OrderConstans.BP20);
        intentFilter.addAction(OrderConstans.BP26);
        intentFilter.addAction(OrderConstans.BP15);
        intentFilter.addAction(OrderConstans.BP45);
        intentFilter.addAction(ReceiverConstant.ACTION_BPCL);
        registerReceiver(dynamicReceiver, intentFilter);
    }

    /**
     * TODO 请求监护人列表
     */
    private void requestBPGL() {
        MsgSender.sendTxtMsg(MsgType.IWAPCL, GlobalSettings.instance().getImei() + GlobalSettings.MSG_CONTENT_SEPERATOR + "1");
    }

    /**
     * TODO 请求天气信息
     */
    public void requestWeather() {
        OrderUtil.getInstance().requestWeather(GSMCellLocationUtils.getParseBaseStation(Utils.getContext()), "0");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dynamicReceiver);
    }

    public class DynamicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("BP00")) {
                Log.i("MainActivity", "BP00=" + intent.getStringExtra("BP00"));
            } else if (intent.getAction().equals("BP03")) {
                Log.i("MainActivity", "BP03=" + intent.getStringExtra("BP03"));
            } else if (intent.getAction().equals("BP01")) {
                Log.i("MainActivity", "BP01=" + intent.getStringExtra("BP01"));
            } else if (intent.getAction().equals("BP12")) {
                Log.i("MainActivity", "BP12=" + intent.getStringExtra("BP12"));
            } else if (intent.getAction().equals("BP46")) {
                Log.i("MainActivity", "BP46=" + intent.getStringExtra("BP46"));
            } else if (intent.getAction().equals("BP31")) {
                Log.i("MainActivity", "BP31=" + intent.getStringExtra("BP31"));
            } else if (intent.getAction().equals("BP13")) {
                Log.i("MainActivity", "BP13=" + intent.getStringExtra("BP13"));
            } else if (intent.getAction().equals("BP20")) {
                Log.i("MainActivity", "BP20=" + intent.getStringExtra("BP20"));
            } else if (intent.getAction().equals("BP26")) {
                Log.i("MainActivity", "BP26=" + intent.getStringExtra("BP26"));
            } else if (intent.getAction().equals("BP15")) {
                Log.i("MainActivity", "BP15=" + intent.getStringExtra("BP15"));
            } else if (intent.getAction().equals("BP45")) {
                Log.i("MainActivity", "BP45=" + intent.getStringExtra("BP45"));
            } else if (intent.getAction().equals(ReceiverConstant.ACTION_BPCL)) {
                bpGlList.clear();
                Log.i("MainActivity", "EXTRA_BPCL=" + intent.getStringExtra(ReceiverConstant.EXTRA_BPCL));
                String bpclString = intent.getStringExtra(ReceiverConstant.EXTRA_BPCL);
                String[] split = bpclString.split(",");
                for (int i = 0; i < split.length && split.length > 2; i++) {
                    String[] split1 = split[i].split("\\|");
                    String content = "关系:"+split1[0]+ "\n" + UnicodeUtils.unicodeNo0xuToString(split1[1])+"\n"  + split1[2];
                    bpGlList.add(content);
                }
                ((BaseAdapter) bpGL_listView.getAdapter()).notifyDataSetChanged();
            }

        }
    }

}