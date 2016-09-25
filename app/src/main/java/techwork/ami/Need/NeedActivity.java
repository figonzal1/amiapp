package techwork.ami.Need;


import android.Manifest;
import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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

import java.util.HashMap;

import techwork.ami.Config;
import techwork.ami.R;
import techwork.ami.RequestHandler;


public class NeedActivity extends AppCompatActivity implements LocationListener {
    // State Keys for saving activity's instance
    static final String KEY_FORM_STATE = "formState";
    static final String KEY_CATEGORIES_NAMES = "categoriesNames";
    static final String KEY_CATEGORIES_MAP = "categoriesMap";
    static final String KEY_CATEGORIES_SELECTION = "categoriesSelection";
    static final String KEY_SUBCATEGORIES_STATE = "subCategoriesState";
    static final String KEY_SUBCATEGORIES_NAMES = "subCategoriesNames";
    static final String KEY_SUBCATEGORIES_MAP = "subCategoriesMap";
    static final String KEY_SUBCATEGORIES_SELECTION = "subCategoriesSelection";

    //Declaring an Spinner
    private Spinner spinnerCategory;
    private Spinner spinnerSubcategory;

    // Spinner arrays
    String[] categoriesNames;
    HashMap<String,String> categoriesMap;
    String[] subCategoriesNames;
    HashMap<String,String> subCategoriesMap;

    // Defines if the form is enable
    boolean formState;

    private EditText editTextDescription;
    private EditText editTextMoney;
    private EditText editTextDays;
    private EditText editTextTitle;

    private Button buttonRegister;

    private String Lat;
    private String Lon;
    private String user_id;
    private String user_name;
    private String commune_id;
    private Vibrator c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need);

        // Prevent the keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // UI references
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        spinnerSubcategory = (Spinner) findViewById(R.id.spinnerSubcategory);
        editTextDescription = (EditText) findViewById(R.id.Need_editTextDescription);
        editTextMoney = (EditText) findViewById(R.id.Need_editTextMoney);
        editTextTitle = (EditText) findViewById(R.id.Need_editTextTitle);
        editTextDays = (EditText) findViewById(R.id.Need_editTextDays);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        getProfileId();

        TextView textViewTitle = (TextView) findViewById(R.id.NE_textViewTitle);
        textViewTitle.setText(user_name + " " + getResources().getString(R.string.NeedLeyend));

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSave();
            }
        });

        setLocationSettings();

        SpinnerCategoryListener categoryListener = new SpinnerCategoryListener();
        SpinnerSubcategoryListener subcategoryListener = new SpinnerSubcategoryListener();

        spinnerCategory.setOnTouchListener(categoryListener);
        spinnerCategory.setOnItemSelectedListener(categoryListener);
        spinnerSubcategory.setOnTouchListener(subcategoryListener);
        spinnerSubcategory.setOnItemSelectedListener(subcategoryListener);

        // If the activity is restoring after screen rotation
        if (savedInstanceState != null)
            restoreState(savedInstanceState);
        else
            // Disable the form until get the categories data
            enableForm(false);

        // If the activity is on creation (not restore after rotation)
        if (savedInstanceState == null)
            // Attempt to get the categories data
            getData("1", "1");
    }

    // Get the profile's id from the shared preferences
    private void getProfileId(){
        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        user_id = sharedPref.getString(Config.KEY_SP_ID, "-1");
        user_name = sharedPref.getString(Config.KEY_SP_NAME, "");
        commune_id = sharedPref.getString(Config.KEY_SP_COMMUNE, "");
    }

    @SuppressWarnings("unchecked")
    private void restoreState(Bundle savedInstanceState) {// Set the previous state of the form
        enableForm(savedInstanceState.getBoolean(KEY_FORM_STATE));

        if (formState) {
            spinnerSubcategory.setEnabled(savedInstanceState.getBoolean(KEY_SUBCATEGORIES_STATE));

            // Set the categories arrays
            categoriesNames = savedInstanceState.getStringArray(KEY_CATEGORIES_NAMES);
            categoriesMap = (HashMap<String, String>) savedInstanceState.getSerializable(KEY_CATEGORIES_MAP);
            setSpinnerList(spinnerCategory, categoriesNames);
            spinnerCategory.setSelection(savedInstanceState.getInt(KEY_CATEGORIES_SELECTION));

            if (spinnerSubcategory.isEnabled()) {
                // Set the subcategories arrays
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
        spinnerCategory.setEnabled(e);
        editTextTitle.setEnabled(e);
        editTextDescription.setEnabled(e);
        editTextMoney.setEnabled(e);
        editTextDays.setEnabled(e);
        if (!e) {
            spinnerSubcategory.setEnabled(false);
            buttonRegister.setEnabled(false);
        }
    }

    public void setLocationSettings() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

    // Send the new profile data to the server
    private void sendSaveRequest() {
        final String title = editTextTitle.getText().toString();
        final String description = editTextDescription.getText().toString();
        final String money = editTextMoney.getText().toString();
        final String days = editTextDays.getText().toString();
        final String subcategory_id = subCategoriesMap.get(spinnerSubcategory.getSelectedItem().toString());

        class saveNeed extends AsyncTask<Void,Void,String> {
            private ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(NeedActivity.this,
                        getResources().getString(R.string.saving),
                        getResources().getString(R.string.wait),false,false);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(editTextDays, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendSaveRequest();
                    }
                });

                if (connectionStatus) {
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put(Config.KEY_NE_USER_ID, user_id);
                    hashMap.put(Config.KEY_NE_COMMUNE_ID, commune_id);
                    hashMap.put(Config.KEY_NE_SUBCATEGORY_ID, subcategory_id);
                    hashMap.put(Config.KEY_NE_LAT, Lat);
                    hashMap.put(Config.KEY_NE_LON, Lon);
                    hashMap.put(Config.KEY_NE_TITLE, title);
                    hashMap.put(Config.KEY_NE_DESCRIPTION, description);
                    hashMap.put(Config.KEY_NE_MONEY, money);
                    hashMap.put(Config.KEY_NE_DAYS, days);

                    return rh.sendPostRequest(Config.URL_NEW_NEED, hashMap);
                }
                else
                    return "-1";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equals("0")) {
                    //Play a success sound
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.arpeggio);
                    mp.start();

                    // Vibrate for 500 milliseconds
                    c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    c.vibrate(500);

                    Toast.makeText(NeedActivity.this, getResources().getString(R.string.NeedRegistered), Toast.LENGTH_LONG).show();

                    /* TODO: Check this
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {

                        // Salir de la activity despues de que la necesidad haya sido registrada
                        @Override
                        public void run() {
                            startActivity(new Intent(NeedActivity.this, MainActivity.class));
                        }

                    }, 2500);
                    */
                }
                else if (!s.equals("-1")) {
                    Toast.makeText(NeedActivity.this, getResources().getString(R.string.NeedError), Toast.LENGTH_LONG).show();
                    c = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    c.vibrate(500);
                }
            }
        }

        saveNeed sn = new saveNeed();
        sn.execute();
    }

    public void attemptSave() {

        String TextDescription = editTextDescription.getText().toString();
        String TextMoney = editTextMoney.getText().toString();
        String TextDays = editTextDays.getText().toString();
        String TextTitle = editTextTitle.getText().toString();
        View focusView;

        //Check if the fields are empties
        if(TextUtils.isEmpty(TextTitle)) {
            editTextTitle.setError(getString(R.string.error_field_required));
            focusView = editTextTitle;
            focusView.requestFocus();
        } else if(TextUtils.isEmpty(TextDescription)) {
            editTextDescription.setError(getString(R.string.error_field_required));
            focusView = editTextDescription;
            focusView.requestFocus();
        } else  if(TextUtils.isEmpty(TextMoney)) {
            editTextMoney.setError(getString(R.string.error_field_required));
            focusView = editTextMoney;
            focusView.requestFocus();
        } else  if(TextUtils.isEmpty(TextDays)) {
            editTextDays.setError(getString(R.string.error_field_required));
            focusView = editTextDays;
            focusView.requestFocus();
        }
        else{
            sendSaveRequest();

            Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.NeedWait), Toast.LENGTH_SHORT).show();

            //Hide keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editTextMoney.getWindowToken(), 0);
        }
    }

    private void getData(final String type, final String id) {

        class GetProfile extends AsyncTask<String,Void,String> {
            private ProgressDialog loading;
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

                Boolean connectionStatus = rh.isConnectedToServer(editTextDays, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        getData(type, id);
                    }
                });

                if (connectionStatus)
                    return rh.sendGetRequestParam(Config.URL_NEED_DATA, params[0]);
                else
                    return "-1";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (!s.equals("-1"))
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

    class SpinnerCategoryListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {
        private boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
                // Empty dependent spinners
                spinnerSubcategory.setAdapter(null);

                // Set Enable if has been selected a valid option
                spinnerSubcategory.setEnabled(position != 0);

                // Set disable dependent button
                buttonRegister.setEnabled(false);

                // Get the options of the next spinner
                if (position != 0) {
                    System.out.println("fuuuuuuuu");
                    getData("2", categoriesMap.get(spinnerCategory.getSelectedItem().toString()));
                }
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class SpinnerSubcategoryListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {
        private boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
            if (i != 0) {
                buttonRegister.setEnabled(true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
