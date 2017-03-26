package com.example.airport;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.airport.MyApplication.currentDateTimeString;

public class MainActivity extends AppCompatActivity {

    private static final Map<String, List<String>> PLACES_BY_BEACONS;

    View rootView;
    static Boolean foundPatientA = false;
    TextView textView;

    // TODO: replace "<major>:<minor>" strings to match your own beacons.
    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("36646:10564", new ArrayList<String>() {{
            add("Bathroom");
            // read as: "Heavenly Sandwiches" is closest
            // to the beacon with major 22504 and minor 48827
            add("Patient room A");
            // "Green & Green Salads" is the next closest
            add("Patient room B");
            // "Mini Panini" is the furthest away
        }});
        placesByBeacons.put("36288:15983", new ArrayList<String>() {{
            add("Patient room B");
            add("Patient room A");
            add("Bathroom");
        }});
        placesByBeacons.put("54922:9228", new ArrayList<String>() {{
            add("Patient room A");
            add("Patient room B");
            add("Bathroom");
        }});
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }

    private BeaconManager beaconManager;
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.WHITE);
        textView = (TextView) findViewById(R.id.textView);
        textView.setTextSize(50.0f);
        textView.setTextColor(Color.WHITE);

        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    List<String> places = placesNearBeacon(nearestBeacon);
                    System.out.println(places.toString());
                    // TODO: update the UI here
                    Log.d("Doctor's office", "Nearest places: " + places);
                    currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    if (places.get(0).equals("Patient room A")) {
                        rootView.setBackgroundColor(Color.rgb(114, 31, 33));
                        textView.setText(currentDateTimeString);
                    }

                    if (places.get(0).equals("Bathroom")) {
                        rootView.setBackgroundColor(Color.rgb(255, 132, 172));
                        textView.setText(currentDateTimeString);
                    }

                    if (places.get(0).equals("Patient room B")) {
                        rootView.setBackgroundColor(Color.rgb(221, 221, 0));
                        textView.setText(currentDateTimeString);
                    }
                } else {
                    rootView.setBackgroundColor(Color.WHITE);
                }
            }
        });
        region = new Region("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
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
}
