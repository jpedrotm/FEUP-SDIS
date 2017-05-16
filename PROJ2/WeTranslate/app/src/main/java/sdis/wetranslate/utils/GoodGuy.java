package sdis.wetranslate.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

import sdis.wetranslate.MenuActivity;

public class GoodGuy {

    public static Drawable setTint(Drawable d, String hexColor) {
        int color = Color.parseColor(hexColor);
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    public static void changeActivity(Context currentActivity,Class nextActivity)
    {
        //muda para a atividade desejada
        Intent in =new Intent(currentActivity,nextActivity);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_LAUNCHER);

        currentActivity.startActivity(in);
    }
}
