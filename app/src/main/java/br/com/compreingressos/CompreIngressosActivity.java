package br.com.compreingressos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import br.com.compreingressos.utils.WebAppInterface;


public class CompreIngressosActivity extends ActionBarActivity {

    private static final String LOG_TAG = CompreIngressosActivity.class.getSimpleName();
    private static final String URL_ESPETACULOS = "http://www.compreingressos.com/?app=tokecompre";
    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compre_ingressos);

        webView = (WebView) findViewById(R.id.webview);

        webView.setWebChromeClient(new WebChromeClient());

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setLoadWithOverviewMode(true);

        webSettings.setUseWideViewPort(true);

        webSettings.setGeolocationEnabled(true);



        webView.addJavascriptInterface(new WebAppInterface(this), "Android");


        webView.setWebViewClient(new WebViewClient() {

            ProgressDialog progressDialog;

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("onPageFinished", "Carregou????");
                view.loadUrl("javascript:Android.showToast($(\".destaque_menor_v2\").first().find(\"h3\").text());");


                if (url.contains("etapa3.php")){
//                    Log.w(LOG_TAG, "entrou para carregar o source");
//                    String fulljs = "javascript:$('#identificacaoForm').on('submit', function(e){Android.getLogin($('#identificacaoForm').serialize());})";
//                    Log.w(LOG_TAG, "" + fulljs);
//                    view.loadUrl(fulljs);
                }
                Log.e(LOG_TAG, "antes de fechar o  loading");
                try{
                    if (progressDialog.isShowing()) {
                        view.setVisibility(View.VISIBLE);
                        Log.e(LOG_TAG, "fechar o  loading");
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                Log.e("shouldOverrideKeyEvent", " keyevent -" + event);

                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                if (Uri.parse(url).getHost().equals("compra.compreingressos.com"))
                    url = "http://186.237.201.132:81/compreingressos2/comprar/etapa1.php?apresentacao=61566&eventoDS=COSI%20FAN%20TUT%20TE";

                if (Uri.parse(url).getHost().equals("www.compreingressos.com") || Uri.parse(url).getHost().equals("compra.compreingressos.com") || Uri.parse(url).getHost().equals("186.237.201.132")) {
                    // This is my web site, so do not override; let my WebView load the page
                    view.loadUrl(url);
                    return false;
                }
                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("onPageStarted", "Iniciou");
                view.setVisibility(View.GONE);
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(CompreIngressosActivity.this);
                    progressDialog.setMessage("Aguarde...");
                    progressDialog.show();
                }
            }


            @Override
            public void onLoadResource(WebView view, String url) {
                Log.e("onLoadResource", "Passou aqui");
                view.loadUrl("javascript:$(\"#menu_topo\").hide();$('.aba' && '.fechado').hide();$(\"#footer\").hide();$(\"#selos\").hide();");
            }


//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                Log.e("shoudInterceptRequest", "intercepta requests");
//                Log.e("shoudInterceptRequest", "intercepta requests ---- > " + request.toString() );
//                return super.shouldInterceptRequest(view, request);
//            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.e("onReceivedSslError", "aqui");
            }


        });
        webView.loadUrl(URL_ESPETACULOS);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compre_ingressos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}
