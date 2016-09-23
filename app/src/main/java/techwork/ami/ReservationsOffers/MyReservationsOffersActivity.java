package techwork.ami.ReservationsOffers;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import techwork.ami.Config;
import techwork.ami.Dialogs.CustomAlertDialogBuilder;
import techwork.ami.MainActivity;
import techwork.ami.ReservationsOffers.ReservationOffer;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class MyReservationsOffersActivity extends AppCompatActivity {

    // UI references
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private List<ReservationOffer> reservationsOffersList;
    private MyReservationsOffersListAdapter adapter;
    FragmentManager fm;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
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

        // Set behavior to click in some item
        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(final View view) {
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

                // Create the CustomAlertDialogBuilder
                CustomAlertDialogBuilder dialogBuilder = new CustomAlertDialogBuilder(context);

                // Set the usual data, as you would do with AlertDialog.Builder
                dialogBuilder.setTitle(getResources().getString(R.string.my_reservations_offers_validate_title));
                dialogBuilder.setMessage(getResources().getString(R.string.my_reservations_offers_validate_message));

                // Create a EditText
                final EditText edittext = new EditText(context);
                // Type no visible password
                edittext.setInputType(Config.inputPasswordType);
                dialogBuilder.setView(edittext);
                // Set your buttons OnClickListeners
                dialogBuilder.setPositiveButton (getResources().getString(R.string.my_reservations_offers_validate_positiveText),
                        new DialogInterface.OnClickListener() {
                    public void onClick (DialogInterface dialog, int which) {
                        // Dialog will not dismiss when the button is clicked
                        // call dialog.dismiss() to actually dismiss it.
                    }
                });

                // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
                dialogBuilder.setNegativeButton(getResources().getString(R.string.my_reservations_offers_validate_negativeText),
                null);

                // (optional) set whether to dismiss dialog when touching outside
                dialogBuilder.setCanceledOnTouchOutside(false);

                // Show the dialog
                dialogBuilder.show();
            }

            @Override
            public void onItemLongClick(View view) {
                Snackbar.make(view, "Long snackbar", Snackbar.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),"Long click",Toast.LENGTH_LONG).show();

                // Rate the Offer
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.rank_dialog, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);
                dialogBuilder.setMessage("Tu opinión nos interesa");
                dialogBuilder.setTitle("Califica tu experiencia con esta empresa");

                dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                Button rank = (Button) dialogView.findViewById(R.id.rank_dialog_button);

                final AlertDialog alertDialog = dialogBuilder.create();

                rank.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
                /*Button rankBtn = (Button) findViewById(R.id.rank_dialog_button);
                rankBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Dialog rankDialog = new Dialog(context);
                        rankDialog.setContentView(R.layout.rank_dialog);
                        rankDialog.setCancelable(true);
                        RatingBar ratingBar = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);
                        float userRankValue = 10;
                        ratingBar.setRating(userRankValue);

                        TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
                        text.setText("AMI");

                        Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
                        updateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //rankDialog.dismiss();
                            }
                        });
                        //now that the dialog is set up, it's time to show it
                        rankDialog.show();
                    }
                });*/
            }
        });
    }

    // Actions to validate Offer
    private int doPositive(View view, String editTextPromotionCode) {
        Toast.makeText(getApplicationContext(),reservationsOffersList.get(mRecyclerView.getChildAdapterPosition(view)).getPromotionCode(),Toast.LENGTH_SHORT).show();
        Snackbar.make(view, "Cobrado!", Snackbar.LENGTH_LONG).show();
        return 0;
    }

    // Actions to calificate Offer


    // Get data from DB and put into each ReservationOffer (to be shown)
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
                item.setPromotionCode(jsonObjectItem.getString(Config.TAG_GRO_PROMOCOD));
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
