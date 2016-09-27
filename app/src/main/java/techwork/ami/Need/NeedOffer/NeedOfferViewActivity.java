package techwork.ami.Need.NeedOffer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import techwork.ami.Config;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class NeedOfferViewActivity extends AppCompatActivity {

    TextView tvTittle,tvPrice,tvCompany,tvDescription,tvDateIni,tvDateFin,tvStock,tvMaxPPerson;
    Button btnAccept;
    Button btnDiscard;
    String idOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_offer_view_activity);

        //Init textviews
        tvTittle = (TextView)findViewById(R.id.tv_need_offer_view_tittle);
        tvPrice = (TextView)findViewById(R.id.tv_need_offer_view_price);
        tvCompany= (TextView)findViewById(R.id.tv_need_offer_view_company);
        tvDescription= (TextView)findViewById(R.id.tv_need_offer_view_description);
        tvDateIni = (TextView)findViewById(R.id.tv_need_offer_view_date_ini);
        tvDateFin = (TextView)findViewById(R.id.tv_need_offer_view_date_fin);
        //tvStock = (TextView)findViewById(R.id.tv_need_offer_view_stock);
        tvMaxPPerson = (TextView)findViewById(R.id.tv_need_offer_view_max_person);

        //Init buttons
        btnAccept = (Button)findViewById(R.id.btn_need_offer_view_accept);
        btnDiscard= (Button)findViewById(R.id.btn_need_offer_view_discard);

        //Get info from NeedOfferActivity
        Bundle bundle = getIntent().getExtras();

        idOffer = bundle.getString(Config.TAG_GNO_IDOFFER);
        tvTittle.setText(bundle.getString(Config.TAG_GNO_TITTLE));
        tvPrice.setText("$"+String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GNO_PRICEOFFER)));
        tvCompany.setText(bundle.getString(Config.TAG_GNO_COMPANY));
        tvDateIni.setText("Fecha de publicación: "+bundle.getString(Config.TAG_GNO_DATEINI));
        tvDateFin.setText("Fecha de expiración: "+bundle.getString(Config.TAG_GNO_DATEFIN));
        tvMaxPPerson.setText("¡Puedes reservar hasta "+bundle.getString(Config.TAG_GNO_MAXPPERSON)+" unidades!");

        //Get id of user
        SharedPreferences sharePref= getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        final String id = sharePref.getString(Config.KEY_SP_ID,"-1");

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se realiza la aceptacion de la oferta en BD
                class acceptNeedOfferAsyncTask extends AsyncTask<Void,Void,String>{


                    @Override
                    protected void onPreExecute(){
                        super.onPreExecute();
                        Toast.makeText(getApplicationContext(),"Aceptando oferta..",Toast.LENGTH_SHORT).show();
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
                        if (s.equals("0")){
                            Toast.makeText(getApplicationContext(), "Oferta Aceptada", Toast.LENGTH_SHORT).show();

                            //Se redirije a la activity del detalle del local.
                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {

                                // Salir de la activity despues de que la necesidad haya sido registrada
                                @Override
                                public void run() {
                                    startActivity(new Intent(NeedOfferViewActivity.this, NeedOfferReservDetails.class));
                                }

                            }, 2500);


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

                    @Override
                    protected void onPreExecute(){
                        super.onPreExecute();
                        Toast.makeText(getApplicationContext(),"Aceptando oferta..",Toast.LENGTH_SHORT).show();
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
