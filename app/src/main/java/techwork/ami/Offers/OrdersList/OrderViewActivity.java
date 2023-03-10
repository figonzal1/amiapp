package techwork.ami.Offers.OrdersList;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import techwork.ami.Config;
import techwork.ami.ExpiryTime;
import techwork.ami.MainActivity;
import techwork.ami.R;

public class OrderViewActivity extends AppCompatActivity {

    TextView tvTittle,tvDescription,tvSubcategory,tvPrice,tvExpiry;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_view_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //arrow back refresh main activity.
                finish();
            }
        });


        //Get info of parent activity
        Bundle bundle = getIntent().getExtras();

        tvTittle = (TextView)findViewById(R.id.tv_order_view_tittle);
        tvDescription =(TextView)findViewById(R.id.tv_order_view_description);
        tvExpiry=(TextView)findViewById(R.id.tv_order_view_expiry);
        tvPrice=(TextView)findViewById(R.id.tv_order_view_price);
        tvSubcategory=(TextView)findViewById(R.id.tv_order_view_subcategory);

        if (bundle.getString("CallFrom").equals("CreateOrderActivity")){


            tvTittle.setText(bundle.getString(Config.TAG_GET_ORDER_TITTLE));
            tvDescription.setText(bundle.getString(Config.TAG_GET_ORDER_DESCRIPTION));
            tvSubcategory.setText(bundle.getString(Config.TAG_GET_ORDER_SUBCATEGORY));
            tvPrice.setText(getApplicationContext().getResources().getString(R.string.OrderViewPrice2)+" "+String.format(Config.CLP_FORMAT,Integer.valueOf(bundle.getString(Config.TAG_GET_ORDER_PRICEMAX))));

            //If intent is come to CreateOrder, expiry show days

            //If days =0
            if (Integer.valueOf(bundle.getString(Config.TAG_GET_ORDER_EXPIRATIONDATE))==0){
                tvExpiry.setText(getApplicationContext().getResources().getString(R.string.OrderViewExpiryTodayCreateOrder));
            }
            else if(Integer.valueOf(bundle.getString(Config.TAG_GET_ORDER_EXPIRATIONDATE))==1){
                tvExpiry.setText(getApplicationContext().getResources().getString(R.string.OrderViewExpiryCreateOrder)+bundle.getString(Config.TAG_GET_ORDER_EXPIRATIONDATE)+" d??a");
            }
            else if (Integer.valueOf(bundle.getString(Config.TAG_GET_ORDER_EXPIRATIONDATE))>1){
                tvExpiry.setText(getApplicationContext().getResources().getString(R.string.OrderViewExpiryCreateOrder)+bundle.getString(Config.TAG_GET_ORDER_EXPIRATIONDATE)+" d??as");
            }

        }
        else if (bundle.getString("CallFrom").equals("FragmentOrder")){

            tvTittle.setText(bundle.getString(Config.TAG_GET_ORDER_TITTLE));
            tvDescription.setText(bundle.getString(Config.TAG_GET_ORDER_DESCRIPTION));
            tvSubcategory.setText(bundle.getString(Config.TAG_GET_ORDER_SUBCATEGORY));
            tvPrice.setText(getApplicationContext().getResources().getString(R.string.OrderViewPrice2)+" "+String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GET_ORDER_PRICEMAX)));

            //If intent is come to Fragment, expiry show countdownTimer
            ExpiryTime expt = new ExpiryTime();
            long expiryTime = expt.getTimeDiference(bundle.getString(Config.TAG_GET_ORDER_EXPIRATIONDATE));

            countDownTimer = new CountDownTimer(expiryTime, 1000) {
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;

                    String time = days + " " + "d??as" + " " + hours % 24 + "h:" + (minutes % 60) + "m:" + seconds % 60+"s";

                    if (days==0){
                        time= hours % 24 + "h:" + minutes % 60 + "m:" + seconds % 60+"s";
                        if (hours<1){
                            time= minutes % 60 + "m:" + seconds % 60+"s";
                            tvExpiry.setTextColor(Color.parseColor("#FF0000"));
                        }
                        tvExpiry.setText(time);

                    }
                    else if (days==1){
                        time =days + " " + "d??a" + " " + hours % 24 + "h:" + (minutes % 60) + "m:" + seconds % 60+"s";
                    }
                    tvExpiry.setText(getApplicationContext().getResources().getString(R.string.OrderViewExpiryFragmentOrder)+time);
                }

                public void onFinish() {
                    tvExpiry.setText(R.string.OfferExpiredShort);
                    tvExpiry.setTextColor(Color.parseColor("#FF0000"));
                }
            }.start();

        }
    }
}

