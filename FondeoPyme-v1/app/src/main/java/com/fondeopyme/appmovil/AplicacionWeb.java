package com.fondeopyme.appmovil;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fondeopyme.appmovil.Clases.Funciones;
import com.fondeopyme.appmovil.Clases.Variables;

import java.util.HashMap;

public class AplicacionWeb extends AppCompatActivity {

    private WebView webView;
    private AplicacionWeb activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aplicacion_web);

        activity = this;

        webView = findViewById(R.id.navegacion);
        webView.setWebViewClient(new CustomWebClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(false);
        webSettings.setAppCacheEnabled(true);

        final String url = Funciones.armarDominio(Variables.url) + getString(R.string.path);

        final String cookie = Variables.session_id;

        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        Funciones.abrirEspera(R.string.cargando, activity);

        // Se deben vaciar las cookies de la caché de la aplicación para agregar de nuevo la cookie actual que es válida.
        // Se evita así errores inesperados
        cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean value) {
                cookieManager.setCookie(Funciones.armarDominio(Variables.url), cookie);
                cookieManager.flush();

                webView.loadUrl(url);
            }
        });
    }

    private class CustomWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            int mensaje ;
            // Aunque el certificado de la web app sea incorrecto o inválido, se permite que siempre continúe la navegación
            switch(error.getPrimaryError()) {
                case SslError.SSL_DATE_INVALID:
                    mensaje = R.string.notification_error_ssl_date_invalid;
                    handler.proceed();
                    break;
                case SslError.SSL_EXPIRED:
                    mensaje = R.string.notification_error_ssl_expired;
                    handler.proceed();
                    break;
                case SslError.SSL_IDMISMATCH:
                    mensaje = R.string.notification_error_ssl_idmismatch;
                    handler.proceed();
                    break;
                case SslError.SSL_INVALID:
                    mensaje = R.string.notification_error_ssl_invalid;
                    handler.proceed();
                    break;
                case SslError.SSL_NOTYETVALID:
                    mensaje = R.string.notification_error_ssl_not_yet_valid;
                    handler.proceed();
                    break;
                case SslError.SSL_UNTRUSTED:
                    mensaje = R.string.notification_error_ssl_untrusted;
                    handler.proceed();
                    break;
                default:
                    mensaje = 0;
            }
            if (mensaje == 0) {
                handler.proceed();
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            switch (error.getErrorCode()) {
                case WebViewClient.ERROR_CONNECT:
                    super.onReceivedError(view, request, error);
                    break;
                default:
                    Funciones.mostrarAlerta(R.string.error_web, activity);
                    webView.setVisibility(View.INVISIBLE);
                    finish();
                    break;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.contains("/logout") || url.contains("/login")) {
                // Detección del logout por parte del usuario para regresar al login.
                Funciones.abrirEspera(R.string.cerrando_sesion, activity);
                webView.setVisibility(View.GONE);
                webView.stopLoading();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("CierreSesion", true);
                startActivity(intent);
                finish();
            } else {
                Funciones.cerrarEspera();
            }
        }
    }
}
