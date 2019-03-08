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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class EndActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    private boolean mLocationPermissionGranted = false;

    private int counter;
    private Button play;
    private Button menu;
    private Button saveScoreBtn;
    private EditText playerName;
    private DatabaseReference ref;
    private ValueEventListener valueEventListener;
    private Map<String, Object> players;
    private LocationManager locationManager;
    private LocationListener locationListener;
    Location myLocation;
    private Player player;
    private DatabaseHelper mDatabasehelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        final Bundle bundle = getIntent().getExtras();
        TextView statusText = findViewById(R.id.statusText);
        statusText.setText(bundle.getString("Winner", "0"));

        initLocation();
        initFirebase();


        play = findViewById(R.id.playBtn);
        menu = findViewById(R.id.menuBtn);
        saveScoreBtn = findViewById(R.id.saveScoreBtn);
        playerName = findViewById(R.id.playerName);


        play.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            intent.putExtra("numberOfButtons", bundle.getInt("numberOfButtons", 0));
            intent.putExtra("Difficulty", bundle.getString("Difficulty"));
            finish();
            startActivity(intent);
        });

        menu.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), OpeningActivity.class);
            finish();
            startActivity(intent);
        });

        saveScoreBtn.setOnClickListener(v -> {

            if (playerName.getText().toString().contentEquals(""))
                Toast.makeText(getApplicationContext(), "Name cannot be blank!", Toast.LENGTH_LONG).show();
            else {
                if (myLocation == null) {
                    Toast.makeText(getApplicationContext(), "Please wait for GPS to locate you", Toast.LENGTH_LONG).show();
                } else {
                    player = new Player(playerName.getText().toString(), bundle.getInt("Score", 0), getCity(myLocation), myLocation.getLatitude(), myLocation.getLongitude());
                    counter = 0;
                    while (players.keySet().contains(counter + "")) {
                        counter++;
                    }
                    players.put(counter + "", player);
                    ref.updateChildren(players);

                    mDatabasehelper = new DatabaseHelper(getApplicationContext());
                    boolean insertData = mDatabasehelper.insertData(bundle.getString("Difficulty"), player);

                    if (insertData) {
                        Toast.makeText(this, "Data Successfully added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Something Went Wrong ", Toast.LENGTH_SHORT).show();

                    }
                }

            }

        });
    }

    private void initFirebase() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("Leader_Boards");
        players = new HashMap<>();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    players.put(ds.getKey(), ds.getValue(Player.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addValueEventListener(valueEventListener);
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
                myLocation = location;
                Log.e("location", "location");
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

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(EndActivity.this);

        if (available == ConnectionResult.SUCCESS)
            return true;

        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(EndActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
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
                // If request is cancelled, the result arrays are empty.
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
}
