package techwork.ami.Need;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import techwork.ami.R;

public class NeedOffersList extends AppCompatActivity {

    private RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_offers_list_view);

        //rv = (RecyclerView)findViewById(R.id.recyler_view_need_offer_list);

    }
}
