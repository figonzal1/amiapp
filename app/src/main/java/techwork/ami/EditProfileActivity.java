package techwork.ami;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {
	// State Keys for saving activity's instance
	static final String KEY_FORM_STATE = "formState";
	static final String KEY_OCCUPATIONS_NAMES = "occupationsNames";
	static final String KEY_OCCUPATIONS_MAP = "occupationsMap";
	static final String KEY_OCCUPATIONS_SELECTION = "occupationsSelection";
	static final String KEY_GENRES_NAMES = "genresNames";
	static final String KEY_GENRES_MAP = "genresMap";
	static final String KEY_GENRES_SELECTION = "genresSelection";

	// UI references
	EditText editTextName;
	EditText editTextEmail;
	EditText editTextDate;
	EditText editTextPhone;
	Spinner spinnerOccupation;
	Spinner spinnerGenre;
	Button buttonSave;
	DatePickerFragment datePickerFragment;
	View editProfileView;

	// Occupations arrays
	String[] occupationsNames;
	HashMap<String,String> occupationsMap;

	// Genres arrays
	String[] genresNames;
	HashMap<String,String> genresMap;

	// Defines if the form is enable
	boolean formState;

	static int day;
	static int month; // Starts from 0
	static int year;

	// id of the person;
	String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		// Get the id of the profile from the login data
		id = getProfileId();

		// Prevent the keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// get the UI references
		editTextName = (EditText) findViewById(R.id.EP_editTextName);
		editTextEmail = (EditText) findViewById(R.id.EP_editTextEmail);
		editTextDate = (EditText) findViewById(R.id.EP_editTextDate);
		editTextPhone = (EditText) findViewById(R.id.EP_editTextPhone);
		spinnerOccupation = (Spinner) findViewById(R.id.EP_spinnerOccupation);
		spinnerGenre = (Spinner) findViewById(R.id.EP_spinnerGenre);
		buttonSave = (Button) findViewById(R.id.EP_buttonSave);
		editProfileView = findViewById(R.id.editProfile_form);

		// If the activity is restoring after screen rotation
		if (savedInstanceState != null) {
			// Set the previous state of the form
			enableForm(savedInstanceState.getBoolean(KEY_FORM_STATE));
			if (formState) {
				// Set the occupations arrays
				occupationsNames = savedInstanceState.getStringArray(KEY_OCCUPATIONS_NAMES);
				occupationsMap = (HashMap<String, String>) savedInstanceState.getSerializable(KEY_OCCUPATIONS_MAP);
				setOccupationsList();
				spinnerOccupation.setSelection(savedInstanceState.getInt(KEY_OCCUPATIONS_SELECTION));

				// Set the genre arrays
				genresNames = savedInstanceState.getStringArray(KEY_GENRES_NAMES);
				genresMap = (HashMap<String, String>) savedInstanceState.getSerializable(KEY_GENRES_MAP);
				setGenresList();
				spinnerGenre.setSelection(savedInstanceState.getInt(KEY_GENRES_SELECTION));
			}
		} else
			// Disable the form until get the profile data
			enableForm(false);

		// If the activity is on creation (not restore after rotation)
		if (savedInstanceState == null)
			// Attempt to get the profile
			getProfile();

		editTextDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showDatePicker();
			}
		});
		buttonSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptUpdate();
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
	}

	// Get the profile's id from the shared preferences
	private String getProfileId(){
		SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
		return sharedPref.getString(Config.KEY_SP_ID, "-1");
	}

	// Change the enable state of the views
	private void enableForm(boolean e) {
		// Update the formState member
		formState = e;

		// Update the enable state of the views
		editTextName.setEnabled(e);
		editTextEmail.setEnabled(e);
		editTextDate.setEnabled(e);
		editTextPhone.setEnabled(e);
		spinnerOccupation.setEnabled(e);
		buttonSave.setEnabled(e);
	}

	// Get the data from the DB
	public void getProfile() {
		sendGetRequest();
	}

	// AsyncTask that send a request to the server
	private void sendGetRequest(){
		class GetProfile extends AsyncTask<Void,Void,String> {
			ProgressDialog loading;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				loading = ProgressDialog.show(EditProfileActivity.this,
						getResources().getString(R.string.gettingData),
						getResources().getString(R.string.wait),false,false);
			}

			@Override
			protected String doInBackground(Void... params) {
				RequestHandler rh = new RequestHandler();
				return rh.sendGetRequestParam(Config.URL_GET_PROFILE,id);
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				loading.dismiss();
				showProfile(s);
			}
		}
		GetProfile gp = new GetProfile();
		gp.execute();
	}

	// Fill the fields with the data from the JSON
	private void showProfile(String json){
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject profile = jsonObject.getJSONArray(Config.TAG_PROFILE).getJSONObject(0);
			String id = profile.getString(Config.TAG_ID_P);

			if (!id.equals(this.id)) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.editProfileFetchingFail), Toast.LENGTH_LONG).show();
				return;
			} else {
				enableForm(true);
			}

			String name = profile.getString(Config.TAG_NAME);
			String email = profile.getString(Config.TAG_EMAIL);
			String date = profile.getString(Config.TAG_DATE);
			String phone = profile.getString(Config.TAG_PHONE);
			String occupation = profile.getString(Config.TAG_OCCUPATION);
			String genre = profile.getString(Config.TAG_GENRE);

			// Fill the name field
			editTextName.setText(name);

			// Fill the email field
			editTextEmail.setText(email);

			if (!phone.equals("null"))
				editTextPhone.setText(phone);

			if (!date.isEmpty() && !date.equals("null")) {
				// Read the date from the JSON
				SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
				Date d = format.parse(date);
				final Calendar c = Calendar.getInstance();
				c.setTime(d);
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);

				// Fill the birth date field
				setDate(year, month + 1, day);
			}

			// Fill the occupation spinner
			getOccupations(jsonObject);

			ArrayAdapter<String> adapterOccupation = setOccupationsList();

			// Select the current profile occupation
			if (!occupation.isEmpty() && !occupation.equals("null"))
				spinnerOccupation.setSelection(adapterOccupation.getPosition(occupation));
			else
				spinnerOccupation.setSelection(adapterOccupation.getPosition(getResources().getString(R.string.defaultOccupation)));

			// Fill the genres spinner
			getGenres(jsonObject);

			ArrayAdapter<String> adapterGenre = setGenresList();

			// Select the current profile genre
			if (!genre.isEmpty() && !genre.equals("null"))
				spinnerGenre.setSelection(adapterGenre.getPosition(genre));
			else
				spinnerGenre.setSelection(adapterGenre.getPosition(getResources().getString(R.string.defaultGenre)));

		} catch (JSONException | ParseException e) {
			e.printStackTrace();
		}
	}

	// Fill the occupation arrays with the data from the JSON
	private void getOccupations(JSONObject jsonObject) {
		try {
			JSONArray occupations = jsonObject.getJSONArray(Config.TAG_OCCUPATIONS);

			// Prepare the Map and the String array where to store the occupations
			if (occupationsMap != null)
				occupationsMap = null;
			occupationsMap = new HashMap<>();

			if (occupationsNames != null)
				occupationsNames = null;
			occupationsNames = new String[occupations.length()];

			for(int i = 0; i < occupations.length(); i++){
				JSONObject jo = occupations.getJSONObject(i);
				String id = jo.getString(Config.TAG_ID_OCCUPATION);
				String name = jo.getString(Config.TAG_OCCUPATION_NAME);

				occupationsMap.put(name, id);
				occupationsNames[i] = name;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Set the Spinner list with the data from the occupations arrays
	private ArrayAdapter<String> setOccupationsList() {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, occupationsNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerOccupation.setAdapter(adapter);
		return adapter;
	}

	// Fill the genres arrays with the data from the JSON
	private void getGenres(JSONObject jsonObject) {
		try {
			JSONArray genres = jsonObject.getJSONArray(Config.TAG_GENRES);

			// Prepare the Map and the String array where to store the genres
			if (genresMap != null)
				genresMap = null;
			genresMap = new HashMap<>();

			if (genresNames != null)
				genresNames = null;
			genresNames = new String[genres.length()];

			for(int i = 0; i < genres.length(); i++){
				JSONObject jo = genres.getJSONObject(i);
				String id = jo.getString(Config.TAG_ID_GENRE);
				String name = jo.getString(Config.TAG_NAME);

				genresMap.put(name, id);
				genresNames[i] = name;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Set the Spinner list with the data from the genre arrays
	private ArrayAdapter<String> setGenresList() {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genresNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerGenre.setAdapter(adapter);
		return adapter;
	}

	// Write the birth date on the EditText View with the defined format
	private void setDate(int y, int m, int d) {
		editTextDate.setText(String.format(Locale.US, Config.DATE_FORMAT, d, m, y));
	}

	// Validates the data and send it to the server
	private boolean attemptUpdate() {
		// Check if the form is enable
		if (!formState)
			return false;

		// Validate the data
		editTextName.setError(null);
		editTextEmail.setError(null);

		String name = editTextName.getText().toString();
		String email = editTextEmail.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			editTextEmail.setError(getString(R.string.error_field_required));
			focusView = editTextEmail;
			cancel = true;
		} else if (!isEmailValid(email)) {
			editTextEmail.setError(getString(R.string.error_invalid_email));
			focusView = editTextEmail;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(name)) {
			editTextName.setError(getString(R.string.error_field_required));
			focusView = editTextName;
			cancel = true;
		} else if (!isNameValid(name)) {
			editTextName.setError(getString(R.string.errorInvalidName));
			focusView = editTextName;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt save the data
			focusView.requestFocus();
		} else {
			sendUpdateRequest();
			return true;
		}
		return false;
	}

	// Validate the name
	private boolean isNameValid(String name) {
		//TODO: Replace this and prevent SQL injection
		return name.length() > 1;
	}

	// Validate the email
	private boolean isEmailValid(String email) {
		// TODO: Replace comment
		return true; //android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	// Send the new profile data to the server
	private void sendUpdateRequest() {
		final String name = editTextName.getText().toString().trim();
		final String email = editTextEmail.getText().toString().trim();
		final String phone = editTextPhone.getText().toString().trim();
		Calendar c = Calendar.getInstance();
		c.set(year, month, day, 0, 0, 0);
		Date d = c.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
		final String date = formatter.format(d);
		final String idOccupation = occupationsMap.get(
				spinnerOccupation.getSelectedItem().toString());
		final String idGenre = genresMap.get(
				spinnerGenre.getSelectedItem().toString());

		class UpdateProfile extends AsyncTask<Void,Void,String>{
			ProgressDialog loading;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				loading = ProgressDialog.show(EditProfileActivity.this,
						getResources().getString(R.string.saving),
						getResources().getString(R.string.wait),false,false);
			}

			@Override
			protected String doInBackground(Void... params) {
				HashMap<String,String> hashMap = new HashMap<>();
				hashMap.put(Config.KEY_ID, id);
				hashMap.put(Config.KEY_NAME, name);
				hashMap.put(Config.KEY_EMAIL, email);
				hashMap.put(Config.KEY_DATE, date);
				hashMap.put(Config.KEY_PHONE, phone);
				hashMap.put(Config.KEY_OCCUPATION, idOccupation);
				hashMap.put(Config.KEY_GENRE, idGenre);

				RequestHandler rh = new RequestHandler();

				return rh.sendPostRequest(Config.URL_UPDATE_PROFILE, hashMap);
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				loading.dismiss();
				if (s.equals("0")) {
					Snackbar.make(findViewById(android.R.id.content), R.string.saveOk, Snackbar.LENGTH_LONG)
							.show();

					// Save the new data of the profile on the application's shared preferences
					SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putString(Config.KEY_SP_NAME, name);
					editor.putString(Config.KEY_SP_EMAIL, email);
					editor.putString(Config.KEY_SP_GENRE, idGenre);
					editor.apply();
				}
				else
					Snackbar.make(findViewById(android.R.id.content), R.string.saveFail, Snackbar.LENGTH_LONG)
							.setAction(R.string.retry, new View.OnClickListener() {
								@Override
								@TargetApi(Build.VERSION_CODES.M)
								public void onClick(View v) {
									attemptUpdate();
								}
							}).show();

			}
		}

		UpdateProfile up = new UpdateProfile();
		up.execute();
	}

	// Show the DatePicker Dialog for birth date input
	private void showDatePicker() {
		if (formState)
			datePickerFragment.show(getFragmentManager(), "datePicker");
	}

	public static class DatePickerFragment extends DialogFragment{

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
			savedInstanceState.putInt(KEY_OCCUPATIONS_SELECTION, spinnerOccupation.getSelectedItemPosition());
			savedInstanceState.putStringArray(KEY_OCCUPATIONS_NAMES, occupationsNames);
			savedInstanceState.putSerializable(KEY_OCCUPATIONS_MAP, occupationsMap);
			savedInstanceState.putInt(KEY_GENRES_SELECTION, spinnerGenre.getSelectedItemPosition());
			savedInstanceState.putStringArray(KEY_GENRES_NAMES, genresNames);
			savedInstanceState.putSerializable(KEY_GENRES_MAP, genresMap);
		}
		super.onSaveInstanceState(savedInstanceState);
	}

	private void delay(long d) {
		try {
			Thread.sleep(d);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
