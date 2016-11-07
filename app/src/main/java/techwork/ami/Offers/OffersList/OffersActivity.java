package techwork.ami.Offers.OffersList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import techwork.ami.Offers.OffersDetails.OfferViewActivity;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class OffersActivity extends AppCompatActivity {


    private RecyclerView rv;
    private List<OffersModel> offerList;
    private OffersAdapter adapter;
    private GridLayoutManager layout;
    private SwipeRefreshLayout refreshLayout;
    private String idNeedOffer;
    public static Activity activity;
    TextView tvOfferEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers_activity);
        activity=this;

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        idNeedOffer = bundle.getString(Config.TAG_GET_OFFER_IDNEED);

        tvOfferEmpty = (TextView)findViewById(R.id.tv_offer_company_empty);

        rv = (RecyclerView)findViewById(R.id.recycler_view_offer);
        rv.setHasFixedSize(true);

        layout= new GridLayoutManager(this,1);
        rv.setLayoutManager(layout);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_offer);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOffers();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getOffers();

    }

    //Permit go to back activity
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

    private void getOffers(){
        sendPostRequest();
    }

    private void sendPostRequest(){

        class OfferAsyncTask extends AsyncTask<Void,Void,String>{

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }
            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashmap = new HashMap<>();
                hashmap.put(Config.KEY_GET_OFFER_IDNEED,idNeedOffer);
                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Config.URL_GET_OFFER,hashmap);
            }
            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                showOffers(s);
            }
        }
        OfferAsyncTask go = new OfferAsyncTask();
        go.execute();
    }

    //Show info in model in a recycler view
    private void showOffers(String s){
        getOffersData(s);

        adapter = new OffersAdapter(getApplicationContext(),offerList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);

        if (offerList.size()==0){
            tvOfferEmpty.setText(R.string.OfferEmpty);
        }else {
            tvOfferEmpty.setText("");
        }

        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OfferViewActivity.class);
                int position = rv.getChildAdapterPosition(view);
                OffersModel m = offerList.get(position);

                Calendar c = Calendar.getInstance();
                SimpleDateFormat format2 = new SimpleDateFormat(Config.DATETIME_FORMAT_ANDROID);

                try {
                    //If offer date is end you not open the offer.
                    if (format2.parse(m.getDateTimeFin()).after(c.getTime())) {

                        //Send to NeedOfferDetails the details of each offer.
                        intent.putExtra(Config.TAG_GET_OFFER_IDOFFER, m.getIdOferta());
                        intent.putExtra(Config.TAG_GET_OFFER_IDLOCAL, m.getIdLocal());
                        intent.putExtra(Config.TAG_GET_OFFER_TITTLE, m.getTittle());
                        intent.putExtra(Config.TAG_GET_OFFER_DESCRIPTION, m.getDescription());
                        intent.putExtra(Config.TAG_GET_OFFER_PRICEOFFER, m.getPrice());
                        intent.putExtra(Config.TAG_GET_OFFER_DESCRIPTION, m.getDescription());
                        intent.putExtra(Config.TAG_GET_OFFER_STOCK, m.getStock());
                        intent.putExtra(Config.TAG_GET_OFFER_MAXPPERSON, m.getMaxPPerson());
                        intent.putExtra(Config.TAG_GET_OFFER_CODPROMOTION, m.getCodPromotion());
                        intent.putExtra(Config.TAG_GET_OFFER_DATEFIN, m.getDateFin());
                        intent.putExtra(Config.TAG_GET_OFFER_DATEINI, m.getDateIni());
                        intent.putExtra(Config.TAG_GET_OFFER_COMPANY, m.getCompany());
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),R.string.OfferExpired,Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onItemLongClick(View view) {
                //Nada
            }
        });


    }

    //Get information of each offer and put in model of offers
    private void getOffersData(String json){
        String dIni,dFin,dTimeFin;
        Date dateIni,dateFin,dateTimeFin;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonNeedOffer = jsonObject.optJSONArray(Config.TAG_GET_OFFER_NEED);
            Calendar c = Calendar.getInstance();
            offerList = new ArrayList<>();

            for (int i=0;i<jsonNeedOffer.length();i++){
                JSONObject jsonObjectItem = jsonNeedOffer.getJSONObject(i);
                OffersModel item = new OffersModel();

                item.setIdOferta(jsonObjectItem.getString(Config.TAG_GET_OFFER_IDOFFER));
                item.setIdNeed(jsonObjectItem.getString(Config.TAG_GET_OFFER_IDNEED));
                item.setIdLocal(jsonObjectItem.getString(Config.TAG_GET_OFFER_IDLOCAL));
                item.setTittle(jsonObjectItem.getString(Config.TAG_GET_OFFER_TITTLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GET_OFFER_DESCRIPTION));
                item.setCodPromotion(jsonObjectItem.getString(Config.TAG_GET_OFFER_CODPROMOTION));
                item.setStock(jsonObjectItem.getString(Config.TAG_GET_OFFER_STOCK));
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GET_OFFER_PRICEOFFER));
                item.setMaxPPerson(jsonObjectItem.getString(Config.TAG_GET_OFFER_MAXPPERSON));
                item.setCompany(jsonObjectItem.getString(Config.TAG_GET_OFFER_COMPANY));
                item.setImage(jsonObjectItem.getString(Config.TAG_GET_OFFER_IMAGE));

                dIni = jsonObjectItem.getString(Config.TAG_GET_OFFER_DATEINI);
                dFin = jsonObjectItem.getString(Config.TAG_GET_OFFER_DATEFIN);
                dTimeFin= jsonObjectItem.getString(Config.TAG_GET_OFFER_DATETIME_FIN);
                dateIni= format.parse(dIni);
                dateFin = format.parse(dFin);
                dateTimeFin=format.parse(dTimeFin);

                c.setTime(dateIni);
                item.setDateIni(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                c.setTime(dateFin);
                item.setDateFin(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                c.setTime(dateTimeFin);
                item.setDateTimeFin(String.format(Locale.US,Config.DATETIME_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR),c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));

                offerList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
