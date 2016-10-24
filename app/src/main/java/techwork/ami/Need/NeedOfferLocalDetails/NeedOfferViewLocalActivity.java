package techwork.ami.Need.NeedOfferLocalDetails;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import techwork.ami.MainActivity;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class NeedOfferViewLocalActivity extends AppCompatActivity{

    private String idLocal,lat,lon,address,web,image,commune;
    Button btnStreetView,btnNeedOfferReserv;
    TextView tvAddress,tvWeb;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_offer_view_local_activity);

        Bundle bundle = getIntent().getExtras();
        idLocal= bundle.getString(Config.TAG_GNO_IDLOCAL);

        //GetLocal info
        getLocal();

        btnStreetView = (Button)findViewById(R.id.btn_local_street_view);
        btnStreetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Funciona",Toast.LENGTH_LONG).show();
            }
        });

        btnNeedOfferReserv = (Button)findViewById(R.id.btn_mis_pedidos_reservados);
        btnNeedOfferReserv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NeedOfferViewLocalActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


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

                hashmap.put(Config.KEY_GL_IDLOCAL,idLocal);
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
            JSONObject local = jsonObject.getJSONArray(Config.TAG_GL_LOCAL).getJSONObject(0);

            lat = local.getString(Config.TAG_GL_LAT);
            lon= local.getString(Config.TAG_GL_LONG);
            address= local.getString(Config.TAG_GL_ADDRESS);
            web = local.getString(Config.TAG_GL_WEB);
            image= local.getString(Config.TAG_GL_IMAGE);
            commune=local.getString(Config.TAG_GL_COMMUNE);

            tvAddress = (TextView)findViewById(R.id.tv_address2);
            tvWeb = (TextView)findViewById(R.id.tv_web2);
            ivImage = (ImageView)findViewById(R.id.iv_local);

            tvAddress.setText(address+", "+commune);
            tvWeb.setText(web);

            if (image.isEmpty()){
                Picasso.with(getApplicationContext())
                        .load(R.drawable.not_check)
                        .into(ivImage);
            }else {
                Picasso.with(getApplicationContext())
                        .load(image)
                        .into(ivImage);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
