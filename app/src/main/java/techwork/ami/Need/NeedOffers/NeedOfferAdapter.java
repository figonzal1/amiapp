package techwork.ami.Need.NeedOffers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import techwork.ami.Need.MyNeeds.NeedAdapter;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;

/**
 * Created by tataf on 25-09-2016.
 */

public class NeedOfferAdapter extends RecyclerView.Adapter<NeedOfferAdapter.NeedOfferViewHolder> implements View.OnClickListener{

    private List<NeedOfferModel> items;
    private OnItemClickListenerRecyclerView itemClick;
    private Context context;

    //Construct
    public NeedOfferAdapter(Context context, List<NeedOfferModel> items){
        this.context=context;
        this.items=items;
    }

    //Holder of class
    public static class NeedOfferViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTittle;
        public TextView tvPrice;


        public NeedOfferViewHolder(View itemView) {
            super(itemView);

            tvTittle = (TextView)itemView.findViewById(R.id.tv_need_offer_tittle_card_view);
            tvPrice = (TextView)itemView.findViewById(R.id.tv_need_offer_price_card_view);
        }
    }

    @Override
    public NeedOfferAdapter.NeedOfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.need_card_view,parent,false);
        v.setOnClickListener(this);
        NeedOfferViewHolder vh = new NeedOfferViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(NeedOfferAdapter.NeedOfferViewHolder holder, int position) {
        NeedOfferModel model = items.get(position);
        holder.tvTittle.setText(model.getTittle());
        holder.tvPrice.setText(model.getPrice());

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

    public void setOnItemClickListener(OnItemClickListenerRecyclerView listener){
        this.itemClick=listener;
    }

}
