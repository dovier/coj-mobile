package cu.uci.coj.Application;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.view.Surface;

/**
 * Created by osvel on 3/7/16.
 */
public class ScreenOrientationLocker {

    private Activity activity;

    public ScreenOrientationLocker(Activity activity) {
        this.activity = activity;
    }

    //esto esta dando error en algn lugar
    public void lock(){
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        switch (rotation){

            case Surface.ROTATION_0: {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                System.out.println("portrait");
                break;
            }
            case Surface.ROTATION_90: {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                System.out.println("landscape");
                break;
            }
            case Surface.ROTATION_180: {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                System.out.println("reverse portrait");
                break;
            }
            case Surface.ROTATION_270: {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                System.out.println("reverse landscape");
                break;
            }
        }
    }

    public void unlock(){
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

}
