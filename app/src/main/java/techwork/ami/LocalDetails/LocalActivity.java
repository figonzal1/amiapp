package techwork.ami.LocalDetails;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import techwork.ami.Config;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class LocalActivity extends AppCompatActivity{

    private String idLocal,lat,lon,address,web,image,commune, localName;
    Button btnStreetView, btnMap;
    TextView tvAddress,tvWeb;
    ImageView ivImage;
    private SwipeRefreshLayout refreshLayout;
    private NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //If connection to internet is false, activity local not open.
        if (!checkInternet()){
            Toast.makeText(getApplicationContext(),R.string.ConnectToInternet,Toast.LENGTH_LONG).show();
            finish();
        }

        final Bundle bundle = getIntent().getExtras();
        idLocal= bundle.getString(Config.TAG_GET_OFFER_IDLOCAL);
        localName = bundle.getString(Config.TAG_LOCAL_ACTIVITY_COMPANY);

        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_offer_view_local);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLocal();
            }
        });

        btnStreetView = (Button)findViewById(R.id.btn_local_street_view);
        btnStreetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if the phone is connect to internet
                if (checkInternet()){

                    /*Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+lat+","+lon+"");
                    Intent intent2 = new Intent(Intent.ACTION_VIEW, gmmIntentUri);*/

                    Intent intent = new Intent(LocalActivity.this,StreetViewPanoramaFragment.class);
                    intent.putExtra(Config.TAG_GET_LOCAL_LAT,lat);
                    intent.putExtra(Config.TAG_GET_LOCAL_LONG,lon);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),R.string.ConnectToInternet,Toast.LENGTH_LONG).show();
                }
            }
        });
        btnMap = (Button)findViewById(R.id.btn_local_map) ;
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if the phone is connect to internet
                if (checkInternet()){
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("geo:"+lat+","+lon+"?z=16&q="+lat+","+lon+"("+localName+")"));
                    intent.setClassName("com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),R.string.ConnectToInternet,Toast.LENGTH_LONG).show();
                }
            }
        });

        //GetLocal info
        getLocal();

    }
    private boolean checkInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }


    private void getLocal(){ sendPostRequest();}

    private void sendPostRequest() {

        final View v1 = getWindow().getDecorView().getRootView();

        class LocalDetailsAsyncTask extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }

            @Override
            protected String doInBackground(Void... params) {

                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(v1, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendPostRequest();
                    }
                });

                if (connectionStatus) {
                    HashMap<String, String> hashmap = new HashMap<>();
                    //Send id local to php archive.
                    hashmap.put(Config.KEY_GET_LOCAL_IDLOCAL, idLocal);

                    return rh.sendPostRequest(Config.URL_GET_LOCAL, hashmap);
                }
                else{
                    return "-1";
                }
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                if (!s.equals("-1")){
                    showLocal(s);
                }

            }
        }
        LocalDetailsAsyncTask go = new LocalDetailsAsyncTask();
        go.execute();
    }

    //Getting info of json for each local
    private void showLocal(String json) {
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject local = jsonObject.getJSONArray(Config.TAG_GET_LOCAL).getJSONObject(0);

            lat = local.getString(Config.TAG_GET_LOCAL_LAT);
            lon= local.getString(Config.TAG_GET_LOCAL_LONG);
            address= local.getString(Config.TAG_GET_LOCAL_ADDRESS);
            web = local.getString(Config.TAG_GET_LOCAL_WEB);
            image= local.getString(Config.TAG_GET_LOCAL_IMAGE);
            commune=local.getString(Config.TAG_GET_LOCAL_COMMUNE);

            tvAddress = (TextView)findViewById(R.id.tv_address);
            tvWeb = (TextView)findViewById(R.id.tv_web2);
            ivImage = (ImageView)findViewById(R.id.iv_local);

            tvAddress.setText(address+", "+commune);
            tvWeb.setText(web);

            //Image of local are get of admin directory
            Picasso.with(getApplicationContext())
                        .load(Config.URL_LOCAL_IMAGE+image)
                        .into(ivImage);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
