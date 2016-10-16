package techwork.ami.Offer.OfferDetail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import techwork.ami.Config;
import techwork.ami.R;
import techwork.ami.RequestHandler;

/**
 * Created by Daniel on 15-10-2016.
 */
public class OfferDetailActivity extends AppCompatActivity {
    // UI references
    private RecyclerView rv;
    private List<ProductModel> productList;
    private ProductAdapter adapter;
    private GridLayoutManager layout;
    private SwipeRefreshLayout refreshLayout;
    private String idOffer;
    private String idPersona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_detail_activity);

        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        idPersona = sharedPref.getString(Config.KEY_SP_ID, "-1");

        Bundle bundle = getIntent().getExtras();
        idOffer = bundle.getString(Config.TAG_GO_OFFER_ID);

        rv = (RecyclerView)findViewById(R.id.recycler_view_offer_detail);
        rv.setHasFixedSize(true);

        layout= new GridLayoutManager(this, 1);
        rv.setLayoutManager(layout);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_offer_detail);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProducts();
            }
        });
        getProducts();
    }

    private void getProducts(){
        sendPostRequest();
    }

    private void sendPostRequest(){
        class OfferDetailAsyncTask extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }
            @Override
            protected String doInBackground(String... params) {
                System.out.println(Config.URL_GOD+params[1]+params[0]);
                RequestHandler rh = new RequestHandler();

                // Notify that the user saw the offer
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_RESERVE_OFFER_ID, params[0]);
                hashMap.put(Config.KEY_RESERVE_PERSON_ID, idPersona);
                System.out.println(hashMap);
                rh.sendPostRequest(Config.URL_OFFER_SAW, hashMap);

                // Get offer detail
                return rh.sendGetRequest(Config.URL_GOD+params[1]+params[0]);
            }
            @Override
            protected void onPostExecute(String s){
                System.out.println(s);
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                showProducts(s);
            }
        }
        OfferDetailAsyncTask go = new OfferDetailAsyncTask();
        go.execute(idOffer, "?idOferta=");
    }

    private void showProducts(String s){
        getProductsData(s);
        adapter = new ProductAdapter(getApplicationContext(), productList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);
    }

    private void getProductsData(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonProductsOffer = jsonObject.optJSONArray(Config.TAG_GOD_PRODUCT);
            productList= new ArrayList<>();

            for (int i=0;i<jsonProductsOffer.length();i++){
                JSONObject jsonObjectItem = jsonProductsOffer.getJSONObject(i);
                ProductModel item = new ProductModel();

                item.setName(jsonObjectItem.getString(Config.TAG_GOD_NAME));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GOD_DESCRIPTION));
                item.setPrice(jsonObjectItem.getString(Config.TAG_GOD_PRICE));
                item.setImage(jsonObjectItem.getString(Config.TAG_GOD_IMAGE));
                productList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
