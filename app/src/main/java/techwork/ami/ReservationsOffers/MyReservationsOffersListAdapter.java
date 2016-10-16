package techwork.ami.ReservationsOffers;

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
import techwork.ami.OnItemClickListenerRecyclerView;
import techwork.ami.R;

/**
 * Created by Daniel on 19-09-2016.
 */

public class MyReservationsOffersListAdapter
        extends RecyclerView.Adapter<MyReservationsOffersListAdapter.MyReservationsListViewHolder>
        implements View.OnClickListener,View.OnLongClickListener {

    private OnItemClickListenerRecyclerView itemClick;
    private List<ReservationOffer> items;
    private Context context;

    // Class constructor
    public MyReservationsOffersListAdapter(Context context, List<ReservationOffer> items) {
        this.context = context;
        this.items = items;
    }

    // Class Holder
    public static class MyReservationsListViewHolder extends RecyclerView.ViewHolder {
        public TextView reservationTitle;
        public TextView reservationFinalDate;
        public TextView reservationPrice;
        public TextView reservationCompany;
        public TextView reservationReservationDate;
        public TextView reservationQuantity;
        public ImageView reservationImage;

        public MyReservationsListViewHolder(View itemView) {
            super(itemView);
            reservationTitle = (TextView) itemView.findViewById(R.id.my_reservations_offers_list_title);
            reservationFinalDate = (TextView) itemView.findViewById(R.id.my_reservations_offers_list_final_date);
            reservationPrice = (TextView) itemView.findViewById(R.id.my_reservations_offers_list_price);
            reservationCompany = (TextView) itemView.findViewById(R.id.my_reservations_offers_list_company);
            reservationReservationDate = (TextView) itemView.findViewById(R.id.my_reservations_offers_list_reservation_date);
            reservationQuantity = (TextView) itemView.findViewById(R.id.my_reservations_offers_list_quantity);
            reservationImage = (ImageView) itemView.findViewById(R.id.my_reservations_offers_list_photo);
        }
    }

    // Inflate the view
    @Override
    public MyReservationsOffersListAdapter.MyReservationsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reservations_offers_list_card_view, parent, false);
        v.setOnLongClickListener(this);
        v.setOnClickListener(this);
        MyReservationsListViewHolder vh = new MyReservationsListViewHolder(v);
        return vh;
    }

    // Set data into view reservations (list)
    @Override
    public void onBindViewHolder(MyReservationsOffersListAdapter.MyReservationsListViewHolder holder, int position) {
        ReservationOffer reservation = items.get(position);

        holder.reservationTitle.setText(reservation.getTitle());
        holder.reservationFinalDate.setText(reservation.getFinalDate());
        holder.reservationPrice.setText("$" + String.format(Config.CLP_FORMAT, reservation.getPrice()));
        holder.reservationCompany.setText(reservation.getCompany());
        holder.reservationReservationDate.setText(reservation.getReservationDate());
        holder.reservationQuantity.setText(reservation.getQuantity());
        String s = reservation.getImage();
        Picasso.with(context).load(Config.URL_IMAGES_OFFER + s)
                .placeholder(R.drawable.image_default)
                .into(holder.reservationImage);
    }

    @Override
    public int getItemCount() {
        return (null != items) ? items.size() : 0;
    }

    //Simple click item
    @Override
    public void onClick(View view) {
        if (itemClick != null) {
            itemClick.onItemClick(view);
        }
    }

    //Long click item
    @Override
    public boolean onLongClick(View view) {
        if (itemClick != null) {
            itemClick.onItemLongClick(view);
        }
        return true;
    }

    public void setOnItemClickListener(OnItemClickListenerRecyclerView listener) {
        this.itemClick = listener;
    }

}
