package techwork.ami.ReservationsOffers;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
import techwork.ami.MainActivity;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class MyReservationsOffersActivity extends AppCompatActivity {

    // Two recycle views in one layout by https://goo.gl/Iy5prs (natrio)

    // UI references
    private RecyclerView mRecyclerViewReserved;
    private RecyclerView mRecyclerViewCharged;
    private RecyclerView.LayoutManager mLayoutManagerResrved, mLayoutManagerCharged;
    private SwipeRefreshLayout refreshLayout;
    private List<ReservationOffer> reservationsOffersListReserved;
    private List<ReservationOffer> reservationOffersListCharged;
    private MyReservationsOffersListAdapter adapterReserved;
    private MyReservationsOffersListAdapter adapterCharged;
    Context context;
    CustomAlertDialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_my_reservations_offers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Recycler view
        mRecyclerViewReserved = (RecyclerView) findViewById(R.id.my_reservations_offers_list_reserved);
        mRecyclerViewCharged = (RecyclerView) findViewById(R.id.my_reservations_offers_list_charged);

        // Use this setting to improve performance if you know that change in content do not change the layout size of the RecyclerView
        mRecyclerViewReserved.setHasFixedSize(true);
        mRecyclerViewCharged.setHasFixedSize(true);

        // Use a linear layout manager
        mLayoutManagerResrved = new LinearLayoutManager(this);
        mLayoutManagerCharged = new LinearLayoutManager(this);

        mRecyclerViewReserved.setLayoutManager(mLayoutManagerResrved);
        mRecyclerViewCharged.setLayoutManager(mLayoutManagerCharged);

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

        //Get and show data
        getReservations();
    }

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
                refreshLayout.setRefreshing(true);
            }

            // Class that execute background task (get BD data).
            @Override
            protected String doInBackground(Void... strings) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(mRecyclerViewReserved, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendPostRequest();
                    }
                });

                if (connectionStatus) {
                    HashMap<String,String> hashMap = new HashMap<>();

                // Found person id from shared preferences
                SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
                String idPersona = sharedPref.getString(Config.KEY_SP_ID, "-1");

                    hashMap.put(Config.KEY_RESERVE_PERSON_ID, idPersona);
                    return rh.sendPostRequest(Config.URL_GET_RESERVATIONS_OFFERS, hashMap);
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
                    showOffersReservations(s);
            }
        }
        MyAsyncTask go = new MyAsyncTask();
        go.execute();
    }

    private void showOffersReservations(String s){
        getData(s);

        System.out.println("Tamaño list reserved = " + reservationsOffersListReserved.size());
        System.out.println("Tamaño list charged = " + reservationOffersListCharged.size());

        adapterReserved = new MyReservationsOffersListAdapter(this, reservationsOffersListReserved);
        adapterCharged = new MyReservationsOffersListAdapter(this, reservationOffersListCharged);

        ScaleInAnimationAdapter scaleAdapterReserved = new ScaleInAnimationAdapter(adapterReserved);
        ScaleInAnimationAdapter scaleAdapterCharged = new ScaleInAnimationAdapter(adapterCharged);

        mRecyclerViewReserved.setAdapter(scaleAdapterReserved);
        mRecyclerViewCharged.setAdapter(scaleAdapterCharged);

        // Set behavior to click in some item (offer validate)
        adapterReserved.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(final View view) {
                final ReservationOffer ro = reservationsOffersListReserved.get(mRecyclerViewReserved.getChildAdapterPosition(view));
                // If the offer was already charged
                if(!ro.getPaymentDate().equals("")){
                    Toast.makeText(context,R.string.my_reservations_offers_already,Toast.LENGTH_SHORT).show();
                    Snackbar.make(mRecyclerViewReserved, R.string.my_reservations_offers_already, Snackbar.LENGTH_SHORT).show();
                }
                else {
                    dialogLocalCode(ro);
                }
            }

            @Override
            public void onItemLongClick(View view) {
                final ReservationOffer ro = reservationsOffersListReserved.get(mRecyclerViewReserved.getChildAdapterPosition(view));
                // Validate before rate
                if (ro.getPaymentDate().equals("")){
                    Toast.makeText(getApplicationContext(), R.string.my_reservations_offers_unvalidated, Toast.LENGTH_SHORT).show();
                }
                // If has already validated but not rated
                else if (!ro.getCalificacion().equals("")){
                    Toast.makeText(getApplicationContext(), R.string.my_reservations_offers_already_commented, Toast.LENGTH_SHORT).show();
                }
                else {
                    rateOffer(ro, false);
                }
            }
        });

        adapterCharged.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(final View view) {
                final ReservationOffer ro = reservationOffersListCharged.get(mRecyclerViewCharged.getChildAdapterPosition(view));
                // If the offer was already charged
                if(!ro.getPaymentDate().equals("")){
                    Toast.makeText(context,R.string.my_reservations_offers_already,Toast.LENGTH_SHORT).show();
                    Snackbar.make(mRecyclerViewCharged, R.string.my_reservations_offers_already, Snackbar.LENGTH_SHORT).show();
                }
                else {
                    dialogLocalCode(ro);
                }
            }

            @Override
            public void onItemLongClick(View view) {
                final ReservationOffer ro = reservationOffersListCharged .get(mRecyclerViewCharged.getChildAdapterPosition(view));
                // Validate before rate
                if (ro.getPaymentDate().equals("")){
                    Toast.makeText(getApplicationContext(), R.string.my_reservations_offers_unvalidated, Toast.LENGTH_SHORT).show();
                }
                // If has already validated but not rated
                else if (!ro.getCalificacion().equals("")){
                    Toast.makeText(getApplicationContext(), R.string.my_reservations_offers_already_commented, Toast.LENGTH_SHORT).show();
                }
                else {
                    rateOffer(ro, false);
                }
            }
        });

    }

    private void dialogLocalCode(final ReservationOffer ro) {
        // Create the CustomAlertDialogBuilder
        dialogBuilder = new CustomAlertDialogBuilder(context);

        // Set the usual data, as you would do with AlertDialog.Builder
        dialogBuilder.setTitle(R.string.my_reservations_offers_validate_title);
        dialogBuilder.setMessage(getString(R.string.my_reservations_offers_validate_message).replace("%s", ro.getCompany()));

        // Create a EditText
        final EditText edittext = new EditText(context);
        // Type no visible password
        edittext.setInputType(Config.inputPromotionCodeType);
        dialogBuilder.setView(edittext);

        // Set your buttons OnClickListeners
        dialogBuilder.setPositiveButton(R.string.my_reservations_offers_validate_positiveText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Dialog will not dismiss when the button is clicked
                        // Call dialog.dismiss() to actually dismiss it.
                        // If promotion code from edittext is equals to the object promotion code
                        if (!ro.getLocalCode().equals(edittext.getText().toString())) {
                            edittext.setError(getString(R.string.my_reservations_offers_validate_errorPromotionCode));
                        }
                        // Else
                        else {
                            // First validate for the local operator
                            dialog.dismiss();
                            dialogPromotionCode(ro);
                        }
                    }
                });

        // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
        dialogBuilder.setNegativeButton(R.string.my_reservations_offers_validate_negativeText, null);

        // (optional) set whether to dismiss dialog when touching outside
        dialogBuilder.setCanceledOnTouchOutside(false);

        // Show the dialog
        dialogBuilder.show();
    }

    private void dialogPromotionCode(final ReservationOffer ro) {
        // Create the CustomAlertDialogBuilder
        dialogBuilder = new CustomAlertDialogBuilder(context);

        // Set the usual data, as you would do with AlertDialog.Builder
        dialogBuilder.setTitle(R.string.my_reservations_offers_validate_local);
        dialogBuilder.setMessage(ro.getPromotionCode());

        // Set your buttons OnClickListeners
        dialogBuilder.setPositiveButton(R.string.my_reservations_offers_validate_positiveText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Write in the DB that offer has been hired
                        class ValidateReservationOffer extends AsyncTask<String, Void, String> {
                            ProgressDialog loading;
                            DialogInterface dialog;

                            ValidateReservationOffer(DialogInterface dialog) {
                                this.dialog = dialog;
                            }

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                loading = ProgressDialog.show(MyReservationsOffersActivity.this,
                                        getString(R.string.my_reservations_offers_validate_processing),
                                        getString(R.string.wait), false, false);
                            }

                            @Override
                            protected String doInBackground(String... params) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put(Config.KEY_RESERVE_PERSON_ID,
                                        getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE)
                                                .getString(Config.KEY_SP_ID, "-1"));
                                hashMap.put(Config.KEY_RESERVE_OFFER_ID, params[0]);
                                RequestHandler rh = new RequestHandler();
                                return rh.sendPostRequest(Config.URL_MRO_VALIDATE, hashMap);
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                loading.dismiss();
                                if (s.equals("0")) {
                                    Toast.makeText(getApplicationContext(),
                                            R.string.my_reservations_offers_validate_ok, Toast.LENGTH_LONG).show();
                                    //Snackbar.make(mRecyclerView, R.string.my_reservations_offers_validate_ok, Snackbar.LENGTH_LONG).show();
                                    this.dialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            R.string.operation_fail, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        new ValidateReservationOffer(dialog).execute(ro.getIdReservationOffer());
                        rateOffer(ro, true);
                    }
                });

        // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
        dialogBuilder.setNegativeButton(R.string.my_reservations_offers_validate_negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // (optional) set whether to dismiss dialog when touching outside
        dialogBuilder.setCanceledOnTouchOutside(false);
        // Show the dialog
        dialogBuilder.show();
    }

    private void rateOffer(final ReservationOffer ro, final boolean isFirst) {
        // Rate the Offer
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rank_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setMessage(R.string.my_reservations_offers_rate_title);
        dialogBuilder.setTitle(R.string.my_reservations_offers_rate_message);
        // Back button no close the dialog
        //dialogBuilder.setCancelable(false);
        dialogBuilder.setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if(isFirst) getReservations();
            }
        });

        dialogBuilder.setPositiveButton(R.string.my_reservations_offers_rate_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get reference to rating bar

                // TODO: Set the minimun rate value to 1
                RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.my_reservations_offers_rate_bar);
                doPositiveRate(dialog, ro, ratingBar.getRating()+"", isFirst);
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        // No close the dialog when touched outside
        //alertDialog.setCanceledOnTouchOutside(false);

        // Can use the button in the layout (but uncomment first)
        /*Button rank = (Button) dialogView.findViewById(R.id.rank_dialog_button);
        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });*/
        alertDialog.show();
    }

    // Actions to rate Offer
    private void doPositiveRate(DialogInterface dialog, ReservationOffer ro, String rate, boolean isFirst) {
        class RateReservationOffer extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            DialogInterface dialog;

            RateReservationOffer(DialogInterface dialog) {
                this.dialog = dialog;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MyReservationsOffersActivity.this,
                        getString(R.string.my_reservations_offers_rate_processing),
                        getString(R.string.wait), false, false);
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_RESERVE_PERSON_ID,
                        getSharedPreferences(Config.KEY_SHARED_PREF,Context.MODE_PRIVATE)
                                .getString(Config.KEY_SP_ID, "-1"));
                hashMap.put(Config.KEY_RESERVE_OFFER_ID, params[0]);
                hashMap.put(Config.KEY_RESERVE_RATE, params[1]);
                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Config.URL_MRO_RATE, hashMap);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (s.equals("0")) {
                    Toast.makeText(getApplicationContext(),
                            R.string.my_reservations_offers_rate_ok, Toast.LENGTH_LONG).show();
                    //Snackbar.make(mRecyclerView, R.string.my_reservations_offers_validate_ok, Snackbar.LENGTH_LONG).show();
                    dialog.dismiss();
                    getReservations();
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.operation_fail, Toast.LENGTH_LONG).show();
                }
            }
        }
        new RateReservationOffer(dialog).execute(ro.getIdReservationOffer(), rate);
    }

    // Get data from DB and put into each ReservationOffer (to be shown)
    private void getData(String s) {
        String sFinalDate, sReservationDate;
        Date dFinalDate, dReservationDate;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);

        try{
            JSONObject jsonObject = new JSONObject(s);

            JSONArray jsonOffers = jsonObject.optJSONArray(Config.TAG_GRO);

            Calendar c = Calendar.getInstance();
            reservationOffersListCharged = new ArrayList<>();
            reservationsOffersListReserved = new ArrayList<>();

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
                item.setPromotionCode(jsonObjectItem.getString(Config.TAG_GRO_PROMOCOD));
                item.setLocalCode(jsonObjectItem.getString(Config.TAG_GRO_LOCCODE));
                item.setCalificacion(jsonObjectItem.getString(Config.TAG_GRO_CALIFICATION));
                item.setImage(jsonObjectItem.getString(Config.TAG_GRO_IMAGE));

                item.setPaymentDate(jsonObjectItem.getString(Config.TAG_GRO_PAYDATE));

                if (item.getPaymentDate().compareTo("") == 0) reservationsOffersListReserved.add(item);
                else reservationOffersListCharged.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
