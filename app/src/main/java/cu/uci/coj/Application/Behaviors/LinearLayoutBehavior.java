package cu.uci.coj.Application.Behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

/**
 * Created by osvel on 5/7/16.
 */
public class LinearLayoutBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    private boolean scrollable = true;
    private boolean show;

    public LinearLayoutBehavior(boolean scrollable) {
        this.scrollable = scrollable;
        show = scrollable;
    }

    public LinearLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        show = true;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, LinearLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return scrollable && nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, final LinearLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (show && dyConsumed > 5) {

            child.setClickable(false);

            ViewCompat.animate(child)
                    .translationY(-child.getHeight())
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(400)
                    .start();
            show = false;

        } else if (!show && dyConsumed < -5) {

            child.setClickable(true);

            ViewCompat.animate(child)
                    .translationY(0f)
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(400)
                    .start();
            show = true;

        }
    }
}
