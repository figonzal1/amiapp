package techwork.ami.Need;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import techwork.ami.Config;
import techwork.ami.R;

public class NeedOffersList extends AppCompatActivity {
    TextView tvId;
    TextView tvTittle;


    private RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_offers_list_view);

        tvId = (TextView)findViewById(R.id.tv_need_offer_list_id);
        tvTittle= (TextView)findViewById(R.id.tv_need_offer_list_tittle);

        Bundle bundle = getIntent().getExtras();
        tvId.setText(bundle.getString(Config.TAG_GN_IDNEED));
        tvTittle.setText(bundle.getString(Config.TAG_GN_TITTLE));

    }
}
