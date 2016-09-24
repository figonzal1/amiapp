package techwork.ami.Need;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import techwork.ami.Config;
import techwork.ami.MainActivity;
import techwork.ami.R;
import techwork.ami.RequestHandler;


public class NeedActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    // State Keys for saving activity's instance
    static final String KEY_FORM_STATE = "formState";
    static final String KEY_SUBCATEGORIES_STATE = "subCategoriesState";
    static final String KEY_SUBCATEGORIES_NAMES = "subCategoriesNames";
    static final String KEY_SUBCATEGORIES_MAP = "subCategoriesMap";
    static final String KEY_SUBCATEGORIES_SELECTION = "subCategoriesSelection";
    static final String KEY_CATEGORIES_NAMES = "categoriesNames";
    static final String KEY_CATEGORIES_MAP = "categoriesMap";
    static final String KEY_CATEGORIES_SELECTION = "categoriesSelection";

    //Declaring an Spinner
    private Spinner spinnerCategory;
    private Spinner spinnerSubcategory;

    // Spinner arrays
    String[] categoriesNames;
    HashMap<String,String> categoriesMap;
    String[] subCategoriesNames;
    HashMap<String,String> subCategoriesMap;

    //An ArrayList for Spinner Items
    private ArrayList<String> categories;

    //JSON Array
    private JSONArray result;

    // Defines if the form is enable
    boolean formState;

    //TextViews to display details
    private TextView textViewName;
    private TextView textViewId;
    private TextView textViewTitle;

    private LocationManager locationManager;
    private EditText editTextData;
    private EditText editTextMoney;
    private EditText editTextUserTitle;

    private String Lat;
    private String Lon;
    private String user_id;
    private String user_name;
    private String user_commune;
    private String NeedLeyend;
    private String NeedRegistered;
    private String NeedError;
    Vibrator c;

    private Button buttonRegister;

    //url del php que inserta los datos de la necesidad en la BD
    public static final String ROOT_URL = Config.URL_NEW_NEED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need);

        // UI references
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        spinnerSubcategory = (Spinner) findViewById(R.id.spinnerSubcategory);
        editTextData = (EditText) findViewById(R.id.Need_editTextData);
        editTextMoney = (EditText) findViewById(R.id.Need_editTextMoney);
        editTextUserTitle = (EditText) findViewById(R.id.Need_editTextUserTitle);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        textViewTitle = (TextView) findViewById(R.id.EP_textViewTitle);

        //Nombre e id de la categoria
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewId = (TextView) findViewById(R.id.textViewId);

        //Detalles y presupuesto de la necesidad
        editTextData.setError(null);
        editTextMoney.setError(null);
        editTextUserTitle.setError(null);

        getProfileId();

        buttonRegister.setOnClickListener(this);

        setLocationSettings();

        NeedLeyend = getResources().getString(R.string.NeedLeyend);
        textViewTitle.setText(user_name + " " + NeedLeyend);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Empty dependent spinners
                spinnerSubcategory.setAdapter(null);

                // Set Enable if has been selected a valid option
                spinnerSubcategory.setEnabled(position != 0);

                // Set disable dependent button
                buttonRegister.setEnabled(false);

                // Get the options of the next spinner
                if (position != 0)
                    getData("2", categoriesMap.get(spinnerCategory.getSelectedItem().toString()));

                textViewName.setText(spinnerCategory.getSelectedItem().toString());
                textViewId.setText(categoriesMap.get(spinnerCategory.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerSubcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    buttonRegister.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // If the activity is restoring after screen rotation
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else
            // Disable the form until get the profile data
            enableForm(false);

        // If the activity is on creation (not restore after rotation)
        if (savedInstanceState == null)
            // Attempt to get the data
            getData("1", "1");
    }

    // Get the profile's id from the shared preferences
    private void getProfileId(){
        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        user_id = sharedPref.getString(Config.KEY_SP_ID, null);
        user_name = sharedPref.getString(Config.KEY_SP_NAME, null);
        user_commune = sharedPref.getString(Config.KEY_SP_COMMUNE, null);
    }

    private void restoreState(Bundle savedInstanceState) {// Set the previous state of the form
        enableForm(savedInstanceState.getBoolean(KEY_FORM_STATE));

        if (formState) {
            spinnerSubcategory.setEnabled(savedInstanceState.getBoolean(KEY_SUBCATEGORIES_STATE));

            // Set the occupations arrays
            categoriesNames = savedInstanceState.getStringArray(KEY_CATEGORIES_NAMES);
            categoriesMap = (HashMap<String, String>) savedInstanceState.getSerializable(KEY_CATEGORIES_MAP);
            setSpinnerList(spinnerCategory, categoriesNames);
            spinnerCategory.setSelection(savedInstanceState.getInt(KEY_CATEGORIES_SELECTION));

            if (spinnerSubcategory.isEnabled()) {
                // Set the regions arrays
                subCategoriesNames = savedInstanceState.getStringArray(KEY_SUBCATEGORIES_NAMES);
                subCategoriesMap = (HashMap<String, String>) savedInstanceState.getSerializable(KEY_SUBCATEGORIES_MAP);
                setSpinnerList(spinnerSubcategory, subCategoriesNames);
                spinnerSubcategory.setSelection(savedInstanceState.getInt(KEY_SUBCATEGORIES_SELECTION));
            }
        }
    }

    // Change the enable state of the views
    private void enableForm(boolean e) {
        // Update the formState member
        formState = e;

        // Update the enable state of the views
        buttonRegister.setEnabled(e);
        spinnerSubcategory.setEnabled(e);
        if (!e) {
            spinnerSubcategory.setEnabled(false);
            buttonRegister.setEnabled(false);
        }
    }

    public void setLocationSettings() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //La localizacion se actualiza cada 4 segundos, cada 0 metros de desplazamiento
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                4000, 0, this);

        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        Lat = "" + location.getLatitude();
        Lon = "" + location.getLongitude();
    }


    @Override
    public void onLocationChanged(Location location) {
        Lat=""+location.getLatitude();
        Lon=""+location.getLongitude();
    }

    //En este caso se deberia usar la localizacion que predefinio la persona
    @Override
    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "La localizacion esta desactivada",
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(getBaseContext(), "La localizacion ha sido activada",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private void insertData(){
        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter

        NeedRegistered = getResources().getString(R.string.NeedRegistered);
        NeedError = getResources().getString(R.string.NeedError);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL) //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating object for our interface
        NeedData obj = adapter.create(NeedData.class);

        System.out.println("userTitle = " + editTextUserTitle.getText().toString());
        System.out.println("Data = " + editTextData.getText().toString());
        System.out.println("Money = " + editTextMoney.getText().toString());
        System.out.println("Lat = " + Lat);
        System.out.println("Lon = " + Lon);
        System.out.println("user_id = " + user_id);
        System.out.println("subCategoria = " + subCategoriesMap.get(spinnerSubcategory.getSelectedItem().toString()));
        System.out.println("commune = " + user_commune);

        //Defining the method insertuser of our interface
        obj.insertData(
            //Valores que usara NeedData para enviar a la BD
            editTextUserTitle.getText().toString(),
            editTextData.getText().toString(),
            editTextMoney.getText().toString(),
            Lat,
            Lon,
            user_id,
            subCategoriesMap.get(spinnerSubcategory.getSelectedItem().toString()),
            user_commune,

            //Creating an anonymous callback
            new Callback<Response>() {
                @Override
                public void success(Response result, Response response) {
                    //On success we will read the server's output using bufferedreader
                    //Creating a bufferedreader object
                    BufferedReader reader = null;

                    //An string to store output from the server
                    String output = "";

                    try {
                        //Initializing buffered reader
                        reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                        //Reading the output in the string
                        output = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Analiza la respuesta del servidor
                    if(output.equals("1")) {
                        //Reproducir un sonido
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.arpeggio);
                        mp.start();

                        // Vibrar por 500 millisegundos
                        c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                        c.vibrate(500);

                        // Mensaje de respuesta del servidor
                        Toast.makeText(NeedActivity.this, NeedRegistered, Toast.LENGTH_LONG).show();

                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {

                            // Salir de la activity despues de que la necesidad haya sido registrada
                            @Override
                            public void run() {
                                startActivity(new Intent(NeedActivity.this, MainActivity.class));
                            }

                        }, 2500);

                    } else  if(output.equals("2")) {

                        Toast.makeText(NeedActivity.this, NeedError, Toast.LENGTH_LONG).show();
                        c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                        c.vibrate(500);

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    //If any error occured displaying the error as toast
                    Toast.makeText(NeedActivity.this, error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    @Override
    public void onClick(View v) {

        String TextData = editTextData.getText().toString();
        String TextMoney = editTextMoney.getText().toString();
        String TextUserTitle = editTextUserTitle.getText().toString();
        View focusView = null;

        //Validar si los campos no estan vacios
        if(TextUtils.isEmpty(TextUserTitle)) {
            editTextUserTitle.setError(getString(R.string.error_field_required));
            focusView = editTextUserTitle;
            focusView.requestFocus();
            return;
        } else if(TextUtils.isEmpty(TextData)) {
            editTextData.setError(getString(R.string.error_field_required));
            focusView = editTextData;
            focusView.requestFocus();
            return;
        } else  if(TextUtils.isEmpty(TextMoney)) {
            editTextMoney.setError(getString(R.string.error_field_required));
            focusView = editTextMoney;
            focusView.requestFocus();
            return;
        }
        else{

            insertData();

            Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.NeedWait), Toast.LENGTH_SHORT).show();

        //Ocultar el teclado virtual (Hide keyboard)
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextMoney.getWindowToken(), 0);

        }

    }

    private void getData(String type, String id) {
        class GetProfile extends AsyncTask<String,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(NeedActivity.this,
                        getResources().getString(R.string.fetching),
                        getResources().getString(R.string.wait),false,false);
            }

            @Override
            protected String doInBackground(String... params) {

                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequestParam(Config.URL_NEED_DATA, params[0]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showData(s);
            }
        }
        GetProfile gp = new GetProfile();
        gp.execute("type=" + type + "&id=" + id);
    }

    private void showData(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String type = jsonObject.getString(Config.TAG_TYPE);
            JSONArray array;
            array = jsonObject.getJSONArray(Config.TAG_RESULT);

            switch (type) {
                case "1":  // Categories
                    categoriesMap = new HashMap<>();
                    categoriesNames = new String[array.length() + 1];
                    getArraysFromJSON(array, categoriesNames, categoriesMap, Config.TAG_ID_CATEGORIES,
                            getResources().getString(R.string.categoriesNoSelected));
                    setSpinnerList(spinnerCategory, categoriesNames);
                    spinnerCategory.setSelection(0);
                    spinnerCategory.setEnabled(true);
                    break;

                case "2":  // Subcategories
                    subCategoriesMap = new HashMap<>();
                    subCategoriesNames = new String[array.length() + 1];
                    getArraysFromJSON(array, subCategoriesNames, subCategoriesMap, Config.TAG_ID_SUBCATEGORIES,
                            getResources().getString(R.string.categoriesNoSelected));
                    setSpinnerList(spinnerSubcategory, subCategoriesNames);
                    spinnerSubcategory.setSelection(0);
                    spinnerCategory.setEnabled(true);
                    break;
            }

            if (!formState)
                enableForm(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Fill the correspond array with the data from the JSON
    private void getArraysFromJSON(JSONArray array,
                                   String[] spinnerStrings,
                                   HashMap<String, String> spinnerMap,
                                   String tagId,
                                   String noSelected) {
        try {
            spinnerStrings[0] = noSelected;

            for(int i = 0; i < array.length(); i++){
                JSONObject jo = array.getJSONObject(i);
                String id = jo.getString(tagId);
                String name = jo.getString(Config.TAG_NAME);

                spinnerMap.put(name, id);
                spinnerStrings[i+1] = name;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Set the Spinner list with the data from the correspond array
    private ArrayAdapter<String> setSpinnerList(Spinner spinner, String[] spinnerStrings) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return adapter;
    }

    // Before the activity is destroyed, save the state info to can restore it later
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the form state
        savedInstanceState.putBoolean(KEY_FORM_STATE, formState);
        if (formState) {
            savedInstanceState.putBoolean(KEY_SUBCATEGORIES_STATE, spinnerSubcategory.isEnabled());

            savedInstanceState.putInt(KEY_CATEGORIES_SELECTION, spinnerCategory.getSelectedItemPosition());
            savedInstanceState.putStringArray(KEY_CATEGORIES_NAMES, categoriesNames);
            savedInstanceState.putSerializable(KEY_CATEGORIES_MAP, categoriesMap);

            if (spinnerSubcategory.isEnabled()) {
                savedInstanceState.putInt(KEY_SUBCATEGORIES_SELECTION, spinnerSubcategory.getSelectedItemPosition());
                savedInstanceState.putStringArray(KEY_SUBCATEGORIES_NAMES, subCategoriesNames);
                savedInstanceState.putSerializable(KEY_SUBCATEGORIES_MAP, subCategoriesMap);
            }
        }
        super.onSaveInstanceState(savedInstanceState);
    }
}
