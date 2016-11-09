package techwork.ami;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import techwork.ami.Dialogs.CustomAlertDialogBuilder;

public class MyProfileActivity extends AppCompatActivity {

    // UI references
    TextView textViewLeyend;
    TextView textViewName;
    TextView textViewLastnames;
    TextView textViewEmail;
    TextView textViewDate;
    TextView textViewOccupation;
    TextView textViewGender;
    TextView textViewPhone;
    Button buttonEdit;
    Button buttonCloseAccount;
    View myProfileView;

    CustomAlertDialogBuilder dialogBuilder;

    // id of the person;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_activity);

        // Get the id of the profile from the login data
        id = getProfileId();

        // get the UI references
        textViewLeyend = (TextView) findViewById(R.id.MP_textViewTitle);
        textViewName = (TextView) findViewById(R.id.MP_textViewFieldName);
        textViewLastnames = (TextView) findViewById(R.id.MP_textViewFieldLastnames);
        textViewEmail = (TextView) findViewById(R.id.MP_textViewFieldEmail);
        textViewDate = (TextView) findViewById(R.id.MP_textViewFieldDate);
        textViewOccupation = (TextView) findViewById(R.id.MP_textViewFieldOccupation);
        textViewGender = (TextView) findViewById(R.id.MP_textViewFieldGender);
        textViewPhone = (TextView) findViewById(R.id.MP_textViewFieldPhone);
        buttonEdit = (Button) findViewById(R.id.MP_buttonEdit);
        buttonCloseAccount = (Button) findViewById(R.id.MP_buttonCloseAccount);
        myProfileView = findViewById(R.id.myProfile_form);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfile = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                startActivity(editProfile);
            }
        });

        buttonCloseAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MyProfileActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getResources().getString(R.string.myProfileCloseAccountAlertTitle))
                        .setMessage(getResources().getString(R.string.myProfileCloseAccountAlertMessage))
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialogCancelAccount(false);
                            }

                        })
                        .show();
            }
        });

        buttonCloseAccount.setBackgroundColor(Color.RED);

        // If the activity is not restoring after screen rotation
        if (savedInstanceState == null)
            getProfile();

    }

    // Get the profile's id from the shared preferences
    private String getProfileId(){
        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        return sharedPref.getString(Config.KEY_SP_ID, "-1");
    }

    // Get the data from the DB
    public void getProfile() {
        sendGetRequest();
    }

    // AsyncTask that send a request to the server
    private void sendGetRequest(){
        class GetProfile extends AsyncTask<String,Void,String> {
            private ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MyProfileActivity.this,
                        getResources().getString(R.string.gettingData),
                        getResources().getString(R.string.wait),false,false);
            }

            @Override
            protected String doInBackground(String... params) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(myProfileView, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendGetRequest();
                    }
                });

                if (connectionStatus)
                    return rh.sendGetRequestParam(Config.URL_GET_PROFILE, params[0]);
                else
                    return "-1";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (!s.equals("-1"))
                    showProfile(s);
            }
        }
        GetProfile gp = new GetProfile();
        gp.execute("id=" + id);
    }

    @SuppressLint("SimpleDateFormat")
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
            }

            ImageView imageViewProfileIcon = (ImageView) findViewById(R.id.myProfileImageView);

            String name = profile.getString(Config.TAG_NAME);
            String lastnames = profile.getString(Config.TAG_LASTNAMES);
            String email = profile.getString(Config.TAG_EMAIL);
            String date = profile.getString(Config.TAG_DATE);
            String phone = profile.getString(Config.TAG_PHONE);
            String occupation = profile.getString(Config.TAG_OCCUPATION);
            String gender = profile.getString(Config.TAG_GENDER);
            String points = profile.getString(Config.TAG_POINTS);

            // Fill the name field
            textViewName.setText(name);

            // Fill the lastnames field
            textViewLastnames.setText(lastnames);

            // Fill the email field
            textViewEmail.setText(email);

            // Fill the points leyend
            textViewLeyend.setText(getString(R.string.myProfileLeyend).replace("%s", points));

            if (!phone.isEmpty() && !phone.equals("null"))
                textViewPhone.setText(phone);
            else
                textViewPhone.setText("-");

            if (!date.isEmpty() && !date.equals("null")) {
                // Read the date from the JSON
                SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
                Date d = format.parse(date);
                final Calendar c = Calendar.getInstance();
                c.setTime(d);
                textViewDate.setText(String.format(Locale.US, Config.DATE_FORMAT,
                        c.get(Calendar.DAY_OF_MONTH),
                        c.get(Calendar.MONTH) + 1,
                        c.get(Calendar.YEAR)));
            }

            if (!occupation.isEmpty() && !occupation.equals("null")) {
                textViewOccupation.setText(occupation);
            }

            if (!gender.isEmpty() && !gender.equals("null")) {
                textViewGender.setText(gender);
            }

            switch (gender) {
                case "Femenino":
                    imageViewProfileIcon.setImageResource(R.drawable.profile_icon_woman);
                    break;
                case "Masculino":
                    imageViewProfileIcon.setImageResource(R.drawable.profile_icon_man);
                    break;
                default:
                    imageViewProfileIcon.setImageResource(R.drawable.profile_icon_other);
                    break;
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void dialogCancelAccount(boolean incorrectPassword) {
        // Create the CustomAlertDialogBuilder
        dialogBuilder = new CustomAlertDialogBuilder(this);

        // Set the usual data, as you would do with AlertDialog.Builder
        dialogBuilder.setTitle(R.string.myProfileCloseAccountAlertTitle);
        dialogBuilder.setMessage(getString(R.string.myProfileCloseAccountAlertDialogMessage));

        // Create a EditText
        final EditText edittext = new EditText(this);
        // Type no visible password
        edittext.setInputType(Config.inputNoVisiblePasswordType);

        if (incorrectPassword)
            edittext.setError(getString(R.string.error_incorrect_password));

        dialogBuilder.setView(edittext);

        // Set your buttons OnClickListeners
        dialogBuilder.setPositiveButton(R.string.continueDialog,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Check if the password editText is empty
                        if (TextUtils.isEmpty(edittext.getText().toString())) {
                            edittext.setError(getString(R.string.error_field_required));
                        }
                        else {
                            dialog.dismiss();
                            attemptCloseAccount(edittext.getText().toString());
                        }
                    }
                });

        // By passing null as the OnClickListener the dialog will dismiss when the button is clicked.
        dialogBuilder.setNegativeButton(R.string.cancel, null);

        // (optional) set whether to dismiss dialog when touching outside
        dialogBuilder.setCanceledOnTouchOutside(false);

        // Show the dialog
        dialogBuilder.show();
    }

    // Send the close account request
    public void attemptCloseAccount(String pass) {
        sendCloseAccountRequest(pass);
    }

    // AsyncTask that send a request to the server
    private void sendCloseAccountRequest(String pass){
        class CloseAccount extends AsyncTask<String,Void,String> {
            private ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MyProfileActivity.this,
                        getResources().getString(R.string.profileClosingAccount),
                        getResources().getString(R.string.wait),false,false);
            }

            @Override
            protected String doInBackground(String... params) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(myProfileView, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        dialogCancelAccount(false);
                    }
                });

                if (connectionStatus) {
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put(Config.KEY_ID, params[0]);
                    hashMap.put(Config.KEY_PASS, params[1]);
                    hashMap.put(Config.KEY_STATUS, "0");
                    return rh.sendPostRequest(Config.URL_CHANGE_ACCOUNT_STATUS, hashMap);
                } else
                    return "-1";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                // Success
                if (s.equals("0")) {
                    Toast.makeText(MyProfileActivity.this, R.string.profileCloseAccountSuccess, Toast.LENGTH_LONG).show();
                    logout();
                    Intent iLogin = new Intent(MyProfileActivity.this, LoginActivity.class);
                    finish();
                    startActivity(iLogin);
                }
                // Incorrect password
                else if (s.equals("1")) {
                    dialogCancelAccount(true);
                }
                // Failure
                else if (!s.equals("-1"))
                        Snackbar.make(findViewById(R.id.MP_coordinatorLayout), R.string.profileCloseAccountFail, Snackbar.LENGTH_LONG)
                                .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    @TargetApi(Build.VERSION_CODES.M)
                                    public void onClick(View v) {
                                        dialogCancelAccount(false);
                                    }
                                }).show();
            }
        }
        CloseAccount ca = new CloseAccount();
        ca.execute(id, pass);
    }


    // logout the current session
    private void logout(){
        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Config.KEY_SP_ID, "-1");
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        getProfile();
    }
}
