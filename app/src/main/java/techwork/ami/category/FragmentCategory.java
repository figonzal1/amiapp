package techwork.ami.Category;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import techwork.ami.Config;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;


public class FragmentCategory extends Fragment {

    //Campos necesarios para el funcionamiento del fragment
    private CategoryAdapter adapter;
    private List<CategoryModel> categoryList;
    private RecyclerView rv;
    private GridLayoutManager layout;
    private SwipeRefreshLayout refreshLayout;

    public FragmentCategory() {
        // Required empty public constructor
    }

    public static FragmentCategory newInstance() {
        FragmentCategory fragment = new FragmentCategory();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //Inflamos el view y buscamos el recycle view en los layouts
        View v = inflater.inflate(R.layout.category_fragment, container, false);
        rv = (RecyclerView) v.findViewById(R.id.recycler_view_category);
        rv.setHasFixedSize(true);

        //Evita el error de skipping layout
        //adapter= new CategoryAdapter(getActivity(),categoryList);

        // Category list in normal size screen. (Tablet Screen )
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Toast.makeText(getContext(), "Large screen", Toast.LENGTH_LONG).show();
            //3 cardviews per row in portrait view
            if (getResources().getConfiguration().orientation==1){
                layout = new GridLayoutManager(getActivity(), 3);
            }
            //4 cardviews per row in landscape view
            else if (getResources().getConfiguration().orientation==2){
                layout = new GridLayoutManager(getActivity(), 4);
            }
        }

        // Category list in normal size screen. (Phone Screen )
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            Toast.makeText(getContext(), "Normal sized screen", Toast.LENGTH_LONG).show();

            //2 cardviews per row in portrait view
            if (getResources().getConfiguration().orientation==1){
                layout = new GridLayoutManager(getActivity(), 2);
            }
            //3 cardviews per row in landscape view
            else if (getResources().getConfiguration().orientation==2){
                layout = new GridLayoutManager(getActivity(), 3);
            }
        }
        rv.setLayoutManager(layout);

        // Refreshing fragment
        refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh_category);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCategories();
            }
        });

        //Llamamos a la clase que permitira realzar acciones de segundo plano.
        getCategories();
        return v;
    }

    private void getCategories() {
        sendGetRequest();
    }

    private void sendGetRequest() {

        //Clase que realiza operaciones antes,durante y despues de la carga de datos.
        class CategoryAsyncTask extends AsyncTask<Void, Void, String> {
            ProgressDialog loading = new ProgressDialog(getActivity());


            //Se ejecuta antes de que se carguen los datos (Hacemos esperar al usuario)
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            /*Clase que realiza tareas de segundo plano y
            llama a otra la clase categoryData que obtendra los datos de la BD.*/
            @Override
            protected String doInBackground(Void... strings) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequest(Config.URL_GET_CATEGORY);

            }

            //Clase que realiza operaciones despues de cargar datos. Carga los datos en el adapter de RecycleView.
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                showCategories(s);
            }
        }

        CategoryAsyncTask go = new CategoryAsyncTask();
        go.execute();
    }

    //Clase que muestra los datos en el recycler view y realiza el listener del click
    private void showCategories(String s){
        getCategoryData(s);
        adapter= new CategoryAdapter(getActivity(),categoryList);
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(View view) {
                Intent intent = new Intent(getActivity(),CategoryView.class);
                int position = rv.getChildAdapterPosition(view);
                CategoryModel c = categoryList.get(position);

                intent.putExtra(Config.TAG_GC_ID,c.getId());
                intent.putExtra(Config.TAG_GC_NAME,c.getName());
                intent.putExtra(Config.TAG_GC_IMAGE,c.getImage());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view) {
                Toast.makeText(getContext(),"Long click",Toast.LENGTH_LONG).show();
            }
        });

    }

    //Clase que itera sobre el json array para obtener datos de la BD.
    private void getCategoryData(String json){
        try{

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonCategories = jsonObject.optJSONArray(Config.TAG_GC_CATEGORY);
            categoryList = new ArrayList<>();

            for(int i=0;i<jsonCategories.length();i++){

                JSONObject jsonObjectItem = jsonCategories.optJSONObject(i);
                CategoryModel item = new CategoryModel();


                item.setName(jsonObjectItem.getString(Config.TAG_GC_NAME));
                item.setId(jsonObjectItem.getInt(Config.TAG_GC_ID));
                item.setImage(jsonObjectItem.getString(Config.TAG_GC_IMAGE));

                categoryList.add(item);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}