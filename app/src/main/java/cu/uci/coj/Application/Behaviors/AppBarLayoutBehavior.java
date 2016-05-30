package cu.uci.coj.Application.Behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by osvel on 3/28/16.
 */
public class AppBarLayoutBehavior extends AppBarLayout.Behavior {

    private boolean scrollable = false;

    public AppBarLayoutBehavior() {
    }

    public AppBarLayoutBehavior(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public AppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        return scrollable && super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return scrollable && super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
        return scrollable && super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }
}
