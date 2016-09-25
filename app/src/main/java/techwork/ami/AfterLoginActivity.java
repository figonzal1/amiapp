package techwork.ami;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AfterLoginActivity extends AppCompatActivity {
	// State Keys for saving activity's instance
	static final String KEY_FORM_STATE = "formState";
	static final String KEY_REGIONS_STATE = "regionsState";
	static final String KEY_PROVINCES_STATE = "provincesState";
	static final String KEY_COMMUNES_STATE = "communesState";
	static final String KEY_OCCUPATIONS_NAMES = "occupationsNames";
	static final String KEY_OCCUPATIONS_MAP = "occupationsMap";
	static final String KEY_OCCUPATIONS_SELECTION = "occupationsSelection";
	static final String KEY_GENRES_NAMES = "genresNames";
	static final String KEY_GENRES_MAP = "genresMap";
	static final String KEY_GENRES_SELECTION = "genresSelection";
	static final String KEY_COUNTRIES_NAMES = "countriesNames";
	static final String KEY_COUNTRIES_MAP = "countriesMap";
	static final String KEY_COUNTRIES_SELECTION = "countriesSelection";
	static final String KEY_REGIONS_NAMES = "regionsNames";
	static final String KEY_REGIONS_MAP = "regionsMap";
	static final String KEY_REGIONS_SELECTION = "regionsSelection";
	static final String KEY_PROVINCES_NAMES = "provincesNames";
	static final String KEY_PROVINCES_MAP = "provincesMap";
	static final String KEY_PROVINCES_SELECTION = "provincesSelection";
	static final String KEY_COMMUNES_NAMES = "communesNames";
	static final String KEY_COMMUNES_MAP = "communesMap";
	static final String KEY_COMMUNES_SELECTION = "communesSelection";

	// UI References
	EditText editTextDate;
	TextView textViewGenre;
	TextView textViewOccupation;
	TextView textViewUbication;
	Spinner spinnerGenre;
	Spinner spinnerOccupation;
	Spinner spinnerCountry;
	Spinner spinnerRegion;
	Spinner spinnerProvince;
	Spinner spinnerCommune;
	Button buttonContinue;
	DatePickerFragment datePickerFragment;

	// Spinner arrays
	String[] occupationsNames;
	HashMap<String,String> occupationsMap;
	String[] genreNames;
	HashMap<String,String> genreMap;
	String[] countryNames;
	HashMap<String,String> countryMap;
	String[] regionNames;
	HashMap<String,String> regionMap;
	String[] provinceNames;
	HashMap<String,String> provinceMap;
	String[] communeNames;
	HashMap<String,String> communeMap;

	// Birth date variables
	static int day;
	static int month; // Starts from 0
	static int year;

	// Defines if the form is enable
	boolean formState;

	// User id
	String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_after_login);

		// Get the id of the profile from the login data
		id = getProfileId();

		editTextDate = (EditText) findViewById(R.id.AL_editTextBirthDate);
		textViewGenre = (TextView) findViewById(R.id.AL_textViewGenre);
		textViewOccupation = (TextView) findViewById(R.id.AL_textViewOccupation);
		textViewUbication = (TextView) findViewById(R.id.AL_textViewUbication);
		spinnerGenre = (Spinner) findViewById(R.id.AL_spinnerGenre);
		spinnerOccupation = (Spinner) findViewById(R.id.AL_spinnerOccupation);
		spinnerCountry = (Spinner) findViewById(R.id.AL_spinnerCountry);
		spinnerRegion = (Spinner) findViewById(R.id.AL_spinnerRegion);
		spinnerProvince = (Spinner) findViewById(R.id.AL_spinnerProvince);
		spinnerCommune = (Spinner) findViewById(R.id.AL_spinnerCommune);
		buttonContinue = (Button) findViewById(R.id.AL_buttonContinue);

		// If the activity is restoring after screen rotation
		if (savedInstanceState != null) {
			restoreState(savedInstanceState);
		} else
			// Disable the form until get the profile data
			enableForm(false);

		// If the activity is on creation (not restore after rotation)
		if (savedInstanceState == null)
			// Attempt to get the data
			sendGetRequest("1", "1");

		editTextDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showDatePicker();
			}
		});
		buttonContinue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptSave();
			}
		});
		datePickerFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int y, int m, int d) {
				year = y;
				day = d;
				month = m;
				// Update the textEdit with the selected date
				setDate(year, month + 1, day);
			}
		});

		// Set the initial date to now
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		SpinnerCountryListener countryListener = new SpinnerCountryListener();
		SpinnerRegionListener regionListener = new SpinnerRegionListener();
		SpinnerProvinceListener provinceListener = new SpinnerProvinceListener();
		SpinnerCommuneListener communeListener = new SpinnerCommuneListener();

		spinnerCountry.setOnTouchListener(countryListener);
		spinnerRegion.setOnTouchListener(regionListener);
		spinnerProvince.setOnTouchListener(provinceListener);
		spinnerCommune.setOnTouchListener(communeListener);
		spinnerCountry.setOnItemSelectedListener(countryListener);
		spinnerRegion.setOnItemSelectedListener(regionListener);
		spinnerProvince.setOnItemSelectedListener(provinceListener);
		spinnerCommune.setOnItemSelectedListener(communeListener);

	}

	@SuppressWarnings("unchecked")
	private void restoreState(Bundle savedInstanceState) {// Set the previous state of the form
		enableForm(savedInstanceState.getBoolean(KEY_FORM_STATE));

		if (formState) {
			spinnerRegion.setEnabled(savedInstanceState.getBoolean(KEY_REGIONS_STATE));
			spinnerProvince.setEnabled(savedInstanceState.getBoolean(KEY_PROVINCES_STATE));
			spinnerCommune.setEnabled(savedInstanceState.getBoolean(KEY_COMMUNES_STATE));

			// Set the occupations arrays
			occupationsNames = savedInstanceState.getStringArray(KEY_OCCUPATIONS_NAMES);
			occupationsMap = (HashMap<String, String>) savedInstanceState.getSerializable(KEY_OCCUPATIONS_MAP);
			setSpinnerList(spinnerOccupation, occupationsNames);
			spinnerOccupation.setSelection(savedInstanceState.getInt(KEY_OCCUPATIONS_SELECTION));

			// Set the genres arrays
			genreNames = savedInstanceState.getStringArray(KEY_GENRES_NAMES);
			genreMap = (HashMap<String, String>) savedInstanceState.getSerializable(KEY_GENRES_MAP);
			setSpinnerList(spinnerGenre, genreNames);
			spinnerGenre.setSelection(savedInstanceState.getInt(KEY_GENRES_SELECTION));

			// Set the countries arrays
			countryNames = savedInstanceState.getStringArray(KEY_COUNTRIES_NAMES);
			countryMap = (HashMap<String, String>) savedInstanceState.getSerializable(KEY_COUNTRIES_MAP);
			setSpinnerList(spinnerCountry, countryNames);
			spinnerCountry.setSelection(savedInstanceState.getInt(KEY_COUNTRIES_SELECTION));

			if (spinnerRegion.isEnabled()) {
				// Set the regions arrays
				regionNames = savedInstanceState.getStringArray(KEY_REGIONS_NAMES);
				regionMap = (HashMap<String, String>) savedInstanceState.getSerializable(KEY_REGIONS_MAP);
				setSpinnerList(spinnerRegion, regionNames);
				spinnerRegion.setSelection(savedInstanceState.getInt(KEY_REGIONS_SELECTION));

				if (spinnerProvince.isEnabled()) {
					// Set the provinces arrays
					provinceNames = savedInstanceState.getStringArray(KEY_PROVINCES_NAMES);
					provinceMap = (HashMap<String, String>) savedInstanceState.getSerializable(KEY_PROVINCES_MAP);
					setSpinnerList(spinnerProvince, provinceNames);
					spinnerProvince.setSelection(savedInstanceState.getInt(KEY_PROVINCES_SELECTION));

					if (spinnerCommune.isEnabled()) {
						// Set the provinces arrays
						communeNames = savedInstanceState.getStringArray(KEY_COMMUNES_NAMES);
						communeMap = (HashMap<String, String>) savedInstanceState.getSerializable(KEY_COMMUNES_MAP);
						setSpinnerList(spinnerCommune, communeNames);
						spinnerCommune.setSelection(savedInstanceState.getInt(KEY_COMMUNES_SELECTION));
					}
				}
			}
		}
	}

	// Get the profile's id from the shared preferences
	private String getProfileId(){
		SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
		return sharedPref.getString(Config.KEY_SP_ID, "-1");
	}

	// AsyncTask that send a request to the server
	private void sendGetRequest(final String type, final String id){

		class GetOptions extends AsyncTask<String,Void,String> {
			private ProgressDialog loading;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				loading = ProgressDialog.show(AfterLoginActivity.this,
						getResources().getString(R.string.fetching),
						getResources().getString(R.string.wait),false,false);
			}

			@Override
			protected String doInBackground(String... params) {
				RequestHandler rh = new RequestHandler();
				Boolean connectionStatus = rh.isConnectedToServer(textViewGenre, new View.OnClickListener() {
					@Override
					@TargetApi(Build.VERSION_CODES.M)
					public void onClick(View v) {
						sendGetRequest(type, id);
					}
				});

				if (connectionStatus)
					return rh.sendGetRequestParam(Config.URL_GET_AFTER_LOGIN_DATA, params[0]);
				else
					return "-1";
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				loading.dismiss();
				if (!s.equals("-1"))
					setOptions(s);
			}
		}
		GetOptions go = new GetOptions();
		go.execute("type=" + type + "&id=" + id);
	}


	// Change the enable state of the views
	private void enableForm(boolean e) {
		// Update the formState member
		formState = e;

		// Update the enable state of the views
		editTextDate.setEnabled(e);
		spinnerGenre.setEnabled(e);
		spinnerOccupation.setEnabled(e);
		spinnerCountry.setEnabled(e);
		if (!e) {
			spinnerRegion.setEnabled(false);
			spinnerProvince.setEnabled(false);
			spinnerCommune.setEnabled(false);
			buttonContinue.setEnabled(false);
		}
	}

	// Fill the spinner's arrays and enable the form
	private void setOptions(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String type = jsonObject.getString(Config.TAG_TYPE);
			JSONArray array;

			switch (type) {
				case "1":  // Genres, Occupations and Countries
					JSONObject jo = jsonObject.getJSONObject(Config.TAG_RESULT);
					// Fill the occupation spinner
					occupationsMap = new HashMap<>();
					array = jo.getJSONArray(Config.TAG_OCCUPATIONS);
					occupationsNames = new String[array.length() + 1];
					getArraysFromJSON(array, occupationsNames, occupationsMap, Config.TAG_ID_OCCUPATION,
							getResources().getString(R.string.occupationNoSelected));
					setSpinnerList(spinnerOccupation, occupationsNames);
					spinnerOccupation.setSelection(0);
					spinnerOccupation.setEnabled(true);

					// Fill the genres spinner
					genreMap = new HashMap<>();
					array = jo.getJSONArray(Config.TAG_GENRES);
					genreNames = new String[array.length() + 1];
					getArraysFromJSON(array, genreNames, genreMap, Config.TAG_ID_GENRE,
							getResources().getString(R.string.genreNoSelected));
					setSpinnerList(spinnerGenre, genreNames);
					spinnerGenre.setSelection(0);
					spinnerGenre.setEnabled(true);

					// Fill the countries spinner
					countryMap = new HashMap<>();
					array = jo.getJSONArray(Config.TAG_COUNTRIES);
					countryNames = new String[array.length() + 1];
					getArraysFromJSON(array, countryNames, countryMap, Config.TAG_ID_COUNTRY,
							getResources().getString(R.string.countryNoSelected));
					setSpinnerList(spinnerCountry, countryNames);
					spinnerCountry.setSelection(0);
					spinnerCountry.setEnabled(true);

					break;
				case "2":  // Regions
					// Fill the regions spinner
					regionMap = new HashMap<>();
					array = jsonObject.getJSONArray(Config.TAG_RESULT);
					regionNames = new String[array.length() + 1];
					getArraysFromJSON(array, regionNames, regionMap, Config.TAG_ID_REGION,
							getResources().getString(R.string.regionNoSelected));
					setSpinnerList(spinnerRegion, regionNames);
					spinnerRegion.setSelection(0);
					spinnerRegion.setEnabled(true);

					break;
				case "3":  // Provinces
					// Fill the Provinces spinner
					provinceMap = new HashMap<>();
					array = jsonObject.getJSONArray(Config.TAG_RESULT);
					provinceNames = new String[array.length() + 1];
					getArraysFromJSON(array, provinceNames, provinceMap, Config.TAG_ID_PROVINCE,
							getResources().getString(R.string.provinceNoSelected));
					setSpinnerList(spinnerProvince, provinceNames);
					spinnerProvince.setSelection(0);
					spinnerProvince.setEnabled(true);
					break;
				default:  // Communes
					// Fill the Provinces spinner
					communeMap = new HashMap<>();
					array = jsonObject.getJSONArray(Config.TAG_RESULT);
					communeNames = new String[array.length() + 1];
					getArraysFromJSON(array, communeNames, communeMap, Config.TAG_ID_COMMUNE,
							getResources().getString(R.string.communeNoSelected));
					setSpinnerList(spinnerCommune, communeNames);
					spinnerCommune.setSelection(0);
					spinnerCommune.setEnabled(true);
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


	// Validates the data and send it to the server
	private boolean attemptSave() {
		// Validate the data
		textViewGenre.setError(null);
		textViewOccupation.setError(null);
		textViewUbication.setError(null);
		editTextDate.setError(null);

		String date = editTextDate.getText().toString();

		boolean cancel = false;
		View focusView = null;


		// Check for a valid ubication
		if (spinnerCountry.getSelectedItemPosition() == 0 ||
				spinnerRegion.getSelectedItemPosition() == 0 ||
				spinnerProvince.getSelectedItemPosition() == 0 ||
				spinnerCommune.getSelectedItemPosition() == 0) {
			textViewUbication.setError(getString(R.string.error_field_required));
			focusView = textViewUbication;
			cancel = true;
		}

		// Check for a valid occupation.
		if (spinnerOccupation.getSelectedItemPosition() == 0) {
			textViewOccupation.setError(getString(R.string.error_field_required));
			focusView = textViewOccupation;
			cancel = true;
		}

		// Check for a valid genre.
		if (spinnerGenre.getSelectedItemPosition() == 0) {
			textViewGenre.setError(getString(R.string.error_field_required));
			focusView = textViewGenre;
			cancel = true;
		}

		// Check for a valid birth date.
		if (TextUtils.isEmpty(date)) {
			editTextDate.setError(getString(R.string.error_field_required));
			focusView = editTextDate;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt save the data
			focusView.requestFocus();
		} else {
			sendSaveRequest();
			return true;
		}
		return false;
	}

	@SuppressLint("SimpleDateFormat")
	// Send the new profile data to the server
	private void sendSaveRequest() {
		Calendar c = Calendar.getInstance();
		c.set(year, month, day, 0, 0, 0);
		Date d = c.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
		final String date = formatter.format(d);
		final String idGenre = genreMap.get(
				spinnerGenre.getSelectedItem().toString());
		final String idOccupation = occupationsMap.get(
				spinnerOccupation.getSelectedItem().toString());
		final String idCommune = communeMap.get(
				spinnerCommune.getSelectedItem().toString());

		class saveProfile extends AsyncTask<Void,Void,String> {
			private ProgressDialog loading;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				loading = ProgressDialog.show(AfterLoginActivity.this,
						getResources().getString(R.string.saving),
						getResources().getString(R.string.wait),false,false);
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();

				Boolean connectionStatus = rh.isConnectedToServer(textViewGenre, new View.OnClickListener() {
					@Override
					@TargetApi(Build.VERSION_CODES.M)
					public void onClick(View v) {
						sendSaveRequest();
					}
				});

				if (connectionStatus) {
					HashMap<String, String> hashMap = new HashMap<>();
					hashMap.put(Config.KEY_ID, id);
					hashMap.put(Config.KEY_DATE, date);
					hashMap.put(Config.KEY_OCCUPATION, idOccupation);
					hashMap.put(Config.KEY_COMMUNE, idCommune);
					hashMap.put(Config.KEY_GENRE, idGenre);

					return rh.sendPostRequest(Config.URL_UPDATE_AFTER_LOGIN_DATA, hashMap);
				}
				else
					return "-1";
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				loading.dismiss();
				if (s.equals("0")) {
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.saveOk), Toast.LENGTH_LONG).show();

					// Save the data of the profile on the application's shared preferences
					SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putString(Config.KEY_SP_GENRE, idGenre);
					editor.putString(Config.KEY_SP_COMMUNE, idCommune);
					editor.apply();

					// Close this Activity
					Intent iLogin = new Intent(AfterLoginActivity.this, MainActivity.class);
					finish();
					startActivity(iLogin);
				} else if (!s.equals("-1"))
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.saveFail) + " Error: " + s, Toast.LENGTH_LONG).show();
			}
		}

		saveProfile sp = new saveProfile();
		sp.execute();
	}


	// Write the birth date on the EditText View with the defined format
	private void setDate(int y, int m, int d) {
		editTextDate.setText(String.format(Locale.US, Config.DATE_FORMAT, d, m, y));
	}

	// Show the DatePicker Dialog for birth date input
	private void showDatePicker() {
		datePickerFragment.show(getFragmentManager(), "datePicker");
	}

	public static class DatePickerFragment extends DialogFragment {

		private DatePickerDialog.OnDateSetListener onDateSetListener;

		static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener onDateSetListener) {
			DatePickerFragment pickerFragment = new DatePickerFragment();
			pickerFragment.setOnDateSetListener(onDateSetListener);
			return pickerFragment;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			super.onCreateDialog(savedInstanceState);
			DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Panel,
					onDateSetListener, year, month, day);
			Calendar calendar = Calendar.getInstance();
			datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
			calendar.set(1900, 0, 1);
			datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
			datePickerDialog.setTitle(getResources().getString(R.string.dateDialog));
			return datePickerDialog;
		}

		private void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
			this.onDateSetListener = listener;
		}
	}

	// Before the activity is destroyed, save the state info to can restore it later
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the form state
		savedInstanceState.putBoolean(KEY_FORM_STATE, formState);
		if (formState) {
			savedInstanceState.putBoolean(KEY_REGIONS_STATE, spinnerRegion.isEnabled());
			savedInstanceState.putBoolean(KEY_PROVINCES_STATE, spinnerProvince.isEnabled());
			savedInstanceState.putBoolean(KEY_COMMUNES_STATE, spinnerCommune.isEnabled());

			savedInstanceState.putInt(KEY_OCCUPATIONS_SELECTION, spinnerOccupation.getSelectedItemPosition());
			savedInstanceState.putStringArray(KEY_OCCUPATIONS_NAMES, occupationsNames);
			savedInstanceState.putSerializable(KEY_OCCUPATIONS_MAP, occupationsMap);

			savedInstanceState.putInt(KEY_GENRES_SELECTION, spinnerGenre.getSelectedItemPosition());
			savedInstanceState.putStringArray(KEY_GENRES_NAMES, genreNames);
			savedInstanceState.putSerializable(KEY_GENRES_MAP, genreMap);

			savedInstanceState.putInt(KEY_COUNTRIES_SELECTION, spinnerCountry.getSelectedItemPosition());
			savedInstanceState.putStringArray(KEY_COUNTRIES_NAMES, countryNames);
			savedInstanceState.putSerializable(KEY_COUNTRIES_MAP, countryMap);

			if (spinnerRegion.isEnabled()) {
				savedInstanceState.putInt(KEY_REGIONS_SELECTION, spinnerRegion.getSelectedItemPosition());
				savedInstanceState.putStringArray(KEY_REGIONS_NAMES, regionNames);
				savedInstanceState.putSerializable(KEY_REGIONS_MAP, regionMap);

				if (spinnerProvince.isEnabled()) {
					savedInstanceState.putInt(KEY_PROVINCES_SELECTION, spinnerProvince.getSelectedItemPosition());
					savedInstanceState.putStringArray(KEY_PROVINCES_NAMES, provinceNames);
					savedInstanceState.putSerializable(KEY_PROVINCES_MAP, provinceMap);

					if (spinnerCommune.isEnabled()) {
						savedInstanceState.putInt(KEY_COMMUNES_SELECTION, spinnerCommune.getSelectedItemPosition());
						savedInstanceState.putStringArray(KEY_COMMUNES_NAMES, communeNames);
						savedInstanceState.putSerializable(KEY_COMMUNES_MAP, communeMap);
					}
				}
			}
		}
		super.onSaveInstanceState(savedInstanceState);
	}

	class SpinnerCountryListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {
		private boolean userSelect = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			userSelect = true;
			return false;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
			if (userSelect) {
				// Empty dependent spinners
				spinnerRegion.setAdapter(null);
				spinnerProvince.setAdapter(null);
				spinnerCommune.setAdapter(null);

				// Set Enable if has been selected a valid option
				spinnerRegion.setEnabled(i != 0);

				// Set disable dependent spinners and button
				spinnerProvince.setEnabled(false);
				spinnerCommune.setEnabled(false);
				buttonContinue.setEnabled(false);

				// Get the options of the next spinner
				if (i != 0)
					sendGetRequest("2", countryMap.get(spinnerCountry.getSelectedItem().toString()));
				userSelect = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	class SpinnerRegionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {
		private boolean userSelect = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			userSelect = true;
			return false;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
			if (userSelect) {
				// Empty dependent spinners
				spinnerProvince.setAdapter(null);
				spinnerCommune.setAdapter(null);

				// Set Enable if has been selected a valid option
				spinnerProvince.setEnabled(i != 0);

				// Set disable dependent spinners and button
				spinnerCommune.setEnabled(false);
				buttonContinue.setEnabled(false);

				// Get the options of the next spinner
				if (i != 0)
					sendGetRequest("3", regionMap.get(spinnerRegion.getSelectedItem().toString()));
				userSelect = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	class SpinnerProvinceListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {
		private boolean userSelect = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			userSelect = true;
			return false;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
			if (userSelect) {
				// Empty dependent spinners
				spinnerCommune.setAdapter(null);

				// Set Enable if has been selected a valid option
				spinnerCommune.setEnabled(i != 0);

				// Set disable dependent spinners and button
				buttonContinue.setEnabled(false);

				// Get the options of the next spinner
				if (i != 0)
					sendGetRequest("4", provinceMap.get(spinnerProvince.getSelectedItem().toString()));
				userSelect = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	class SpinnerCommuneListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {
		private boolean userSelect = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			userSelect = true;
			return false;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
			if (userSelect) {
				if (i != 0) {
					buttonContinue.setEnabled(true);
				}
				userSelect = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

}
