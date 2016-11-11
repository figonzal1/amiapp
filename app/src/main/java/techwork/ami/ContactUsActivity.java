package techwork.ami;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class ContactUsActivity extends AppCompatActivity {

    private EditText editTextSubject;
    private EditText editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button send = (Button) findViewById(R.id.CU_buttonSend);
        editTextSubject = (EditText) findViewById(R.id.CU_editTextSubject);
        editTextMessage = (EditText) findViewById(R.id.CU_editTextMessage);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });
    }

    private Boolean attemptSend() {
        editTextSubject.setError(null);
        editTextMessage.setError(null);

        String subject = editTextSubject.getText().toString();
        String message = editTextMessage.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(message)) {
            editTextMessage.setError(getString(R.string.error_field_required));
            focusView = editTextMessage;
            cancel = true;
        }

        if (TextUtils.isEmpty(subject)) {
            editTextSubject.setError(getString(R.string.error_field_required));
            focusView = editTextSubject;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt send the email
            focusView.requestFocus();
        } else {
            sendContactUsEmail();
            return true;
        }
        return false;
    }

    private void sendContactUsEmail() {
        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);

        final String name = sharedPref.getString(Config.KEY_SP_NAME, "-1");
        final String email = sharedPref.getString(Config.KEY_SP_EMAIL, "-1");
        final String subject = editTextSubject.getText().toString();
        final String message = editTextMessage.getText().toString();

        class SendEmail extends AsyncTask<Void,Void,String> {
            private ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ContactUsActivity.this,
                        getResources().getString(R.string.attemptSend),
                        getResources().getString(R.string.wait),false,false);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(editTextSubject, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        attemptSend();
                    }
                });

                if (connectionStatus) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(Config.TAG_CU_NAME, name);
                    hashMap.put(Config.TAG_CU_MAIL, email);
                    hashMap.put(Config.TAG_CU_SUBJECT, subject);
                    hashMap.put(Config.TAG_CU_MESSAGE, message);

                    return rh.sendPostRequest(Config.URL_CONTACT_US, hashMap);
                }
                else
                    return "-1";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s.equals("0")) {
                    Toast.makeText(getBaseContext(), R.string.sendOK,
                            Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ContactUsActivity.this, MainActivity.class);
                    finish();
                    startActivity(i);
                }
                else if (!s.equals("-1"))
                Snackbar.make(findViewById(R.id.CU_coordinatorLayout), R.string.sendFail, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {
                                attemptSend();
                            }
                        }).show();
            }
        }

        SendEmail se = new SendEmail();
        se.execute();
    }
}
