package techwork.ami.Need.NeedReservations;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import techwork.ami.Config;
import techwork.ami.Need.ListOfferCompanies.NeedOfferAdapter;
import techwork.ami.Need.ListOfferCompanies.NeedOfferModel;
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
        public TextView tvPrice;
        public TextView tvStatus;
        public Button popupMenu;


        public NeedReservationsViewHolder(View itemView) {
            super(itemView);


            tvTittle=(TextView)itemView.findViewById(R.id.tv_need_reservations_tittle);
            tvPrice=(TextView)itemView.findViewById(R.id.tv_need_reservations_price);
            tvStatus=(TextView)itemView.findViewById(R.id.tv_need_reservations_status);
            popupMenu=(Button)itemView.findViewById(R.id.btn_need_reservations_popup_menu);
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
        holder.tvPrice.setText("$"+String.format(Config.CLP_FORMAT,model.getPrice()));

        //Si no esta cobrada
        if (model.getCashed().equals("0")){
            holder.tvStatus.setText("Esperando cobro");


        }else{
            //
            if (model.getCalification().equals("")){
                holder.tvStatus.setText("Esperando opinion");
            }
            else {
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
