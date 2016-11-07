package techwork.ami.Need.OffersReservations.OffersReservationsDetails;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import techwork.ami.Config;
import techwork.ami.Need.OffersLocalDetails.StreetViewPanoramaFragment;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class OffersReservationsViewLocalActivity extends AppCompatActivity {

    private String idLocal,lat,lon,address,web,image,commune;
    Button btnStreetView;
    TextView tvAddress,tvWeb;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers_reservations_view_local_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();

        //Get local info
        idLocal= bundle.getString(Config.TAG_GET_OFFER_RESERVED_IDLOCAL);

        tvAddress=(TextView)findViewById(R.id.tv_address);
        tvWeb=(TextView)findViewById(R.id.tv_web2);
        ivImage=(ImageView)findViewById(R.id.iv_local);
        btnStreetView = (Button)findViewById(R.id.btn_local_street_view);

        getLocal();

        btnStreetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(OffersReservationsViewLocalActivity.this, StreetViewPanoramaFragment.class);
                intent.putExtra(Config.TAG_GET_LOCAL_LAT,lat);
                intent.putExtra(Config.TAG_GET_LOCAL_LONG,lon);
                startActivity(intent);
            }
        });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLocal(){ sendPostRequest();}

    private void sendPostRequest() {

        class LocalDetailsAsyncTask extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashmap= new HashMap<>();

                hashmap.put(Config.KEY_GET_LOCAL_IDLOCAL,idLocal);
                RequestHandler rh = new RequestHandler();

                return rh.sendPostRequest(Config.URL_GET_LOCAL,hashmap);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                showLocal(s);
            }
        }
        LocalDetailsAsyncTask go = new LocalDetailsAsyncTask();
        go.execute();
    }

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

            Picasso.with(getApplicationContext())
                    .load(Config.URL_LOCAL_IMAGE+image)
                    .into(ivImage);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

