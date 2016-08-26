package techwork.ami.Offer;


import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import techwork.ami.Config;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> implements View.OnClickListener {

    private OnItemClickListenerRecyclerView itemClick;
    private List<OfferModel> items;
    private Context context;

    // Class constructor
    public OfferAdapter(Context context, List<OfferModel> items){
        this.context = context;
        this.items = items;
    }

    // Class Holder
    public static class OfferViewHolder extends RecyclerView.ViewHolder{
        public TextView offerTitle;
        public TextView offerPrice;
        public TextView offerCompany;
        // TODO: no se aprecia, borrar
        // public TextView offerDescription;
        public ImageView offerImage;

        public OfferViewHolder(View itemView) {
            super(itemView);
            offerTitle = (TextView)itemView.findViewById(R.id.offer_tittle);
            offerPrice = (TextView)itemView.findViewById(R.id.offer_price);
            // TODO: implementar
            //offerCompany = (TextView)itemView.findViewById(R.id.offer_company);
            //offerDescription= (TextView)itemView.findViewById(R.id.offer_description);
            offerImage = (ImageView)itemView.findViewById(R.id.offer_photo);
        }
    }

    // Inflate the view
    @Override
    public OfferAdapter.OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_card_view,parent,false);
        v.setOnClickListener(this);
        OfferViewHolder vh = new OfferViewHolder(v);
        return vh;
    }

    // Set data into view offers (list)
    @Override
    public void onBindViewHolder(OfferAdapter.OfferViewHolder holder, int position) {
        OfferModel offer = items.get(position);
        holder.offerTitle.setText(offer.getTitle());
        holder.offerPrice.setText("$"+String.format(Config.CLP_FORMAT,offer.getPrice()));
        //holder.offerCompany.setText(offer.getCompany());
        String s = offer.getImage();
        /*
        if (null == s || s.equals("")){
            holder.offerImage.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.image_default, null));
        }
        else{*/
            Picasso.with(context).load(Config.URL_IMAGES_OFFER+s).placeholder(R.drawable.image_default).into(holder.offerImage);
        //}
    }
    @Override
    public int getItemCount() {
        return (null != items)? items.size() : 0;
    }

    @Override
    public void onClick(View view) {
        if (itemClick!=null){
            itemClick.onItemClick(view);
        }
    }

    public void setOnItemClickListener(OnItemClickListenerRecyclerView listener){
        this.itemClick = listener;
    }

}

