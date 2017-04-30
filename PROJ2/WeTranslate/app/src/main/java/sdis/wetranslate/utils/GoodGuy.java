package sdis.wetranslate.utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

public class GoodGuy {

    public static Drawable setTint(Drawable d, String hexColor) {
        int color = Color.parseColor(hexColor);
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }
}
