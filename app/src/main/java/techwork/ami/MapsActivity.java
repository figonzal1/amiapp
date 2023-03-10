package techwork.ami;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;

import techwork.ami.Promotion.FilterActivity;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private HashMap<String, Store> stores;
    private Hashtable<String, String> markers;
    private Hashtable<String, Boolean> markerSet;
    private String lastLatitude;
    private String lastLongitude;
    private GoogleMap googleMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);

        markers = new Hashtable<>();
        markerSet = new Hashtable<>();
        stores = new HashMap<>();

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
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

                offerTitle = c.getString(Config.TAG_NAME);
                address = c.getString(Config.TAG_ADDRESS);

                marker = new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(offerTitle)
                        .snippet(address)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                if (googleMap != null) {
                    Marker m = googleMap.addMarker(marker);
                    Store s = new Store(c.getString(Config.TAG_ID_STORE),
                            c.getString(Config.TAG_NAME),
                            c.getString(Config.TAG_OFFERS_QUANTITY));
                    stores.put(m.getId(), s);
                    markers.put(m.getId(), Config.URL_ENTERPRISE_IMAGE + c.getString(Config.TAG_IMAGE));
                    markerSet.put(m.getId(), false);
                }
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
            lastLatitude = location.getLatitude()+"";
            lastLongitude = location.getLongitude()+"";
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 12));
        }
    }

    // AsyncTask that send a request to the server
    private void sendGetRequest(){
        class GetOffersLocations extends AsyncTask<String,Void,String> {
            private ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MapsActivity.this,
                        getResources().getString(R.string.gettingData),
                        getResources().getString(R.string.wait),false,false);
            }

            @Override
            protected String doInBackground(String... params) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(findViewById(R.id.googleMap), new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendGetRequest();
                    }
                });

                if (connectionStatus)
                    return rh.sendGetRequestParam(Config.URL_GET_MAP_OFFERS, params[0]);
                else
                    return "-1";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (!s.equals("-1"))
                    fillOffersLocations(s);
            }
        }

        String params = "";
        if ((lastLatitude != null) && (lastLongitude != null))
            if ((!lastLatitude.equals("")) && (!lastLongitude.equals("")))
                params = params + "lat=" + lastLatitude + "&lon=" + lastLongitude;

        String idPerson = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE)
                .getString(Config.KEY_SP_ID, "-1");

        params = params + "&idPersona=" +idPerson;

        GetOffersLocations go = new GetOffersLocations();
        go.execute(params);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        this.googleMap = map;

        googleMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);

        // Add top padding
        googleMap.setPadding(0, 200, 0, 0);

        googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showSnackbar(marker);
                return false;
            }
        });

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

        setCameraPosition();
        sendGetRequest();
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;
        private Context context;

        MyInfoWindowAdapter(Context context){
            this.context = context;
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());

            ImageView ivIcon = ((ImageView)myContentsView.findViewById(R.id.icon));

            if (markerSet.get(marker.getId())) {
                Picasso.with(context)
                        .load(markers.get(marker.getId()))
                        .placeholder(R.drawable.ic_loading_image)
                        .into(ivIcon);
            } else {
                markerSet.put(marker.getId(), true);
                Picasso.with(context)
                        .load(markers.get(marker.getId()))
                        .placeholder(R.drawable.ic_loading_image)
                        .into(ivIcon, new InfoWindowRefresher(marker));
            }

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        class InfoWindowRefresher implements Callback {
            private Marker markerToRefresh;

            InfoWindowRefresher(Marker markerToRefresh) {
                this.markerToRefresh = markerToRefresh;
            }

            @Override
            public void onSuccess() {
                markerToRefresh.showInfoWindow();
            }

            @Override
            public void onError() {}
        }

    }

    private String makeSnackbarTitle(Store s) {
        String format = (s.getnOffers().equals("1"))? getResources().getString(R.string.MA_storeFormatSingle)
                : getResources().getString(R.string.MA_storeFormatPlural);

        return String.format(format, s.getnOffers());
    }

    GoogleMap.OnInfoWindowClickListener MyOnInfoWindowClickListener
            = new GoogleMap.OnInfoWindowClickListener(){
        @Override
        public void onInfoWindowClick(Marker marker) {
            showSnackbar(marker);
        }
    };

    private void showSnackbar(Marker marker) {
        final Store s = stores.get(marker.getId());
        Snackbar.make(findViewById(R.id.googleMap), makeSnackbarTitle(s), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.MA_go, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        // TODO: call offers activity
                        Intent i = new Intent(MapsActivity.this, FilterActivity.class);
                        i.putExtra("idStore", s.getIdStore());
                        i.putExtra("companyName", s.getName());
                        startActivity(i);
                    }
                }).show();
    }

    class Store {
        private String idStore;
        private String name;
        private String nOffers;

        Store(String i, String n, String nO) {
            this.idStore = i;
            this.name = n;
            this.nOffers = nO;
        }

        public String getIdStore() {
            return idStore;
        }

        public String getName() {
            return name;
        }

        public String getnOffers() {
            return nOffers;
        }
    }
}
