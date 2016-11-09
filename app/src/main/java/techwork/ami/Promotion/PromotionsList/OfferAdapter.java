package techwork.ami.Promotion.PromotionsList;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import techwork.ami.Config;
import techwork.ami.Dialogs.CustomAlertDialogBuilder;
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;

public class OfferAdapter
        extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder>
        implements View.OnClickListener,View.OnLongClickListener {

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
        public TextView offerDescription;
        public TextView offerDsct;
        public TextView offerDiscard;
        public ImageView offerImage;

        public OfferViewHolder(View itemView) {
            super(itemView);
            offerTitle = (TextView)itemView.findViewById(R.id.offer_cv_tittle);
            offerPrice = (TextView)itemView.findViewById(R.id.offer_cv_price);
            offerDsct = (TextView)itemView.findViewById(R.id.offer_cv_dsct);
            offerCompany = (TextView)itemView.findViewById(R.id.offer_cv_company);
            offerDescription= (TextView)itemView.findViewById(R.id.offer_cv_description);
            offerDiscard = (TextView) itemView.findViewById(R.id.tv_discard_offer);
            offerImage = (ImageView)itemView.findViewById(R.id.offer_cv_photo);
        }
    }

    // Inflate the view
    @Override
    public OfferAdapter.OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotion_card_view,parent,false);
        v.setOnLongClickListener(this);
        v.setOnClickListener(this);
        OfferViewHolder vh = new OfferViewHolder(v);
        return vh;
    }

    // Set data into view offers (list)
    @Override
    public void onBindViewHolder(OfferAdapter.OfferViewHolder holder, int position) {
        final OfferModel offer = items.get(position);
        holder.offerTitle.setText(offer.getTitle());
        holder.offerPrice.setText("$"+String.format(Config.CLP_FORMAT,offer.getPrice()));
        holder.offerCompany.setText(offer.getCompany());
        int perc = (offer.getTotalPrice() != 0)? (offer.getPrice()*100)/offer.getTotalPrice() : 100;
        // If offer price is greater than total price
        String s = "-";
        if (perc == 100){
            holder.offerDsct.setText("");
            s = "";
        }
        else if (perc > 100){
            // Red color
            holder.offerDsct.setTextColor(ContextCompat.getColor(context, R.color.red));
            s = "+";
        }

        if (!s.equals("")) holder.offerDsct.setText(s+String.valueOf(Math.abs(100-perc))+"%");

        holder.offerDescription.setText(offer.getDescription());
        s = offer.getImage();
        Picasso.with(context).load(Config.URL_IMAGES_OFFER+s)
                .placeholder(R.drawable.image_default)
                .into(holder.offerImage);

        holder.offerDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomAlertDialogBuilder(context)
                        .setTitle(R.string.offers_list_discard_question)
                        .setMessage("Confirme la acción")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String idPerson = context.getSharedPreferences(Config.KEY_SHARED_PREF, Context.MODE_PRIVATE)
                                        .getString(Config.KEY_SP_ID, "-1");
                                String idOffer = offer.getId();
                                new FragmentHome.DiscardOffer(context, dialog).execute(idPerson, idOffer);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return (null != items)? items.size() : 0;
    }

    //Simple click item
    @Override
    public void onClick(View view) {
        if (itemClick!=null){
            itemClick.onItemClick(view);
        }
    }

    //Long click item
    @Override
    public boolean onLongClick(View view) {
        if (itemClick!=null) {
            itemClick.onItemLongClick(view);
        }
        return true;
    }


    public void setOnItemClickListener(OnItemClickListenerRecyclerView listener){
        this.itemClick = listener;

    }

}

