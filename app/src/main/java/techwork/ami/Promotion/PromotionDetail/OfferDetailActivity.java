package techwork.ami.Promotion.PromotionDetail;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import techwork.ami.Config;
import techwork.ami.Dialogs.CustomAlertDialogBuilder;
import techwork.ami.ExpiryTime;
import techwork.ami.R;
import techwork.ami.RequestHandler;

/**
 * Created by Daniel on 15-10-2016.
 */
public class OfferDetailActivity extends AppCompatActivity {
    // UI references
    private RecyclerView rv;
    private List<ProductModel> productList;
    private ProductAdapter adapter;
    private GridLayoutManager layout;
    private SwipeRefreshLayout refreshLayout;
    private NumberPicker numberPicker;
    private TextView title, company, description, tPriceTxt, tPrice, dsctTxt, dsct, priceTxt, price, remainingDays;
    private FloatingActionButton floatingButton;

    private String idOffer;
    private String idPersona;
    private Context context;
    public CountDownTimer countDownTimer;
    private Vibrator c;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion_detail_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.offer_detail_toolbar);
        setSupportActionBar(toolbar);

        context = this;

        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        idPersona = sharedPref.getString(Config.KEY_SP_ID, "-1");

        Bundle bundle = getIntent().getExtras();
        idOffer = bundle.getString(Config.TAG_GO_OFFER_ID);

        rv = (RecyclerView)findViewById(R.id.recycler_view_offer_detail);
        rv.setHasFixedSize(true);

        layout= new GridLayoutManager(this, 1);
        rv.setLayoutManager(layout);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_offer_detail);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProducts();
            }
        });

        // References
        title = (TextView)findViewById(R.id.od_tv_title);
        company = (TextView)findViewById(R.id.od_tv_company);
        description = (TextView)findViewById(R.id.od_tv_description);
        tPrice = (TextView)findViewById(R.id.od_tv_tprice);
        tPriceTxt = (TextView)findViewById(R.id.od_tv_tprice_txt);
        dsct = (TextView)findViewById(R.id.od_tv_dsct);
        dsctTxt = (TextView)findViewById(R.id.od_tv_dsct_txt);
        price = (TextView)findViewById(R.id.od_tv_price);
        priceTxt = (TextView)findViewById(R.id.od_tv_price_txt);
        remainingDays = (TextView)findViewById(R.id.od_tv_rd);

        //Calculate remainigDays
        ExpiryTime expt= new ExpiryTime();
        long expiryTime = expt.getTimeDiference(bundle.getString(Config.TAG_GO_DATETIMEFIN));
        countDownTimer = new CountDownTimer(expiryTime,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished/1000;
                    long minutes = seconds/60;
                    long hours = minutes/60;
                    long days= hours/24;

                    String time= days + " " + "días" + " " + hours % 24 + "h:" + (minutes % 60) + "m:" + seconds % 60+"s";

                    if (days==0){
                        time= hours % 24 + "h:" + minutes % 60 + "m:" + seconds % 60+"s";
                        if (hours<1){
                            time= minutes % 60 + "m:" + seconds % 60+"s";
                            remainingDays.setTextColor(Color.parseColor("#FF0000"));
                        }
                        remainingDays.setText(time);

                    }
                    else if (days==1){
                        time =days + " " + "día" + " " + hours % 24 + "h:" + (minutes % 60) + "m:" + seconds % 60+"s";
                    }
                    remainingDays.setText(time);

                }

                @Override
                public void onFinish() {
                    remainingDays.setText("Expirada");
                    remainingDays.setTextColor(Color.parseColor("#FF0000"));
                }
        }.start();


        // Text
        priceTxt.setText(getResources().getString(R.string.od_tv_price_txt));
        tPriceTxt.setText(getResources().getString(R.string.od_tv_tprice_txt));

        // From offer
        title.setText(bundle.getString(Config.TAG_GO_TITLE));
        company.setText(bundle.getString(Config.TAG_GO_COMPANY));
        description.setText(bundle.getString(Config.TAG_GO_DESCRIPTION));
        tPrice.setText("$"+String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GO_TOTALPRICE)));
        price.setText("$"+String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GO_PRICE)));
        int perc = (bundle.getInt(Config.TAG_GO_TOTALPRICE) != 0) ?
                bundle.getInt(Config.TAG_GO_PRICE)*100/bundle.getInt(Config.TAG_GO_TOTALPRICE):
                100;
        // If offer price is greater than total price
        String s = "";
        if (perc == 100){
            dsctTxt.setText("");
        }
        else if (perc > 100){
            // Red color
            dsctTxt.setText(getResources().getString(R.string.od_tv_increase_txt));
            dsctTxt.setTextColor(ContextCompat.getColor(context, R.color.red));
            s = "+";
            dsct.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
        else{
            s = "-";
        }
        if(!s.equals("")){
            dsct.setText(s+String.valueOf(Math.abs(100-perc))+"%");
        }
        else dsct.setText("");

        // Floating Action Button
        floatingButton = (FloatingActionButton) findViewById(R.id.floating_button);
        if (!getIntent().hasExtra(Config.TAG_GO_NO_RESERVE_OPTION)) {

            floatingButton.setScaleX(0);
            floatingButton.setScaleY(0);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                final Interpolator interpolador = AnimationUtils.loadInterpolator(getApplicationContext(),
                        android.R.interpolator.fast_out_slow_in);

                floatingButton.animate()
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
                                floatingButton.animate()
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
            floatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create custom dialog.
                    final CustomAlertDialogBuilder dialogBuilder = new CustomAlertDialogBuilder(context);
                    dialogBuilder.setTitle(R.string.offer_detail_reserve);
                    dialogBuilder.setMessage(R.string.offer_detail_quantity);

                    // Create number picker (can be seek bar) into dialog
                    numberPicker = new NumberPicker(dialogBuilder.getContext());

                    final Bundle bundle = getIntent().getExtras();

                    // By default the min value to reserve is 1 (cause the offer only is displayed when exist at least one)
                    numberPicker.setMinValue(1);
                    // Verify if the stock is greater than max per person, otherwise stock is a upper bound
                    int quantity =
                            (bundle.getInt(Config.TAG_GO_MAXXPER) <= bundle.getInt(Config.TAG_GO_STOCK)) ?
                                    bundle.getInt(Config.TAG_GO_MAXXPER) : bundle.getInt(Config.TAG_GO_STOCK);
                    numberPicker.setMaxValue(quantity);
                    numberPicker.setValue(1);

                    // Options will not be repeated infinitely
                    numberPicker.setWrapSelectorWheel(false);

                    // To no show number keys
                    numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                    dialogBuilder.setView(numberPicker);
                    dialogBuilder.setPositiveButton(R.string.offer_detail_reserve, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Reservar
                            sendGetRequest(bundle);
                        }
                    });
                    // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
                    dialogBuilder.setNegativeButton(R.string.my_reservations_offers_validate_negativeText, null);

                    // (optional) set whether to dismiss dialog when touching outside
                    dialogBuilder.setCanceledOnTouchOutside(false);

                    // Show the dialog
                    dialogBuilder.show();
                }
            });
        }
        else {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) floatingButton.getLayoutParams();
            p.setBehavior(null); //should disable default animations
            p.setAnchorId(View.NO_ID); //should let you set visibility
            floatingButton.setLayoutParams(p);
            floatingButton.setVisibility(View.INVISIBLE); // View.INVISIBLE might also be worth trying
        }

        /*if (getIntent().hasExtra(Config.TAG_GO_RESERVE_OPTION)){
            //floatingButton.setVisibility(View.INVISIBLE);
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) floatingButton.getLayoutParams();
            p.setBehavior(null); //should disable default animations
            p.setAnchorId(View.NO_ID); //should let you set visibility
            floatingButton.setLayoutParams(p);
            floatingButton.setVisibility(View.GONE); // View.INVISIBLE might also be worth trying
        }*/

        getProducts();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getProducts(){
        sendPostRequest();
    }

    private void sendPostRequest(){
        class OfferDetailAsyncTask extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }
            @Override
            protected String doInBackground(String... params) {
                RequestHandler rh = new RequestHandler();

                // Notify that the user saw the offer
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_RESERVE_OFFER_ID, params[0]);
                hashMap.put(Config.KEY_RESERVE_PERSON_ID, idPersona);
                rh.sendPostRequest(Config.URL_OFFER_SAW, hashMap);

                // Get offer detail
                return rh.sendGetRequest(Config.URL_GOD+params[1]+params[0]);
            }
            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                showProducts(s);
            }
        }
        OfferDetailAsyncTask go = new OfferDetailAsyncTask();
        go.execute(idOffer, "?idOferta=");
    }

    private void showProducts(String s){
        getProductsData(s);
        adapter = new ProductAdapter(context, productList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);
    }

    private void getProductsData(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonProductsOffer = jsonObject.optJSONArray(Config.TAG_GOD_PRODUCT);
            productList= new ArrayList<>();

            for (int i=0;i<jsonProductsOffer.length();i++){
                JSONObject jsonObjectItem = jsonProductsOffer.getJSONObject(i);
                ProductModel item = new ProductModel();

                item.setName(jsonObjectItem.getString(Config.TAG_GOD_NAME));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GOD_DESCRIPTION));
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GOD_PRICE));
                item.setImage(jsonObjectItem.getString(Config.TAG_GOD_IMAGE));
                productList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // AsyncTask that send a request to the server
    private void sendGetRequest(final Bundle bundle) {

        final String quantity = Integer.toString(numberPicker.getValue());

        // First are params to doInBackground and last are params that returns
        class Reserve extends AsyncTask<Bundle, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,
                        getResources().getString(R.string.reserving),
                        getResources().getString(R.string.wait), false, false);
            }

            @Override
            protected String doInBackground(Bundle... params) {
                RequestHandler rh = new RequestHandler();

                // TODO: no entiendo muy bien qué hace esta función
                Boolean connectionStatus = rh.isConnectedToServer(rv, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendGetRequest(bundle);
                    }
                });

                if (connectionStatus) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(Config.KEY_RESERVE_OFFER_ID, bundle.getString(Config.TAG_GO_OFFER_ID));

                    hashMap.put(Config.KEY_RESERVE_PERSON_ID, idPersona);

                    hashMap.put(Config.KEY_RESERVE_QUANTITY, quantity);

                    // Date and time is getting directly for SQL, the next line is unnecessary
                    //hashMap.put(Config.KEY_RESERVE_RESERVE_DATE, date);

                    return rh.sendPostRequest(Config.URL_OFFER_RESERVE, hashMap);
                }
                else
                    return "-1";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equals("0")) {
                    c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    c.vibrate(500);
                    Toast.makeText(context, R.string.reserve_ok, Toast.LENGTH_SHORT).show();
                    finish();
                } else if (!s.equals("-1")) {
                    c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    c.vibrate(500);
                    Toast.makeText(context, R.string.operation_fail, Toast.LENGTH_SHORT).show();
                }
            }
        }
        Reserve r = new Reserve();
        r.execute(bundle);
    }

}
