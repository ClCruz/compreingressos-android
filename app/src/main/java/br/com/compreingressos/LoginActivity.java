package br.com.compreingressos;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.helper.UserHelper;
import br.com.compreingressos.session.SessionManager;

/**
 * Created by zaca on 9/4/15.
 */
public class LoginActivity extends AppCompatActivity {

    private final static String LOG_TAG = LoginActivity.class.getSimpleName();

    private SessionManager session;
    private WebView webView;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Login");
            toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            setSupportActionBar(toolbar);

            if (Build.VERSION.SDK_INT >= 21) {
                this.setTheme(R.style.Base_ThemeOverlay_AppCompat_Dark);
                toolbar.setBackgroundColor(getResources().getColor(R.color.red_compreingressos));
                getWindow().setStatusBarColor(getResources().getColor(R.color.red_status_bar));
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_action_close_white));
            } else {
                toolbar.setTitleTextColor(getResources().getColor(R.color.red_compreingressos));
                getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_action_close));
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        webView = (WebView) findViewById(R.id.webview_login);

        webView.setWebChromeClient(new WebChromeClient());

        final WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setLoadWithOverviewMode(true);

        webSettings.setUseWideViewPort(true);

        webSettings.setGeolocationEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

//                view.loadUrl("javascript:$(\"#selos\").remove();");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                view.loadUrl("javascript:$(\"#identificacaoForm\").children()[1].remove();");
                view.loadUrl("javascript:$(\"#footer\").remove();");
            }
        });

        webView.loadUrl("https://compra.compreingressos.com/comprar/login.php");

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void getCookies(String url) {
        String cookies = CookieManager.getInstance().getCookie(url);
        Log.e(LOG_TAG, "getCookies --> + " + CookieManager.getInstance().getCookie(url));
        Map<String, String> mapCookies = new HashMap<>();
        if (mapCookies != null) {
            if (cookies.contains(";")) {
                String[] arrayCookies = cookies.split(";");
                for (int i = 0; i < arrayCookies.length; i++) {
                    String[] temp = arrayCookies[i].split("=");
                    if (arrayCookies != null) {
                        if (temp.length > 1) {
                            mapCookies.put(temp[0], temp[1]);
                        }
                    }
                }
            }

            for (Object o : mapCookies.keySet()) {
                if (o.toString().contains("user")) {
                    UserHelper.saveUserIdOnSharedPreferences(LoginActivity.this, mapCookies.get(o.toString()));
                }
            }
        }

    }


}

