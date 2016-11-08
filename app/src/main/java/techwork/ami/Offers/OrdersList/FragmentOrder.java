package techwork.ami.Offers.OrdersList;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import techwork.ami.AnimateFab;
import techwork.ami.Config;
import techwork.ami.Dialogs.CustomAlertDialogBuilder;
import techwork.ami.Offers.OffersList.OffersActivity;
import techwork.ami.Offers.CreateOrderActivity;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;
import techwork.ami.RequestHandler;


public class FragmentOrder extends Fragment {

    private OrderAdapter adapter;
    private List<OrderModel> orderList;
    private RecyclerView rv;
    private GridLayoutManager layout;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton fab;
    private TextView tvOrderEmpty;
    private Vibrator c;


    public FragmentOrder() {
    }

    public static FragmentOrder newInstance() {
        FragmentOrder fragment = new FragmentOrder();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter= new OrderAdapter(getActivity(),orderList, FragmentOrder.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.order_fragment,container,false);
        tvOrderEmpty = (TextView)v.findViewById(R.id.tv_order);
        rv = (RecyclerView)v.findViewById(R.id.recycler_view_order);
        rv.setHasFixedSize(true);

        //1 cardviews in portrait mode.
        if (getResources().getConfiguration().orientation==1){
            layout = new GridLayoutManager(getActivity(), 1);
        }
        //2 cardviews per row in landscape view
        else if (getResources().getConfiguration().orientation==2){
            layout = new GridLayoutManager(getActivity(), 2);
        }
        rv.setLayoutManager(layout);

        //Setting of a refresh layout.
        refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh_need);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrders();
            }
        });

        //Settings of a fab button.
        fab=(FloatingActionButton)v.findViewById(R.id.floating_button);
        //Call the animatefab archive with a fab button and context;
        AnimateFab.doAnimate(fab,getContext());

        //Onclick listener for fab button.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateOrderActivity.class);
                getActivity().startActivity(intent);
            }
        });
        getOrders();
        return v;
    }

    //Show menu in cardview
    public void showPopupMenu(final View view, final OrderModel model){

        PopupMenu popup= new PopupMenu(view.getContext(),view);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.popup_menu,popup.getMenu());

        //Set on click listener in popup menu on cardview.
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    // If case is a "Ver Ofertas"
                    case R.id.item_popup_menu_need_view:

                        Intent intent = new Intent(view.getContext(),OffersActivity.class);

                        //Send de idNecesidad for obtain each Offers with this id.
                        intent.putExtra(Config.TAG_GET_ORDER_IDNEED,model.getIdNeed());
                        view.getContext().startActivity(intent);
                        return true;

                    //If case is "Descartar Pedido"
                    case R.id.item_popup_menu_discard_order:

                        //Show a CustomDialog in screen
                        new CustomAlertDialogBuilder(getContext())
                                .setTitle(R.string.OrderDeleteConfirm)
                                .setMessage(R.string.OrderConfirmAction)
                                .setCancelable(false)

                                //If positive button is clicked deleteOrder
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteOrder(view,dialog,model);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    public void getOrders(){
        sendPostRequest();
    }

    private void sendPostRequest() {

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE);

        final String idPerson = sharedPref.getString(Config.KEY_SP_ID, "-1");


        class OrderAsyncTask extends AsyncTask<Void,Void,String>{

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                refreshLayout.setRefreshing(true);
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler rh = new RequestHandler();

                Boolean connectionStatus = rh.isConnectedToServer(rv, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        sendPostRequest();
                    }
                });

                if (connectionStatus) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(Config.KEY_GET_ORDER_IDPERSON, idPerson);

                    return rh.sendPostRequest(Config.URL_GET_ORDER, hashMap);
                }
                else
                    return "-1";
            }

            @Override
            protected  void onPostExecute(String s){
                super.onPostExecute(s);
                refreshLayout.setRefreshing(false);
                if (!s.equals("-1"))
                    showOrders(s);
            }
        }
        OrderAsyncTask go = new OrderAsyncTask();
        go.execute();
    }

    //Show info in model in a recycler view
    private void showOrders(String s) {
        getOrdersData(s);

        adapter = new OrderAdapter(getActivity(),orderList,FragmentOrder.this);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(adapter);
        rv.setAdapter(scaleAdapter);

        if (orderList.size()==0){
            tvOrderEmpty.setText(R.string.OrderListTitleEmpty);
        }else {
            tvOrderEmpty.setText(R.string.OrderListTitle);
        }

        adapter.setOnItemClickListener(new OnItemClickListenerRecyclerView() {
            @Override
            public void onItemClick(View view) {
                Intent intent = new Intent(getActivity(),OffersActivity.class);
                int position = rv.getChildAdapterPosition(view);
                OrderModel n = orderList.get(position);

                //Send de idNecesidad to send post request for obtain each NeedOffer with this id.
                intent.putExtra(Config.TAG_GET_ORDER_IDNEED,n.getIdNeed());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(final View view) {
                new CustomAlertDialogBuilder(getContext())
                        .setTitle(R.string.OrderDeleteConfirm)
                        .setMessage(R.string.OrderConfirmAction)
                        .setCancelable(false)

                        //If positive button is clicked ... discard Order.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteOrder(view,dialog, orderList.get(rv.getChildAdapterPosition(view)));
                            }
                        })

                        //if negativ is clicked .... cancel dialog.
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    private void deleteOrder(final View view, DialogInterface dialog, final OrderModel model) {

        class deleteOrderAsyncTask extends AsyncTask<String, Void, String> {
            private ProgressDialog loading;
            private DialogInterface dialog;

            private deleteOrderAsyncTask(DialogInterface dialog) {
                this.dialog = dialog;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(view.getContext(),
                        getString(R.string.OrderDeleteProcessing),
                        getString(R.string.wait), false, false);



            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Config.KEY_NE_ID, model.getIdNeed());
                RequestHandler rh = new RequestHandler();

                return rh.sendPostRequest(Config.URL_DELETE_NEED, hashMap);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                //If operation is correct dialog close in 1,5 [s]
                if (s.equals("0")) {

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            loading.dismiss();

                            c=(Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            c.vibrate(500);
                            Toast.makeText(view.getContext(),
                                  R.string.OrderDeleteOk, Toast.LENGTH_LONG).show();

                            //If operations is ok refresh orders.
                            getOrders();
                        }
                    },1500);

                }

                //If not correct depends of the operation.
                else {
                    loading.dismiss();
                    Toast.makeText(view.getContext(),
                            R.string.operation_fail, Toast.LENGTH_LONG).show();
                }
                this.dialog.dismiss();
            }
        }
        deleteOrderAsyncTask go = new deleteOrderAsyncTask(dialog);
        go.execute();

    }

    //Get information of each order and put in model of orders.
    private void getOrdersData(String json) {

        String dExp;
        Date dateExp;

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_DB);
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonNeed = jsonObject.optJSONArray(Config.TAG_GET_ORDER);
            Calendar c = Calendar.getInstance();
            orderList = new ArrayList<>();

            for (int i=0;i<jsonNeed.length();i++){
                JSONObject jsonObjectItem = jsonNeed.getJSONObject(i);
                OrderModel item = new OrderModel();

                item.setIdNeed(jsonObjectItem.getString(Config.TAG_GET_ORDER_IDNEED));
                item.setTittle(jsonObjectItem.getString(Config.TAG_GET_ORDER_TITTLE));
                item.setDescription(jsonObjectItem.getString(Config.TAG_GET_ORDER_DESCRIPTION));

                dExp = jsonObjectItem.getString(Config.TAG_GET_ORDER_EXPIRATIONDATE);
                dateExp = format.parse(dExp);
                c.setTime(dateExp);

                item.setDateFin(String.format(Locale.US,Config.DATE_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR)));
                item.setDateTimeFin(String.format(Locale.US,Config.DATETIME_FORMAT,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR),c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));

                item.setPriceMax(jsonObjectItem.getInt(Config.TAG_GET_ORDER_PRICEMAX));
                item.setLat(jsonObjectItem.getString(Config.TAG_GET_ORDER_LATITUDE));
                item.setLon(jsonObjectItem.getString(Config.TAG_GET_ORDER_LONGITUDE));
                item.setRadio(jsonObjectItem.getString(Config.TAG_GET_ORDER_RADIO));
                item.setOffersCompany(jsonObjectItem.getString(Config.TAG_GET_ORDER_OFFERS_COMPANY));
                item.setnDiscardOffers(jsonObjectItem.getString(Config.TAG_GET_ORDER_NDISCARD_OFFERS));

                orderList.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
