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
import techwork.ami.Need.ListNeeds.FragmentNeed;
import techwork.ami.Offer.OfferList.FragmentHome;

public class MainPageAdapter extends FragmentPagerAdapter {


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
        return Config.PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch(position) {
            case Config.HOME:
               f = FragmentHome.newInstance();
                break;
            case Config.CATEGORY:
                f = FragmentCategory.newInstance();
                break;
            case Config.NEED:
                f = FragmentNeed.newInstance();
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
