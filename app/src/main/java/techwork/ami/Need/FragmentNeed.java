package techwork.ami.Need;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import techwork.ami.Config;
import techwork.ami.R;
import techwork.ami.RequestHandler;


public class FragmentNeed extends Fragment {

    private NeedAdapter adapter;
    private List<NeedModel> needList;
    private RecyclerView rv;
    private LinearLayoutManager layout;

    public FragmentNeed() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentNeed newInstance() {
        FragmentNeed fragment = new FragmentNeed();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //Listo
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.need_fragment,container,false);
        rv = (RecyclerView)v.findViewById(R.id.recycler_view_need);

        layout = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layout);

        getNeeds();
        return v;
    }

    private void getNeeds(){
        sendPostRequest();
    }

    private void sendPostRequest() {

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);

        final String id = sharedPref.getString(Config.KEY_SP_ID, "-1");


        class NeedAsyncTask extends AsyncTask<Void,Void,String>{

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_GN_ID,id);

                RequestHandler rh = new RequestHandler();
                return rh.sendPostRequest(Config.URL_GET_NEED,hashMap);
            }

            @Override
            protected  void onPostExecute(String s){
                super.onPostExecute(s);
                showNeeds(s);
            }
        }
        NeedAsyncTask go = new NeedAsyncTask();
        go.execute();
    }



    //Clase que muestra los datos en el recycler view y realiza el listener del click
    private void showNeeds(String s) {
        getNeedsData(s);

        adapter = new NeedAdapter(getActivity(),needList);
        rv.setAdapter(adapter);

    }

    //Clase que itera sobre el json array y llena el listado de necesidades.
    private void getNeedsData(String json) {

        String dExp;
        Date dateExp;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonNeed = jsonObject.optJSONArray(Config.TAG_GN_NEED);
            Calendar c = Calendar.getInstance();
            needList = new ArrayList<>();

            for (int i=0;i<jsonNeed.length();i++){
                JSONObject jsonObjectItem = jsonNeed.getJSONObject(i);
                NeedModel item = new NeedModel();

                item.setIdNeed(jsonObjectItem.getString(Config.TAG_GN_IDNEED));
                item.setIdPerson(jsonObjectItem.getString(Config.TAG_GN_IDPER));
                item.setTittle(jsonObjectItem.getString(Config.TAG_GN_TITTLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GN_DESCRIPTION));

                dExp = jsonObjectItem.getString(Config.TAG_GN_EXPIRATIONDATE);
                dateExp = format.parse(dExp);
                c.setTime(dateExp);
                item.setDateFin(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                item.setPriceMin(jsonObjectItem.getString(Config.TAG_GN_PRICEMIN));
                item.setLat(jsonObjectItem.getString(Config.TAG_GN_LATITUDE));
                item.setLon(jsonObjectItem.getString(Config.TAG_GN_LONGITUDE));
                item.setRadio(jsonObjectItem.getString(Config.TAG_GN_RADIO));

                needList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
