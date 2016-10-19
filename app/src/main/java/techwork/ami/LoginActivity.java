package techwork.ami;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import techwork.ami.Dialogs.CustomAlertDialogBuilder;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    CustomAlertDialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login_login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.login_email_sign_in_button);

        Button mStartRestorePassActivity = (Button) findViewById(R.id.login_start_restorePassActivity);
        mStartRestorePassActivity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,RestorePassActivity.class);
                i.putExtra("email", mEmailView.getText().toString());
                startActivity(i);
            }
        });
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mStartRegisterActivity = (Button) findViewById(R.id.login_start_registerActivity);
        mStartRegisterActivity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {

        //AQUI Validar con expresión regular

        //TODO: Replace this with your own logic
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            RequestHandler rh = new RequestHandler();

            Boolean connectionStatus = rh.isConnectedToServer(mEmailView, new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.M)
                public void onClick(View v) {
                    attemptLogin();
                }
            });

            if (connectionStatus) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_LI_EMAIL, mEmail);
                hashMap.put(Config.KEY_LI_PASS, mPassword);
                return rh.sendPostRequest(Config.URL_LOGIN, hashMap);
            }
            else
                return "-1";
        }

        @Override
        protected void onPostExecute(final String s) {
            mAuthTask = null;
            showProgress(false);
            if (!s.equals("-1"))
                processResult(s, mEmail);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void processResult(String json, String email) {
        // If the account has been closed
        if (json.equals("1")) {
            Snackbar.make(mEmailView, R.string.account_closed, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.yes, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            dialogReactivateAccount(false);
                        }
                    }).show();
        } else {
            try {
                JSONObject jsonObject = new JSONObject(json);
                String id = jsonObject.getString(Config.TAG_ID_P);

                // Check if the login was successful
                if (id.equals("-1")) {
                    // If login fails, check if the account exist
                    if (jsonObject.getString(Config.TAG_EXIST_EMAIL).equals("1")) {
                        // If the account exist
                        Snackbar.make(mEmailView, R.string.error_incorrect_password, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.did_forget_your_pass, new View.OnClickListener() {
                                    @Override
                                    @TargetApi(Build.VERSION_CODES.M)
                                    public void onClick(View v) {
                                        Intent iRestorePass = new Intent(LoginActivity.this, RestorePassActivity.class);
                                        iRestorePass.putExtra("email", mEmailView.getText().toString());
                                        startActivity(iRestorePass);
                                    }
                                }).show();

                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.setText("");
                        mPasswordView.requestFocus();
                    } else {
                        // If the account doesn't exist
                        Snackbar.make(mEmailView, R.string.email_dont_exist, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.ask_new_account, new View.OnClickListener() {
                                    @Override
                                    @TargetApi(Build.VERSION_CODES.M)
                                    public void onClick(View v) {
                                        Intent iRegisterPass = new Intent(LoginActivity.this, RegisterActivity.class);
                                        iRegisterPass.putExtra("email", mEmailView.getText().toString());
                                        startActivity(iRegisterPass);
                                    }
                                }).show();
                    }
                } else {
                    // Successful login

                    // Save the data of the profile on the application's shared preferences
                    SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putString(Config.KEY_SP_ID, id);
                    editor.putString(Config.KEY_SP_NAME, jsonObject.getString(Config.TAG_NAME));
                    editor.putString(Config.KEY_SP_LASTNAMES, jsonObject.getString(Config.TAG_LASTNAMES));
                    editor.putString(Config.KEY_SP_EMAIL, email);
                    editor.putString(Config.KEY_SP_GENDER, jsonObject.getString(Config.TAG_ID_GENDER));
                    editor.putString(Config.KEY_SP_FIRST_LOGIN, jsonObject.getString(Config.TAG_FIRST_LOGIN));

                    // Check if the user made the first login
                    if (jsonObject.getString(Config.TAG_FIRST_LOGIN).equals("1")) {
                        editor.apply();
                        Intent it = new Intent(LoginActivity.this, AfterLoginActivity.class);
                        startActivity(it);
                    } else {
                        editor.putString(Config.KEY_SP_COMMUNE, jsonObject.getString(Config.TAG_ID_COMMUNE));
                        editor.apply();
                        Intent iLogin = new Intent(LoginActivity.this, MainActivity.class);
                        finish();
                        startActivity(iLogin);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void dialogReactivateAccount(boolean incorrectPassword) {
        // Create the CustomAlertDialogBuilder
        dialogBuilder = new CustomAlertDialogBuilder(this);

        // Set the usual data, as you would do with AlertDialog.Builder
        dialogBuilder.setTitle(R.string.reactivate_account_closed_title);
        dialogBuilder.setMessage(getString(R.string.reactivate_account_closed_message));

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
                            attemptReactivateAccount(edittext.getText().toString());
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

    void attemptReactivateAccount(String pass) {
        sendReactivateAccountRequest(pass);
    }

    // AsyncTask that send a request to the server
    private void sendReactivateAccountRequest(String pass){
        class ReactivateAccount extends AsyncTask<String,Void,String> {
            private ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this,
                        getResources().getString(R.string.activatingAccount),
                        getResources().getString(R.string.wait),false,false);
            }

            @Override
            protected String doInBackground(String... params) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(mEmailView, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        dialogReactivateAccount(false);
                    }
                });

                if (connectionStatus) {
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put(Config.KEY_EMAIL, params[0]);
                    hashMap.put(Config.KEY_PASS, params[1]);
                    hashMap.put(Config.KEY_STATUS, "1");
                    return rh.sendPostRequest(Config.URL_CHANGE_ACCOUNT_STATUS, hashMap);
                } else
                    return "-1";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                System.out.println("s: " + s);

                // Success
                if (s.equals("0")) {
                    Toast.makeText(LoginActivity.this, R.string.reactivateAccountSuccess, Toast.LENGTH_LONG).show();
                }
                // Incorrect password
                else if (s.equals("1")) {
                    dialogReactivateAccount(true);
                }
                // Failure
                else if (!s.equals("-1"))
                    Snackbar.make(findViewById(R.id.LI_coordinatorLayout), R.string.reactivateAccountFail, Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(View v) {
                                    dialogReactivateAccount(false);
                                }
                            }).show();
            }
        }
        ReactivateAccount ra = new ReactivateAccount();
        ra.execute(mEmailView.getText().toString(), pass);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null)
            mEmailView.setText(intent.getStringExtra("email"));
    }
}

