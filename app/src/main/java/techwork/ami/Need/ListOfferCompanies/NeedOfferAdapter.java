package techwork.ami.Need.ListOfferCompanies;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import techwork.ami.Config;
import techwork.ami.DateDifference;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;

/**
 * Created by tataf on 25-09-2016.
 */

public class NeedOfferAdapter extends RecyclerView.Adapter<NeedOfferAdapter.NeedOfferViewHolder> implements View.OnClickListener {

    private OnItemClickListenerRecyclerView itemClick;

    private List<NeedOfferModel> items;
    private Context context;


    //Constructor de la clase
    public NeedOfferAdapter(Context context,List<NeedOfferModel> items){
        this.context=context;
        this.items=items;
    }

    //Holder de la clase
    public static class NeedOfferViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTittle;
        public TextView tvPrice;
        public TextView tvCompany;
        public TextView tvTime;
        public CountDownTimer countDownTimer;

        public NeedOfferViewHolder(View itemView) {
            super(itemView);
            tvTittle = (TextView)itemView.findViewById(R.id.tv_need_offer_tittle);
            tvPrice = (TextView)itemView.findViewById(R.id.tv_need_offer_price);
            tvCompany= (TextView)itemView.findViewById(R.id.tv_need_offer_company);
            tvTime= (TextView)itemView.findViewById(R.id.tv_need_offer_time);
        }
    }

    @Override
    public NeedOfferAdapter.NeedOfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.need_offer_card_view,parent,false);
        v.setOnClickListener(this);
        return new NeedOfferViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final NeedOfferAdapter.NeedOfferViewHolder holder, int position) {
        NeedOfferModel model = items.get(position);
        holder.tvTittle.setText(model.getTittle());
        holder.tvPrice.setText("$"+String.format(Config.CLP_FORMAT,model.getPrice()));
        holder.tvCompany.setText(model.getCompany());

        SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_ANDROID);

        try {

            Log.d("hola",model.getDateTimeFin());
            Date future = format.parse(model.getDateTimeFin());

            Date today = new Date();
            final long currentTime = today.getTime();
            long futureTime = future.getTime();
            long expiryTime = futureTime - currentTime;

            holder.countDownTimer = new CountDownTimer(expiryTime, 500) {
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;
                    String time = days + " " + "days" + "  " + hours % 24 + "h:" + minutes % 60 + "m:" + seconds % 60+"s";

                    if (days==0){
                        time= hours % 24 + "h:" + minutes % 60 + "m:" + seconds % 60+"s";
                        if (hours==0){
                            time= minutes % 60 + "m :" + seconds % 60+"s";
                        }
                        holder.tvTime.setText(time);
                        holder.tvTime.setTextColor(Color.parseColor("#FF0000"));
                    }
                    holder.tvTime.setText(time);
                }

                public void onFinish() {
                    holder.tvTime.setText("Expirada");
                }
            }.start();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View v) {
        if (itemClick!=null){
            itemClick.onItemClick(v);
        }

    }
    public void setOnItemClickListener(OnItemClickListenerRecyclerView listener){
        this.itemClick=listener;
    }
}
