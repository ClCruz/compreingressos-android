package br.com.compreingressos;

import android.content.Intent;
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
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.helper.UserHelper;

/**
 * Created by zaca on 9/4/15.
 */
public class LoginActivity extends AppCompatActivity {

    private final static String LOG_TAG = LoginActivity.class.getSimpleName();

    private WebView webView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    String pathUrl;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Intent intent = getIntent();
        if (!intent.getStringExtra("path").isEmpty()){
            pathUrl = intent.getStringExtra("path");
            title = intent.getStringExtra("title");
        }

        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(title);
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
                if (view.isShown()){
                    view.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("login.php")){
                    view.setVisibility(View.VISIBLE);
                    view.loadUrl("javascript:$(\"#selos\").remove();");
                }else if (url.contains("minha_conta.php")){
                     finish();
                }else if (url.contains("espetaculos?auto=true")){
                    UserHelper.cleanUserId(LoginActivity.this);
                    finish();
                }

                getCookies(url);

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                removeHtml(view);
            }
        });

        webView.loadUrl("https://compra.compreingressos.com"+pathUrl);

    }


    private void removeHtml(WebView view) {
        view.loadUrl("javascript:$(\"#identificacaoForm\").children()[1].remove();");
        view.loadUrl("javascript:$(\"#footer\").remove();");
        view.loadUrl("javascript:$(\"#top\").remove();");

    }


    private void getCookies(String url) {
        String cookies = CookieManager.getInstance().getCookie(url);
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

