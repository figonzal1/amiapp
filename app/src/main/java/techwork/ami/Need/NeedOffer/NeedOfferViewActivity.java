package techwork.ami.Need.NeedOffer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import techwork.ami.Config;
import techwork.ami.Need.NeedActivity;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class NeedOfferViewActivity extends AppCompatActivity {

    TextView tvTittle;
    TextView tvPrice;
    Button btnAccept;
    Button btnDiscard;
    String idOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_offer_view_activity);

        tvTittle = (TextView)findViewById(R.id.tv_need_offer_view_tittle);
        tvPrice = (TextView)findViewById(R.id.tv_need_offer_view_price);
        btnAccept = (Button)findViewById(R.id.btn_need_offer_view_accept);
        btnDiscard= (Button)findViewById(R.id.btn_need_offer_view_discard);

        Bundle bundle = getIntent().getExtras();

        idOffer = bundle.getString(Config.TAG_GNO_IDOFFER);
        tvTittle.setText(bundle.getString(Config.TAG_GNO_TITTLE));
        tvPrice.setText("$"+String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GNO_PRICEOFFER)));

        //Gettin id of user
        SharedPreferences sharePref= getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        final String id = sharePref.getString(Config.KEY_SP_ID,"-1");

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                class acceptNeedOfferAsyncTask extends AsyncTask<Void,Void,String>{

                    private ProgressDialog loading;
                    @Override
                    protected void onPreExecute(){
                        super.onPreExecute();
                        loading = ProgressDialog.show(NeedOfferViewActivity.this,
                                getResources().getString(R.string.saving),
                                getResources().getString(R.string.wait),false,false);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put(Config.KEY_ANO_IDOFFER,idOffer);
                        hashMap.put(Config.KEY_ANO_IDPERSON,id);

                        RequestHandler rh = new RequestHandler();
                        return rh.sendPostRequest(Config.URL_ACCEPT_NEED_OFFER,hashMap);
                    }
                    @Override
                    protected void onPostExecute(String s){
                        super.onPostExecute(s);
                        loading.dismiss();
                        if (s.equals("0")){
                            Toast.makeText(getApplicationContext(), "Oferta Aceptada", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"No se ha podido realizar la reserva",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                acceptNeedOfferAsyncTask go = new acceptNeedOfferAsyncTask();
                go.execute();

            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                class discardNeedOfferAsyncTask extends AsyncTask<Void,Void,String>{
                    private ProgressDialog loading;
                    @Override
                    protected void onPreExecute(){
                        super.onPreExecute();
                        loading = ProgressDialog.show(NeedOfferViewActivity.this,
                                getResources().getString(R.string.saving),
                                getResources().getString(R.string.wait),false,false);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put(Config.KEY_DNO_IDOFFER,idOffer);
                        hashMap.put(Config.KEY_DNO_IDPERSON,id);

                        RequestHandler rh = new RequestHandler();

                        return rh.sendPostRequest(Config.URL_DISCARD_NEED_OFFER,hashMap);
                    }
                    @Override
                    protected void onPostExecute(String s){
                        super.onPostExecute(s);
                        loading.dismiss();
                        if (s.equals("0")){
                            Toast.makeText(getApplicationContext(), "Oferta rechazada", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"No se ha podido rechazar",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                discardNeedOfferAsyncTask go = new discardNeedOfferAsyncTask();
                go.execute();
            }
        });

    }
}
