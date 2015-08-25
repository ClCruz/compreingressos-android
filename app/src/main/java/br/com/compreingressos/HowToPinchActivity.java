package br.com.compreingressos;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by zaca on 5/14/15.
 */
public class HowToPinchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tutorial_pinch);

        ImageView imgClose = (ImageView) findViewById(R.id.img_close);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(HowToPinchActivity.this).edit().putBoolean("show_pinch_screen", false).commit();
                finish();
            }
        });

    }
}
