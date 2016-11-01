package techwork.ami.Need.NeedReservations.NeedReservationsDetails;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import techwork.ami.Config;
import techwork.ami.Need.NeedOfferDetails.ProductAdapter;
import techwork.ami.Need.NeedOfferDetails.ProductModel;
import techwork.ami.Need.NeedOfferLocalDetails.NeedOfferViewLocalActivity;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class NeedReservationsDetailsActivity extends AppCompatActivity {

    TextView tvCompany,tvTittle,tvDescription,tvDateIni,tvDateFin,tvPrice;
    Button btnLocal;
    String idOffer,idLocal;
    private RecyclerView rv;
    private LinearLayoutManager layout;
    private List<ProductModel> productList;
    private ProductAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_reservations_details_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();

        //Capture id's .
        idOffer = bundle.getString(Config.TAG_GNR_IDOFFER);
        idLocal = bundle.getString(Config.TAG_GNR_IDLOCAL);


        tvCompany = (TextView)findViewById(R.id.tv_need_reservations_view_company);
        tvTittle=(TextView)findViewById(R.id.tv_need_reservations_view_tittle);
        tvDescription=(TextView)findViewById(R.id.tv_need_reservations_view_description);
        tvDateFin=(TextView)findViewById(R.id.tv_need_reservations_view_date_fin);
        tvDateIni=(TextView)findViewById(R.id.tv_need_reservations_view_date_ini);
        tvPrice=(TextView)findViewById(R.id.tv_need_reservations_view_price);
        btnLocal=(Button)findViewById(R.id.btn_need_reservations_view_local_details);

        tvCompany.setText(bundle.getString(Config.TAG_GNR_COMPANY));
        tvTittle.setText(bundle.getString(Config.TAG_GNR_TITTLE));
        tvDescription.setText(bundle.getString(Config.TAG_GNR_DESCRIPTION));
        tvDateIni.setText("Fecha de publicación: "+bundle.getString(Config.TAG_GNR_DATEINI));
        tvDateFin.setText("Fecha de expiración: "+bundle.getString(Config.TAG_GNR_DATEFIN));
        tvPrice.setText("$"+String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GNR_PRICEOFFER)));

        rv = (RecyclerView)findViewById(R.id.recycler_view_need_reservations_view) ;
        rv.setHasFixedSize(true);
        layout = new LinearLayoutManager(this);
        rv.setLayoutManager(layout);

        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NeedReservationsDetailsActivity.this, NeedReservationsLocalDetails.class);
                intent.putExtra(Config.TAG_GNR_IDLOCAL,idLocal);
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

    private void showProducts(String s) {
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
