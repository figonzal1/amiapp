package techwork.ami;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Promo extends AppCompatActivity {

    //UI references
    EditText tiPromo, tiTel;
    Button btnDonde, btnParticipar;
    Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        contexto = this;
        tiPromo = (EditText) findViewById(R.id.promoCode);
        tiTel = (EditText) findViewById(R.id.telefono);
        btnDonde = (Button) findViewById(R.id.button2);
        btnDonde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("geo:"+"-33.490144,-70.619663?z=16&q="+"-33.490144,-70.619663"+"("+"Stand AMI"+")"));
                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

        btnParticipar = (Button) findViewById(R.id.button3);
        btnParticipar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tiPromo.getText().toString().compareTo("promoami") == 0) {
                    participar();
                }else {
                    tiPromo.setError("Código incorrecto");
                    tiPromo.requestFocus();
                }
            }
        });
    }

    private void participar() {
        sendPost();
    }

    private void sendPost() {
        class Participar extends AsyncTask<String, Void, String> {
            private ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Promo.this,
                        getResources().getString(R.string.gettingData),
                        getResources().getString(R.string.wait), false, false);
            }

            @Override
            protected String doInBackground(String... params) {
                RequestHandler rh = new RequestHandler();
                return rh.sendGetRequestParam("http://amiapp.cl/public/PromoFeria.php", params[0]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                String msg;
                int i = Integer.valueOf(s);
                System.out.println("print i ="+i);
                if (i == 0) {
                    msg = "¡Ya estabas participando!";
                } else if (i == 1) {
                    msg = "¡Tus posibilidades de ganar se han duplicado!";
                } else if (i == 2) {
                    msg = "¡Gracias por participar! Ahora tienes el doble de posibilidades de ganar.";
                } else {
                    msg = "Ha ocurrido un error.";
                }
                System.out.println("print s = "+s);
                System.out.println("print msg = "+msg);
                Vibrator c = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                c.vibrate(500);
                Toast.makeText(contexto, msg, Toast.LENGTH_LONG).show();
                if (i == 1 || i == 2){
                    finish();
                }
            }
        }
        SharedPreferences sharedPref = getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        String email = sharedPref.getString(Config.KEY_SP_EMAIL, "-1");
        String idPersona = sharedPref.getString(Config.KEY_SP_ID, "-1");
        String name = sharedPref.getString(Config.KEY_SP_NAME, "");
        String params = "idPersona=" + idPersona +
                "&email=" + email +
                "&telefono=" + tiTel.getText().toString() +
                "&nombre=" + name;
        System.out.println("print params = "+params);
        (new Participar()).execute(params);
    }
}
