package techwork.ami.category;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import techwork.ami.Config;
import techwork.ami.R;

public class CategoryView extends AppCompatActivity {
    TextView tvName,tvId;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_view);

        ivImage =(ImageView)findViewById(R.id.iv_categoria);
        tvName = (TextView)findViewById(R.id.tv_nombre_cat);
        tvId =(TextView)findViewById(R.id.tv_id_categoria);

        Bundle bundle = getIntent().getExtras();
        tvName.setText(String.format("Categoria: \n %s", bundle.getString(Config.TAG_GC_NAME)));
        tvId.setText(String.valueOf(bundle.getInt(Config.TAG_GC_ID)));
        Picasso.with(this).load(Config.URL_IMAGES_CATEGORY+bundle.getString(Config.TAG_GC_IMAGE))
                .into(ivImage);



    }
}
