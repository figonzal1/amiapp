package techwork.ami.Promotion;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import techwork.ami.Config;
import techwork.ami.Dialogs.CustomAlertDialogBuilder;
import techwork.ami.Promotion.PromotionDetail.PromotionDetailActivity;
import techwork.ami.Promotion.PromotionsList.FragmentHome;
import techwork.ami.Promotion.PromotionsList.PromotionAdapter;
import techwork.ami.Promotion.PromotionsList.PromotionModel;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class FilterActivity extends AppCompatActivity {

    // UI references
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<PromotionModel> offersList;
    private int page;
    private String idCategory;
    private String idStore;
    private TextView tvFilterOffersEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);

        tvFilterOffersEmpty = (TextView)findViewById(R.id.tv_filter_offers_empty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        idCategory = "";
        idStore = "";

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("idCategory")) {
                idCategory = extras.getString("idCategory", "");
                setTitle("Promociones de " + extras.getString("categoryName", ""));
            }
            if (extras.containsKey("idStore")) {
                idStore = extras.getString("idStore", "");
                setTitle("Promociones de " + extras.getString("companyName", ""));
            }
        }

        // Recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.filter_offers_list);

        // Use this setting to improve performance if you know that change in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Refreshing layout
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_filter_offers_list);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFilterOffers();
            }
        });

        // Set the initial page
        page = 1;

        //Get and show data
        getFilterOffers();
    }

    // Call to DB
    private void getFilterOffers(){
        sendPostRequest();
    }

    private void sendPostRequest(){
        // Execute operations before, during and after of data load
        class GetFilterOffers extends AsyncTask<String,Void,String> {

            // Execute before load data (user waiting)
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }

            // Class that execute background task (get BD data).
            @Override
            protected String doInBackground(String... params) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(mRecyclerView, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendPostRequest();
                    }
                });

                if (connectionStatus) {
                    return rh.sendGetRequestParam(Config.URL_FILTER_OFFERS, params[0]);
                }
                else
                    return "-1";
            }

            // Do operations after load data from DB.
            // Put data in RecycleView Adapter.
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                if (!s.equals("-1"))
                    showFilterOffers(s);
            }
        }

        String options = "page=" + page;
        if (!idCategory.equals(""))
            options = options + "&" + Config.TAG_FO_ID_CATEGORY + "=" + idCategory;
        if (!idStore.equals(""))
            options = options + "&" + Config.TAG_FO_ID_STORE + "=" + idStore;

        String idPerson = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE)
                .getString(Config.KEY_SP_ID, "-1");

        options = options + "&idPersona=" +idPerson;

        GetFilterOffers gfo = new GetFilterOffers();
        gfo.execute(options);
    }

    private void showFilterOffers(String json) {
        getData(json);

        PromotionAdapter adapter = new PromotionAdapter(this, offersList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        mRecyclerView.setAdapter(scaleAdapter);

        if (offersList.size()==0){
            tvFilterOffersEmpty.setText("¡Oops! \n No encontramos ninguna promoción :( \n Inténtalo nuevamente más tarde...");
        }else {
            tvFilterOffersEmpty.setText("");
        }

        // Set behavior to click in some item (offer validate)
        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(final View view) {
                Intent intent = new Intent(FilterActivity.this, PromotionDetailActivity.class);
                int position = mRecyclerView.getChildAdapterPosition(view);
                PromotionModel o = offersList.get(position);
                intent.putExtra(Config.TAG_GO_TITLE, o.getTitle());
                intent.putExtra(Config.TAG_GO_IMAGE, o.getImage());
                intent.putExtra(Config.TAG_GO_DESCRIPTION, o.getDescription());
                intent.putExtra(Config.TAG_GO_COMPANY, o.getCompany());
                intent.putExtra(Config.TAG_GO_PRICE, o.getPrice());
                intent.putExtra(Config.TAG_GO_OFFER_ID, o.getId());
                intent.putExtra(Config.TAG_GO_MAXXPER, o.getMaxPPerson());
                intent.putExtra(Config.TAG_GO_STOCK, o.getStock());
                intent.putExtra(Config.TAG_GO_DATEFIN, o.getFinalDate());
                intent.putExtra(Config.TAG_GO_TOTALPRICE, o.getTotalPrice());
                intent.putExtra(Config.TAG_GO_DATETIMEFIN,o.getFinalDateTime());
                intent.putExtra(Config.TAG_GO_IDLOCAL,o.getIdLocal());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(final View view) {
                new CustomAlertDialogBuilder(FilterActivity.this)
                        .setTitle(R.string.offers_list_discard_question)
                        .setMessage("Confirme la acción")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                discardOffer(dialog, offersList.get(mRecyclerView.getChildAdapterPosition(view)));
                                getFilterOffers();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    private void getData(String json) {
        String dIni,dFin,dTimeFin;
        Date dateIni,dateFin,dateTimeFin;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try{
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonOffers = jsonObject.optJSONArray(Config.TAG_GO_OFFERS);
            Calendar c = Calendar.getInstance();
            offersList = new ArrayList<>();

            for(int i=0;i<jsonOffers.length();i++){

                JSONObject jsonObjectItem = jsonOffers.optJSONObject(i);
                PromotionModel item = new PromotionModel();

                dIni =jsonObjectItem.getString(Config.TAG_GO_DATEINI);
                dFin = jsonObjectItem.getString(Config.TAG_GO_DATEFIN);
                dTimeFin = jsonObjectItem.getString(Config.TAG_GO_DATETIMEFIN);
                dateIni= format.parse(dIni);
                dateFin =format.parse(dFin);
                dateTimeFin=format.parse(dTimeFin);

                item.setId(jsonObjectItem.getString(Config.TAG_GO_OFFER_ID));
                item.setTitle(jsonObjectItem.getString(Config.TAG_GO_TITLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GO_DESCRIPTION));
                item.setStock(jsonObjectItem.getInt(Config.TAG_GO_STOCK));
                item.setPromotionCode(Config.TAG_GO_PROMCOD);
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GO_PRICE));
                item.setIdLocal(jsonObjectItem.getString(Config.TAG_GO_IDLOCAL));
                //TODO: en teoría se debería poder borrar, puesto que el precio siempre exisitrá (tendrán al menos un producto asociado)
                try {
                    item.setTotal(jsonObjectItem.getInt(Config.TAG_GO_TOTALPRICE));
                }catch (Exception e){
                    item.setTotal(0);
                }

                c.setTime(dateIni);
                item.setInitialDate(String.format(Locale.US, Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                c.setTime(dateFin);
                item.setFinalDate(String.format(Locale.US, Config.DATE_FORMAT, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR)));

                c.setTime(dateTimeFin);
                item.setFinalDateTime(String.format(Locale.US,Config.DATETIME_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR),c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));


                item.setMaxPPerson(jsonObjectItem.getInt(Config.TAG_GO_MAXXPER));
                item.setCompany(jsonObjectItem.getString(Config.TAG_GO_COMPANY));
                item.setImage(jsonObjectItem.getString(Config.TAG_GO_IMAGE));

                offersList.add(item);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void discardOffer(DialogInterface dialog, PromotionModel offer) {
        String idPerson = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE)
                .getString(Config.KEY_SP_ID, "-1");
        String idOffer = offer.getId();
        new FragmentHome.DiscardOffer(getApplicationContext(), dialog).execute(idPerson, idOffer);
    }

}
