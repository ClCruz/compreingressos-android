package br.com.compreingressos.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class CustomTypeFace {


    private static Typeface typefaceLora;


    public static Typeface setFontLora(Context context){
        if (typefaceLora == null){
            typefaceLora = Typeface.createFromAsset(context.getAssets(), "fonts/Lora-Regular.ttf");
        }

        return typefaceLora;
    }
}
