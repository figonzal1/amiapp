package techwork.ami.Promotion.MyPromotions;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import techwork.ami.Config;
import techwork.ami.ExpiryTime;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;

/**
 * Created by Daniel on 19-09-2016.
 */

public class MyPromotionsAdapter
        extends RecyclerView.Adapter<MyPromotionsAdapter.MyReservationsListViewHolder>
        implements View.OnClickListener,View.OnLongClickListener {

    private OnItemClickListenerRecyclerView itemClick;
    private List<MyReservationPromotionModel> items;
    private Context context;
    private MyPromotionsActivity offersReservationsActivity;

    // Class constructor
    public MyPromotionsAdapter(Context context, List<MyReservationPromotionModel> items, MyPromotionsActivity offersReservationsActivity) {
        this.context = context;
        this.items = items;
        this.offersReservationsActivity=offersReservationsActivity;
    }

    // Class Holder
    public static class MyReservationsListViewHolder extends RecyclerView.ViewHolder {
        public TextView reservationTitle;
        public TextView reservationFinalDate;
        public TextView reservationPrice;
        public TextView reservationCompany;
        public TextView reservationReservationDate;
        public ImageView reservationImage;
        public TextView tvStatus;
        public TextView popupMenu;

        public MyReservationsListViewHolder(View itemView) {
            super(itemView);
            reservationTitle = (TextView) itemView.findViewById(R.id.my_reservations_offers_list_title);
            reservationFinalDate = (TextView) itemView.findViewById(R.id.my_reservations_offers_list_final_date);
            reservationPrice = (TextView) itemView.findViewById(R.id.my_reservations_offers_list_quantity);
            reservationCompany = (TextView) itemView.findViewById(R.id.my_reservations_offers_list_company);
            reservationReservationDate = (TextView) itemView.findViewById(R.id.my_reservations_offers_list_reservation_date);
            reservationImage = (ImageView) itemView.findViewById(R.id.my_reservations_offers_list_photo);
            popupMenu=(TextView)itemView.findViewById(R.id.tv_offers_popup_menu);
            tvStatus =(TextView)itemView.findViewById(R.id.tv_offers_status);
        }
    }

    // Inflate the view
    @Override
    public MyPromotionsAdapter.MyReservationsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_promotions_card_view, parent, false);
        v.setOnLongClickListener(this);
        v.setOnClickListener(this);
        MyReservationsListViewHolder vh = new MyReservationsListViewHolder(v);
        return vh;
    }

    // Set data into view reservations (list)
    @Override
    public void onBindViewHolder(MyPromotionsAdapter.MyReservationsListViewHolder holder, int position) {
        final MyReservationPromotionModel model = items.get(position);

        holder.reservationTitle.setText(model.getTitle());
        holder.reservationFinalDate.setText(model.getFinalDate());
        if(Integer.valueOf(model.getQuantity())>1){
            holder.reservationPrice.setText(String.format(Config.QUANTITYPRICE_FORMAT,
                    model.getQuantity(),
                    String.format(Config.CLP_FORMAT, model.getPrice()),
                    context.getResources().getString(R.string.offer_detail_cv_each)));
        }
        else{
            holder.reservationPrice.setText(String.format(Config.CLP_FORMAT, model.getPrice()));
        }
        holder.reservationCompany.setText(model.getCompany());
        holder.reservationReservationDate.setText(model.getReservationDate());

        String s = model.getImage();
        Picasso.with(context).load(Config.URL_IMAGES_OFFER + s)
                .placeholder(R.drawable.image_default)
                .into(holder.reservationImage);

        //Calculate remainig time
        ExpiryTime expt= new ExpiryTime();
        long expiryTime = expt.getTimeDiference(model.getFinalDateTime());
        // If promotion is available
        if (expiryTime > 0.0){
            if (model.getCashed().equals("0")){
                ((GradientDrawable)holder.tvStatus.getBackground()).setColor(ContextCompat.getColor(context,R.color.orange));
                holder.tvStatus.setText("Reservada");

            }else{
                if (model.getCalification().equals("")){
                    ((GradientDrawable)holder.tvStatus.getBackground()).setColor(ContextCompat.getColor(context,R.color.green));
                    holder.tvStatus.setText("Cobrada");
                }
                else {
                    ((GradientDrawable)holder.tvStatus.getBackground()).setColor(ContextCompat.getColor(context,R.color.blue));
                    holder.tvStatus.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.white, null));
                    holder.tvStatus.setText("Calificada");
                }
            }
        }
        // Unavailable
        else {
            if (model.getCashed().equals("0")){
                ((GradientDrawable)holder.tvStatus.getBackground()).setColor(ContextCompat.getColor(context,R.color.red));
                holder.tvStatus.setText("Vencida");
            }
            else {
                if (model.getCalification().equals("")) {
                    ((GradientDrawable) holder.tvStatus.getBackground()).setColor(ContextCompat.getColor(context, R.color.green));
                    holder.tvStatus.setText("Cobrada");
                } else {
                    ((GradientDrawable) holder.tvStatus.getBackground()).setColor(ContextCompat.getColor(context, R.color.blue));
                    holder.tvStatus.setText("Calificada");
                }
            }
        }
        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offersReservationsActivity.showPopupMenu(v,model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != items) ? items.size() : 0;
    }

    //Simple click item
    @Override
    public void onClick(View view) {
        if (itemClick != null) {
            itemClick.onItemClick(view);
        }
    }

    //Long click item
    @Override
    public boolean onLongClick(View view) {
        if (itemClick != null) {
            itemClick.onItemLongClick(view);
        }
        return true;
    }

    public void setOnItemClickListener(OnItemClickListenerRecyclerView listener) {
        this.itemClick = listener;
    }

}
