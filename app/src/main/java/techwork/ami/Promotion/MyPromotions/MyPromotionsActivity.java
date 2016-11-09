package techwork.ami.Promotion.MyPromotions;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.RatingBar;
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
import techwork.ami.Dialogs.CustomAlertDialogBuilder;
import techwork.ami.Promotion.PromotionDetail.PromotionDetailActivity;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class MyPromotionsActivity extends AppCompatActivity {

    // Two recycle views in one layout by https://goo.gl/Iy5prs (natrio)

    // UI references
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private List<MyReservationPromotionModel> reservationsOffersList;
    private MyPromotionsAdapter adapter;
    private TextView tvReservationsOffersEmpty;
    Context context;
    CustomAlertDialogBuilder dialogBuilder;
    FloatingActionButton fab;
    private Vibrator c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.my_promotions_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.my_reservations_offers_list_reserved);
        tvReservationsOffersEmpty = (TextView)findViewById(R.id.tv_reservations_empty);


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
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setScaleX(0);
        fab.setScaleY(0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Interpolator interpolador = AnimationUtils.loadInterpolator(getApplicationContext(),
                    android.R.interpolator.fast_out_slow_in);

            fab.animate()
                    .scaleX((float) 1.5)
                    .scaleY((float) 1.5)
                    .setInterpolator(interpolador)
                    .setDuration(600)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fab.animate()
                                    .scaleY(1)
                                    .scaleX(1)
                                    .setInterpolator(interpolador)
                                    .setDuration(1000)
                                    .start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the Activity
                finish();
                // Call to FragmentHome and show available offers
                //startActivity(new Intent(MyPromotionsActivity.this, MainActivity.class) );
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get and show data
        getReservations();
    }

    public void showPopupMenu(final View view, final MyReservationPromotionModel model){

        final PopupMenu popup= new PopupMenu(view.getContext(),view);
        final MenuInflater inflater = popup.getMenuInflater();

        final Menu popumenu = popup.getMenu();
        inflater.inflate(R.menu.popup_menu_reservations,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId()){
                    case R.id.item_popup_menu_reservations_details:
                        Intent intent = new Intent(MyPromotionsActivity.this, PromotionDetailActivity.class);
                        intent.putExtra(Config.TAG_GO_TITLE, model.getTitle());
                        intent.putExtra(Config.TAG_GO_DESCRIPTION, model.getDescription());
                        intent.putExtra(Config.TAG_GO_IMAGE, model.getImage());
                        intent.putExtra(Config.TAG_GO_COMPANY, model.getCompany());
                        intent.putExtra(Config.TAG_GO_PRICE, model.getPrice());
                        intent.putExtra(Config.TAG_GO_OFFER_ID, model.getIdReservationOffer());
                        intent.putExtra(Config.TAG_GO_MAXXPER, model.getMaxPPerson());
                        intent.putExtra(Config.TAG_GO_STOCK, model.getStock());
                        intent.putExtra(Config.TAG_GO_DATEFIN, model.getFinalDate());
                        intent.putExtra(Config.TAG_GO_TOTALPRICE, model.getTotalPrice());
                        intent.putExtra(Config.TAG_GO_DATETIMEFIN, model.getFinalDateTime());
                        intent.putExtra(Config.TAG_GO_NO_RESERVE_OPTION, false);
                        startActivity(intent);
                        return true;

                    case R.id.item_popup_menu_reservations_charge:
                        item.setEnabled(true);
                        //If needOffer is charge, not charged permitted.
                        if (model.getCashed().equals("1")){
                            popumenu.findItem(R.id.item_popup_menu_reservations_charge).setEnabled(false);
                            //Toast.makeText(getApplicationContext(),R.string.need_reservations_offers_already,Toast.LENGTH_LONG).show();
                            Snackbar.make(view,R.string.OfferReservedAlready,Snackbar.LENGTH_LONG).show();
                        }
                        //Do charge
                        else {
                            dialogLocalCode(model);
                        }
                        return true;

                    case R.id.item_popup_menu_reservations_calificate:
                        //If needOffer is not charge, not calificate.
                        if (model.getCashed().equals("0")){
                            //Toast.makeText(getApplicationContext(), R.string.need_reservations_offers_unvalidated, Toast.LENGTH_LONG).show();
                            Snackbar.make(fab, R.string.OffersReservedUnvalidated, Snackbar.LENGTH_LONG).show();
                        }
                        //If has already validated and rated.
                        else if (!model.getCalification().equals("")){
                            item.setEnabled(false);
                            //Toast.makeText(getApplicationContext(), R.string.my_reservations_offers_already_commented, Toast.LENGTH_LONG).show();
                            Snackbar.make(fab, R.string.OfferReservedAlreadyCommented, Snackbar.LENGTH_LONG).show();
                        }
                        else{
                            rateOffer(model,false);
                        }
                }
                return false;
            }
        });
        popup.show();
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

                Boolean connectionStatus = rh.isConnectedToServer(mRecyclerView, new View.OnClickListener() {
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

        adapter = new MyPromotionsAdapter(this, reservationsOffersList, MyPromotionsActivity.this);
        ScaleInAnimationAdapter scaleAdapterReserved = new ScaleInAnimationAdapter(adapter);

        mRecyclerView.setAdapter(scaleAdapterReserved);

        if (reservationsOffersList.size()==0){
            tvReservationsOffersEmpty.setText("No has hecho reservas aún.");
        } else {
            tvReservationsOffersEmpty.setText("");
        }

        // Set behavior to click in some item (offer validate)
        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(final View view) {
                final MyReservationPromotionModel model = reservationsOffersList.get(mRecyclerView.getChildAdapterPosition(view));
                // If the offer was already charged
                if(model.getCashed().equals("1")){
                    //Toast.makeText(context,R.string.my_reservations_offers_already,Toast.LENGTH_SHORT).show();
                    Snackbar.make(fab, R.string.my_reservations_offers_already, Snackbar.LENGTH_LONG).show();
                }
                else {
                    dialogLocalCode(model);
                }
            }

            @Override
            public void onItemLongClick(View view) {
                final MyReservationPromotionModel model = reservationsOffersList.get(mRecyclerView.getChildAdapterPosition(view));
                // Validate before rate
                if (model.getCashed().equals("0")){
                    //Toast.makeText(getApplicationContext(), R.string.my_reservations_offers_unvalidated, Toast.LENGTH_SHORT).show();
                    Snackbar.make(fab, R.string.my_reservations_offers_unvalidated, Snackbar.LENGTH_LONG).show();
                }
                // If has already validated but not rated
                else if (!model.getCalification().equals("")){
                    Snackbar.make(fab, R.string.my_reservations_offers_already_commented, Snackbar.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), R.string.my_reservations_offers_already_commented, Toast.LENGTH_SHORT).show();
                }
                else {
                    rateOffer(model, false);
                }
            }
        });


    }

    private void dialogLocalCode(final MyReservationPromotionModel ro) {
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

    private void dialogPromotionCode(final MyReservationPromotionModel ro) {
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
                                loading = ProgressDialog.show(MyPromotionsActivity.this,
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
                                    c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                                    c.vibrate(500);
                                    this.dialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            R.string.operation_fail, Toast.LENGTH_LONG).show();
                                    c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                                    c.vibrate(500);
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

    private void rateOffer(final MyReservationPromotionModel ro, final boolean isFirst) {
        // Rate the Promotion
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

    // Actions to rate Promotion
    private void doPositiveRate(DialogInterface dialog, MyReservationPromotionModel ro, String rate, boolean isFirst) {
        class RateReservationOffer extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            DialogInterface dialog;

            RateReservationOffer(DialogInterface dialog) {
                this.dialog = dialog;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MyPromotionsActivity.this,
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
                    c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    c.vibrate(500);
                    dialog.dismiss();
                    getReservations();
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.operation_fail, Toast.LENGTH_LONG).show();
                    c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    c.vibrate(500);
                }
            }
        }
        new RateReservationOffer(dialog).execute(ro.getIdReservationOffer(), rate);
    }

    // Get data from DB and put into each MyReservationPromotionModel (to be shown)
    private void getData(String s) {
        String sFinalDate, sReservationDate, dTimeFin;
        Date dFinalDate, dReservationDate, dateTimeFin;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);

        try{
            JSONObject jsonObject = new JSONObject(s);

            JSONArray jsonOffers = jsonObject.optJSONArray(Config.TAG_GRO);

            Calendar c = Calendar.getInstance();
            reservationsOffersList = new ArrayList<>();

            for(int i=0;i<jsonOffers.length();i++){

                JSONObject jsonObjectItem = jsonOffers.optJSONObject(i);
                MyReservationPromotionModel item = new MyReservationPromotionModel();

                sFinalDate = jsonObjectItem.getString(Config.TAG_GRO_DATEFIN);
                sReservationDate = jsonObjectItem.getString(Config.TAG_GRO_RESERDATE);
                dTimeFin = jsonObjectItem.getString(Config.TAG_GRO_DATEFIN);
                dFinalDate = format.parse(sFinalDate);
                dReservationDate = format.parse(sReservationDate);
                dateTimeFin=format.parse(dTimeFin);;

                item.setIdReservationOffer(jsonObjectItem.getString(Config.TAG_GRO_ID_OFFER));

                item.setTitle(jsonObjectItem.getString(Config.TAG_GRO_TITLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GRO_DESCRIPTION));
                c.setTime(dFinalDate);
                item.setFinalDate(String.format(Locale.US, Config.DATE_FORMAT,
                        c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GRO_PRICE));
                item.setCompany(jsonObjectItem.getString(Config.TAG_GRO_COMPANY));
                c.setTime(dReservationDate);
                item.setReservationDate(String.format(Locale.US, Config.DATE_FORMAT,
                        c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR)));
                c.setTime(dateTimeFin);
                item.setFinalDateTime(String.format(Locale.US,Config.DATETIME_FORMAT,
                        c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,
                        c.get(Calendar.YEAR),c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));
                item.setQuantity(jsonObjectItem.getString(Config.TAG_GRO_QUANTITY));
                item.setPromotionCode(jsonObjectItem.getString(Config.TAG_GRO_PROMOCOD));
                item.setLocalCode(jsonObjectItem.getString(Config.TAG_GRO_LOCCODE));
                item.setCalification(jsonObjectItem.getString(Config.TAG_GRO_CALIFICATION));
                item.setImage(jsonObjectItem.getString(Config.TAG_GRO_IMAGE));
                item.setCashed(jsonObjectItem.getString(Config.TAG_GRO_CASHED));
                item.setPaymentDate(jsonObjectItem.getString(Config.TAG_GRO_PAYDATE));
                item.setMaxPPerson(jsonObjectItem.getInt(Config.TAG_GRO_MAXXPER));
                item.setStock(jsonObjectItem.getInt(Config.TAG_GRO_STOCK));
                item.setTotalPrice(jsonObjectItem.getInt(Config.TAG_GRO_TOTALPRICE));


                reservationsOffersList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
