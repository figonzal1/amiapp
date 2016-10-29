package techwork.ami.Need.NeedReservations;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import techwork.ami.Need.ListOfferCompanies.NeedOfferModel;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class NeedReservationsActivity extends AppCompatActivity {

    private List<NeedReservationsModel> needReservationsList;
    private RecyclerView rv;
    private LinearLayoutManager layout;
    private NeedReservationsAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvNeedReservationEmpty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_reservations_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvNeedReservationEmpty = (TextView)findViewById(R.id.tv_need_reservations_empty);

        rv = (RecyclerView) findViewById(R.id.recycler_view_need_reservations);
        rv.setHasFixedSize(true);

        layout = new LinearLayoutManager(this);
        rv.setLayoutManager(layout);

        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_need_reservations);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNeedReservs();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getNeedReservs();

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

    private void getNeedReservs() {
        sendPostRequest();
    }

    private void sendPostRequest() {

        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        final String idperson = sharedPref.getString(Config.KEY_SP_ID, "-1");

        class NeedReservationsAsyncTask extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashmap = new HashMap<>();

                hashmap.put(Config.KEY_GNR_IDPERSON,idperson);
                RequestHandler rh = new RequestHandler();

                return rh.sendPostRequest(Config.URL_GET_NEED_RESERVATIONS,hashmap);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                showNeedReservations(s);
            }
        }
        NeedReservationsAsyncTask go = new NeedReservationsAsyncTask();
        go.execute();
    }

    private void showNeedReservations(String s) {
        getNeedReservationsData(s);

        adapter = new NeedReservationsAdapter(getApplicationContext(), needReservationsList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);

        if (needReservationsList.size()==0){
            tvNeedReservationEmpty.setText("¡Oops! \n No tienes pedidos reservados :(");
        }else {
            tvNeedReservationEmpty.setText("");
        }


        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(View view) {
                Toast.makeText(getApplicationContext(), "Normal click", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemLongClick(View view) {

            }
        });
    }

    private void getNeedReservationsData(String json) {
        String dIni, dFin,dReserv,dCashed;
        Date dateIni, dateFin,dateReserv,dateCashed;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonNeedReservs = jsonObject.optJSONArray(Config.TAG_GNR_NEED);
            Calendar c = Calendar.getInstance();
            needReservationsList = new ArrayList<>();

            for (int i = 0; i < jsonNeedReservs.length(); i++) {
                JSONObject jsonObjectItem = jsonNeedReservs.getJSONObject(i);
                NeedReservationsModel item = new NeedReservationsModel();

                item.setIdOffer(jsonObjectItem.getString(Config.TAG_GNR_IDOFFER));
                item.setIdNeed(jsonObjectItem.getString(Config.TAG_GNR_IDNEED));
                item.setIdLocal(jsonObjectItem.getString(Config.TAG_GNR_IDLOCAL));
                item.setTittle(jsonObjectItem.getString(Config.TAG_GNR_TITTLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GNR_DESCRIPTION));
                item.setCodPromotion(jsonObjectItem.getString(Config.TAG_GNR_CODPROMOTION));
                item.setStock(jsonObjectItem.getString(Config.TAG_GNR_STOCK));
                item.setQuantity(jsonObjectItem.getString(Config.TAG_GNR_QUANTITY));
                item.setCashed(jsonObjectItem.getString(Config.TAG_GNR_CASHED));
                item.setCalification(jsonObjectItem.getString(Config.TAG_GNR_CALIFICATION));
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GNR_PRICEOFFER));


                dIni = jsonObjectItem.getString(Config.TAG_GNR_DATEINI);
                dFin = jsonObjectItem.getString(Config.TAG_GNR_DATEFIN);
                dReserv= jsonObjectItem.getString(Config.TAG_GNR_DATERESERV);
                //dCashed= jsonObjectItem.getString(Config.TAG_GNR_DATECASHED);
                dateIni= format.parse(dIni);
                dateFin = format.parse(dFin);
                dateReserv=format.parse(dReserv);
                //dateCashed=format.parse(dCashed);

                c.setTime(dateIni);
                item.setDateIni(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));
                c.setTime(dateFin);
                item.setDateFin(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));
                c.setTime(dateReserv);
                item.setDateReserv(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));
                //c.setTime(dateCashed);
                //item.setDateCashed(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                needReservationsList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
