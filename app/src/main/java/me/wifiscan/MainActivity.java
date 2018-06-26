package me.wifiscan;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

import java.util.List;

public class MainActivity extends Activity  {
    ListView lv;
    WifiManager wifi;
    String wifis[]=null;
    WifiScanReceiver wifiReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=(ListView)findViewById(R.id.listView);

        wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);

        wifiReciever = new WifiScanReceiver();
        if(!wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(),
                    "Wifi is disabled..Making it enabled", Toast.LENGTH_LONG)
                    .show();
            wifi.setWifiEnabled(true);
        }

        scanWifiList();
        Toast.makeText(this, "There are no available Wi-Fi connections",
                Toast.LENGTH_LONG).show();

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Toast.makeText(getApplicationContext(),
                        "Refresh", Toast.LENGTH_LONG)
                        .show();
           scanWifiList();
            }
        });
    }

    private void scanWifiList() {
        wifi.startScan();
    }

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class WifiScanReceiver extends BroadcastReceiver{
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList= wifi.getScanResults();
            wifis = new String[wifiScanList.size()];

           for(int i = 0; i < wifiScanList.size(); i++){
                wifis[i] = ("SSID :: " + wifiScanList.get(i).SSID
                        + "\nStrength :: " + wifiScanList.get(i).level
                        + "\nBSSID :: " + wifiScanList.get(i).BSSID
                        + "\nChannel :: "
                        + convertFrequencyToChannel(wifiScanList.get(i).frequency)
                        + "\nFrequency :: " + wifiScanList.get(i).frequency
                        + "\nCapability :: " + wifiScanList.get(i).capabilities);
               // ((wifiScanList.get(i)).toString());
            }

          if(wifiScanList.size()==0) {
               Toast.makeText(getApplicationContext(),
                       "There are no available Wi-Fi connections", Toast.LENGTH_LONG)
                       .show();
           }else
             lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,wifis));
        }
            public int convertFrequencyToChannel(int freq) {
            if (freq>= 2412 &&freq<= 2484) { return (freq - 2412) / 5 + 1; } else
            if (freq>= 5170 &&freq<= 5825) {
                return (freq - 5170) / 5 + 34;
            } else {
                return -1;
            }
        }
    }
}