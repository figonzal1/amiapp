package techwork.ami;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import techwork.ami.Offer.OfferAdapter;
import techwork.ami.Offer.OfferModel;
import techwork.ami.Offer.OfferView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@ link FragmentHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {

    // Required for fragment use
    private OfferAdapter adapter;
    private List<OfferModel> offerList;
    private RecyclerView rv;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view, find recycle view in layouts
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        rv = (RecyclerView) v.findViewById(R.id.recycler_view_offer);
        rv.setHasFixedSize(true);

        // Set the layout that will use recycle view
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layout);

        // TODO: es posible borrar esto
        // Set the adapter, pass empty object offers (not necessary, cause set this in onPostExecute
        //offerList = new ArrayList<OfferModel>();
        //adapter = new OfferAdapter(getContext(),offerList);
        //rv.setAdapter(adapter);

        // Set class to execute async task
        // TODO: actualizar cuando se mejore la DB (CAMBIAR DE OFFER2 A 1 para el otro select por ej)
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
            ProgressDialog loading = new ProgressDialog(getActivity());

            // Execute before load data (user waiting)
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // Pop up waiting (for user)
                loading = ProgressDialog.show(getContext(), getResources().getString(R.string.fetching), getResources().getString(R.string.wait), false, false);
            }

            // Class that execute background task (get BD data).
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequest(Config.URL_GET_OFFERS);
                /* TODO
                Integer result = 0;
                HttpURLConnection urlConnection;
                try {
                    URL url = new URL(strings[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    int statusCode = urlConnection.getResponseCode();

                    if (statusCode == 200) {
                        BufferedReader r = new BufferedReader((new InputStreamReader(urlConnection.getInputStream())));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            response.append(line);
                        }
                        // Get data from DB
                        getData(response.toString());
                        // Successful
                        result = 1;
                    } else {
                        // Failed to fetch data!;
                        result = 0;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.d("Recycle view",e.getLocalizedMessage());
                }*/
            }

            // Do operations after load data from DB.
            // Put data in RecycleView Adapter.
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showOffers(s);
            }
        }
        MyAsyncTask go = new MyAsyncTask();
        go.execute();
    }

    private void showOffers(String s){
        getData(s);
        adapter = new OfferAdapter(getActivity(), offerList);
        rv.setAdapter(adapter);
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
        });
    }

    //Clase que itera sobre el json array para obtener datos de la BD.
    private void getData(String json){
        String dIni,dFin;
        Date dateIni,dateFin;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try{

            JSONObject jsonObject = new JSONObject(json);
            //CAMBIAR OFFER2 a OFFER para acceder al otro select
            JSONArray jsonOffers = jsonObject.optJSONArray(Config.TAG_GO_OFFERS);
            Calendar c = Calendar.getInstance();
            offerList = new ArrayList<>();

            for(int i=0;i<jsonOffers.length();i++){

                JSONObject jsonObjectItem = jsonOffers.optJSONObject(i);
                OfferModel item = new OfferModel();

                dIni =jsonObjectItem.getString(Config.TAG_GO_DATEFIN);
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
                //item.setCompany(jsonObjectItem.getString(Config.TAG_GO_COMPANY));
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

