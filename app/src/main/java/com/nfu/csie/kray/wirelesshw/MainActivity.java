package com.nfu.csie.kray.wirelesshw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String SSID;
    private String MAC;
    private ArrayList<Pair<String, Pair<String, Integer>>> wifiInfo;
    private boolean isSelect = false;
    private boolean Run = false;
    private int currentPosition = -1;

    private Dialog dialog;
    private WifiManager wifiManager;
    private TextView target_ssid_text;
    private TextView target_mac_text;
    private TextView target_level_text;
    private CheckBox ckb_forceUpdate;
    private BroadcastReceiver wifiScanReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addListener();
    }

    private void addListener(){
        Button searchWifiBtn = findViewById(R.id.btn1);
        target_ssid_text = findViewById(R.id.target_ssid);
        target_mac_text = findViewById(R.id.target_mac);
        target_level_text = findViewById(R.id.target_level);
        ckb_forceUpdate = findViewById(R.id.forceUpdate);
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        searchWifiBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                    ckb_forceUpdate.setEnabled(true);
                } else {
                    scanFailure();
                }
            }
        };
        ckb_forceUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Run = true;
                    new Thread(force_update_task).start();
                }else
                    Run = false;
            }
        });
    }

    private void startScan(){
        if(isSelect)
            unregisterReceiver(wifiScanReceiver);
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    private void scanSuccess(){
        Log.i("wifi", "startScan");
        isSelect = false;
        wifiInfo = new ArrayList<Pair<String, Pair<String, Integer>>>();
        List<ScanResult> results = wifiManager.getScanResults();
        Log.i("wifi", "stopScan");
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
        isSelect = true;
    }

    private void  scanFailure(){
        Toast.makeText(this, "Scan Failure", Toast.LENGTH_SHORT).show();
        isSelect = false;
    }

    Runnable force_update_task = new Runnable() {
        @Override
        public void run() {
            while (Run) {
                if (currentPosition == -1)
                    break;
                wifiInfo = new ArrayList<Pair<String, Pair<String, Integer>>>();
                List<ScanResult> results = wifiManager.getScanResults();
                ArrayList<String> str = new ArrayList<String>();
                for (ScanResult SR : results) {
                    str.add(SR.SSID);
                    wifiInfo.add(new Pair<String, Pair<String, Integer>>(SR.SSID, new Pair<String, Integer>(SR.BSSID, SR.level)));
                }

                target_ssid_text.setText(wifiInfo.get(currentPosition).first);
                target_mac_text.setText(wifiInfo.get(currentPosition).second.first);
                target_level_text.setText(String.valueOf(wifiInfo.get(currentPosition).second.second));
            }
        }
    };

    ListView.OnItemClickListener lv_listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentPosition = position;
            target_ssid_text.setText(wifiInfo.get(position).first);
            target_mac_text.setText(wifiInfo.get(position).second.first);
            target_level_text.setText(String.valueOf(wifiInfo.get(position).second.second));
            dialog.dismiss();
        }
    };
}
