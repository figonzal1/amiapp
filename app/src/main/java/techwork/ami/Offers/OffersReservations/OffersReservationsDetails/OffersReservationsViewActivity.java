package techwork.ami.Offers.OffersReservations.OffersReservationsDetails;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import techwork.ami.Config;
import techwork.ami.Offers.OffersDetails.ProductAdapter;
import techwork.ami.Offers.OffersDetails.ProductModel;
import techwork.ami.Offers.OffersLocalDetails.OffersViewLocalActivity;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class OffersReservationsViewActivity extends AppCompatActivity {

    TextView tvCompany,tvTittle,tvDescription,tvPriceOffer,tvPriceNormal,tvDsctSym,tvDsct;
    Button btnLocal;
    private String idOffer,idLocal;
    private RecyclerView rv;
    private LinearLayoutManager layout;
    private List<ProductModel> productList;
    private ProductAdapter adapter;
    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers_reservations_view_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();

        //Capture id's .
        idOffer = bundle.getString(Config.TAG_GET_OFFER_RESERVED_IDOFFER);
        idLocal = bundle.getString(Config.TAG_GET_OFFER_RESERVED_IDLOCAL);


        tvCompany = (TextView)findViewById(R.id.tv_offer_reservations_view_company);
        tvTittle=(TextView)findViewById(R.id.tv_offer_reservations_view_tittle);
        tvDescription=(TextView)findViewById(R.id.tv_offer_reservations_view_description);
        tvPriceOffer=(TextView)findViewById(R.id.tv_offer_reservations_view_price_offer);
        tvPriceNormal=(TextView)findViewById(R.id.tv_offer_reservations_view_price_normal);
        tvDsct=(TextView)findViewById(R.id.tv_offer_reservations_view_dsct);
        tvDsctSym=(TextView)findViewById(R.id.tv_offer_reservations_view_dsct_sy);
        btnLocal=(Button)findViewById(R.id.btn_offer_reservations_view_local_details);

        tvCompany.setText(bundle.getString(Config.TAG_GET_OFFER_RESERVED_COMPANY));
        tvTittle.setText(bundle.getString(Config.TAG_GET_OFFER_RESERVED_TITTLE));
        tvDescription.setText(bundle.getString(Config.TAG_GET_OFFER_RESERVED_DESCRIPTION));
        tvPriceOffer.setText(String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GET_OFFER_RESERVED_PRICEOFFER)));
        tvPriceNormal.setText(String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GET_OFFER_RESERVED_PRICE_TOTAL)));
        int perc = (bundle.getInt(Config.TAG_GET_OFFER_RESERVED_PRICE_TOTAL) != 0) ?
                bundle.getInt(Config.TAG_GET_OFFER_RESERVED_PRICEOFFER)*100/bundle.getInt(Config.TAG_GET_OFFER_RESERVED_PRICE_TOTAL):
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

        rv = (RecyclerView)findViewById(R.id.recycler_view_offer_reservations_view) ;
        rv.setHasFixedSize(true);
        layout = new LinearLayoutManager(this);
        rv.setLayoutManager(layout);

        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_offers_reservations_view);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNeedOfferProducts();
            }
        });

        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OffersReservationsViewActivity.this, OffersReservationsViewLocalActivity.class);
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_IDLOCAL,idLocal);
                startActivity(intent);
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getNeedOfferProducts();
    }

    private void getNeedOfferProducts(){
        sendPostRequest();
    }

    private void sendPostRequest() {

        class ProductOfferAsyncTask extends AsyncTask<Void,Void,String> {

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
                else{
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

    private void showProducts(String s) {
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
                productList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
