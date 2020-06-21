package com.nfu.csie.kray.wirelesshw;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<WifiList> wifiInfo;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    private boolean Run = false;

    private WifiManager wifiManager;
    private TextView AccessPointName;
    private TextView coordinate;
    private ListView listView;
    private ListAdapter adapter;
    private CanvasView drawView;
    private BroadcastReceiver wifiScanReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("請給予定位權限")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        addListener();
        startScan();
    }

    private void addListener(){
        Button clearBtn = findViewById(R.id.clearBtn);
        Button changeAPBtn = findViewById(R.id.btn1);
        Button showDetail = findViewById(R.id.showDetail);
        coordinate = findViewById(R.id.coordinate);
        AccessPointName = findViewById(R.id.AccessPointName);
        listView = findViewById(R.id.listView_);
        drawView = findViewById(R.id.DrawView);
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.clearCircle();
            }
        });
        changeAPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.changePosition();
                AccessPointName.setText(drawView.getSSID());
            }
        });
        showDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
                ArrayList<WireLessList> list = drawView.get_dot_detail();
                if(list != null && !list.isEmpty())
                    for(WireLessList item : list){
                        arrayAdapter.add("("+item.x+","+item.y+")"+item.radius);
                    }
                dialog.setTitle("detail")
                        .setPositiveButton("OK", null)
                        .setAdapter(arrayAdapter, null)
                        .show();
            }
        });
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    if(wifiInfo == null)
                        firstScanSuccess();
                } else {
                    scanFailure();
                }
            }
        };
    }

    private void startScan(){
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void firstScanSuccess(){
        unregisterReceiver(wifiScanReceiver);
        wifiInfo = new ArrayList<>();
        List<ScanResult> results = wifiManager.getScanResults();
        ArrayList<String> str = new ArrayList<>();
        for(ScanResult SR : results) {
            str.add(SR.SSID);
            wifiInfo.add(new WifiList(SR.SSID, SR.BSSID, SR.level));
        }
        adapter = new ListAdapter(this, wifiInfo);
        listView.setAdapter(adapter);

        AccessPointName.setText(drawView.getSSID());
        Run = true;
        drawView.setOnTouchListener(view_listener);
        new Thread(runnable).start();
    }

    private void  scanFailure(){
        unregisterReceiver(wifiScanReceiver);
        Toast.makeText(this, "Scan Failure", Toast.LENGTH_SHORT).show();
    }

    View.OnTouchListener view_listener = new View.OnTouchListener() {
        @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) drawView.getLayoutParams();
            if(event.getAction() == MotionEvent.ACTION_DOWN && wifiInfo.size() > 0) {
                int x = Math.round(event.getX() - lp.leftMargin);
                int y = Math.round(event.getY() - lp.topMargin);
                coordinate.setText("(" + x + "," + y + ")");
                drawView.drawCircle(x, y, wifiInfo);
            }
            return false;
        }
    };

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (Run){
                startScan();
                unregisterReceiver(wifiScanReceiver);
                wifiInfo = new ArrayList<>();
                List<ScanResult> results = wifiManager.getScanResults();
                for(int i=0; i<results.size(); ++i) {
                    wifiInfo.add(new WifiList(results.get(i).SSID, results.get(i).BSSID, results.get(i).level));
                }
//                drawView.updateList(wifiInfo);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.update_list(wifiInfo);
                        adapter.notifyDataSetChanged();
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
