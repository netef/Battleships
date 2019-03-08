//206020414
//307968016
package com.example.netef.battleships;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OpeningActivity extends AppCompatActivity {

    public static final String TAG = "DatabaseHelper";

    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    public static final int ERROR_DIALOG_REQUEST = 9001;


    private DatabaseReference ref;
    private FirebaseDatabase database;
    private Map<String, Object> players;
    private RadioGroup radioGroup;
    private Button leaderBoardsBtn, startBtn;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ValueEventListener valueEventListener;
    private Fragment fragment;

    private boolean showTable = false;
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        initFirebase();
        initLocation();

        radioGroup = findViewById(R.id.radioGroup);
        startBtn = findViewById(R.id.startBtn);
        leaderBoardsBtn = findViewById(R.id.leaderBoardsBtn);


        startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(radioButtonId);
            switch (radioButton.getText().toString()) {
                case "Easy": {
                    intent.putExtra("numberOfButtons", 12);
                    intent.putExtra("Difficulty", "Easy");
                    break;
                }

                case "Normal": {
                    intent.putExtra("numberOfButtons", 16);
                    intent.putExtra("Difficulty", "Normal");
                    break;
                }

                case "Hard": {
                    intent.putExtra("numberOfButtons", 20);
                    intent.putExtra("Difficulty", "Hard");
                    break;
                }
            }
            finish();
            startActivity(intent);
        });

        leaderBoardsBtn.setOnClickListener(v -> {
            if (showTable == true) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                showTable = false;
            } else {

                showTable = true;
                fragment = new SQliteFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.viewside, fragment).commit();
            }

        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String getCity(Location loc) {
        Geocoder g = new Geocoder(getBaseContext(), Locale.getDefault());
        String cityName = "Not Found!";
        List<Address> addressList;
        try {
            addressList = g.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addressList.size() > 0) {
                cityName = addressList.get(0).getLocality();
            }
        } catch (IOException e) {

        }
        return cityName;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isServicesOK() {

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(OpeningActivity.this);

        if (available == ConnectionResult.SUCCESS)
            return true;

        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(OpeningActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    public boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                getLocationPermission();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG);
                } else {
                    getLocationPermission();
                }
            }
        }

    }

    private void initLocation() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return;
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


    }

    private void initFirebase() {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Leader_Boards");
        players = new HashMap<>();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.addValueEventListener(valueEventListener);
    }


}
