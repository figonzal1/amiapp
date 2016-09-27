package techwork.ami.Need.NeedOffer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import techwork.ami.Config;
import techwork.ami.R;

public class NeedOfferViewActivity extends AppCompatActivity {

    TextView tvTittle;
    TextView tvPrice;
    Button btnAccept;
    Button btnDiscard;
    String idOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_offer_view_activity);

        tvTittle = (TextView)findViewById(R.id.tv_need_offer_view_tittle);
        tvPrice = (TextView)findViewById(R.id.tv_need_offer_view_price);
        btnAccept = (Button)findViewById(R.id.btn_need_offer_view_accept);
        btnDiscard= (Button)findViewById(R.id.btn_need_offer_view_discard);

        Bundle bundle = getIntent().getExtras();

        idOffer = bundle.getString(Config.TAG_GNO_IDOFFER);
        tvTittle.setText(bundle.getString(Config.TAG_GNO_TITTLE));
        tvPrice.setText("$"+String.format(Config.CLP_FORMAT,bundle.getInt(Config.TAG_GNO_PRICEOFFER)));

    }
}
