package com.fondeopyme.appmovil.Clases;

import android.app.Application;
import android.content.Context;

import com.fondeopyme.appmovil.R;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ConfigInit extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();

        // Deshabilitación de comprobación de certificados para adminitr certificados no válidos o autofirmados
        disableSSLCertificateChecking();

    }

    public static Context getAppContext() {
        return appContext;
    }

    public static TrustManager[] certificados() {
        return new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };
    }

    public static void disableSSLCertificateChecking() {
        try {

            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());

            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, certificados(), new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            // Autorización del host de servidores
            boolean returnValue = false;
            try {
                URI url = new URI(Funciones.armarDominio(Variables.url));
                if (hostname.toLowerCase().equals(url.getHost())) returnValue = true;
            } catch(Exception ex) {

            }
            if (!returnValue) {
                throw new RuntimeException("Host not verified " + hostname);
            }
            return returnValue;
        }

    }

}
