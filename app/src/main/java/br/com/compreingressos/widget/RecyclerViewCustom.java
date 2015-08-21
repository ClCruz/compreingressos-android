package br.com.compreingressos.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.crashlytics.android.Crashlytics;

/**
 * Created by zaca on 6/17/15.
 */
public class RecyclerViewCustom extends RecyclerView{
    public RecyclerViewCustom(Context context) {
        super(context);
    }

    public RecyclerViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void stopScroll() {
        try
        {
            super.stopScroll();
        }
        catch( NullPointerException exception )
        {
            Crashlytics.logException(exception);
            exception.printStackTrace();
            /**
             *  The mLayout has been disposed of before the
             *  RecyclerView and this stops the application
             *  from crashing.
             */
        }
    }
}
