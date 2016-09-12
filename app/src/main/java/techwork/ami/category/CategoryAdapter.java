package techwork.ami.Category;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
 * Created by tataf on 20-08-2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements View.OnClickListener,View.OnLongClickListener{


    private OnItemClickListenerRecyclerView itemClick;
    private List<CategoryModel> items;
    private Context context;


    //Constructor de la clase
    public CategoryAdapter(Context context, List<CategoryModel> items){
        this.context=context;
        this.items=items;
    }


    //Holder de la clase
    public static class CategoryViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName;
        public ImageView ivImage;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView)itemView.findViewById(R.id.category_name);
            ivImage = (ImageView) itemView.findViewById(R.id.category_photo);

        }
    }

    //Inflar el layout
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card_view,parent,false);
        v.setOnLongClickListener(this);
        v.setOnClickListener(this);
        CategoryViewHolder vh = new CategoryViewHolder(v);
        return vh;
    }

    //Bind data

    @Override
    public void onBindViewHolder(CategoryAdapter.CategoryViewHolder holder, int position) {
        CategoryModel model = items.get(position);
        holder.tvName.setText(model.getName());
        Picasso.with(context).load(Config.URL_IMAGES_CATEGORY+model.getImage())
                .into(holder.ivImage);

    }

    //Retornar el tamaño de la lista.
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View view) {
        if (itemClick!=null){
            itemClick.onItemClick(view);
        }
    }
    @Override
    public boolean onLongClick(View view) {
        if (itemClick!=null) {
            itemClick.onItemLongClick(view);
        }
        return true;
    }

    public void setOnItemClickListener(OnItemClickListenerRecyclerView listener){
        this.itemClick=listener;
    }


}
