package techwork.ami.Offer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import techwork.ami.Config;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;


public class FragmentHome extends Fragment {

    // Required for fragment use
    private OfferAdapter adapter;
    private List<OfferModel> offerList;
    private RecyclerView rv;
    private LinearLayoutManager layout;
    private SwipeRefreshLayout refreshLayout;

    // Empty constructor (default)
    public FragmentHome() {
        // Required empty public constructor
    }

    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter= new OfferAdapter(getActivity(),offerList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view, find recycle view in layouts
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        rv = (RecyclerView) v.findViewById(R.id.recycler_view_offer);
        rv.setHasFixedSize(true);

        //Evita el error de skipping layout
        adapter= new OfferAdapter(getActivity(),offerList);

        // Set the layout that will use recycle view
        layout = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layout);

        //Refreshing layout
        refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh_offer);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOffers();
            }
        });

        getOffers();
        return v;
    }

    // Call to DB
    private void getOffers(){
        sendGetRequest();
    }

    private void sendGetRequest(){
        // Execute operations before, durign and after of data load
        class MyAsyncTask extends AsyncTask<Void,Void,String> {


            // Execute before load data (user waiting)
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }

            // Class that execute background task (get BD data).
            @Override
            protected String doInBackground(Void... strings) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequest(Config.URL_GET_OFFERS);
            }

            // Do operations after load data from DB.
            // Put data in RecycleView Adapter.
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                showOffers(s);
            }
        }
        MyAsyncTask go = new MyAsyncTask();
        go.execute();
    }

    private void showOffers(String s){
        getData(s);
        adapter = new OfferAdapter(getActivity(), offerList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);

        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(View view) {
                Intent intent = new Intent(getActivity(), OfferView.class);
                int position = rv.getChildAdapterPosition(view);
                OfferModel o = offerList.get(position);
                intent.putExtra(Config.TAG_GO_TITLE, o.getTitle());
                intent.putExtra(Config.TAG_GO_IMAGE, o.getImage());
                intent.putExtra(Config.TAG_GO_DESCRIPTION, o.getDescription());
                intent.putExtra(Config.TAG_GO_PRICE, o.getPrice());
                intent.putExtra(Config.TAG_GO_OFFER_ID, o.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("¿Desea descartar esta oferta?")
                        .setMessage("Confirme la acción")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with discard
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    //Clase que itera sobre el json array para obtener datos de la BD.
    private void getData(String json){
        String dIni,dFin;
        Date dateIni,dateFin;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try{

            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonOffers = jsonObject.optJSONArray(Config.TAG_GO_OFFERS);
            Calendar c = Calendar.getInstance();
            offerList = new ArrayList<>();

            for(int i=0;i<jsonOffers.length();i++){

                JSONObject jsonObjectItem = jsonOffers.optJSONObject(i);
                OfferModel item = new OfferModel();

                dIni =jsonObjectItem.getString(Config.TAG_GO_DATEINI);
                dFin = jsonObjectItem.getString(Config.TAG_GO_DATEFIN);
                dateIni= format.parse(dIni);
                dateFin =format.parse(dFin);

                item.setId(jsonObjectItem.getString(Config.TAG_GO_OFFER_ID));
                item.setTitle(jsonObjectItem.getString(Config.TAG_GO_TITLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GO_DESCRIPTION));
                item.setStock(jsonObjectItem.getInt(Config.TAG_GO_STOCK));
                item.setPromotionCode(Config.TAG_GO_PROMCOD);
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GO_PRICE));

                c.setTime(dateIni);
                item.setInitialDate(String.format(Locale.US, Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                c.setTime(dateFin);
                item.setFinalDate(String.format(Locale.US, Config.DATE_FORMAT, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR)));

                item.setMaxPPerson(jsonObjectItem.getInt(Config.TAG_GO_MAXXPER));
                item.setCompany(jsonObjectItem.getString(Config.TAG_GO_COMPANY));
                item.setImage(jsonObjectItem.getString(Config.TAG_GO_IMAGE));

                offerList.add(item);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}

