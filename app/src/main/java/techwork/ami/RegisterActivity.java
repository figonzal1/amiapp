package techwork.ami;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import techwork.ami.Dialogs.CustomAlertDialogBuilder;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A register screen that offers register via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the register task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;
    private UserRegisterHashTask mAuthHashTask = null;

    // UI references.
    private AutoCompleteTextView mNameView;
    private AutoCompleteTextView mLastnameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPassword1View;
    private EditText mPassword2View;
    private View mProgressView;
    private View mRegisterFormView;

    //Cambiar variable dependiendo del tipo de registro.
    private boolean registerNormal = false;

    CustomAlertDialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // Set up the register form.
        mNameView = (AutoCompleteTextView) findViewById(R.id.register_name);
        mLastnameView = (AutoCompleteTextView) findViewById(R.id.register_lastnames);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email);

        // Check if this activity was started with any extra argument
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            // Fill the email field with the given data
            mEmailView.setText(extras.getString("email"));
        } else {
            populateAutoComplete();
        }

        mPassword1View = (EditText) findViewById(R.id.register_password1);
        mPassword2View = (EditText) findViewById(R.id.register_password2);

        mPassword2View.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register_login || id == EditorInfo.IME_ACTION_SEND) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.register_email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
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
                    .setAction(android.R.string.ok, new OnClickListener() {
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
     * Attempts to sign in or register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual register attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mEmailView.setError(null);
        mPassword1View.setError(null);
        mPassword2View.setError(null);

        // Store values at the time of the register attempt.
        String name = mNameView.getText().toString();
        String lastnames = mLastnameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password1 = mPassword1View.getText().toString();
        String password2 = mPassword2View.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(password2)) {
            mPassword2View.setError(getString(R.string.error_field_required));
            focusView = mPassword2View;
            cancel = true;
        }

        if (!password1.equals(password2)) {
            mPassword2View.setError(getString(R.string.error_match_password));
            focusView = mPassword2View;
            cancel = true;
        }

        if (TextUtils.isEmpty(password1)) {
            mPassword1View.setError(getString(R.string.error_field_required));
            focusView = mPassword1View;
            cancel = true;
        }

        if (!isPasswordValid(password1)) {
            mPassword1View.setError(getString(R.string.error_invalid_password));
            focusView = mPassword1View;
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

        // Check for a valid name
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;

        } else if (!isNameValid(name)) {
            mNameView.setError(getString(R.string.error_invalid_name));
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            showProgress(true);

            if (registerNormal){
                mAuthTask = new UserRegisterTask(name, email, lastnames, password1);
                mAuthTask.execute((Void) null);
            }else{
                String passwordHash = Bcrypt.hashpw(password1,Bcrypt.gensalt(12));
                mAuthHashTask = new UserRegisterHashTask(name, email, lastnames,passwordHash);
                mAuthHashTask.execute((Void) null);
            }



        }
    }

    private boolean isNameValid(String name) {

        //AQUI Validar con expresi??n regular

        //TODO: Replace this with your own logic
        return name.length() >= 2;
    }

    private boolean isEmailValid(String email) {

        //AQUI Validar con expresi??n regular

        //TODO: Replace this with your own logic
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the register form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserRegisterHashTask extends AsyncTask<Void, Void, String> {


        private final String mEmail;
        private final String mName;
        private final String mLastnames;
        private final String mPasswordHash;

        UserRegisterHashTask(String name, String email, String lastnames, String passwordHash) {
            mName = name;
            mLastnames = lastnames;
            mEmail = email;
            mPasswordHash = passwordHash;
        }

        @Override
        protected String doInBackground(Void... params) {
            RequestHandler rh = new RequestHandler();

            Boolean connectionStatus = rh.isConnectedToServer(mEmailView, new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.M)
                public void onClick(View v) {
                    attemptRegister();
                }
            });

            if (connectionStatus) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_NAME, mName);
                hashMap.put(Config.KEY_LASTNAMES, mLastnames);
                hashMap.put(Config.KEY_EMAIL, mEmail);
                hashMap.put(Config.KEY_HASH_PASSWORD, mPasswordHash);

                return rh.sendPostRequest(Config.URL_REGISTER_HASH, hashMap);
            }
            else
                return "-1";
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthHashTask = null;
            showProgress(false);

            switch (result) {
                case "0":
                    showProgress(true);
                    SendWelcomeEmailTask swe = new SendWelcomeEmailTask(mEmail);
                    swe.execute();
                    break;
                case "1":
                    Snackbar.make(mEmailView, R.string.email_already_exist, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.did_forget_your_pass, new OnClickListener() {
                                @Override
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(View v) {
                                    Intent iRestorePass = new Intent(RegisterActivity.this, RestorePassActivity.class);
                                    iRestorePass.putExtra("email", mEmailView.getText().toString());
                                    startActivity(iRestorePass);
                                }
                            }).show();
                    mEmailView.setError(getResources().getString(R.string.insert_another_email));
                    mEmailView.requestFocus();
                    mPassword1View.setText("");
                    mPassword2View.setText("");
                    break;
                case "2":
                    Snackbar.make(mEmailView, R.string.account_closed, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.yes, new OnClickListener() {
                                @Override
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(View v) {
                                    dialogReactivateAccount(false);
                                }
                            }).show();
                    break;
                case "-1":
                    break;
                default:
                    Snackbar.make(findViewById(android.R.id.content), R.string.saveFail, Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(View v) {
                                    attemptRegister();
                                }
                            }).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthHashTask = null;
            showProgress(false);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, String> {


        private final String mEmail;
        private final String mName;
        private final String mLastnames;
        private final String mPassword;

        UserRegisterTask(String name, String email, String lastnames, String password) {
            mName = name;
            mLastnames = lastnames;
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
                    attemptRegister();
                }
            });

            if (connectionStatus) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_NAME, mName);
                hashMap.put(Config.KEY_LASTNAMES, mLastnames);
                hashMap.put(Config.KEY_EMAIL, mEmail);
                hashMap.put(Config.KEY_PASS, mPassword);

                return rh.sendPostRequest(Config.URL_REGISTER, hashMap);
            }
            else
                return "-1";
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            showProgress(false);

            switch (result) {
                case "0":
                    showProgress(true);
                    SendWelcomeEmailTask swe = new SendWelcomeEmailTask(mEmail);
                    swe.execute();
                    break;
                case "1":
                    Snackbar.make(mEmailView, R.string.email_already_exist, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.did_forget_your_pass, new OnClickListener() {
                                @Override
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(View v) {
                                    Intent iRestorePass = new Intent(RegisterActivity.this, RestorePassActivity.class);
                                    iRestorePass.putExtra("email", mEmailView.getText().toString());
                                    startActivity(iRestorePass);
                                }
                            }).show();
                    mEmailView.setError(getResources().getString(R.string.insert_another_email));
                    mEmailView.requestFocus();
                    mPassword1View.setText("");
                    mPassword2View.setText("");
                    break;
                case "2":
                    Snackbar.make(mEmailView, R.string.account_closed, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.yes, new OnClickListener() {
                                @Override
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(View v) {
                                    dialogReactivateAccount(false);
                                }
                            }).show();
                    break;
                case "-1":
                    break;
                default:
                    Snackbar.make(findViewById(android.R.id.content), R.string.saveFail, Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(View v) {
                                    attemptRegister();
                                }
                            }).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class SendWelcomeEmailTask extends AsyncTask<Void, Void, Void> {


        private final String mEmail;

        SendWelcomeEmailTask(String email) {
            mEmail = email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            RequestHandler rh = new RequestHandler();
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Config.KEY_EMAIL, mEmail);

            rh.sendPostRequest(Config.URL_REGISTER_EMAIL, hashMap);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mAuthTask = null;
            showProgress(false);
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.saveOk), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("email", mEmailView.getText().toString());
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
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
                loading = ProgressDialog.show(RegisterActivity.this,
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

                // Success
                if (s.equals("0")) {
                    Toast.makeText(RegisterActivity.this, R.string.reactivateAccountSuccess, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("email", mEmailView.getText().toString());
                    startActivity(intent);
                }
                // Incorrect password
                else if (s.equals("1")) {
                    dialogReactivateAccount(true);
                }
                // Failure
                else if (!s.equals("-1"))
                    Snackbar.make(findViewById(R.id.RE_coordinatorLayout), R.string.reactivateAccountFail, Snackbar.LENGTH_LONG)
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
}

