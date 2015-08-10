package br.com.compreingressos;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.compreingressos.contants.ConstantsGoogleAnalytics;
import br.com.compreingressos.dao.OrderDao;
import br.com.compreingressos.helper.DatabaseHelper;
import br.com.compreingressos.helper.UserHelper;
import br.com.compreingressos.interfaces.WebAppInterfaceListener;
import br.com.compreingressos.model.Order;
import br.com.compreingressos.utils.AndroidUtils;
import br.com.compreingressos.utils.WebAppInterface;


public class CompreIngressosActivity extends ActionBarActivity {

    private static final String LOG_TAG = CompreIngressosActivity.class.getSimpleName();
    private WebView webView;
    private String url;
    private String tituloEspetaculo;

    private Toolbar toolbar;
    private Button btnAvancar;
    private int countReading = 0;
    private ProgressBar progressBar;
    private String codePromo;
    public WebAppInterface webAppInterface;
    boolean hasSupportPinch = true;
    boolean isFirstUrlLoading = true;
    boolean hasAssinatura = false;
    private DatabaseHelper databaseHelper;
    private OrderDao orderDao;


    Tracker t = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compre_ingressos);
        t = ((CompreIngressosApplication) getApplication()).getTracker(CompreIngressosApplication.TrackerName.APP_TRACKER);

        if (getIntent().hasExtra("u")) {
            url = getIntent().getStringExtra("u");
            codePromo = getIntent().getStringExtra("c");
        }

        if (getIntent().hasExtra("titulo_espetaculo"))
            tituloEspetaculo = getIntent().getStringExtra("titulo_espetaculo");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(tituloEspetaculo);
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

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        btnAvancar = (Button) findViewById(R.id.btn_avancar);

        webView = (WebView) findViewById(R.id.webview);

        webView.setWebChromeClient(new WebChromeClient());

        final WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setLoadWithOverviewMode(true);

        webSettings.setUseWideViewPort(true);

        webSettings.setGeolocationEnabled(true);

        webAppInterface = new WebAppInterface(this);


        webView.addJavascriptInterface(webAppInterface, "Android");


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {

                try {
                    if (progressBar.isShown()) {
                        webView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (url.contains("pagamento_ok.php")) {
                    if (countReading > 0) {
                        if (url.contains("assinaturas")) {
                            hasAssinatura = true;
                        }
                        codePromo = "";
                        webView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        view.loadUrl(runScripGetInfoPayment());


                        webAppInterface.loadDataResultFromWebviewListener(new WebAppInterfaceListener() {
                            @Override
                            public String onFinishLoadResultData(String resultData) {
                                Intent intent = new Intent(CompreIngressosActivity.this, PaymentFinishedActivity.class);
                                intent.putExtra("assinatura", hasAssinatura);
                                startActivity(intent);
                                finish();
                                return resultData;
                            }

                            @Override
                            public String onLoadItemsGoogleAnalytics(String resultData) {
                                Log.e(LOG_TAG, "--->> " + resultData);
                                trackOrderWithItemsOnGA(resultData);

                                return resultData;
                            }
                        });
                    }
                    isFirstUrlLoading = false;
                    countReading++;
                }

                if (url.contains("etapa5.php")) {
                    view.loadUrl("javascript:$(\".meu_codigo_cartao\").hide();");
                }

                if (url.contains("etapa1.php")) {
                    if (PreferenceManager.getDefaultSharedPreferences(CompreIngressosActivity.this).getBoolean("show_pinch_screen", true)) {
                        if (countReading == 1) {
                            if (hasSupportPinch) {
                                Intent i = new Intent(CompreIngressosActivity.this, HowToPinchActivity.class);
                                startActivity(i);
                            }
                        }

                        isFirstUrlLoading = false;
                        countReading++;
                    }
                }

                if (url.contains("etapa2.php")) {
                    view.loadUrl(injectPromoCode());
                }

                getCookies(url);

            }


            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {

                return super.shouldOverrideKeyEvent(view, event);
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {

                if (Uri.parse(url).getHost().equals("compra.compreingressos.com") && !url.contains("CHAVES"))
                    url = "http://186.237.201.150:8081/compreingressos2/comprar/etapa1.php?apresentacao=61565";

                if (url.contains("etapa1.php")) {
                    WebSettings webSettings = view.getSettings();
                    webSettings.setBuiltInZoomControls(true);

                    try {
                        webSettings.setDisplayZoomControls(false);
                    } catch (NoSuchMethodError e) {
                        hasSupportPinch = false;
                        e.printStackTrace();
                    }

                    showNextButton();
                    btnAvancar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StringBuilder scriptOnClick = new StringBuilder("javascript:var length = $('.container_botoes_etapas').find('a').length;");
                            scriptOnClick.append("$('.container_botoes_etapas').find('a')[length - 1].click(); ");
                            view.loadUrl(scriptOnClick.toString());
                        }
                    });


                }

                if (Uri.parse(url).getHost().equals("www.compreingressos.com") || Uri.parse(url).getHost().equals("compra.compreingressos.com") || Uri.parse(url).getHost().equals("186.237.201.150")) {
                    view.loadUrl(getUrlFromTokecompre(url));
                    return false;
                }

                return true;

            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, getUrlFromTokecompre(url), isReload);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                if (url.contains("espetaculos")) {
                    webView.setVisibility(View.VISIBLE);
                } else {
                    webView.setVisibility(View.GONE);
                }


                progressBar.setVisibility(View.VISIBLE);

                hideNextButton();

                if (url.contains("etapa1.php")) {
                    trackScreenNameOnGA(ConstantsGoogleAnalytics.WEBVIEW_SEUINGRESSO);
                    toolbar.setTitle("Escolha um assento");
                    showNextButton();
                } else if (url.contains("etapa2.php")) {
                    trackScreenNameOnGA(ConstantsGoogleAnalytics.WEBVIEW_TIPO_INGRESSO);
                    showNextButton();
                    toolbar.setTitle("Tipo de ingresso");
                } else if (url.contains("etapa3.php")) {
                    trackScreenNameOnGA(ConstantsGoogleAnalytics.WEBVIEW_LOGIN);
                    hideNextButton();
                    toolbar.setTitle("Login");
                } else if (url.contains("etapa4.php")) {
                    trackScreenNameOnGA(ConstantsGoogleAnalytics.WEBVIEW_CONFIRMACAO);
                    showNextButton();
                    toolbar.setTitle("Confirmação");
                } else if (url.contains("etapa5.php")) {
                    trackScreenNameOnGA(ConstantsGoogleAnalytics.WEBVIEW_PAGAMENTO);
                    showNextButton();
                    toolbar.setTitle("Pagamento");
                    btnAvancar.setText("Pagar");
                } else if (url.contains("pagamento_ok.php")) {
                    trackScreenNameOnGA(ConstantsGoogleAnalytics.WEBVIEW_FINAL_PAGAMENTO);
                    toolbar.setTitle("Compra Finalizada");
                    hideNextButton();
                } else if (url.contains("espetaculos/")) {
                    if (tituloEspetaculo.equals("Destaque")) {
                        trackScreenNameOnGA(ConstantsGoogleAnalytics.WEBVIEW_DESTAQUE);
                    } else {
                        trackScreenNameOnGA(ConstantsGoogleAnalytics.WEBVIEW_ESPETACULO);
                    }

                    toolbar.setTitle(tituloEspetaculo);
                } else if (url.contains("assinatura")) {
                    trackScreenNameOnGA(ConstantsGoogleAnalytics.WEBVIEW_ASSINATURAS);
                }

            }


            @Override
            public void onLoadResource(WebView view, String url) {
                view.loadUrl("javascript:$(\"#menu_topo\").hide();$('.aba' && '.fechado').hide();$(\"#footer\").hide();$(\"#selos\").hide();");
                view.loadUrl("javascript:$('.imprima_agora').hide();");

                if (url.contains("etapa1.php")) {
                    view.loadUrl("javascript:Android.showToast($(\".destaque_menor_v2\").first().find(\"h3\").text());");
                }

                view.loadUrl("javascript:$(document).ready(function(){$('.voltar').hide();});");

                if (AndroidUtils.isKitKatOrNewer(CompreIngressosActivity.this)) {
                    view.loadUrl("javascript:$(document).ready(function(){$('.container_botoes_etapas').hide();});");
                } else {
                    view.loadUrl("javascript:$(document).ready(function(){$('.container_botoes_etapas').show();});");

                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

            }
        });

        if (getIntent().getStringExtra("url_flux_webview") == null) {
            webView.loadUrl(getUrlFromTokecompre(url));
        } else {
            webView.loadUrl(getIntent().getStringExtra("url_flux_webview"));
        }
    }

    private void getCookies(String url) {
        String cookies = CookieManager.getInstance().getCookie(url);
        Map<String, String> mapCookies = new HashMap<>();
        if (mapCookies != null) {
            try {
                String[] arrayCookies = cookies.split(";");
                for (int i = 0; i < arrayCookies.length; i++) {
                    String[] temp = arrayCookies[i].split("=");
                    if (arrayCookies != null) {
                        if (temp.length > 1) {
                            mapCookies.put(temp[0], temp[1]);
                        }
                    }
                }
            } catch (Exception e) {
                Crashlytics.log(cookies);
                Crashlytics.logException(e);
            }

            for (Object o : mapCookies.keySet()) {
                if (o.toString().contains("user")) {
                    UserHelper.saveUserIdOnSharedPreferences(CompreIngressosActivity.this, mapCookies.get(o.toString()));
                }
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String runScripGetInfoPayment() {
        StringBuilder scriptGetInfoPayment = new StringBuilder("javascript:var date_aux = new Array; \n");
        scriptGetInfoPayment.append("$('.data').children().each(function(){date_aux.push($(this).html())}); \n");
        scriptGetInfoPayment.append("var order_date = date_aux.join(' '); \n");
        scriptGetInfoPayment.append("var spectacle_name = $('.resumo').find('.nome').html(); \n");
        scriptGetInfoPayment.append("var address = $('.resumo').find('.endereco').html(); \n");
        scriptGetInfoPayment.append("var theater = $('.resumo').find('.teatro').html(); \n");
        scriptGetInfoPayment.append("var time = $('.resumo').find('.horario').html(); \n");
        scriptGetInfoPayment.append("var order_number = $('.numero').find('a').html(); \n");
        scriptGetInfoPayment.append("var order_total = $('.pedido_total').find('.valor').html(); \n");
        scriptGetInfoPayment.append("var tickets = new Array; \n");
        scriptGetInfoPayment.append("$('tr').each(function() { \n");
        scriptGetInfoPayment.append("var qrcode = $(this).attr('data:uid'); \n");
        scriptGetInfoPayment.append("if (typeof qrcode !== typeof undefined && qrcode !== false) { \n");
        scriptGetInfoPayment.append("var local   = $(this).find('.local').find('td').html().replace('<br>', '').split('\\n').map(trim).join(' ').trim(); \n");
        scriptGetInfoPayment.append("var type    = $(this).find('.tipo').html(); \n");
        scriptGetInfoPayment.append("var aux     = $(this).find('td'); \n");
        scriptGetInfoPayment.append("var price   = aux.eq(3).children().eq(0).html(); \n");
        scriptGetInfoPayment.append("var service = aux.eq(4).html().replace('R$', ''); \n");
        scriptGetInfoPayment.append("var total   = aux.eq(5).children().eq(0).html(); \n");
        scriptGetInfoPayment.append("tickets.push({ \n");
        scriptGetInfoPayment.append("qrcode:        qrcode, \n");
        scriptGetInfoPayment.append("local:         local, \n");
        scriptGetInfoPayment.append("type:          type, \n");
        scriptGetInfoPayment.append("price:         price, \n");
        scriptGetInfoPayment.append("service_price: service, \n");
        scriptGetInfoPayment.append("total:         total \n");
        scriptGetInfoPayment.append("}); \n");
        scriptGetInfoPayment.append("} \n");
        scriptGetInfoPayment.append("}); \n");
        scriptGetInfoPayment.append("var payload = \n");
        scriptGetInfoPayment.append("{ \n");
        scriptGetInfoPayment.append("number: order_number, \n");
        scriptGetInfoPayment.append("date:   order_date, \n");
        scriptGetInfoPayment.append("total:  order_total, \n");
        scriptGetInfoPayment.append("espetaculo: { \n");
        scriptGetInfoPayment.append("titulo: spectacle_name, \n");
        scriptGetInfoPayment.append("endereco: address, \n");
        scriptGetInfoPayment.append("nome_teatro: theater, \n");
        scriptGetInfoPayment.append("horario: time \n");
        scriptGetInfoPayment.append("}, \n");
        scriptGetInfoPayment.append("ingressos: tickets \n");
        scriptGetInfoPayment.append("}; \n");
        scriptGetInfoPayment.append("Android.getInfoPagamento(JSON.stringify(payload));");
        scriptGetInfoPayment.append("$('.imprima_agora').hide();");
        scriptGetInfoPayment.append(runScriptGetItemsGoogleAnalytics());

        return scriptGetInfoPayment.toString();
    }

    public String injectPromoCode() {
        StringBuilder script = new StringBuilder("javascript:var codigo = " + codePromo + ";\n");
        script.append("var groups = /<a href=\\\"#([\\d]+)\\\" rel=\\\"[\\d]+\\\">PROMO APP<\\/a>/.exec(document.documentElement.outerHTML);\n");
        script.append("var ref;\n");
        script.append("if (groups.length == 2) ref = groups[1];\n");
        script.append("if (ref) {\n");
        script.append("$('a[href=\"#'+ref+'\"]').click();\n");
        script.append("$('.container_beneficio').find('input[type=\"text\"]').val(codigo);\n");
        script.append("$('a[class^=\"validarBin\"]').click();\n");
        script.append("}");


        return script.toString();
    }

    public String runScriptGetItemsGoogleAnalytics() {
        Log.e(LOG_TAG, "entre aqui");
        StringBuilder stringBuilder = new StringBuilder("var regex = /_gaq.push\\(\\['_addItem',[\\W]+'([\\d]+)',[\\W]+'([\\d_]+)',[\\W]+'([\\w \\-\\u00C0-\\u017F]+)',[\\W]+'([\\w \\-\\u00C0-\\u017F]+)',[\\W]+'([\\d\\.,]*)',[\\W]+'(\\d)'/g; var match;var ret = ''; while (match = regex.exec(document.documentElement.outerHTML)) {var i;for(i=1; i<= 6; i++) {ret += match[i]; if (i != 6) { ret += '<.elem,>'}} ret = ret + '<.item,>';}Android.getItemsGoogleAnalytics(ret);");
//        stringBuilder.append("var match; \n");
//        stringBuilder.append("var ret = ''; \n");
//        stringBuilder.append("while (match = regex.exec(document.documentElement.outerHTML)) { \n");
//        stringBuilder.append("var i; \n");
//        stringBuilder.append("for(i=1; i<= 6; i++) { \n");
//        stringBuilder.append("ret += match[i]\n");
//        stringBuilder.append("if (i != 6) {\n");
//        stringBuilder.append("ret += '<.elem,>'\n");
//        stringBuilder.append("}\n");
//        stringBuilder.append("}\n");
//        stringBuilder.append("ret = ret + '<.item,>';\n");
////        stringBuilder.append("console.log('Add Item: ', match[1],', ', match[2],', ', match[3],', ', match[4],', ', match[5],', ', match[6]);\n");
//        stringBuilder.append("}\n");
//        stringBuilder.append("Android.getItemsGoogleAnalytics(ret);");

        Log.e(LOG_TAG, "-> " + stringBuilder.toString());

        return stringBuilder.toString();
    }


    private void showNextButton() {
        if (AndroidUtils.isKitKatOrNewer(CompreIngressosActivity.this)) {
            if (!btnAvancar.isShown()) {
                btnAvancar.setVisibility(View.VISIBLE);
            }
        }

    }

    private void hideNextButton() {
        if (btnAvancar.isShown()) {
            btnAvancar.setVisibility(View.GONE);
        }
    }

    private String getUrlFromTokecompre(String mUrl) {
        Uri.Builder urlResult = Uri.parse(mUrl).buildUpon();

        urlResult.appendQueryParameter("os", "android");
        urlResult.appendQueryParameter("app", "tokecompre");
        return urlResult.toString();
    }


    public void trackScreenNameOnGA(String screenName) {

        t.setScreenName(screenName);
    }

    public void trackOrderWithItemsOnGA(String resultData) {
        databaseHelper = new DatabaseHelper(CompreIngressosActivity.this);
        Order order = null;

        try {
            orderDao = new OrderDao(databaseHelper.getConnectionSource());
            QueryBuilder<Order, Integer> qb = orderDao.queryBuilder();
            qb.orderBy("id", false);
            order = orderDao.queryForFirst(qb.prepare());
            order.setIngressos(new ArrayList<>(order.getIngressosCollection()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Log.e(LOG_TAG, "-> " + order.getTotal());

        List<Product> products = new ArrayList<>();
        String TransactionId = null;

        String[] items = resultData.split("<.item,>");
        for (int i = 0; i < items.length; i++) {
            String[] elements = items[i].split("<.elem,>");
            Log.e(LOG_TAG, "trans - " + elements[0]);
            Log.e(LOG_TAG, "sku - " + elements[1]);
            Log.e(LOG_TAG, "name - " + elements[2]);
            Log.e(LOG_TAG, "category - " + elements[3]);
            Log.e(LOG_TAG, "price - " + elements[4]);
            Log.e(LOG_TAG, "quantity - " + elements[5]);

            TransactionId = elements[0];

            Product product = new Product();
            product.setId(elements[1]);
            product.setName(elements[2]);
            product.setCategory(elements[3]);
            product.setPrice(Integer.parseInt(elements[4]));
            product.setQuantity(Integer.parseInt(elements[5]));

            products.add(product);
        }

        ProductAction productAction = new ProductAction(ProductAction.ACTION_CHECKOUT)
                .setTransactionId(TransactionId)
                .setTransactionAffiliation("compreingressos")
                .setTransactionRevenue(0)
                .setTransactionTax(0)
                .setTransactionShipping(0);


        for (Product product : products) {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder()
                    .addProduct(product)
                    .setProductAction(productAction);

            t.send(screenViewBuilder.build());
        }


    }

}
