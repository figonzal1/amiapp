package techwork.ami.Need.NeedOfferDetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.shawnlin.numberpicker.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import techwork.ami.Config;
import techwork.ami.Need.ListOfferCompanies.NeedOfferActivity;
import techwork.ami.Need.NeedOfferLocalDetails.NeedOfferViewLocalActivity;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class NeedOfferViewActivity extends AppCompatActivity {

    TextView tvTittle,tvPrice,tvCompany,tvDescription,tvDateIni,tvDateFin,tvMaxPPerson;
    Button btnAccept, btnDiscard;
    private String idOffer,idLocal;
    private List<ProductModel> productList;
    private RecyclerView rv;
    private GridLayoutManager layout;
    private ProductAdapter adapter;
    private Vibrator c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_offer_view_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Init textviews
        tvTittle = (TextView)findViewById(R.id.tv_need_offer_view_tittle);
        tvPrice = (TextView)findViewById(R.id.tv_need_offer_view_price);
        tvCompany= (TextView)findViewById(R.id.tv_need_offer_view_company);
        tvDescription= (TextView)findViewById(R.id.tv_need_offer_view_description);
        tvDateIni = (TextView)findViewById(R.id.tv_need_offer_view_date_ini);
        tvDateFin = (TextView)findViewById(R.id.tv_need_offer_view_date_fin);
        tvMaxPPerson = (TextView)findViewById(R.id.tv_need_offer_view_max_person);


        //Init buttons
        btnAccept = (Button)findViewById(R.id.btn_need_offer_view_accept);
        btnDiscard= (Button)findViewById(R.id.btn_need_offer_view_discard);


        //Get info from NeedOfferActivity
        final Bundle bundle = getIntent().getExtras();

        //Capture id's
        idOffer = bundle.getString(Config.TAG_GNO_IDOFFER);
        idLocal = bundle.getString(Config.TAG_GNO_IDLOCAL);

        //Set TextViews with the information of each NeedOffer.
        tvTittle.setText(bundle.getString(Config.TAG_GNO_TITTLE));
        tvDescription.setText(bundle.getString(Config.TAG_GNO_DESCRIPTION));
        tvPrice.setText("$"+String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GNO_PRICEOFFER)));
        tvCompany.setText(bundle.getString(Config.TAG_GNO_COMPANY));
        tvDateIni.setText("Fecha de publicación: "+bundle.getString(Config.TAG_GNO_DATEINI));
        tvDateFin.setText("Fecha de expiración: "+bundle.getString(Config.TAG_GNO_DATEFIN));

        //If stock > maxxperson, textview show maxxperson
        if (Integer.valueOf(bundle.getString(Config.TAG_GNO_STOCK))>Integer.valueOf(bundle.getString(Config.TAG_GNO_MAXPPERSON))) {
            if (Integer.valueOf(bundle.getString(Config.TAG_GNO_MAXPPERSON)) > 1) {
                tvMaxPPerson.setText("¡Puedes reservar hasta " + bundle.getString(Config.TAG_GNO_MAXPPERSON) + " unidades!");
            } else {
                tvMaxPPerson.setText("¡Puedes reservar hasta " + bundle.getString(Config.TAG_GNO_MAXPPERSON) + " unidad!");
            }
        }
        //if stock < maxpperson, textview show stock
        else{
            if (Integer.valueOf(bundle.getString(Config.TAG_GNO_STOCK)) > 1) {
                tvMaxPPerson.setText("¡Puedes reservar hasta " + bundle.getString(Config.TAG_GNO_STOCK) + " unidades!");
            } else {
                tvMaxPPerson.setText("¡Puedes reservar hasta " + bundle.getString(Config.TAG_GNO_STOCK) + " unidad!");
            }
        }


        //Load recycle view
        rv = (RecyclerView)findViewById(R.id.recycler_view_need_offer_view);
        rv.setHasFixedSize(true);
        layout = new GridLayoutManager(this,1);
        rv.setLayoutManager(layout);

        //Get id of user
        SharedPreferences sharePref= getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        final String idPerson = sharePref.getString(Config.KEY_SP_ID,"-1");

        //Settings of number picker
        final NumberPicker numberPicker = (NumberPicker)findViewById(R.id.number_picker);
        numberPicker.setMinValue(1);
        int quantity =
                (Integer.valueOf(bundle.getString(Config.TAG_GNO_MAXPPERSON)) <= Integer.valueOf(bundle.getString(Config.TAG_GNO_STOCK)))?
                        Integer.valueOf(bundle.getString(Config.TAG_GNO_MAXPPERSON)) : Integer.valueOf(bundle.getString(Config.TAG_GNO_STOCK));
        Toast.makeText(getApplicationContext(),String.valueOf(quantity),Toast.LENGTH_LONG).show();
        numberPicker.setMaxValue(quantity);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(1);

        final String[] cantidad = new String[1];
        cantidad[0]= String.valueOf(numberPicker.getValue());

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                 cantidad[0] = ""+ newVal;
            }
        });


        //Actions of Buttons
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Accept NeedOffer Task
                class acceptNeedOfferAsyncTask extends AsyncTask<Void,Void,String>{


                    //TODO: Reemplazar por un dialog.
                    @Override
                    protected void onPreExecute(){
                        super.onPreExecute();
                        Toast.makeText(getApplicationContext(),"Aceptando oferta..",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {

                        HashMap<String,String> hashMap = new HashMap<>();

                        hashMap.put(Config.KEY_ANO_IDOFFER,idOffer);
                        hashMap.put(Config.KEY_ANO_IDPERSON,idPerson);
                        hashMap.put(Config.KEY_ANO_MAXPPERSON, cantidad[0]);

                        RequestHandler rh = new RequestHandler();
                        return rh.sendPostRequest(Config.URL_ACCEPT_NEED_OFFER,hashMap);
                    }
                    @Override
                    protected void onPostExecute(String s){
                        super.onPostExecute(s);
                        if (s.equals("0")){
                            Toast.makeText(getApplicationContext(), "Oferta Aceptada", Toast.LENGTH_SHORT).show();


                            //Offer accept go to LocalDetails.
                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {

                                // Salir de la activity despues de que la necesidad haya sido registrada
                                @Override
                                public void run() {
                                    // Vibrate for 500 milliseconds
                                    c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                                    c.vibrate(500);
                                    NeedOfferActivity.fa.finish();
                                    Intent intent = new Intent(NeedOfferViewActivity.this,NeedOfferViewLocalActivity.class);
                                    intent.putExtra(Config.TAG_GNO_IDLOCAL,idLocal);
                                    startActivity(intent);
                                    finish();
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
                        Toast.makeText(getApplicationContext(),"Rechazando oferta..",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put(Config.KEY_DNO_IDOFFER,idOffer);
                        hashMap.put(Config.KEY_DNO_IDPERSON,idPerson);

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getNeedOfferProducts();

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

    private void getNeedOfferProducts(){
        sendPostRequest();
    }

    private void sendPostRequest() {

        class ProductOfferAsyncTask extends AsyncTask<Void,Void,String>{

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {

                HashMap<String,String> hashmap = new HashMap<>();

                hashmap.put(Config.KEY_PNO_IDOFFER,idOffer);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Config.URL_GET_PRODUCT_OFFER,hashmap);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                showProducts(s);
            }
        }
        ProductOfferAsyncTask go = new ProductOfferAsyncTask();
        go.execute();
    }

    private void showProducts(String s){
        getProductData(s);

        adapter = new ProductAdapter(getApplicationContext(),productList);
        rv.setAdapter(adapter);
    }

    private void getProductData(String json) {

        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonProductOffer = jsonObject.optJSONArray(Config.TAG_PNO_PRODUCT);
            productList= new ArrayList<>();

            for (int i=0;i<jsonProductOffer.length();i++){
                JSONObject jsonObjectItem = jsonProductOffer.optJSONObject(i);
                ProductModel item = new ProductModel();
                item.setName(jsonObjectItem.getString(Config.TAG_PNO_NAME));
                productList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
