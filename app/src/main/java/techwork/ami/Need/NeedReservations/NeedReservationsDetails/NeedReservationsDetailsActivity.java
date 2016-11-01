package techwork.ami.Need.NeedReservations.NeedReservationsDetails;

import android.os.ConditionVariable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import techwork.ami.Config;
import techwork.ami.R;

public class NeedReservationsDetailsActivity extends AppCompatActivity {

    TextView tvCompany,tvTittle,tvDescription,tvDateIni,tvDateFin,tvPrice;
    String idOffer;
    private RecyclerView rv;
    private LinearLayoutManager layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_reservations_details_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();

        //Capture id's for get products of each offer.
        idOffer = bundle.getString(Config.TAG_GNR_IDOFFER);


        tvCompany = (TextView)findViewById(R.id.tv_need_reservations_view_company);
        tvTittle=(TextView)findViewById(R.id.tv_need_reservations_view_tittle);
        tvDescription=(TextView)findViewById(R.id.tv_need_reservations_view_description);
        tvDateFin=(TextView)findViewById(R.id.tv_need_reservations_view_date_fin);
        tvDateIni=(TextView)findViewById(R.id.tv_need_reservations_view_date_ini);
        tvPrice=(TextView)findViewById(R.id.tv_need_reservations_view_price);

        tvCompany.setText(bundle.getString(Config.TAG_GNR_COMPANY));
        tvTittle.setText(bundle.getString(Config.TAG_GNR_TITTLE));
        tvDescription.setText(bundle.getString(Config.TAG_GNR_DESCRIPTION));
        tvDateIni.setText(bundle.getString(Config.TAG_GNR_DATEINI));
        tvDateFin.setText("Fecha de publicación: "+bundle.getString(Config.TAG_GNR_DATEFIN));
        tvPrice.setText("Fecha de expiración: "+bundle.getString(Config.TAG_GNR_PRICEOFFER));

        rv = (RecyclerView)findViewById(R.id.recycler_view_need_reservations_view) ;
        rv.setHasFixedSize(true);
        layout = new LinearLayoutManager(this);
        rv.setLayoutManager(layout);




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
