package techwork.ami;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import techwork.ami.Category.FragmentCategory;
import techwork.ami.Offer.FragmentHome;

/**
 * Created by Daniel on 27-07-2016.
 */
public class MainPageAdapter extends FragmentPagerAdapter {
    private static final int HOME = 0;
    private static final int CATEGORY = 1;
    private static final int NECESIDAD = 2;
    final int PAGE_COUNT = 3;

    Context context;

    private int[] imageResId = {
            R.drawable.ic_price_tag,
            R.drawable.ic_categories,
            R.drawable.ic_megaphone
    };

    public MainPageAdapter (FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch(position) {
            case HOME:
               f = FragmentHome.newInstance();
                break;
            case CATEGORY:
                f = FragmentCategory.newInstance();
                break;
            case NECESIDAD:
                f = FragmentMap.newInstance();
                break;
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
