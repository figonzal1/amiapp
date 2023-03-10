package techwork.ami.Promotion.PromotionsList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import techwork.ami.Config;
import techwork.ami.Dialogs.CustomAlertDialogBuilder;
import techwork.ami.MainActivity;
import techwork.ami.Promotion.PromotionDetail.PromotionDetailActivity;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;


public class FragmentHome extends Fragment {

    // Required for fragment use
    private PromotionAdapter adapter;
    private List<PromotionModel> offerList;
    private RecyclerView rv;
    private LinearLayoutManager layout;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvOffersEmpty;
    private static Vibrator c;

    //android.support.v4.app.NotificationCompat.Builder mBuilder;
    static int NOTIFY = 0;
    NotificationManager mNotificationManager;
    static boolean notificado = false;

    // Empty constructor (default)
    public FragmentHome() {
        // Required empty public constructor
    }

    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter= new PromotionAdapter(getActivity(),offerList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate view, find recycle view in layouts
        View v = inflater.inflate(R.layout.promotions_fragment, container, false);
        rv = (RecyclerView) v.findViewById(R.id.recycler_view_offer);
        tvOffersEmpty = (TextView)v.findViewById(R.id.tv_offers_empty);
        rv.setHasFixedSize(true);

        //Evita el error de skipping layout
        adapter= new PromotionAdapter(getActivity(),offerList);

        // Set the layout that will use recycle view
        layout = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layout);

        //Refreshing layout
        refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh_offer);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOffers();
            }
        });

        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        getOffers();
        return v;
    }

    // Call to DB
    public void getOffers(){
        sendGetRequest();

        if(notificado){
            MainActivity.notificate = false;
        }
        if(notificado && !MainActivity.notificate &&
                ((new Date()).getTime() - MainActivity.now.getTime())*Config.MILIS_TO_MIN > Config.NOTIFICATION_SLACK_TIME){
            MainActivity.now = new Date();
            MainActivity.notificate = true;
            notificado = false;
        }
    }

    private void sendGetRequest(){
        // Execute operations before, durign and after of data load
        class MyAsyncTask extends AsyncTask<String,Void,String> {


            // Execute before load data (user waiting)
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }

            // Class that execute background task (get BD data).
            @Override
            protected String doInBackground(String... params) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(rv, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendGetRequest();
                    }
                });

                if (connectionStatus) {
                    return rh.sendGetRequestParam(Config.URL_GET_OFFERS,params[0]);
                }
                else {
                    return "-1";
                }
            }

            // Do operations after load data from DB.
            // Put data in RecycleView Adapter.
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                if (!s.equals("-1"))
                    showOffers(s);
            }
        }
        MyAsyncTask go = new MyAsyncTask();
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);
        String id = sharedPref.getString(Config.KEY_SP_ID, "-1");
        go.execute("idPersona=" + id);
    }

    private void showOffers(String s){
        getData(s);
        adapter = new PromotionAdapter(getActivity(), offerList);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);

        if (offerList.size()==0){
            tvOffersEmpty.setText("??Oops! \n No encontramos ninguna promoci??n :( \n Vuelve m??s tarde...");
        }else {
            tvOffersEmpty.setText("");
        }

        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(View view) {
                Intent intent = new Intent(getActivity(), PromotionDetailActivity.class);
                int position = rv.getChildAdapterPosition(view);
                PromotionModel o = offerList.get(position);
                intent.putExtra(Config.TAG_GO_TITLE, o.getTitle());
                intent.putExtra(Config.TAG_GO_IMAGE, o.getImage());
                intent.putExtra(Config.TAG_GO_DESCRIPTION, o.getDescription());
                intent.putExtra(Config.TAG_GO_COMPANY, o.getCompany());
                intent.putExtra(Config.TAG_GO_PRICE, o.getPrice());
                intent.putExtra(Config.TAG_GO_OFFER_ID, o.getId());
                intent.putExtra(Config.TAG_GO_MAXXPER, o.getMaxPPerson());
                intent.putExtra(Config.TAG_GO_STOCK, o.getStock());
                intent.putExtra(Config.TAG_GO_DATEFIN, o.getFinalDate());
                intent.putExtra(Config.TAG_GO_TOTALPRICE, o.getTotalPrice());
                intent.putExtra(Config.TAG_GO_DATETIMEFIN,o.getFinalDateTime());
                intent.putExtra(Config.TAG_GO_IDLOCAL,o.getIdLocal());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(final View view) {
                new CustomAlertDialogBuilder(getContext())
                        .setTitle(R.string.offers_list_discard_question)
                        .setMessage("Confirme la acci??n")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                discardOffer(dialog, offerList.get(rv.getChildAdapterPosition(view)));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    public void discardOffer(DialogInterface dialog, PromotionModel offer) {
        String idPerson = getActivity().getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE)
                .getString(Config.KEY_SP_ID, "-1");
        String idOffer = offer.getId();
        new DiscardOffer(getActivity(), dialog).execute(idPerson, idOffer);
    }

    //Clase que itera sobre el json array para obtener datos de la BD.
    private void getData(String json){
        String dIni,dFin,dTimeFin;
        Date dateIni,dateFin,dateTimeFin;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try{
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonOffers = jsonObject.optJSONArray(Config.TAG_GO_OFFERS);
            Calendar c = Calendar.getInstance();
            offerList = new ArrayList<>();

            for(int i=0;i<jsonOffers.length();i++){

                JSONObject jsonObjectItem = jsonOffers.optJSONObject(i);
                PromotionModel item = new PromotionModel();

                dIni =jsonObjectItem.getString(Config.TAG_GO_DATEINI);
                dFin = jsonObjectItem.getString(Config.TAG_GO_DATEFIN);
                dTimeFin = jsonObjectItem.getString(Config.TAG_GO_DATETIMEFIN);
                dateIni= format.parse(dIni);
                dateFin =format.parse(dFin);
                dateTimeFin=format.parse(dTimeFin);


                item.setId(jsonObjectItem.getString(Config.TAG_GO_OFFER_ID));
                item.setTitle(jsonObjectItem.getString(Config.TAG_GO_TITLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GO_DESCRIPTION));
                item.setStock(jsonObjectItem.getInt(Config.TAG_GO_STOCK));
                item.setPromotionCode(Config.TAG_GO_PROMCOD);
                item.setPrice(jsonObjectItem.getInt(Config.TAG_GO_PRICE));
                item.setIdLocal(jsonObjectItem.getString(Config.TAG_GO_IDLOCAL));
                //TODO: en teor??a se deber??a poder borrar, puesto que el precio siempre exisitr?? (tendr??n al menos un producto asociado)
                try {
                    item.setTotal(jsonObjectItem.getInt(Config.TAG_GO_TOTALPRICE));
                }catch (Exception e){
                    item.setTotal(0);
                }

                c.setTime(dateIni);

                item.setInitialDate(String.format(Locale.US, Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));

                c.setTime(dateFin);
                item.setFinalDate(String.format(Locale.US, Config.DATE_FORMAT, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR)));

                c.setTime(dateTimeFin);
                item.setFinalDateTime(String.format(Locale.US,Config.DATETIME_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR),c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));

                item.setMaxPPerson(jsonObjectItem.getInt(Config.TAG_GO_MAXXPER));
                item.setCompany(jsonObjectItem.getString(Config.TAG_GO_COMPANY));
                item.setImage(jsonObjectItem.getString(Config.TAG_GO_IMAGE));

                offerList.add(item);

                // Se calcula la diferencia de tiempo acutal con cuando se publica la oferta, si son menor a una cierta holgura entonces se muestra la notificaci??n
                double d = ((new Date()).getTime() - dateIni.getTime())*Config.MILIS_TO_MIN;

                if(MainActivity.notificate && d >= 0.0 && d < Config.NOTIFICATION_SLACK_TIME){
                    notificado = true;
                    myNotification(item);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void myNotification(PromotionModel o){
        Intent nintent = new Intent(getActivity(), PromotionDetailActivity.class);
        nintent.putExtra(Config.TAG_GO_TITLE, o.getTitle());
        nintent.putExtra(Config.TAG_GO_IMAGE, o.getImage());
        nintent.putExtra(Config.TAG_GO_DESCRIPTION, o.getDescription());
        nintent.putExtra(Config.TAG_GO_COMPANY, o.getCompany());
        nintent.putExtra(Config.TAG_GO_PRICE, o.getPrice());
        nintent.putExtra(Config.TAG_GO_OFFER_ID, o.getId());
        nintent.putExtra(Config.TAG_GO_MAXXPER, o.getMaxPPerson());
        nintent.putExtra(Config.TAG_GO_STOCK, o.getStock());
        nintent.putExtra(Config.TAG_GO_DATEFIN, o.getFinalDate());
        nintent.putExtra(Config.TAG_GO_TOTALPRICE, o.getTotalPrice());
        nintent.putExtra(Config.TAG_GO_DATETIMEFIN,o.getFinalDateTime());
        nintent.setAction(Long.toString(System.currentTimeMillis()));

        // Siguiendo https://goo.gl/UGDo7n y https://goo.gl/C25HYF
        PendingIntent contIntent =
                PendingIntent.getActivity(
                        getContext(), NOTIFY, nintent, PendingIntent.FLAG_ONE_SHOT);

        // context, icon that show in notification bar, icon that show in notification (when expand it), title, description, info below time, i dont know xD, autoCancel, pendingintent
        android.support.v4.app.NotificationCompat.Builder mBuilder = creteNotificationBuilder(getContext(), R.mipmap.ic_launcher,
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
                o.getTitle(), o.getDescription(), o.getCompany(), "Ticker", true, contIntent);

        mNotificationManager.notify(NOTIFY, mBuilder.build());
        NOTIFY++;
    }

    private android.support.v4.app.NotificationCompat.Builder creteNotificationBuilder(
            Context context, int smallIcon, Bitmap largeIcon, String title, String content, String info, String ticker, boolean autoCancel,
            PendingIntent contIntent) {
        Notification n = new Notification();
        n.defaults |= Notification.DEFAULT_VIBRATE;
        return new NotificationCompat.Builder(context)
                .setDefaults(n.defaults)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentInfo(info)
                .setTicker(ticker)
                .setAutoCancel(autoCancel)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setContentIntent(contIntent);
    }

    public static class DiscardOffer extends AsyncTask<String, Void, String> {
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
            //TODO: Realizar un chequeo de conexion a internet, arroja problema con clase estatica.
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Config.KEY_DO_PERSON_ID, params[0]);
            hashMap.put(Config.KEY_DO_OFFER_ID, params[1]);
            RequestHandler rh = new RequestHandler();
            return rh.sendPostRequest(Config.URL_DO_DISCARD, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("0")) {

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();

                        c = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        c.vibrate(500);

                        Toast.makeText(context,
                                R.string.my_reservations_offers_rate_ok, Toast.LENGTH_LONG).show();

                    }
                },1500);

            } else {
                loading.dismiss();

                c = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                c.vibrate(500);

                Toast.makeText(context,
                        R.string.operation_fail, Toast.LENGTH_LONG).show();
            }
            this.dialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        getOffers();
    }

}