package techwork.ami.Offers.OffersDetails;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import techwork.ami.Config;
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
        public TextView tvDescription;
        public TextView tvPrice;
        public ImageView ivImage;

        public ProductViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView)itemView.findViewById(R.id.tv_offer_view_product_name);
            tvDescription=(TextView)itemView.findViewById(R.id.tv_offer_view_product_description);
            tvPrice=(TextView)itemView.findViewById(R.id.tv_offer_view_product_price);
            ivImage=(ImageView)itemView.findViewById(R.id.iv_offer_view_product_image);
        }
    }

    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_product_card_view,parent,false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ProductViewHolder holder, int position) {

        ProductModel model = items.get(position);
        holder.tvName.setText(model.getName());
        holder.tvDescription.setText(model.getDescription());
        holder.tvPrice.setText(String.format(Config.CLP_FORMAT,model.getPrice()));

        if (Integer.valueOf(model.getQuantity())>1){
            holder.tvPrice.setText(String.format(Config.QUANTITYPRICE_FORMAT,model.getQuantity(),
                    String.format(Config.CLP_FORMAT,model.getPrice()),
                    context.getResources().getString(R.string.OfferViewPriceCU)));
        }else{
            holder.tvPrice.setText(String.format(Config.CLP_FORMAT,model.getPrice()));
        }

        Picasso.with(context)
                .load(Config.URL_IMAGES_PRODUCTS+model.getImage())
                .placeholder(R.drawable.image_default)
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
