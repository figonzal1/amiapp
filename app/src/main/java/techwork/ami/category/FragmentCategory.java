package techwork.ami.Category;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


public class FragmentCategory extends Fragment {

    //Campos necesarios para el funcionamiento del fragment
    private CategoryAdapter adapter;
    private List<CategoryModel> categoryList;
    private RecyclerView rv;

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

        //Seteamos el layout que usara el recycle view
        GridLayoutManager layout = new GridLayoutManager(getActivity(), 2);
        rv.setLayoutManager(layout);

        //Seteamos el adapter y le pasamos categoryList vacio, para evitar un error skipLayout
        categoryList = new ArrayList<CategoryModel>();
        adapter = new CategoryAdapter(getActivity(),categoryList);
        rv.setAdapter(adapter);

        //Llammamos a la clase que permitira realzar acciones de segundo plano.
        new CategoryAsyncTask().execute(Config.URL_GET_CATEGORY);
        return v;
    }

    //Clase que realiza operaciones antes,durante y despues de la carga de datos.
    class CategoryAsyncTask extends AsyncTask<String,Void,Integer> {
        ProgressDialog loading = new ProgressDialog(getActivity());


        //Se ejecuta antes de que se carguen los datos (Hacemos esperar al usuario)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getContext(),
                    getResources().getString(R.string.fetching),
                    getResources().getString(R.string.wait),false,false);
        }

        /*Clase que realiza tareas de segundo plano y
        llama a otra la clase categoryData que obtendra los datos de la BD.*/
        @Override
        protected Integer doInBackground(String... strings) {
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
                    getCategoryData(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("Recycle view",e.getLocalizedMessage());
            }
            return result;
        }

        //Clase que realiza operaciones despues de cargar datos. Carga los datos en el adapter de RecycleView.
        @Override
        protected void onPostExecute(Integer result) {
            loading.dismiss();

            if(result==1){
                adapter = new CategoryAdapter(getActivity(),categoryList);
                rv.setAdapter(adapter);
                adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
                    @Override
                    public void onItemClick(View view) {
                        Intent intent = new Intent(getActivity(), CategoryView.class);
                        int position= rv.getChildAdapterPosition(view);
                        CategoryModel c = categoryList.get(position);

                        intent.putExtra(Config.TAG_GC_NAME,c.getName());
                        intent.putExtra(Config.TAG_GC_ID,c.getId());
                        intent.putExtra(Config.TAG_GC_IMAGE,c.getImage());
                        startActivity(intent);
                    }
                });
            }else {
                Toast.makeText(getActivity(),"Failed to fetch data!",Toast.LENGTH_SHORT).show();
            }
        }
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