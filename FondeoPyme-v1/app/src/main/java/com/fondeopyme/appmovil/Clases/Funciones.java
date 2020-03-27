package com.fondeopyme.appmovil.Clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fondeopyme.appmovil.R;
import com.google.gson.GsonBuilder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Funciones {

    public static String json(Object mClass) {
         return new GsonBuilder().create().toJson(mClass);
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void mostrarAlerta(String mensaje, Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.titulo_alerta)
                .setMessage(mensaje)
                .setNegativeButton(R.string.aceptar, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void mostrarAlerta(int mensaje, Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.titulo_alerta)
                .setMessage(mensaje)
                .setNegativeButton(R.string.aceptar, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private static ProgressDialog progress = null;

    public static void abrirEspera(int mensaje, Activity activity) {
        progress = new ProgressDialog(activity);
        progress.setMessage(activity.getString(mensaje));
        progress.setCancelable(false);
        progress.show();
    }

    public static void cerrarEspera() {
        if (progress != null) progress.dismiss();
    }

    public static String cifrar(String clearText) {
        String seed = ConfigInit.getAppContext().getString(R.string.clave);
        byte[] encryptedText = null;
        try {
            byte[] keyData = seed.getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, ks);
            encryptedText = c.doFinal(clearText.getBytes("UTF-8"));
            return Base64.encodeToString(encryptedText, Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }
    }

    public static String descifrar(String encryptedText) {
        String seed = ConfigInit.getAppContext().getString(R.string.clave);
        byte[] clearText = null;
        try {
            byte[] keyData = seed.getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, ks);
            clearText = c.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
            return new String(clearText, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static void ocultarTeclado(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    public static String armarDominio(String url) {
        url = url.toLowerCase();
        if (url.startsWith("http://")) {
            url = url.replace("http://", "https://");
        }

        if (url.startsWith("https://")) {
            return url;
        } else if (!Patterns.WEB_URL.matcher(url).matches()) {
            return "https://" + url + ConfigInit.getAppContext().getString(R.string.txt_dominio);
        } else {
            return "https://" + url;
        }
    }

}
