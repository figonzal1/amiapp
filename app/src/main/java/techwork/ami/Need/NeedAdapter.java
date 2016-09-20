package techwork.ami.Need;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;


/*
 * Created by tataf on 11-09-2016.
 */

public class NeedAdapter extends RecyclerView.Adapter<NeedAdapter.NeedViewHolder> implements View.OnClickListener {

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
        public TextView tvCompany;
        public TextView tvpricemin;
        public TextView tvDateFin;


        public NeedViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView)itemView.findViewById(R.id.tv_need_tittle);
            tvCompany = (TextView)itemView.findViewById(R.id.tv_need_company_needs);
            tvpricemin = (TextView)itemView.findViewById(R.id.tv_need_price_min);
            tvDateFin = (TextView)itemView.findViewById(R.id.tv_need_date_fin);
        }
    }



    @Override
    public NeedAdapter.NeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.need_card_view,parent,false);
        //v.setOnLongClickListener(this);
        v.setOnClickListener(this);
        NeedViewHolder vh = new NeedViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(NeedAdapter.NeedViewHolder holder, int position) {
        NeedModel model = items.get(position);
        holder.tvTitle.setText(model.getTittle());
        holder.tvDateFin.setText(model.getDateFin());
        holder.tvpricemin.setText(model.getPriceMin());
        holder.tvCompany.setText("Ofertas recibidas: "+model.getOffersCompany());

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

