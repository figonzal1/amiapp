package techwork.ami.Promotion.OfferDetail;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import techwork.ami.Config;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class OfferDetailOld extends AppCompatActivity {
    // UI references
    private TextView offerTitle, offerDescription,offerPrice;
    private ImageView offerImage;
    private NumberPicker numberPicker;
    private Button btnReserve;
    private String idPersona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_detail_activity);

        // if activity is not create yet, get the references
        if (null == btnReserve) {
            offerTitle = (TextView) findViewById(R.id.viewOfferTitle);
            offerDescription = (TextView) findViewById(R.id.viewOfferDescription);
            offerPrice = (TextView) findViewById(R.id.viewOfferPrice);
            offerImage = (ImageView) findViewById(R.id.viewOfferImage);
            numberPicker = (NumberPicker) findViewById(R.id.viewOfferNumberPicker);

            final Bundle bundle = getIntent().getExtras();
            offerTitle.setText(bundle.getString(Config.TAG_GO_TITLE));
            offerDescription.setText(bundle.getString(Config.TAG_GO_DESCRIPTION));
            offerPrice.setText("$" + String.format(Config.CLP_FORMAT, bundle.getInt(Config.TAG_GO_PRICE)));

            SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
            idPersona = sharedPref.getString(Config.KEY_SP_ID, "-1");

            // By default the min value to reserve is 1 (cause the offer only is displayed when exist at least one)
            numberPicker.setMinValue(1);
            // Verify if the stock is greater than max per person, otherwise stock is a upper bound
            int quantity =
                    (bundle.getInt(Config.TAG_GO_MAXXPER) <= bundle.getInt(Config.TAG_GO_STOCK))?
                            bundle.getInt(Config.TAG_GO_MAXXPER) : bundle.getInt(Config.TAG_GO_STOCK);
            numberPicker.setMaxValue(quantity);
            numberPicker.setValue(1);
            numberPicker.setWrapSelectorWheel(false);

            String s = bundle.getString(Config.TAG_GO_IMAGE);
            Picasso.with(this).load(Config.URL_IMAGES_OFFER + s)
                    .placeholder(R.drawable.image_default)
                    .into(offerImage);

            btnReserve = (Button) findViewById(R.id.viewOfferBtnReserve);
            btnReserve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendGetRequest(bundle);
                }
            });
            // Notify that the user saw the offer
            (new OfertaVista()).execute(bundle);
        }
    }

    // TODO: podría incorporarse un diálogo o algo que deje en espera o haga cargar
    class OfertaVista extends AsyncTask<Bundle, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(final Bundle... params) {
            RequestHandler rh = new RequestHandler();

            // TODO: quité el connection status
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Config.KEY_RESERVE_OFFER_ID, params[0].getString(Config.TAG_GO_OFFER_ID));
            hashMap.put(Config.KEY_RESERVE_PERSON_ID, idPersona);
            rh.sendPostRequest(Config.URL_OFFER_SAW, hashMap);
            return null;
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
                loading = ProgressDialog.show(OfferDetailOld.this,
                        getResources().getString(R.string.reserving),
                        getResources().getString(R.string.wait), false, false);
            }

            @Override
            protected String doInBackground(Bundle... params) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(offerTitle, new View.OnClickListener() {
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
                    Toast.makeText(OfferDetailOld.this, R.string.reserve_ok, Toast.LENGTH_SHORT).show();
                    finish();
                } else if (!s.equals("-1")) {
                    Toast.makeText(OfferDetailOld.this, R.string.operation_fail, Toast.LENGTH_SHORT).show();
                }
            }
        }
        Reserve r = new Reserve();
        r.execute(bundle);
    }
}

