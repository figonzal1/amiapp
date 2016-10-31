package techwork.ami.Offer.OfferDetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import techwork.ami.Config;
import techwork.ami.R;

/**
 * Created by Daniel on 15-10-2016.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private List<ProductModel> items;
    private Context context;

    public ProductAdapter(Context context, List<ProductModel> items){
        this.context=context;
        this.items=items;

    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        public TextView productName;
        public TextView productDescription;
        public TextView productPrice;
        public ImageView productImage;
        public ProductViewHolder(View itemView) {
            super(itemView);
            productName = (TextView)itemView.findViewById(R.id.offer_detail_tittle);
            productDescription = (TextView)itemView.findViewById(R.id.offer_detail_description);
            productPrice = (TextView)itemView.findViewById(R.id.offer_detail_price);
            productImage = (ImageView)itemView.findViewById(R.id.offer_detail_photo);
        }
    }

    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_detail_card_view, parent,false);
        return new ProductAdapter.ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        ProductModel product = items.get(position);
        holder.productName.setText(product.getName());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText(("$"+String.format(Config.CLP_FORMAT,Integer.valueOf(product.getPrice()))));
        Picasso.with(context).load(Config.URL_IMAGES_OFFER + product.getImage())
                .placeholder(R.drawable.image_default)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
