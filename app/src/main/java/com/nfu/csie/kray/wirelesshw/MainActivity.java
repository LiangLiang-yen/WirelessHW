package com.nfu.csie.kray.wirelesshw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Pair<String, Pair<String, Integer>>> wifiInfo;
    private String currentMACADDR = "";
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    private int currentPosition = -1;
    private boolean Run = false;

    private Dialog dialog;
    private WifiManager wifiManager;
    private TextView coordinate;
    private TextView target_ssid_text;
    private TextView target_mac_text;
    private TextView target_level_text;
    private ProgressBar progressBar;
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
    }

    private void addListener(){
        Button clearBtn = findViewById(R.id.clearBtn);
        Button searchWifiBtn = findViewById(R.id.btn1);
        coordinate = findViewById(R.id.coordinate);
        target_ssid_text = findViewById(R.id.target_ssid);
        target_mac_text = findViewById(R.id.target_mac);
        target_level_text = findViewById(R.id.target_level);
        progressBar = findViewById(R.id.progressBar);
        drawView = findViewById(R.id.DrawView);
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        searchWifiBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMACADDR = "";
                startScan();
            }
        });
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    if(currentMACADDR.equals(""))
                        firstScanSuccess();
                } else {
                    scanFailure();
                }
            }
        };
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) drawView.getLayoutParams();
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    int x = Math.round(event.getX() - lp.leftMargin);
                    int y = Math.round(event.getY() - lp.topMargin);
                    coordinate.setText("(" + String.valueOf(x) + "," + String.valueOf(y) + ")");
                    drawView.drawCircle(x, y);
                }
                return false;
            }
        });
    }

    private void startScan(){
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    private void firstScanSuccess(){
        unregisterReceiver(wifiScanReceiver);
        wifiInfo = new ArrayList<Pair<String, Pair<String, Integer>>>();
        List<ScanResult> results = wifiManager.getScanResults();
        ArrayList<String> str = new ArrayList<String>();
        for(ScanResult SR : results) {
            str.add(SR.SSID);
            wifiInfo.add(new Pair<String, Pair<String, Integer>>(SR.SSID, new Pair<String, Integer>(SR.BSSID, SR.level)));
        }
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, str);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.listalertdialog);

        ListView lv = dialog.findViewById(R.id.listview);
        lv.setOnItemClickListener(lv_listener);
        lv.setAdapter(adapter);

        dialog.show();
        Run = true;
    }

    private void  scanFailure(){
        unregisterReceiver(wifiScanReceiver);
        Toast.makeText(this, "Scan Failure", Toast.LENGTH_SHORT).show();
    }

    ListView.OnItemClickListener lv_listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentMACADDR = wifiInfo.get(position).second.first;
            target_ssid_text.setText(wifiInfo.get(position).first);
            target_mac_text.setText(wifiInfo.get(position).second.first);
            target_level_text.setText(String.valueOf(wifiInfo.get(position).second.second));
            dialog.dismiss();
            progressBar.setVisibility(View.VISIBLE);
            new Thread(runnable).start();
        }
    };

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (Run){
                startScan();
                unregisterReceiver(wifiScanReceiver);
                wifiInfo = new ArrayList<Pair<String, Pair<String, Integer>>>();
                List<ScanResult> results = wifiManager.getScanResults();
                for(int i=0; i<results.size(); ++i) {
                    wifiInfo.add(new Pair<String, Pair<String, Integer>>(results.get(i).SSID, new Pair<String, Integer>(results.get(i).BSSID, results.get(i).level)));
                    if (currentMACADDR.equals(results.get(i).BSSID)) {
                        currentPosition = i;
                    }
                }
                if(currentPosition != -1) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            target_level_text.setText(String.valueOf(wifiInfo.get(currentPosition).second.second));
                        }
                    });
                    Log.i("wifi", "Running level: " + wifiInfo.get(currentPosition).second.second);
                }else
                    Log.i("wifi", "Running level: " +  "missing");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
