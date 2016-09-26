package techwork.ami.Need.MyNeeds;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import techwork.ami.Config;
import techwork.ami.Need.NeedOffer.NeedOfferActivity;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;


public class FragmentNeed extends Fragment {

    private NeedAdapter adapter;
    private List<NeedModel> needList;
    private RecyclerView rv;
    private GridLayoutManager layout;
    private SwipeRefreshLayout refreshLayout;

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
        adapter= new NeedAdapter(getActivity(),needList);
    }

    //Listo
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.need_fragment,container,false);
        rv = (RecyclerView)v.findViewById(R.id.recycler_view_need);
        rv.setHasFixedSize(true);

        //1 cardviews in portrait mode.
        if (getResources().getConfiguration().orientation==1){
            layout = new GridLayoutManager(getActivity(), 1);
        }
        //2 cardviews per row in landscape view
        else if (getResources().getConfiguration().orientation==2){
            layout = new GridLayoutManager(getActivity(), 2);
        }
        rv.setLayoutManager(layout);

        refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh_need);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNeeds();
            }
        });

        getNeeds();

        Button button = (Button) v.findViewById(R.id.btn_create_order);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), NeedActivity.class);
                getActivity().startActivity(intent);
            }
        });

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
                refreshLayout.setRefreshing(true);
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(rv, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendPostRequest();
                    }
                });

                if (connectionStatus) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(Config.KEY_GN_ID, id);

                    return rh.sendPostRequest(Config.URL_GET_NEED, hashMap);
                }
                else
                    return "-1";
            }

            @Override
            protected  void onPostExecute(String s){
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                if (!s.equals("-1"))
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
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);

        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(View view) {
                Intent intent = new Intent(getActivity(),NeedOfferActivity.class);
                int position = rv.getChildAdapterPosition(view);
                NeedModel n = needList.get(position);
                intent.putExtra(Config.TAG_GN_IDNEED,n.getIdNeed());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view) {
                //Nada
            }
        });

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
                item.setOffersCompany(jsonObjectItem.getString(Config.TAG_GN_OFFERS_COMPANY));

                needList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
