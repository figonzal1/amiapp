package techwork.ami.Need.ListNeeds;


import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import techwork.ami.Config;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;


/*
 * Created by tataf on 11-09-2016.
 */

public class NeedAdapter extends RecyclerView.Adapter<NeedAdapter.NeedViewHolder> implements View.OnClickListener,View.OnLongClickListener {

    private List<NeedModel> items;
    private OnItemClickListenerRecyclerView itemClick;
    private Context context;

    //Construct
    public NeedAdapter(Context context, List<NeedModel> items){
        this.context=context;
        this.items=items;
    }

    //holder of class
    public static class NeedViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTitle;
        public TextView tvNOffers;
        public TextView tvNOffersDiscard;
        public TextView tvPricemax;
        public TextView tvDateFin;
        public TextView tvDescription;
        public Button popupMenu;


        public NeedViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView)itemView.findViewById(R.id.tv_need_tittle);
            tvNOffers = (TextView)itemView.findViewById(R.id.tv_need_company_needs_number);
            tvNOffersDiscard =(TextView)itemView.findViewById(R.id.tv_need_company_needs_number_discard);
            tvPricemax = (TextView)itemView.findViewById(R.id.tv_need_price_max);
            tvDateFin = (TextView)itemView.findViewById(R.id.tv_need_date_fin);
            tvDescription= (TextView)itemView.findViewById(R.id.tv_need_description);
            popupMenu= (Button)itemView.findViewById(R.id.popup_menu);
        }
    }



    @Override
    public NeedAdapter.NeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.need_card_view,parent,false);
        v.setOnLongClickListener(this);
        v.setOnClickListener(this);
        return new NeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NeedAdapter.NeedViewHolder holder, final int position) {
        NeedModel model = items.get(position);
        holder.tvTitle.setText(model.getTittle());
        holder.tvDateFin.setText(model.getDateFin());
        holder.tvPricemax.setText("$"+String.format(Config.CLP_FORMAT,model.getPriceMax()));
        holder.tvNOffers.setText(model.getOffersCompany());
        holder.tvNOffersDiscard.setText(model.getnDiscardOffers());
        holder.tvDescription.setText(model.getDescription());
        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"CLick",Toast.LENGTH_LONG).show();
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

