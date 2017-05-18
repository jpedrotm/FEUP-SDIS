package sdis.wetranslate.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

import sdis.wetranslate.MenuActivity;
import sdis.wetranslate.NewTranslationFragment;
import sdis.wetranslate.R;
import sdis.wetranslate.TranslateFragment;
import sdis.wetranslate.ViewRequestsFragment;
import sdis.wetranslate.ViewTranslationsFragment;

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

    public static void changeFragment(FragmentType type,Activity activity){
        Fragment newFragment=null;
        boolean change=true;
        switch(type){
            case NEW_TRANSLATION:
                newFragment = new NewTranslationFragment();
                break;
            case TRANSLATE:
                newFragment=new TranslateFragment();
                break;
            case VIEW_REQUESTS:
                newFragment=new ViewRequestsFragment();
                break;
            case VIEW_TRANSLATIONS:
                newFragment=new ViewTranslationsFragment();
                break;
            default:
                change=false;
                break;
        }

        if(change){
            FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public enum FragmentType{
        NEW_TRANSLATION,TRANSLATE,VIEW_REQUESTS,VIEW_TRANSLATIONS
    }
}
