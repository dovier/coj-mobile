package cu.uci.coj.Application.Behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import cu.uci.coj.R;

/**
 * Created by osvel on 3/28/16.
 */
public class FloatingActionButtonBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private boolean nestedScrolled;

    public FloatingActionButtonBehavior(boolean nestedScrolled) {
        this.nestedScrolled = nestedScrolled;
    }

    public FloatingActionButtonBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        nestedScrolled = true;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (child.getVisibility() == View.VISIBLE && dyConsumed > 5) {
            child.startAnimation(AnimationUtils.loadAnimation(coordinatorLayout.getContext(), R.anim.hide_fab));
            child.setClickable(false);
            child.setVisibility(View.GONE);
        } else if (child.getVisibility() == View.GONE && dyConsumed < -5) {
            child.startAnimation(AnimationUtils.loadAnimation(coordinatorLayout.getContext(), R.anim.show_fab));
            child.setClickable(true);
            child.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrolled && nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        layoutParams.bottomMargin = (int)(-translationY + parent.getContext().getResources().getDimension(R.dimen.fab_margin));
        child.setLayoutParams(layoutParams);

        return true;
    }
}
