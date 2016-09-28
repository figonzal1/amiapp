package techwork.ami.Offer;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import techwork.ami.Dialogs.CustomAlertDialogBuilder;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class FilterOfferActivity extends AppCompatActivity {

    // UI references
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private List<OfferModel> offersList;
    private OfferAdapter adapter;
    private int page;
    private String idCategory;
    private String idStore;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_offer);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idCategory = "";
        idStore = "";

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("idCategory")) {
                idCategory = extras.getString("idCategory", "");
                setTitle("Ofertas de " + extras.getString("categoryName", ""));
            }
            if (extras.containsKey("idStore")) {
                idStore = extras.getString("idStore", "");
                setTitle("Ofertas de " + extras.getString("companyName", ""));
            }
        }

        // Recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.filter_offers_list);

        // Use this setting to improve performance if you know that change in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
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

        GetFilterOffers gfo = new GetFilterOffers();
        gfo.execute(options);
    }

    private void showFilterOffers(String json) {
        getData(json);

        adapter = new OfferAdapter(this, offersList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        mRecyclerView.setAdapter(scaleAdapter);

        // Set behavior to click in some item (offer validate)
        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(final View view) {
                Intent intent = new Intent(FilterOfferActivity.this, OfferView.class);
                int position = mRecyclerView.getChildAdapterPosition(view);
                OfferModel o = offersList.get(position);
                intent.putExtra(Config.TAG_GO_TITLE, o.getTitle());
                intent.putExtra(Config.TAG_GO_IMAGE, o.getImage());
                intent.putExtra(Config.TAG_GO_DESCRIPTION, o.getDescription());
                intent.putExtra(Config.TAG_GO_PRICE, o.getPrice());
                intent.putExtra(Config.TAG_GO_OFFER_ID, o.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(final View view) {
                new CustomAlertDialogBuilder(context)
                        .setTitle(R.string.offers_list_discard_question)
                        .setMessage("Confirme la acción")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                discardOffer(dialog, offersList.get(mRecyclerView.getChildAdapterPosition(view)));
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
        String dIni,dFin;
        Date dateIni,dateFin;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try{
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonOffers = jsonObject.optJSONArray(Config.TAG_GO_OFFERS);
            Calendar c = Calendar.getInstance();
            offersList = new ArrayList<>();

            for(int i=0;i<jsonOffers.length();i++){

                JSONObject jsonObjectItem = jsonOffers.optJSONObject(i);
                OfferModel item = new OfferModel();

                dIni =jsonObjectItem.getString(Config.TAG_GO_DATEINI);
                dFin = jsonObjectItem.getString(Config.TAG_GO_DATEFIN);
                dateIni= format.parse(dIni);
                dateFin =format.parse(dFin);

                item.setId(jsonObjectItem.getString(Config.TAG_GO_OFFER_ID));
                item.setTitle(jsonObjectItem.getString(Config.TAG_GO_TITLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GO_DESCRIPTION));
                item.setStock(jsonObjectItem.getInt(Config.TAG_GO_STOCK));
                item.setPromotionCode(Config.TAG_GO_PROMCOD);
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GO_PRICE));

                c.setTime(dateIni);
                item.setInitialDate(String.format(Locale.US, Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                c.setTime(dateFin);
                item.setFinalDate(String.format(Locale.US, Config.DATE_FORMAT, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR)));

                item.setMaxPPerson(jsonObjectItem.getInt(Config.TAG_GO_MAXXPER));
                item.setCompany(jsonObjectItem.getString(Config.TAG_GO_COMPANY));
                item.setImage(jsonObjectItem.getString(Config.TAG_GO_IMAGE));

                offersList.add(item);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void discardOffer(DialogInterface dialog, OfferModel offer) {
        class DiscardOffer extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            DialogInterface dialog;

            DiscardOffer (DialogInterface dialog) {
                this.dialog = dialog;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,
                        getString(R.string.offers_list_discard_processing),
                        getString(R.string.wait), false, false);
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_DO_PERSON_ID,
                        getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE)
                                .getString(Config.KEY_SP_ID, "-1"));
                hashMap.put(Config.KEY_DO_OFFER_ID, params[0]);
                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Config.URL_DO_DISCARD, hashMap);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equals("0")) {
                    Toast.makeText(getApplicationContext(),
                            R.string.my_reservations_offers_rate_ok, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.operation_fail, Toast.LENGTH_LONG).show();
                }
                this.dialog.dismiss();
            }
        }
        new DiscardOffer(dialog).execute(offer.getId());
    }

}
