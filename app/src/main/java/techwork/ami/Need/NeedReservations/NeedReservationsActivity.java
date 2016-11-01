package techwork.ami.Need.NeedReservations;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
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
import java.util.concurrent.locks.Condition;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import retrofit.http.GET;
import techwork.ami.Config;
import techwork.ami.Dialogs.CustomAlertDialogBuilder;
import techwork.ami.Need.ListNeeds.NeedModel;
import techwork.ami.Need.ListOfferCompanies.NeedOfferActivity;
import techwork.ami.Need.ListOfferCompanies.NeedOfferModel;
import techwork.ami.Need.NeedOfferDetails.NeedOfferViewActivity;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;
import techwork.ami.ReservationsOffers.MyReservationsOffersActivity;
import techwork.ami.ReservationsOffers.ReservationOffer;

public class NeedReservationsActivity extends AppCompatActivity {

    private List<NeedReservationsModel> needReservationsList;
    private RecyclerView rv;
    private LinearLayoutManager layout;
    private NeedReservationsAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvNeedReservationEmpty;
    CustomAlertDialogBuilder dialogBuilder;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
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

    public void showPopupMenu(final View view, final NeedReservationsModel model){

        final PopupMenu popup= new PopupMenu(view.getContext(),view);
        MenuInflater inflater = popup.getMenuInflater();

        final Menu popumenu = popup.getMenu();
        inflater.inflate(R.menu.popup_menu_reservations,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId()){


                    case R.id.item_popup_menu_reservations_details:
                        Toast.makeText(view.getContext(),"Ver detalles de oferta",Toast.LENGTH_LONG).show();
                        return true;

                    case R.id.item_popup_menu_reservations_charge:
                        item.setEnabled(true);
                        //If needOffer is charge, not charged permitted.
                        if (model.getCashed().equals("1")){
                            popumenu.findItem(R.id.item_popup_menu_reservations_charge).setEnabled(false);
                            Toast.makeText(getApplicationContext(),R.string.need_reservations_offers_already,Toast.LENGTH_LONG).show();
                            Snackbar.make(view,R.string.need_reservations_offers_already,Snackbar.LENGTH_SHORT).show();
                        }
                        //Do charge
                        else {
                            dialogLocalCode(model);
                        }
                        return true;

                    case R.id.item_popup_menu_reservations_calificate:

                        //If needOffer is not charge, not calificate.
                        if (model.getCashed().equals("0")){

                            Toast.makeText(getApplicationContext(), R.string.need_reservations_offers_unvalidated, Toast.LENGTH_SHORT).show();
                        }
                        //If has already validated and rated.
                        else if (!model.getCalification().equals("")){
                            item.setEnabled(false);
                            Toast.makeText(getApplicationContext(), R.string.my_reservations_offers_already_commented, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            rateNeedOffer(model,false);
                        }

                }

                return false;
            }
        });
        popup.show();
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

        adapter = new NeedReservationsAdapter(getApplicationContext(), needReservationsList,NeedReservationsActivity.this);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);

        if (needReservationsList.size()==0){
            tvNeedReservationEmpty.setText("¡Oops! \n No tienes pedidos reservados :(");
        }else {
            tvNeedReservationEmpty.setText("");
        }


        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {

            @Override
            public void onItemClick(final View view) {

                final NeedReservationsModel model = needReservationsList.get(rv.getChildAdapterPosition(view));

                //TODO: Realizar un "ver mi reserva" donde muestre los detalles de la misma.
                //If the needOffer was already cashed
                if (model.getCashed().equals("1")){
                    Toast.makeText(getApplicationContext(),R.string.need_reservations_offers_already,Toast.LENGTH_LONG).show();
                    Snackbar.make(view,R.string.need_reservations_offers_already,Snackbar.LENGTH_SHORT).show();
                }
                else{
                    dialogLocalCode(model);
                }

            }

            @Override
            public void onItemLongClick(final View view) {
                final NeedReservationsModel model = needReservationsList.get(rv.getChildAdapterPosition(view));

                if (model.getCashed().equals("0")){
                    Toast.makeText(getApplicationContext(), R.string.need_reservations_offers_unvalidated, Toast.LENGTH_SHORT).show();
                }
                //If has already validated and rated.
                else if (!model.getCalification().equals("")){
                    Toast.makeText(getApplicationContext(), R.string.my_reservations_offers_already_commented, Toast.LENGTH_SHORT).show();
                }
                else{
                    rateNeedOffer(model,false);
                }

            }
        });

    }
    private void dialogLocalCode(final NeedReservationsModel model){

        //Create the CustomAlertDialogBuilder
        dialogBuilder = new CustomAlertDialogBuilder(context);

        // Set the usual data, as you would do with AlertDialog.Builder
        dialogBuilder.setTitle(R.string.my_reservations_offers_validate_title);
        dialogBuilder.setMessage(getString(R.string.need_reservations_validate_message).replace("%s", model.getCompany()));

        // Create a EditText
        final EditText edittext = new EditText(this);
        // Type no visible password
        edittext.setInputType(Config.inputPromotionCodeType);
        dialogBuilder.setView(edittext);

        // Set your buttons OnClickListeners
        dialogBuilder.setPositiveButton(R.string.need_reservations_offers_validate_positiveText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Dialog will not dismiss when the button is clicked
                        // Call dialog.dismiss() to actually dismiss it.
                        // If promotion code from edittext is equals to the object promotion code
                        if (!model.getLocalCode().equals(edittext.getText().toString())) {
                            edittext.setError(getString(R.string.need_reservations_offers_validate_errorPromotionCode));
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
        dialogBuilder.setNegativeButton(R.string.need_reservations_offers_validate_negativeText, null);

        // (optional) set whether to dismiss dialog when touching outside
        dialogBuilder.setCanceledOnTouchOutside(false);

        // Show the dialog
        dialogBuilder.show();

    }

    private void dialogPromotionCode(final NeedReservationsModel model){

        //Get idOffer
        final String idOffer = model.getIdOffer();

        // Create the CustomAlertDialogBuilder
        dialogBuilder = new CustomAlertDialogBuilder(context);

        // Set the usual data, as you would do with AlertDialog.Builder
        dialogBuilder.setTitle(R.string.need_reservations_offers_validate_local);
        dialogBuilder.setMessage(model.getCodPromotion());

        // Set your buttons OnClickListeners
        dialogBuilder.setPositiveButton(R.string.need_reservations_offers_validate_positiveText,
                new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    // Write in the DB that offer has been hired
                    class ValidateNeedReservation extends AsyncTask<String, Void, String> {
                        ProgressDialog loading;
                        DialogInterface dialog;

                        ValidateNeedReservation(DialogInterface dialog) {
                            this.dialog = dialog;
                        }

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            loading = ProgressDialog.show(NeedReservationsActivity.this,
                                    getString(R.string.need_reservations_offers_validate_processing),
                                    getString(R.string.wait), false, false);
                        }

                        @Override
                        protected String doInBackground(String... params) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(Config.KEY_GNR_IDPERSON,
                                    getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE)
                                            .getString(Config.KEY_SP_ID, "-1"));


                            hashMap.put(Config.KEY_GNR_IDOFFER, idOffer);
                            RequestHandler rh = new RequestHandler();
                            return rh.sendPostRequest(Config.URL_VALIDATE_NEED_RESERV, hashMap);
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            loading.dismiss();
                            if (s.equals("0")) {
                                Toast.makeText(getApplicationContext(),
                                        R.string.need_reservations_offers_validate_ok, Toast.LENGTH_LONG).show();
                                //Snackbar.make(mRecyclerView, R.string.my_reservations_offers_validate_ok, Snackbar.LENGTH_LONG).show();
                                this.dialog.dismiss();
                                getNeedReservs(); //Refresh activity.
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        R.string.operation_fail, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    ValidateNeedReservation go = new ValidateNeedReservation(dialog);
                    go.execute();
                    //rateOffer(ro, true);
                }
        });

        // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
        dialogBuilder.setNegativeButton(R.string.need_reservations_offers_validate_negativeText,
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

    private void rateNeedOffer(final NeedReservationsModel model, final boolean isFirst) {
        // Rate the Offer
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rank_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setMessage(R.string.need_reservations_offers_rate_title);
        dialogBuilder.setTitle(R.string.need_reservations_offers_rate_message);
        // Back button no close the dialog
        //dialogBuilder.setCancelable(false);
        dialogBuilder.setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if(isFirst) getNeedReservs();
            }
        });

        dialogBuilder.setPositiveButton(R.string.need_reservations_offers_rate_positive, new DialogInterface.OnClickListener() {
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

    // Actions to rate Offer
    private void doPositiveRate(DialogInterface dialog, NeedReservationsModel model, final String rate, boolean isFirst) {

        final String idOffer = model.getIdOffer();

        class RateNeedReservation extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            DialogInterface dialog;

            RateNeedReservation(DialogInterface dialog) {
                this.dialog = dialog;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(NeedReservationsActivity.this,
                        getString(R.string.need_reservations_offers_rate_processing),
                        getString(R.string.wait), false, false);
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_GNR_IDPERSON,
                        getSharedPreferences(Config.KEY_SHARED_PREF,Context.MODE_PRIVATE)
                                .getString(Config.KEY_SP_ID, "-1"));
                hashMap.put(Config.KEY_GNR_IDOFFER, idOffer);
                hashMap.put(Config.KEY_GNR_RATE, rate);
                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Config.URL_NEED_RATE, hashMap);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (s.equals("0")) {
                    Toast.makeText(getApplicationContext(),
                            R.string.need_reservations_offers_rate_ok, Toast.LENGTH_LONG).show();
                    //Snackbar.make(mRecyclerView, R.string.my_reservations_offers_validate_ok, Snackbar.LENGTH_LONG).show();
                    dialog.dismiss();
                    getNeedReservs();
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.operation_fail, Toast.LENGTH_LONG).show();
                }
            }
        }
        RateNeedReservation go = new RateNeedReservation(dialog);
        go.execute();

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
                item.setCompany(jsonObjectItem.getString(Config.TAG_GNR_COMPANY));
                item.setLocalCode(jsonObjectItem.getString(Config.TAG_GNR_LOCALCODE));
                item.setDateCashed(jsonObjectItem.getString(Config.TAG_GNR_CASHED));

                //If dateChashed is != null, save variables.
                if (!jsonObjectItem.getString(Config.TAG_GNR_DATECASHED).equals("")){

                    dCashed= jsonObjectItem.getString(Config.TAG_GNR_DATECASHED);
                    dateCashed=format.parse(dCashed);
                    c.setTime(dateCashed);
                    item.setDateCashed(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));
                }

                dIni = jsonObjectItem.getString(Config.TAG_GNR_DATEINI);
                dFin = jsonObjectItem.getString(Config.TAG_GNR_DATEFIN);
                dReserv= jsonObjectItem.getString(Config.TAG_GNR_DATERESERV);

                dateIni= format.parse(dIni);
                dateFin = format.parse(dFin);
                dateReserv=format.parse(dReserv);

                c.setTime(dateIni);
                item.setDateIni(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));
                c.setTime(dateFin);
                item.setDateFin(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));
                c.setTime(dateReserv);
                item.setDateReserv(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                needReservationsList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
