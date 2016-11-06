package techwork.ami;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

/**
 * Created by tataf on 06-11-2016.
 */

public class AnimateFab {

    private FloatingActionButton fab;
    private Context context;

    public AnimateFab(final FloatingActionButton fab, Context context){
        this.fab=fab;
        this.context=context;

    }

    public static void doAnimate(final FloatingActionButton fab, Context context){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Interpolator interpolador = AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.fast_out_slow_in);

            //Set fab in 0;
            fab.setScaleX(0);
            fab.setScaleY(0);

            //set fab in large size
            fab.animate()
                    .scaleX((float) 1.5)
                    .scaleY((float) 1.5)
                    .setInterpolator(interpolador)
                    .setDuration(600)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        //when animation ends the fab set in noraml size
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fab.animate()
                                    .scaleY(1)
                                    .scaleX(1)
                                    .setInterpolator(interpolador)
                                    .setDuration(1000)
                                    .start();

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    }
}
