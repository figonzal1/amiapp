package techwork.ami.Offers.OffersDetails;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.shawnlin.numberpicker.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import techwork.ami.AnimateMenuFab;
import techwork.ami.Config;
import techwork.ami.LocalDetails.LocalActivity;
import techwork.ami.MainActivity;
import techwork.ami.Offers.OffersList.OffersActivity;
import techwork.ami.Offers.OffersReservations.OffersReservationsList.OffersReservationsActivity;
import techwork.ami.Offers.OrdersList.FragmentOrder;
import techwork.ami.Promotion.PromotionsList.FragmentHome;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class OffersViewActivity extends AppCompatActivity {

    TextView tvTittle,tvCompany,tvDescription,tvMaxPPerson,tvPriceOffer,tvPriceNormal,tvDsctSym,tvDsct;
    Button btnLocal;
    private String idOffer,idLocal,idNeed, companyName;
    private List<ProductModel> productList;
    private RecyclerView rv;
    private GridLayoutManager layout;
    private ProductAdapter adapter;
    private Vibrator c;
    private com.github.clans.fab.FloatingActionButton fabAccept,fabDiscard;
    private FloatingActionMenu fabMenu;
    private SwipeRefreshLayout refreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers_view_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Init textviews
        tvTittle = (TextView)findViewById(R.id.tv_offer_view_tittle);
        tvCompany= (TextView)findViewById(R.id.tv_offer_view_company);
        tvDescription= (TextView)findViewById(R.id.tv_offer_view_description);
        tvMaxPPerson = (TextView)findViewById(R.id.tv_offer_view_max_person);
        tvPriceNormal=(TextView)findViewById(R.id.tv_offer_view_price_normal);
        tvPriceOffer=(TextView)findViewById(R.id.tv_offer_view_price_promotion);
        tvDsct=(TextView)findViewById(R.id.tv_offer_view_dsct);
        tvDsctSym=(TextView)findViewById(R.id.tv_offer_view_dsct_sy);
        fabAccept = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_accept);
        fabDiscard= (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_discard);
        fabMenu=(FloatingActionMenu)findViewById(R.id.menu_fab);
        btnLocal=(Button)findViewById(R.id.btn_offer_view_local);

        //Get info from OffersActivity
        final Bundle bundle = getIntent().getExtras();

        //Capture id's
        idOffer = bundle.getString(Config.TAG_GET_OFFER_IDOFFER);
        idLocal = bundle.getString(Config.TAG_GET_OFFER_IDLOCAL);
        idNeed = bundle.getString(Config.TAG_GET_OFFER_IDNEED);


        //Set TextViews with the information of each NeedOffer.
        tvTittle.setText(bundle.getString(Config.TAG_GET_OFFER_TITTLE));
        tvDescription.setText(bundle.getString(Config.TAG_GET_OFFER_DESCRIPTION));
        tvPriceOffer.setText(String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GET_OFFER_PRICEOFFER)));
        tvPriceNormal.setText(String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GET_OFFER_PRICE_TOTAL)));
        companyName = bundle.getString(Config.TAG_GET_OFFER_COMPANY);
        tvCompany.setText(companyName);

        int perc = (bundle.getInt(Config.TAG_GET_OFFER_PRICE_TOTAL) != 0) ?
                bundle.getInt(Config.TAG_GET_OFFER_PRICEOFFER)*100/bundle.getInt(Config.TAG_GET_OFFER_PRICE_TOTAL):
                100;
        // If offer price is greater than total price
        String s = "";
        if (perc == 100){
            tvDsct.setText("");
        }
        else if (perc > 100){
            // Red color
            tvDsct.setText(getResources().getString(R.string.od_tv_increase_txt));
            tvDsct.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            s = "+";
            tvDsctSym.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }
        else{
            s = "-";
        }
        if(!s.equals("")){
            tvDsctSym.setText(s+String.valueOf(Math.abs(100-perc))+"%");
        }
        else tvDsctSym.setText("");

        //If stock > maxxperson, textview show maxxperson
        if (Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_STOCK))>
                Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_MAXPPERSON))) {
            if (Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_MAXPPERSON)) > 1) {
                tvMaxPPerson.setText(String.format(getResources().getString(R.string.OfferViewMaxppWarnings),
                        bundle.getString(Config.TAG_GET_OFFER_MAXPPERSON)));
            } else {
                tvMaxPPerson.setText(String.format(getResources().getString(R.string.OfferViewMaxppWarning),
                        bundle.getString(Config.TAG_GET_OFFER_MAXPPERSON)));
            }
        }
        //if stock < maxpperson, textview show stock
        else{
            if (Integer.valueOf(bundle.getString(Config.TAG_GET_OFFER_STOCK)) > 1) {
                tvMaxPPerson.setText(String.format(getResources().getString(R.string.OfferViewMaxppWarnings),
                        bundle.getString(Config.TAG_GET_OFFER_STOCK)));
            } else {
                tvMaxPPerson.setText(String.format(getResources().getString(R.string.OfferViewMaxppWarning),
                        bundle.getString(Config.TAG_GET_OFFER_STOCK)));
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

        //Refresh layout
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_offers_view);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOfferProducts();
            }
        });

        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OffersViewActivity.this,LocalActivity.class);
                intent.putExtra(Config.TAG_LOCAL_ACTIVITY_COMPANY, companyName);
                intent.putExtra(Config.TAG_GET_OFFER_IDLOCAL,idLocal);
                startActivity(intent);
            }
        });

        AnimateMenuFab.doAnimateMenuFab(fabMenu,getApplicationContext());
        fabAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Accept Offer Task
                class acceptOfferAsyncTask extends AsyncTask<Void,Void,String>{

                    private ProgressDialog loading;


                    @Override
                    protected void onPreExecute(){
                        super.onPreExecute();
                        loading = ProgressDialog.show(OffersViewActivity.this,
                                getString(R.string.OfferViewAcceptOfferProcessing),
                                getString(R.string.wait), false, false);

                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        RequestHandler rh = new RequestHandler();

                        Boolean connectionStatus = rh.isConnectedToServer(rv, new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {
                                sendPostRequest();
                            }
                        });

                        if (connectionStatus){
                            HashMap<String,String> hashMap = new HashMap<>();

                            hashMap.put(Config.KEY_ACCEPT_OFFER_IDOFFER,idOffer);
                            hashMap.put(Config.KEY_ACCEPT_OFFER_IDPERSON,idPerson);
                            hashMap.put(Config.KEY_ACCEPT_OFFER_MAXPPERSON, cantidad[0]);

                            return rh.sendPostRequest(Config.URL_ACCEPT_OFFER,hashMap);
                        }
                        else {
                            return "-1";
                        }


                    }
                    @Override
                    protected void onPostExecute(String s){
                        super.onPostExecute(s);

                        if (s.equals("0") && !s.equals("-1")){

                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {

                                @Override
                                public void run() {

                                    loading.dismiss();

                                    c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                                    c.vibrate(500);

                                    //Finish OfferListActivity
                                    //OffersActivity.activity.finish();

                                    Toast.makeText(getApplicationContext(),R.string.OfferViewAcceptOffer, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(OffersViewActivity.this,OffersReservationsActivity.class);
                                    finish();
                                    startActivity(intent);

                                }
                            },1500);

                        }else{
                            loading.dismiss();

                            c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            c.vibrate(500);

                            Toast.makeText(getApplicationContext(),R.string.OfferViewAcceptOfferFail,Toast.LENGTH_LONG).show();
                        }
                    }
                }

                acceptOfferAsyncTask go = new acceptOfferAsyncTask();
                go.execute();
            }
        });

        fabDiscard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                class discardOfferAsyncTask extends AsyncTask<Void,Void,String>{

                    private ProgressDialog loading;
                    @Override
                    protected void onPreExecute(){
                        super.onPreExecute();
                        loading = ProgressDialog.show(OffersViewActivity.this,
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
                                    Intent intent = new Intent(OffersViewActivity.this,OffersActivity.class);
                                    intent.putExtra(Config.TAG_GET_OFFER_IDNEED,idNeed);
                                    finish();
                                    startActivity(intent);

                                }
                            },1500);

                        }else{
                            loading.dismiss();
                            c=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            c.vibrate(500);
                            Toast.makeText(getApplicationContext(),R.string.OfferViewDiscardOfferFail,Toast.LENGTH_LONG).show();
                        }
                    }
                }
                discardOfferAsyncTask go = new discardOfferAsyncTask();
                go.execute();
            }
        });
        getOfferProducts();

    }

    private void getOfferProducts(){
        sendPostRequest();
    }

    private void sendPostRequest() {

        class ProductOfferAsyncTask extends AsyncTask<Void,Void,String>{

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }

            @Override
            protected String doInBackground(Void... voids) {

                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(rv, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendPostRequest();
                    }
                });

                if (connectionStatus) {
                    HashMap<String, String> hashmap = new HashMap<>();
                    hashmap.put(Config.KEY_GET_PRODUCT_OFFER_IDOFFER, idOffer);
                    return rh.sendPostRequest(Config.URL_GET_PRODUCT_OFFER, hashmap);
                }
                else {
                    return "-1";
                }
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                if (!s.equals("-1")) {
                    showProducts(s);
                }
            }
        }
        ProductOfferAsyncTask go = new ProductOfferAsyncTask();
        go.execute();
    }

    private void showProducts(String s){
        getProductData(s);

        adapter = new ProductAdapter(getApplicationContext(),productList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);
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
                item.setDescription(jsonObjectItem.getString(Config.TAG_GET_PRODUCT_OFFER_DESCRIPTION));
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GET_PRODUCT_OFFER_PRICE));
                item.setImage(jsonObjectItem.getString(Config.TAG_GET_PRODUCT_OFFER_IMAGE));
                item.setQuantity(jsonObjectItem.getString(Config.TAG_GET_PRODUCT_OFFER_QUANTITY));
                productList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
