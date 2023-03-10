package techwork.ami.Offers.OffersList;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import techwork.ami.Config;
import techwork.ami.ExpiryTime;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;

/**
 * Created by tataf on 25-09-2016.
 */

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.NeedOfferViewHolder> implements View.OnClickListener {

    private OnItemClickListenerRecyclerView itemClick;

    private List<OffersModel> items;
    private Context context;


    public OffersAdapter(Context context, List<OffersModel> items){
        this.context=context;
        this.items=items;
    }


    public static class NeedOfferViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTittle;
        public TextView tvDescription;
        public TextView tvPrice;
        public TextView tvCompany;
        public TextView tvTime;
        public ImageView ivImage;

        public CountDownTimer countDownTimer;

        public NeedOfferViewHolder(View itemView) {
            super(itemView);
            tvTittle = (TextView)itemView.findViewById(R.id.tv_offer_tittle);
            tvDescription=(TextView)itemView.findViewById(R.id.tv_offer_description);
            tvPrice = (TextView)itemView.findViewById(R.id.tv_offer_price);
            tvCompany= (TextView)itemView.findViewById(R.id.tv_offer_company);
            tvTime= (TextView)itemView.findViewById(R.id.tv_offer_time);
            ivImage=(ImageView)itemView.findViewById(R.id.iv_offer_image);
        }
    }

    @Override
    public OffersAdapter.NeedOfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_card_view,parent,false);
        v.setOnClickListener(this);
        return new NeedOfferViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final OffersAdapter.NeedOfferViewHolder holder, int position) {
        OffersModel model = items.get(position);
        holder.tvTittle.setText(model.getTittle());
        holder.tvDescription.setText(model.getDescription());
        holder.tvPrice.setText(String.format(Config.CLP_FORMAT,model.getPrice()));
        holder.tvCompany.setText(model.getCompany());

        Picasso.with(context)
                .load(Config.URL_IMAGES_OFFER_2+model.getImage())
                .placeholder(R.drawable.image_default)
                .into(holder.ivImage);

        ExpiryTime expt = new ExpiryTime();
        long expiryTime = expt.getTimeDiference(model.getDateTimeFin());

        holder.countDownTimer = new CountDownTimer(expiryTime, 1000) {
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;

                    String time = days + " " + "d??as" + " " + hours % 24 + "h:" + (minutes % 60) + "m:" + seconds % 60+"s";
                    
                    if (days==0){
                        time= hours % 24 + "h:" + minutes % 60 + "m:" + seconds % 60+"s";
                        if (hours<1){
                            time= minutes % 60 + "m:" + seconds % 60+"s";
                            holder.tvTime.setTextColor(Color.parseColor("#FF0000"));
                        }
                        holder.tvTime.setText(time);

                    }
                    else if (days==1){
                        time =days + " " + "d??a" + " " + hours % 24 + "h:" + (minutes % 60) + "m:" + seconds % 60+"s";
                    }
                    holder.tvTime.setText("Expira en \n "+time);
                }

                public void onFinish() {
                    holder.tvTime.setText(R.string.OfferExpiredShort);
                    holder.tvTime.setTextColor(Color.parseColor("#FF0000"));
                }
        }.start();
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
