package br.com.compreingressos.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by luiszacheu on 09/04/15.
 */
public class CustomNetworkImageView extends NetworkImageView {

    private static final int FADE_IN_TIME_MS = 2500;

    public CustomNetworkImageView(Context context) {
        super(context);
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



}
