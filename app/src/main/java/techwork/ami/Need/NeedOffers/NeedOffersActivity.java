package techwork.ami.Need.NeedOffers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

import techwork.ami.Config;
import techwork.ami.R;

public class NeedOffersActivity extends AppCompatActivity {


    private NeedOfferAdapter adapter;
    private List<NeedOfferModel> needOfferList;
    private RecyclerView rv;
    private GridLayoutManager layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_offers_list_view);




    }
}
