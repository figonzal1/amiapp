package techwork.ami.ReservationsOffers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
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
import techwork.ami.MainActivity;
import techwork.ami.ReservationsOffers.ReservationOffer;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class MyReservationsOffersActivity extends AppCompatActivity implements MyReservationOfferValidateDialog.MyReservationOfferValidateListener {

    private MyReservationOfferValidateDialog myReservationOfferValidateDialog = new MyReservationOfferValidateDialog();

    // UI references
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private List<ReservationOffer> reservationsOffersList;
    private MyReservationsOffersListAdapter adapter;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations_offers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.my_reservations_offers_list);

        // Use this setting to improve performance if you know that change in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Refreshing layout
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_my_reservations_offers_list);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getReservations();
            }
        });

        // Floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the Activity
                finish();
                // Call to FragmentHome and show available offers
                startActivity(new Intent(MyReservationsOffersActivity.this, MainActivity.class) );
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fragment manager for validate reserve dialog
        fm = getSupportFragmentManager();

        //Get and show data
        getReservations();
    }

    /*
    private void getReservations() {
        reservationsList = new ArrayList<>();
        OfferModel o = new OfferModel();
        for(int i=0; i<100; i++) {
            o.setTitle("título");
            o.setDescription("descripción");
            o.setPrice(1123);
            o.setCompany("Company");
            reservationsList.add(o);
        }
        adapter = new MyReservationsOffersListAdapter(this, reservationsList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        mRecyclerView.setAdapter(scaleAdapter);
    }*/

    // Call to DB
    private void getReservations(){
        sendPostRequest();
    }

    private void sendPostRequest(){
        // Execute operations before, during and after of data load
        class MyAsyncTask extends AsyncTask<Void,Void,String> {

            // Execute before load data (user waiting)
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            // Class that execute background task (get BD data).
            @Override
            protected String doInBackground(Void... strings) {
                HashMap<String,String> hashMap = new HashMap<>();

                // TODO: de alguna parte obtener el id de la persona (?)
                String idPersona = "3";

                hashMap.put(Config.KEY_RESERVE_PERSON_ID, idPersona);
                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Config.URL_GET_RESERVATIONS_OFFERS, hashMap);
            }

            // Do operations after load data from DB.
            // Put data in RecycleView Adapter.
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                showOffersReservations(s);
            }
        }
        MyAsyncTask go = new MyAsyncTask();
        go.execute();
    }

    private void showOffersReservations(String s){
        getData(s);

        adapter = new MyReservationsOffersListAdapter(this, reservationsOffersList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        mRecyclerView.setAdapter(scaleAdapter);

        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(View view) {
                // TODO: adecuar a reserva, abrir otra activity o algo
                //Intent intent = new Intent(ReservationView.class);
                /*Intent intent = new Intent(getApplicationContext(), MyReservationsOffersActivity.class);
                int position = mRecyclerView.getChildAdapterPosition(view);
                ReservationOffer ro = reservationsOffersList.get(position);
                intent.putExtra(Config.TAG_GO_TITLE, ro.getTitle());
                intent.putExtra(Config.TAG_GO_IMAGE, ro.getImage());
                intent.putExtra(Config.TAG_GO_DESCRIPTION, ro.getDescription());
                intent.putExtra(Config.TAG_GO_PRICE, ro.getPrice());
                intent.putExtra(Config.TAG_GO_OFFER_ID, ro.getIdReservationOffer());
                startActivity(intent);*/
                Snackbar.make(view, "Short snackbar", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"Short click",Toast.LENGTH_SHORT).show();
                showMyReservationOfferValidateDialog();
            }

            @Override
            public void onItemLongClick(View view) {
                Snackbar.make(view, "Long snackbar", Snackbar.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),"Long click",Toast.LENGTH_LONG).show();
            }
        });
    }














    private void showMyReservationOfferValidateDialog() {
        myReservationOfferValidateDialog.show(fm, getResources().getString(R.string.myReservationOfferDialog));
    }

    @Override
    public void onFinishMyReservationOfferValidateDialog(String inputText) {
        Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
    }

    private void getData(String s) {
        String sFinalDate, sReservationDate;
        Date dFinalDate, dReservationDate;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);

        try{
            JSONObject jsonObject = new JSONObject(s);

            JSONArray jsonOffers = jsonObject.optJSONArray(Config.TAG_GRO);

            Calendar c = Calendar.getInstance();
            reservationsOffersList = new ArrayList<>();

            for(int i=0;i<jsonOffers.length();i++){

                JSONObject jsonObjectItem = jsonOffers.optJSONObject(i);
                ReservationOffer item = new ReservationOffer();

                sFinalDate = jsonObjectItem.getString(Config.TAG_GRO_DATEFIN);
                sReservationDate = jsonObjectItem.getString(Config.TAG_GRO_RESERDATE);
                dFinalDate = format.parse(sFinalDate);
                dReservationDate = format.parse(sReservationDate);

                item.setIdReservationOffer(jsonObjectItem.getString(Config.TAG_GRO_ID_OFFER));

                item.setTitle(jsonObjectItem.getString(Config.TAG_GRO_TITLE));
                c.setTime(dFinalDate);
                item.setFinalDate(String.format(Locale.US, Config.DATE_FORMAT,
                        c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GRO_PRICE));
                item.setCompany(jsonObjectItem.getString(Config.TAG_GRO_COMPANY));
                c.setTime(dReservationDate);
                item.setReservationDate(String.format(Locale.US, Config.DATE_FORMAT,
                        c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR)));
                item.setQuantity(jsonObjectItem.getString(Config.TAG_GRO_QUANTITY));
                item.setImage(jsonObjectItem.getString(Config.TAG_GRO_IMAGE));

                reservationsOffersList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
