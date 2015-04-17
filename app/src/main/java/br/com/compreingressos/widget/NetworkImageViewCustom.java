package br.com.compreingressos.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by luiszacheu on 17/04/15.
 */
public class NetworkImageViewCustom extends NetworkImageView {

    private ProgressBar progress;

    public NetworkImageViewCustom(Context context) {
        super(context);
    }

    public NetworkImageViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkImageViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

	/* Constructor's*/

    public void setImageUrl( String url, ImageLoader imageLoader, ProgressBar progress ) {
        super.setImageUrl( url, imageLoader );
        this.progress = progress;
        initProgress();
    }

    @Override
    public void setImageBitmap( Bitmap bm ) {
        stopProgress();
    }

    private void initProgress() {
        if( progress != null ) {
            if( progress.getVisibility() == View.INVISIBLE )
                this.progress.setVisibility( View.VISIBLE );
            else
                this.progress.setVisibility( View.INVISIBLE );
        }
    }

    private void stopProgress() {
        if( progress != null )
            this.progress.setVisibility( View.INVISIBLE );
    }
}





