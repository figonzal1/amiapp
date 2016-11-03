package techwork.ami.Offer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;
import java.util.HashMap;
import techwork.ami.Config;
import techwork.ami.R;
import techwork.ami.RequestHandler;

public class DiscardOffer extends AsyncTask<String, Void, String> {
    Context context;
    ProgressDialog loading;
    DialogInterface dialog;

    public DiscardOffer(Context c, DialogInterface dialog) {
        this.context = c;
        this.dialog = dialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading = ProgressDialog.show(context,
                context.getString(R.string.offers_list_discard_processing),
                context.getString(R.string.wait), false, false);
    }

    @Override
    protected String doInBackground(String... params) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Config.KEY_DO_PERSON_ID, params[0]);
        hashMap.put(Config.KEY_DO_OFFER_ID, params[1]);
        RequestHandler rh = new RequestHandler();
        return rh.sendPostRequest(Config.URL_DO_DISCARD, hashMap);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        loading.dismiss();
        if (s.equals("0")) {
            Toast.makeText(context,
                    R.string.my_reservations_offers_rate_ok, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context,
                    R.string.operation_fail, Toast.LENGTH_LONG).show();
        }
        this.dialog.dismiss();
    }
}
