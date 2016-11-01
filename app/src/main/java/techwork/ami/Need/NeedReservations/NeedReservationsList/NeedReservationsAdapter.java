package techwork.ami.Need.NeedReservations.NeedReservationsList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import techwork.ami.Config;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;

/**
 * Created by tataf on 24-10-2016.
 */

public class NeedReservationsAdapter extends RecyclerView.Adapter<NeedReservationsAdapter.NeedReservationsViewHolder> implements View.OnClickListener,View.OnLongClickListener {

    private Context context;
    private List<NeedReservationsModel> items;
    private OnItemClickListenerRecyclerView itemClick;
    private NeedReservationsActivity needReservationsActivity;

    public NeedReservationsAdapter(Context context, List<NeedReservationsModel> items,NeedReservationsActivity needReservationsActivity){
        this.context=context;
        this.items=items;
        this.needReservationsActivity = needReservationsActivity;
    }



    public static class NeedReservationsViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTittle;
        public TextView tvDescription;
        public TextView tvCompany;
        public TextView tvPrice;
        public TextView tvStatus;
        public TextView popupMenu;
        public ImageView ivImage;


        public NeedReservationsViewHolder(View itemView) {
            super(itemView);


            tvTittle=(TextView)itemView.findViewById(R.id.tv_need_reservations_tittle);
            tvDescription=(TextView)itemView.findViewById(R.id.tv_need_reservations_description);
            tvCompany=(TextView)itemView.findViewById(R.id.tv_need_reservations_company);
            tvPrice=(TextView)itemView.findViewById(R.id.tv_need_reservations_quantity_price);
            tvStatus=(TextView)itemView.findViewById(R.id.tv_need_reservations_status);
            popupMenu=(TextView)itemView.findViewById(R.id.tv_need_reservations_popup_menu);
            ivImage=(ImageView)itemView.findViewById(R.id.iv_need_reservations_image);
        }
    }

    @Override
    public NeedReservationsAdapter.NeedReservationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.need_reservations_card_view,parent,false);
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        return new NeedReservationsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NeedReservationsAdapter.NeedReservationsViewHolder holder, int position) {
        final NeedReservationsModel model = items.get(position);
        holder.tvTittle.setText(model.getTittle());
        holder.tvDescription.setText(model.getDescription());
        holder.tvCompany.setText(model.getCompany());
        holder.tvPrice.setText(model.getQuantity()+"x $"+String.format(Config.CLP_FORMAT,model.getPrice()));

        Picasso.with(context)
                .load(Config.URL_IMAGES_NEED_OFFER+model.getImage())
                .placeholder(R.drawable.image_default)
                .into(holder.ivImage);

        //Si no esta cobrada



        if (model.getCashed().equals("0")){
            ((GradientDrawable)holder.tvStatus.getBackground()).setColor(ContextCompat.getColor(context,R.color.red));

            holder.tvStatus.setText("Reservada");


        }else{
            //
            if (model.getCalification().equals("")){
                ((GradientDrawable)holder.tvStatus.getBackground()).setColor(ContextCompat.getColor(context,R.color.orange));
                holder.tvStatus.setText("Cobrada");
            }
            else {
                ((GradientDrawable)holder.tvStatus.getBackground()).setColor(ContextCompat.getColor(context,R.color.green));
                holder.tvStatus.setText("Calificada");
            }
        }

        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needReservationsActivity.showPopupMenu(v,model);
          }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View v){
        if (itemClick!=null){
            itemClick.onItemClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(itemClick!=null){
            itemClick.onItemLongClick(v);
        }
        return true;
    }

    public void setOnItemClickListener(OnItemClickListenerRecyclerView listener){
        this.itemClick=listener;
    }
}
