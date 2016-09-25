package techwork.ami.Need.MyNeeds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import techwork.ami.Config;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class NeedOfferActivity extends AppCompatActivity {

    private RecyclerView rv;
    private List<NeedOfferModel> needOfferList;
    private NeedOfferAdapter adapter;
    private GridLayoutManager layout;
    private String idNeedOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_offer_activity);

        Bundle bundle = getIntent().getExtras();
        idNeedOffer = bundle.getString(Config.TAG_GNO_IDNEED);

        rv = (RecyclerView)findViewById(R.id.recycler_view_need_offer);
        rv.setHasFixedSize(true);

        layout= new GridLayoutManager(this,1);
        rv.setLayoutManager(layout);

        getNeedOffers();
    }

    private void getNeedOffers(){
        sendPostRequest();
    }

    private void sendPostRequest(){

        SharedPreferences sharedPref= getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        final String id = sharedPref.getString(Config.KEY_SP_ID,"-1");

        class NeedOfferAsyncTask extends AsyncTask<Void,Void,String>{

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashmap = new HashMap<>();
                hashmap.put(Config.KEY_GNO_IDPERSON,id);
                hashmap.put(Config.KEY_GNO_IDNEED,idNeedOffer);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Config.URL_GET_NEED_OFFER,hashmap);
            }
            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                showNeedOffers(s);
            }
        }
        NeedOfferAsyncTask go = new NeedOfferAsyncTask();
        go.execute();
    }

    private void showNeedOffers(String s){
        getNeedOffersData(s);

        adapter = new NeedOfferAdapter(getApplicationContext(),needOfferList);
        rv.setAdapter(adapter);
    }


    private void getNeedOffersData(String json){
        String dIni,dFin;
        Date dateIni,dateFin;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonNeedOffer = jsonObject.optJSONArray(Config.TAG_GNO_NEED);
            Calendar c = Calendar.getInstance();
            needOfferList = new ArrayList<>();

            for (int i=0;i<jsonNeedOffer.length();i++){
                JSONObject jsonObjectItem = jsonNeedOffer.getJSONObject(i);
                NeedOfferModel item = new NeedOfferModel();

                item.setIdOferta(jsonObjectItem.getString(Config.TAG_GNO_IDOFFER));
                item.setIdNeed(jsonObjectItem.getString(Config.TAG_GNO_IDNEED));
                item.setTittle(jsonObjectItem.getString(Config.TAG_GNO_TITTLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GNO_DESCRIPTION));
                item.setCodPromotion(jsonObjectItem.getString(Config.TAG_GNO_CODPROMOTION));
                item.setStock(jsonObjectItem.getString(Config.TAG_GNO_STOCK));
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GNO_STOCK));
                item.setMaxPPerson(jsonObjectItem.getString(Config.TAG_GNO_MAXPPERSON));

                dIni = jsonObjectItem.getString(Config.TAG_GNO_DATEINI);
                dFin = jsonObjectItem.getString(Config.TAG_GNO_DATEFIN);
                dateIni= format.parse(dIni);
                dateFin = format.parse(dFin);

                c.setTime(dateIni);
                item.setDateIni(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));
                c.setTime(dateFin);
                item.setDateFin(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                needOfferList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
