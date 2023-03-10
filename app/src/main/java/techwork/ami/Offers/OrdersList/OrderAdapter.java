package techwork.ami.Offers.OrdersList;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import techwork.ami.Config;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;


/*
 * Created by tataf on 11-09-2016.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.NeedViewHolder> implements View.OnClickListener,View.OnLongClickListener {

    private List<OrderModel> items;
    private OnItemClickListenerRecyclerView itemClick;
    private Context context;
    private FragmentOrder fragmentOrder;

    //Construct
    public OrderAdapter(Context context, List<OrderModel> items, FragmentOrder fragmentOrder){
        this.context=context;
        this.items=items;
        this.fragmentOrder = fragmentOrder;
    }

    //holder of class
    public static class NeedViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTitle;
        public TextView tvNOffers;
        public TextView tvNOffersDiscard;
        public TextView tvPricemax;
        public TextView tvDateFin;
        public TextView tvDescription;
        public TextView popupMenu;


        public NeedViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView)itemView.findViewById(R.id.tv_order_tittle);
            tvNOffers = (TextView)itemView.findViewById(R.id.tv_order_company_needs_number);
            tvNOffersDiscard =(TextView)itemView.findViewById(R.id.tv_order_company_needs_number_discard);
            tvPricemax = (TextView)itemView.findViewById(R.id.tv_order_price_max);
            tvDateFin = (TextView)itemView.findViewById(R.id.tv_order_date_fin);
            tvDescription= (TextView)itemView.findViewById(R.id.tv_order_description);
            popupMenu= (TextView)itemView.findViewById(R.id.tv_order_popup_menu);
        }
    }



    @Override
    public OrderAdapter.NeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card_view,parent,false);
        v.setOnLongClickListener(this);
        v.setOnClickListener(this);
        return new NeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final OrderAdapter.NeedViewHolder holder, final int position) {
        final OrderModel model = items.get(position);
        holder.tvTitle.setText(model.getTittle());
        holder.tvDateFin.setText("Expira el "+model.getDateFin());
        holder.tvPricemax.setText(String.format(Config.CLP_FORMAT,model.getPriceMax()));
        holder.tvNOffers.setText(model.getOffersCompany());
        holder.tvNOffersDiscard.setText(model.getnDiscardOffers());
        holder.tvDescription.setText(model.getDescription());

        //display popupMenu on listNeeds
        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentOrder.showPopupMenu(v,model);
            }
        });

    }




    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View view) {
        if(itemClick!=null){
            itemClick.onItemClick(view);
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

