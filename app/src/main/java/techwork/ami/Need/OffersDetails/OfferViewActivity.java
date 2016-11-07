package techwork.ami.Need.OffersDetails;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
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
import techwork.ami.MainActivity;
import techwork.ami.Need.OffersList.OffersActivity;
import techwork.ami.Need.NeedOfferLocalDetails.NeedOfferViewLocalActivity;
import techwork.ami.Need.OrdersList.FragmentOrder;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class OfferViewActivity extends AppCompatActivity {

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
        setContentView(R.layout.offers_view_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Init textviews
        tvTittle = (TextView)findViewById(R.id.tv_offer_view_tittle);
        tvPrice = (TextView)findViewById(R.id.tv_offer_view_price);
        tvCompany= (TextView)findViewById(R.id.tv_offer_view_company);
        tvDescription= (TextView)findViewById(R.id.tv_offer_view_description);
        tvDateIni = (TextView)findViewById(R.id.tv_offer_view_date_ini);
        tvDateFin = (TextView)findViewById(R.id.tv_offer_view_date_fin);
        tvMaxPPerson = (TextView)findViewById(R.id.tv_offer_view_max_person);

        //Init buttons
        btnAccept = (Button)findViewById(R.id.btn_offer_view_accept);
        btnDiscard= (Button)findViewById(R.id.btn_offer_view_discard);

        //Get info from OffersActivity
        final Bundle bundle = getIntent().getExtras();

        //Capture id's
        idOffer = bundle.getString(Config.TAG_GET_OFFER_IDOFFER);
        idLocal = bundle.getString(Config.TAG_GET_OFFER_IDLOCAL);


        //Set TextViews with the information of each NeedOffer.
        tvTittle.setText(bundle.getString(Config.TAG_GET_OFFER_TITTLE));
        tvDescription.setText(bundle.getString(Config.TAG_GET_OFFER_DESCRIPTION));
        tvPrice.setText(String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GET_OFFER_PRICEOFFER)));
        tvCompany.setText(bundle.getString(Config.TAG_GET_OFFER_COMPANY));
        tvDateIni.setText("Fecha de publicación: " + bundle.getString(Config.TAG_GET_OFFER_DATEINI));
        tvDateFin.setText("Fecha de expiración: " + bundle.getString(Config.TAG_GET_OFFER_DATEFIN));

        //If stock > maxxperson, textview show maxxperson
        if (Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_STOCK))>Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_MAXPPERSON))) {
            if (Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_MAXPPERSON)) > 1) {
                tvMaxPPerson.setText("¡Puedes reservar hasta " + bundle.getString(Config.TAG_GET_OFFER_MAXPPERSON) + " unidades!");
            } else {
                tvMaxPPerson.setText("¡Puedes reservar hasta " + bundle.getString(Config.TAG_GET_OFFER_MAXPPERSON) + " unidad!");
            }
        }
        //if stock < maxpperson, textview show stock
        else{
            if (Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_STOCK)) > 1) {
                tvMaxPPerson.setText("¡Puedes reservar hasta " + bundle.getString(Config.TAG_GET_OFFER_STOCK) + " unidades!");
            } else {
                tvMaxPPerson.setText("¡Puedes reservar hasta " + bundle.getString(Config.TAG_GET_OFFER_STOCK) + " unidad!");
            }
        }


        //Load recycle view
        rv = (RecyclerView)findViewById(R.id.recycler_view_offer_view);
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
                (Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_MAXPPERSON)) <= Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_STOCK)))?
                        Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_MAXPPERSON)) : Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_STOCK));

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

                    private ProgressDialog loading;


                    @Override
                    protected void onPreExecute(){
                        super.onPreExecute();
                        loading = ProgressDialog.show(OfferViewActivity.this,
                                getString(R.string.OfferViewAcceptOfferProcessing),
                                getString(R.string.wait), false, false);

                    }

                    @Override
                    protected String doInBackground(Void... params) {

                        HashMap<String,String> hashMap = new HashMap<>();

                        hashMap.put(Config.KEY_ACCEPT_OFFER_IDOFFER,idOffer);
                        hashMap.put(Config.KEY_ACCEPT_OFFER_IDPERSON,idPerson);
                        hashMap.put(Config.KEY_ACCEPT_OFFER_MAXPPERSON, cantidad[0]);

                        RequestHandler rh = new RequestHandler();
                        return rh.sendPostRequest(Config.URL_ACCEPT_OFFER,hashMap);
                    }
                    @Override
                    protected void onPostExecute(String s){
                        super.onPostExecute(s);


                        if (s.equals("0")){

                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    loading.dismiss();
                                    c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                                    c.vibrate(500);

                                    Toast.makeText(getApplicationContext(),R.string.OfferViewAcceptOffer, Toast.LENGTH_LONG).show();

                                    //OffersActivity (List offer companies) is finish.
                                    OffersActivity.activity.finish();

                                    //NeedOffer accept go to LocalDetails.
                                    Intent intent = new Intent(OfferViewActivity.this,NeedOfferViewLocalActivity.class);
                                    intent.putExtra(Config.TAG_GET_OFFER_IDLOCAL,idLocal);
                                    finish();
                                    startActivity(intent);
                                }
                            },1500);

                        }else{
                            loading.dismiss();
                            Toast.makeText(getApplicationContext(),R.string.OfferViewAcceptOfferFail,Toast.LENGTH_LONG).show();
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
                        loading = ProgressDialog.show(OfferViewActivity.this,
                                getString(R.string.OfferViewDiscardOfferProcessing),
                                getString(R.string.wait), false, false);

                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put(Config.KEY_DISCARD_OFFER_IDOFFER,idOffer);
                        hashMap.put(Config.KEY_DISCARD_OFFER_IDPERSON,idPerson);

                        RequestHandler rh = new RequestHandler();

                        return rh.sendPostRequest(Config.URL_DISCARD_OFFER,hashMap);
                    }
                    @Override
                    protected void onPostExecute(String s){
                        super.onPostExecute(s);

                        if (s.equals("0")){

                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();

                                    c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                                    c.vibrate(500);

                                    Toast.makeText(getApplicationContext(), R.string.OfferViewDiscardOffer, Toast.LENGTH_LONG).show();

                                    //If Offer is discard go to Main activity refreshed
                                    Intent intent = new Intent(OfferViewActivity.this,MainActivity.class);
                                    finish();
                                    startActivity(intent);

                                }
                            },1500);

                        }else{
                            Toast.makeText(getApplicationContext(),R.string.OfferViewDiscardOfferFail,Toast.LENGTH_LONG).show();
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

                hashmap.put(Config.KEY_GET_PRODUCT_OFFER_IDOFFER,idOffer);

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
            JSONArray jsonProductOffer = jsonObject.optJSONArray(Config.TAG_GET_PRODUCT_OFFER);
            productList= new ArrayList<>();

            for (int i=0;i<jsonProductOffer.length();i++){
                JSONObject jsonObjectItem = jsonProductOffer.optJSONObject(i);
                ProductModel item = new ProductModel();
                item.setName(jsonObjectItem.getString(Config.TAG_GET_PRODUCT_OFFER_NAME));
                productList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
