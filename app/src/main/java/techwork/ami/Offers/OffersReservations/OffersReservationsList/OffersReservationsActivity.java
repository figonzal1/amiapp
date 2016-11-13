package techwork.ami.Offers.OffersReservations.OffersReservationsList;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import techwork.ami.ExpiryTime;
import techwork.ami.MainActivity;
import techwork.ami.Offers.OffersReservations.OffersReservationsDetails.OffersReservationsViewActivity;
import techwork.ami.Offers.OrdersList.OrderViewActivity;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class OffersReservationsActivity extends AppCompatActivity {

    private List<OffersReservationsModel> offersReservationsList;
    private RecyclerView rv;
    private LinearLayoutManager layout;
    private OffersReservationsAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvOffersReservationEmpty;
    CustomAlertDialogBuilder dialogBuilder;
    Context context;
    private Vibrator c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers_reservations_activity);
        context=this;
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //arrow back refresh main activity.
                int count = getFragmentManager().getBackStackEntryCount();

                if(count==0){
                    Intent intent = new Intent(OffersReservationsActivity.this,MainActivity.class);
                    startActivity(intent);
                }

                else {
                    getFragmentManager().popBackStack();
                }
            }
        });

        tvOffersReservationEmpty = (TextView)findViewById(R.id.tv_offers_reservations_empty);

        rv = (RecyclerView) findViewById(R.id.recycler_view_offers_reservations);
        rv.setHasFixedSize(true);

        layout = new LinearLayoutManager(this);
        rv.setLayoutManager(layout);

        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_offers_reservations);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOfferReservation();
            }
        });

        getOfferReservation();


    }


    public void showPopupMenu(final View view, final OffersReservationsModel model, long expiryTime){

        final PopupMenu popup= new PopupMenu(view.getContext(),view);
        final MenuInflater inflater = popup.getMenuInflater();
        final Menu popumenu = popup.getMenu();
        inflater.inflate(R.menu.popup_menu_reservations,popup.getMenu());

        //If offer is out of time
        if (expiryTime < 0.0){

            //If offer is out of time and not charged (vencida)
            if (model.getCharge().equals("0")){
                popumenu.findItem(R.id.item_popup_menu_reservations_charge).setEnabled(false);
                popumenu.findItem(R.id.item_popup_menu_reservations_calificate).setEnabled(false);
            }

            //If odder is charged and not rated
            else if(model.getCharge().equals("1") && model.getCalification().equals("")){
                popumenu.findItem(R.id.item_popup_menu_reservations_charge).setEnabled(false);
                popumenu.findItem(R.id.item_popup_menu_reservations_delete_reservation).setEnabled(false);
            }
            //if offer is rated, disable all except view details.
            else{
                popumenu.findItem(R.id.item_popup_menu_reservations_charge).setEnabled(false);
                popumenu.findItem(R.id.item_popup_menu_reservations_calificate).setEnabled(false);
                popumenu.findItem(R.id.item_popup_menu_reservations_delete_reservation).setEnabled(false);
            }

        }
        //if offer is in time
        else{

            //If offer is reserved and not charged.
            if (model.getCharge().equals("0")){
                popumenu.findItem(R.id.item_popup_menu_reservations_calificate).setEnabled(false);
            }
            //If offer is charged and not rated
            else if (model.getCharge().equals("1") && model.getCalification().equals("")){
                popumenu.findItem(R.id.item_popup_menu_reservations_charge).setEnabled(false);
                popumenu.findItem(R.id.item_popup_menu_reservations_delete_reservation).setEnabled(false);
            }
            //If offer is charged and rated
            else{
                popumenu.findItem(R.id.item_popup_menu_reservations_charge).setEnabled(false);
                popumenu.findItem(R.id.item_popup_menu_reservations_delete_reservation).setEnabled(false);
                popumenu.findItem(R.id.item_popup_menu_reservations_calificate).setEnabled(false);
            }

        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId()){

                    case R.id.item_popup_menu_reservations_details:

                        //Go to see details of each needOffer reserved.
                        Intent intent = new Intent(OffersReservationsActivity.this,OffersReservationsViewActivity.class);
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_IDOFFER,model.getIdOffer());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_IDLOCAL,model.getIdLocal());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_IDNEED,model.getIdNeed());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_TITTLE,model.getTittle());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_DESCRIPTION,model.getDescription());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_PRICEOFFER,model.getPrice());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_CASHED,model.getDescription());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_DATECASHED,model.getDateCashed());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_CALIFICATION,model.getCalification());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_CODPROMOTION,model.getCodPromotion());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_QUANTITY,model.getQuantity());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_LOCALCODE,model.getLocalCode());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_COMPANY,model.getCompany());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_DATEINI,model.getDateIni());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_DATEFIN,model.getDateFin());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_DATERESERV,model.getDateReserv());
                        intent.putExtra(Config.TAG_GET_OFFER_RESERVED_PRICE_TOTAL, model.getPriceTotal());

                        startActivity(intent);
                        return true;

                    case R.id.item_popup_menu_reservations_charge:

                        dialogLocalCode(model);
                        return true;

                    case R.id.item_popup_menu_reservations_calificate:

                        rateOfferReserved(model,false);
                        return true;

                    case R.id.item_popup_menu_reservations_delete_reservation:

                        deleteOfferReservation(model);
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public void onBackPressed(){

        int count = getFragmentManager().getBackStackEntryCount();

        if(count==0){
            super.onBackPressed();
            Intent intent = new Intent(OffersReservationsActivity.this,MainActivity.class);
            startActivity(intent);
        }

        else {
            getFragmentManager().popBackStack();
        }
    }

    private void getOfferReservation() {
        sendPostRequest();
    }

    private void sendPostRequest() {

        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        final String idperson = sharedPref.getString(Config.KEY_SP_ID, "-1");

        class OfferReservationsAsyncTask extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }

            @Override
            protected String doInBackground(Void... params) {

                RequestHandler rh = new RequestHandler();
                Boolean connectionStatus = rh.isConnectedToServer(rv, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendPostRequest();
                    }
                });

                if (connectionStatus) {
                    HashMap<String, String> hashmap = new HashMap<>();

                    hashmap.put(Config.KEY_GET_OFFER_RESERVED_IDPERSON, idperson);

                    return rh.sendPostRequest(Config.URL_GET_OFFER_RESERVATIONS, hashmap);
                }
                else{
                    return "-1";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                //if conection is correct do show.
                if (!s.equals("-1")){
                    showOfferReservations(s);
                }
            }
        }
        OfferReservationsAsyncTask go = new OfferReservationsAsyncTask();
        go.execute();
    }

    private void showOfferReservations(String s) {
        getOfferReservationsData(s);

        adapter = new OffersReservationsAdapter(getApplicationContext(), offersReservationsList,OffersReservationsActivity.this);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);

        if (offersReservationsList.size()==0){
            tvOffersReservationEmpty.setText(R.string.OfferReservedEmpty);
        }else {
            tvOffersReservationEmpty.setText("");
        }


        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {

            @Override
            public void onItemClick(final View view) {

                final OffersReservationsModel model = offersReservationsList.get(rv.getChildAdapterPosition(view));

                //Go to see details of each needOffer reserved.
                Intent intent = new Intent(OffersReservationsActivity.this,OffersReservationsViewActivity.class);
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_IDOFFER,model.getIdOffer());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_IDLOCAL,model.getIdLocal());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_IDNEED,model.getIdNeed());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_TITTLE,model.getTittle());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_DESCRIPTION,model.getDescription());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_PRICEOFFER,model.getPrice());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_CASHED,model.getDescription());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_DATECASHED,model.getDateCashed());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_CALIFICATION,model.getCalification());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_CODPROMOTION,model.getCodPromotion());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_QUANTITY,model.getQuantity());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_LOCALCODE,model.getLocalCode());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_COMPANY,model.getCompany());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_DATEINI,model.getDateIni());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_DATEFIN,model.getDateFin());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_DATERESERV,model.getDateReserv());
                intent.putExtra(Config.TAG_GET_OFFER_RESERVED_PRICE_TOTAL, model.getPriceTotal());

                startActivity(intent);

            }

            @Override
            public void onItemLongClick(final View view) {
                final OffersReservationsModel model = offersReservationsList.get(rv.getChildAdapterPosition(view));

                ExpiryTime expt = new ExpiryTime();
                long expiryTime = expt.getTimeDiference(model.getDateTimeFin());

                //If offer is in time
                if (expiryTime> 0.0){

                    //If offer not charged yet
                    if (model.getCharge().equals("0")){
                        dialogLocalCode(model);
                    }
                    //If is carged and not rated
                    else if (model.getCalification().equals("")){
                        rateOfferReserved(model,false);
                    }
                    else {
                        Snackbar.make(view,R.string.OfferReservedAlreadyCommented, Snackbar.LENGTH_LONG).show();
                    }
                }

                //If offer is out of time
                else {

                    //If offer is charged and is not rated.
                    if (!model.getCharge().equals("0") && model.getCalification().equals("")){
                        rateOfferReserved(model,false);
                    }
                    else{
                        Snackbar.make(view, R.string.OfferReservedExpired, Snackbar.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

    private void deleteOfferReservation(final OffersReservationsModel model){

        dialogBuilder = new CustomAlertDialogBuilder(context);
        dialogBuilder.setTitle(R.string.OfferReservedDeleteTittle);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setCanceledOnTouchOutside(false);
        dialogBuilder.setMessage(R.string.OfferReservedDeleteConfirm);
        dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
                        final String idPerson = sharedPref.getString(Config.KEY_SP_ID, "-1");

                        class deleteOfferReservationAsyncTask extends AsyncTask<Void,Void,String>{

                            private ProgressDialog loading;
                            private DialogInterface dialog;

                            private deleteOfferReservationAsyncTask(DialogInterface dialog){
                                this.dialog=dialog;
                            }

                            @Override
                            protected void onPreExecute(){
                                super.onPreExecute();

                                loading= ProgressDialog.show(OffersReservationsActivity.this,
                                        getString(R.string.OfferReservedDeleteProcessing),
                                        getString(R.string.wait),false,false);
                            }
                            @Override
                            protected String doInBackground(Void... voids) {

                                RequestHandler rh = new RequestHandler();
                                Boolean connectionStatus = rh.isConnectedToServer(rv, new View.OnClickListener() {
                                    @Override
                                    @TargetApi(Build.VERSION_CODES.M)
                                    public void onClick(View v) {
                                        sendPostRequest();
                                    }
                                });

                                if (connectionStatus) {
                                    HashMap<String, String> hashMap = new HashMap<>();

                                    hashMap.put(Config.KEY_DELETE_OFFER_RESERVED_IDOFFER, model.getIdOffer());
                                    hashMap.put(Config.KEY_DELETE_OFFER_RESERVED_IDPERSON, idPerson);
                                    //Send quantity reserved by user
                                    hashMap.put(Config.KEY_DELETE_OFFER_RESERVED_QUANTITY, model.getQuantity());

                                    return rh.sendPostRequest(Config.URL_DELETE_OFFER_RESERVED, hashMap);
                                }
                                else {
                                    return "-1";
                                }
                            }

                            @Override
                            protected void onPostExecute(String s){
                                super.onPostExecute(s);

                                //If operation is correct dialog close in 1,5 [s]
                                if (s.equals("0") && !s.equals("-1")){

                                    Handler mHandler = new Handler();
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            loading.dismiss();

                                            c=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                                            c.vibrate(500);

                                            //If offer reserved is deleted finish OfferViewLocalActivity.
                                            //LocalActivity.activity.finish();

                                            Toast.makeText(getApplicationContext(),R.string.OfferReservedDeleteOk, Toast.LENGTH_LONG).show();

                                            //If operations is ok refresh orders.
                                            getOfferReservation();
                                        }
                                    },1500);

                                }

                                //If not correct depends of the operation.
                                else {
                                    loading.dismiss();
                                    Toast.makeText(getApplicationContext(),
                                            R.string.operation_fail, Toast.LENGTH_LONG).show();
                                }

                                this.dialog.dismiss();
                            }
                        }
                        deleteOfferReservationAsyncTask go = new deleteOfferReservationAsyncTask(dialog);
                        go.execute();

                    }
        });

        dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.show();

    }


    private void dialogLocalCode(final OffersReservationsModel model){

        //Create the CustomAlertDialogBuilder
        dialogBuilder = new CustomAlertDialogBuilder(context);

        // Set the usual data, as you would do with AlertDialog.Builder
        dialogBuilder.setTitle(R.string.OfferReservedValidateTitle);
        dialogBuilder.setMessage(getString(R.string.OfferReservedValidateMessage).replace("%s", model.getCompany()));

        // Create a EditText
        final EditText edittext = new EditText(this);
        // Type no visible password
        edittext.setInputType(Config.inputPromotionCodeType);
        dialogBuilder.setView(edittext);

        // Set your buttons OnClickListeners
        dialogBuilder.setPositiveButton(R.string.OfferReservedValidatePositiveText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Dialog will not dismiss when the button is clicked
                        // Call dialog.dismiss() to actually dismiss it.
                        // If promotion code from edittext is equals to the object promotion code
                        if (!model.getLocalCode().equals(edittext.getText().toString())) {
                            edittext.setError(getString(R.string.OfferReservedValidateErrorPromotionCode));
                        }
                        // Else
                        else {
                            // First validate for the local operator
                            dialog.dismiss();
                            dialogPromotionCode(model);
                        }
                    }
                });

        // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
        dialogBuilder.setNegativeButton(R.string.OfferReservedValidateNegativeText, null);

        // (optional) set whether to dismiss dialog when touching outside
        dialogBuilder.setCanceledOnTouchOutside(false);

        // Show the dialog
        dialogBuilder.show();

    }

    private void dialogPromotionCode(final OffersReservationsModel model){

        //Get idOffer
        final String idOffer = model.getIdOffer();

        // Create the CustomAlertDialogBuilder
        dialogBuilder = new CustomAlertDialogBuilder(context);

        // Set the usual data, as you would do with AlertDialog.Builder
        dialogBuilder.setTitle(R.string.OfferReservedValidateLocal);
        dialogBuilder.setMessage(model.getCodPromotion());

        // Set your buttons OnClickListeners
        dialogBuilder.setPositiveButton(R.string.OfferReservedValidatePositiveText,
                new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    // Write in the DB that offer has been hired
                    class ValidateOfferReservation extends AsyncTask<String, Void, String> {
                        ProgressDialog loading;
                        DialogInterface dialog;

                        private ValidateOfferReservation(DialogInterface dialog) {
                            this.dialog = dialog;
                        }

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            loading = ProgressDialog.show(OffersReservationsActivity.this,
                                    getString(R.string.OfferReservedValidateProcessing),
                                    getString(R.string.wait), false, false);
                        }

                        @Override
                        protected String doInBackground(String... params) {

                            RequestHandler rh = new RequestHandler();
                            Boolean connectionStatus = rh.isConnectedToServer(rv, new View.OnClickListener() {
                                @Override
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(View v) {
                                    sendPostRequest();
                                }
                            });

                            if (connectionStatus) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put(Config.KEY_GET_OFFER_RESERVED_IDPERSON,
                                        getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE)
                                                .getString(Config.KEY_SP_ID, "-1"));


                                hashMap.put(Config.KEY_GET_OFFER_RESERVED_IDOFFER, idOffer);

                                return rh.sendPostRequest(Config.URL_VALIDATE_OFFER_RESERV, hashMap);
                            }
                            else{
                                return "-1";
                            }
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);

                            if (s.equals("0") && !s.equals("-1")) {

                                Handler mHandler = new Handler();
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.dismiss();

                                        c=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                                        c.vibrate(500);

                                        Toast.makeText(getApplicationContext(),
                                                R.string.OfferReservedValidateOk, Toast.LENGTH_LONG).show();
                                        rateOfferReserved(model,true);
                                    }
                                },1500);

                            } else {
                                loading.dismiss();
                                c=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                                c.vibrate(500);
                                Toast.makeText(getApplicationContext(),
                                        R.string.operation_fail, Toast.LENGTH_LONG).show();
                            }

                            this.dialog.dismiss();
                        }
                    }

                    ValidateOfferReservation go = new ValidateOfferReservation(dialog);
                    go.execute();
                }
        });

        // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
        dialogBuilder.setNegativeButton(R.string.OfferReservedValidateNegativeText,
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

    private void rateOfferReserved(final OffersReservationsModel model, final boolean isFirst) {
        // Rate the Promotion
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rank_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setMessage(R.string.OfferReservedRateMessage);
        dialogBuilder.setTitle(R.string.OfferReservedRateTittle);
        // Back button no close the dialog
        //dialogBuilder.setCancelable(false);
        dialogBuilder.setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if(isFirst) getOfferReservation();
            }
        });

        dialogBuilder.setPositiveButton(R.string.OfferReservedRatePositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get reference to rating bar

                // TODO: Set the minimun rate value to 1
                RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.my_reservations_offers_rate_bar);
                doPositiveRate(dialog, model, ratingBar.getRating()+"", isFirst);
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    // Actions to rate Promotion
    private void doPositiveRate(DialogInterface dialog, OffersReservationsModel model, final String rate, boolean isFirst) {

        final String idOffer = model.getIdOffer();
        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        final String idPerson = sharedPref.getString(Config.KEY_SP_ID, "-1");

        class RateOfferReservation extends AsyncTask<String, Void, String> {
            private ProgressDialog loading;
            private DialogInterface dialog;

            private RateOfferReservation(DialogInterface dialog) {
                this.dialog = dialog;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OffersReservationsActivity.this,
                        getString(R.string.OfferReservedRateProcessing),
                        getString(R.string.wait), false, false);
            }

            @Override
            protected String doInBackground(String... params) {

                RequestHandler rh = new RequestHandler();
                Boolean connectionStatus = rh.isConnectedToServer(rv, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendPostRequest();
                    }
                });

                if (connectionStatus) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(Config.KEY_GET_OFFER_RESERVED_IDPERSON, idPerson);
                    hashMap.put(Config.KEY_GET_OFFER_RESERVED_IDOFFER, idOffer);
                    hashMap.put(Config.KEY_GET_OFFER_RESERVED_RATE, rate);

                    return rh.sendPostRequest(Config.URL_OFFER_RATE, hashMap);
                }
                else {
                    return "-1";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s.equals("0")&& !s.equals("-1")) {

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            loading.dismiss();

                            c=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            c.vibrate(500);

                            Toast.makeText(getApplicationContext(),
                                    R.string.OfferReservedRateOk, Toast.LENGTH_LONG).show();
                            getOfferReservation();
                        }
                    },1500);


                } else {
                    loading.dismiss();

                    c=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    c.vibrate(500);

                    Toast.makeText(getApplicationContext(),
                            R.string.operation_fail, Toast.LENGTH_LONG).show();
                }
                this.dialog.dismiss();
            }
        }
        RateOfferReservation go = new RateOfferReservation(dialog);
        go.execute();

    }

    private void getOfferReservationsData(String json) {
        String dIni, dFin,dReserv,dCashed,dTime;
        Date dateIni, dateFin,dateReserv,dateCashed,dateTime;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonNeedReservs = jsonObject.optJSONArray(Config.TAG_GET_OFFER_RESERVED);
            Calendar c = Calendar.getInstance();
            offersReservationsList = new ArrayList<>();

            for (int i = 0; i < jsonNeedReservs.length(); i++) {
                JSONObject jsonObjectItem = jsonNeedReservs.getJSONObject(i);
                OffersReservationsModel item = new OffersReservationsModel();

                item.setIdOffer(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_IDOFFER));
                item.setIdNeed(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_IDNEED));
                item.setIdLocal(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_IDLOCAL));
                item.setTittle(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_TITTLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_DESCRIPTION));
                item.setCodPromotion(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_CODPROMOTION));
                item.setStock(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_STOCK));
                item.setQuantity(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_QUANTITY));
                item.setCharge(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_CASHED));
                item.setCalification(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_CALIFICATION));
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GET_OFFER_RESERVED_PRICEOFFER));
                item.setCompany(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_COMPANY));
                item.setLocalCode(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_LOCALCODE));
                item.setDateCashed(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_CASHED));
                item.setImage(jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_IMAGE));
                item.setPriceTotal(jsonObjectItem.getInt(Config.TAG_GET_OFFER_RESERVED_PRICE_TOTAL));

                //If dateChashed is != null, save variables.
                if (!jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_DATECASHED).equals("")){

                    dCashed= jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_DATECASHED);
                    dateCashed=format.parse(dCashed);
                    c.setTime(dateCashed);
                    item.setDateCashed(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));
                }

                dIni = jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_DATEINI);
                dFin = jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_DATEFIN);
                dReserv= jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_DATERESERV);
                dTime=jsonObjectItem.getString(Config.TAG_GET_OFFER_RESERVED_DATETIME);

                dateIni= format.parse(dIni);
                dateFin = format.parse(dFin);
                dateReserv=format.parse(dReserv);
                dateTime=format.parse(dTime);

                c.setTime(dateIni);
                item.setDateIni(String.format(Locale.US,Config.DATE_FORMAT,
                        c.get(Calendar.DAY_OF_MONTH),
                        c.get(Calendar.MONTH)+1,
                        c.get(Calendar.YEAR)));

                c.setTime(dateFin);
                item.setDateFin(String.format(Locale.US,Config.DATE_FORMAT,
                        c.get(Calendar.DAY_OF_MONTH),
                        c.get(Calendar.MONTH)+1
                        ,c.get(Calendar.YEAR)));

                c.setTime(dateReserv);
                item.setDateReserv(String.format(Locale.US,Config.DATE_FORMAT,
                        c.get(Calendar.DAY_OF_MONTH),
                        c.get(Calendar.MONTH)+1,
                        c.get(Calendar.YEAR)));

                c.setTime(dateTime);
                item.setDateTimeFin(String.format(Locale.US,Config.DATETIME_FORMAT,
                        c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,
                        c.get(Calendar.YEAR),c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));

                offersReservationsList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



}
