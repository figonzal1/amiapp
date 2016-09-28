package techwork.ami.Need.NeedOfferDetails;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import techwork.ami.R;

/**
 * Created by tataf on 27-09-2016.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductModel> items;
    private Context context;

    public ProductAdapter(Context context, List<ProductModel> items){
        this.context=context;
        this.items=items;

    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName;

        public ProductViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView)itemView.findViewById(R.id.tv_need_offer_product_name);
        }
    }

    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.need_offer_product_card_view,parent,false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ProductViewHolder holder, int position) {

        ProductModel model = items.get(position);
        holder.tvName.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
