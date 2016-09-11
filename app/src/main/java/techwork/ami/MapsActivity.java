package techwork.ami;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    GoogleMap googleMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        // Top bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.maps_toolbar);
        setSupportActionBar(toolbar);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Get the googleMap instance
        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMap();

        googleMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);

        // Add top padding
        googleMap.setPadding(0, 200, 0, 0);


        sendGetRequest();
        setCameraPosition();

    }

    private void fillOffersLocations(String json) {
        Double latitude;
        Double longitude;
        String offerTitle;
        String address;

        try {
            JSONArray data = new JSONArray(json);
            JSONObject c;
            MarkerOptions marker;

            // TODO: Save somewhere the offer's id

            for (int i = 0; i < data.length(); i++) {
                c = data.getJSONObject(i);

                latitude = Double.parseDouble(c.getString(Config.TAG_LATITUDE));
                longitude = Double.parseDouble(c.getString(Config.TAG_LONGITUDE));

                offerTitle = String.format("%s (%s)",
                        c.getString(Config.TAG_NAME),
                        c.getString(Config.TAG_OFFERS_QUANTITY));
                address = c.getString(Config.TAG_ADDRESS);

                marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(offerTitle).snippet(address);
                googleMap.addMarker(marker);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCameraPosition() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 12));
        }
    }

    // AsyncTask that send a request to the server
    private void sendGetRequest(){
        class GetOffersLocations extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MapsActivity.this,
                        getResources().getString(R.string.fetching),
                        getResources().getString(R.string.wait),false,false);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequest(Config.URL_GET_MAP_OFFERS);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                fillOffersLocations(s);
            }
        }
        GetOffersLocations go = new GetOffersLocations();
        go.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setOnInfoWindowClickListener(MyOnInfoWindowClickListener);

        // Enable the current user GPS location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }

    GoogleMap.OnInfoWindowClickListener MyOnInfoWindowClickListener
            = new GoogleMap.OnInfoWindowClickListener(){
        @Override
        public void onInfoWindowClick(Marker marker) {
            // TODO: Show the corresponding offer
            Snackbar.make(findViewById(android.R.id.content), marker.getTitle(), Snackbar.LENGTH_LONG)
                    .show();
        }
    };
}

