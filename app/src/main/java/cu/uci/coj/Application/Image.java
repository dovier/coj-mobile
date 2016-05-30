package cu.uci.coj.Application;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by osvel on 4/20/16.
 */
public class Image implements Serializable {

    private String name;
    private Bitmap image;

    public Image(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
