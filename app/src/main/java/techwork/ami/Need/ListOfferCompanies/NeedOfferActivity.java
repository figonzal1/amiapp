package techwork.ami.Need.ListOfferCompanies;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import techwork.ami.Need.NeedOfferDetails.NeedOfferViewActivity;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class NeedOfferActivity extends AppCompatActivity {


    private RecyclerView rv;
    private List<NeedOfferModel> needOfferList;
    private NeedOfferAdapter adapter;
    private GridLayoutManager layout;
    private SwipeRefreshLayout refreshLayout;
    private String idNeedOffer;
    public static Activity activity;
    TextView tvNeedOfferEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_offer_activity);
        activity=this;

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        idNeedOffer = bundle.getString(Config.TAG_GNO_IDNEED);

        tvNeedOfferEmpty = (TextView)findViewById(R.id.tv_need_company_empty);

        rv = (RecyclerView)findViewById(R.id.recycler_view_need_offer);
        rv.setHasFixedSize(true);

        layout= new GridLayoutManager(this,1);
        rv.setLayoutManager(layout);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_need_offer);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNeedOffers();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getNeedOffers();

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

    private void getNeedOffers(){
        sendPostRequest();
    }

    private void sendPostRequest(){

        class NeedOfferAsyncTask extends AsyncTask<Void,Void,String>{

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }
            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashmap = new HashMap<>();
                hashmap.put(Config.KEY_GNO_IDNEED,idNeedOffer);
                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Config.URL_GET_NEED_OFFER,hashmap);
            }
            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                showNeedOffers(s);
            }
        }
        NeedOfferAsyncTask go = new NeedOfferAsyncTask();
        go.execute();
    }

    private void showNeedOffers(String s){
        getNeedOffersData(s);

        adapter = new NeedOfferAdapter(getApplicationContext(),needOfferList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);

        if (needOfferList.size()==0){
            tvNeedOfferEmpty.setText("¡Oops! \n No tienes ofertas recientes :(");
        }else {
            tvNeedOfferEmpty.setText("");
        }

        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NeedOfferViewActivity.class);
                int position = rv.getChildAdapterPosition(view);
                NeedOfferModel m = needOfferList.get(position);

                Calendar c = Calendar.getInstance();
                SimpleDateFormat format2 = new SimpleDateFormat(Config.DATETIME_FORMAT_ANDROID);

                try {
                    //If needoffer date is end you not open the offer.
                    if (format2.parse(m.getDateTimeFin()).after(c.getTime())) {

                        //Send to NeedOfferDetails the details of each offer.
                        intent.putExtra(Config.TAG_GNO_IDOFFER, m.getIdOferta());
                        intent.putExtra(Config.TAG_GNO_IDLOCAL, m.getIdLocal());
                        intent.putExtra(Config.TAG_GNO_TITTLE, m.getTittle());
                        intent.putExtra(Config.TAG_GNO_DESCRIPTION, m.getDescription());
                        intent.putExtra(Config.TAG_GNO_PRICEOFFER, m.getPrice());
                        intent.putExtra(Config.TAG_GNO_DESCRIPTION, m.getDescription());
                        intent.putExtra(Config.TAG_GNO_STOCK, m.getStock());
                        intent.putExtra(Config.TAG_GNO_MAXPPERSON, m.getMaxPPerson());
                        intent.putExtra(Config.TAG_GNO_CODPROMOTION, m.getCodPromotion());
                        intent.putExtra(Config.TAG_GNO_DATEFIN, m.getDateFin());
                        intent.putExtra(Config.TAG_GNO_DATEINI, m.getDateIni());
                        intent.putExtra(Config.TAG_GNO_COMPANY, m.getCompany());
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Oferta expirada",Toast.LENGTH_LONG).show();
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


    private void getNeedOffersData(String json){
        String dIni,dFin,dTimeFin;
        Date dateIni,dateFin,dateTimeFin;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonNeedOffer = jsonObject.optJSONArray(Config.TAG_GNO_NEED);
            Calendar c = Calendar.getInstance();
            needOfferList = new ArrayList<>();

            for (int i=0;i<jsonNeedOffer.length();i++){
                JSONObject jsonObjectItem = jsonNeedOffer.getJSONObject(i);
                NeedOfferModel item = new NeedOfferModel();

                item.setIdOferta(jsonObjectItem.getString(Config.TAG_GNO_IDOFFER));
                item.setIdNeed(jsonObjectItem.getString(Config.TAG_GNO_IDNEED));
                item.setIdLocal(jsonObjectItem.getString(Config.TAG_GNO_IDLOCAL));
                item.setTittle(jsonObjectItem.getString(Config.TAG_GNO_TITTLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GNO_DESCRIPTION));
                item.setCodPromotion(jsonObjectItem.getString(Config.TAG_GNO_CODPROMOTION));
                item.setStock(jsonObjectItem.getString(Config.TAG_GNO_STOCK));
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GNO_PRICEOFFER));
                item.setMaxPPerson(jsonObjectItem.getString(Config.TAG_GNO_MAXPPERSON));
                item.setCompany(jsonObjectItem.getString(Config.TAG_GNO_COMPANY));
                item.setImage(jsonObjectItem.getString(Config.TAG_GNO_IMAGE));

                dIni = jsonObjectItem.getString(Config.TAG_GNO_DATEINI);
                dFin = jsonObjectItem.getString(Config.TAG_GNO_DATEFIN);
                dTimeFin= jsonObjectItem.getString(Config.TAG_GNO_DATETIME_FIN);
                dateIni= format.parse(dIni);
                dateFin = format.parse(dFin);
                dateTimeFin=format.parse(dTimeFin);

                c.setTime(dateIni);
                item.setDateIni(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                c.setTime(dateFin);
                item.setDateFin(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                c.setTime(dateTimeFin);
                item.setDateTimeFin(String.format(Locale.US,Config.DATETIME_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR),c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));

                needOfferList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
