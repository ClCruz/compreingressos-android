package br.com.compreingressos.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class CustomTypeFace {
    public static Typeface setFontLora(Context context){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lora-Regular.ttf");

        return typeface;
    }
}
