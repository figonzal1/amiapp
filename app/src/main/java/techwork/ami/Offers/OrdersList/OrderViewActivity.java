package techwork.ami.Offers.OrdersList;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import techwork.ami.Config;
import techwork.ami.ExpiryTime;
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

        //Get info of parent activity
        Bundle bundle = getIntent().getExtras();

        tvTittle = (TextView)findViewById(R.id.tv_order_view_tittle);
        tvDescription =(TextView)findViewById(R.id.tv_order_view_description);
        tvExpiry=(TextView)findViewById(R.id.tv_order_view_expiry);
        tvPrice=(TextView)findViewById(R.id.tv_order_view_price);
        tvSubcategory=(TextView)findViewById(R.id.tv_order_view_subcategory);

        tvTittle.setText(bundle.getString(Config.TAG_GET_ORDER_TITTLE));
        tvDescription.setText(bundle.getString(Config.TAG_GET_ORDER_DESCRIPTION));
        tvSubcategory.setText(bundle.getString(Config.TAG_GET_ORDER_SUBCATEGORY));
        tvPrice.setText(getApplicationContext().getResources().getString(R.string.OrderViewPrice2)+" "+String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GET_ORDER_PRICEMAX)));

        ExpiryTime expt = new ExpiryTime();
        long expiryTime = expt.getTimeDiference(bundle.getString(Config.TAG_GET_ORDER_EXPIRATIONDATE));

        countDownTimer = new CountDownTimer(expiryTime, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                String time = days + " " + "días" + " " + hours % 24 + "h:" + (minutes % 60) + "m:" + seconds % 60+"s";

                if (days==0){
                    time= hours % 24 + "h:" + minutes % 60 + "m:" + seconds % 60+"s";
                    if (hours<1){
                        time= minutes % 60 + "m:" + seconds % 60+"s";
                        tvExpiry.setTextColor(Color.parseColor("#FF0000"));
                    }
                    tvExpiry.setText(time);

                }
                else if (days==1){
                    time =days + " " + "día" + " " + hours % 24 + "h:" + (minutes % 60) + "m:" + seconds % 60+"s";
                }
                tvExpiry.setText(getApplicationContext().getResources().getString(R.string.OrderViewExpiryTime2)+time);
            }

            public void onFinish() {
                tvExpiry.setText(R.string.OfferExpiredShort);
                tvExpiry.setTextColor(Color.parseColor("#FF0000"));
            }
        }.start();
    }
}
