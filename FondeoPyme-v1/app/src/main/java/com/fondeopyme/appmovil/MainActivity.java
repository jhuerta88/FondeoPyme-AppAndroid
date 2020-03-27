package com.fondeopyme.appmovil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fondeopyme.appmovil.Clases.Autenticacion;
import com.fondeopyme.appmovil.Clases.Funciones;
import com.fondeopyme.appmovil.Clases.Respuesta;
import com.fondeopyme.appmovil.Clases.Variables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private MainActivity activity;
    private TextView txtContra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        findViewById(R.id.btnIniciarSesion).setOnClickListener(this);

        boolean conectar = false;
        boolean revalidar = false;

        Intent localIntent = getIntent();
        sharedPreferences = getSharedPreferences("DatosSesion", Context.MODE_PRIVATE);

        // Si la apertura de este Intent no es por el cierre de sesión del web view entonces se cargan los datos anteriores
        if (!localIntent.getBooleanExtra("CierreSesion", false)) {

            // Recuperar datos de sesión
            String datosConexion =  sharedPreferences.getString("datosConexion", "");
            if (datosConexion.length() > 0) {

                // Los datos de sesión se deben descifrar
                datosConexion = Funciones.descifrar(datosConexion);

                if (datosConexion.length() > 0) {

                    // Los datos de sesión están en la mismca cadena y deben ser separados
                    String[] valores = datosConexion.split("\\|\\*\\|");

                    if (valores.length > 0) {
                        Variables.url = valores[0];
                        Variables.usuario = valores[1];
                        Variables.contra = valores[2];

                        long expiracion;
                        try {
                            expiracion = Long.parseLong(valores[4]);
                        } catch (Exception e) {
                            expiracion = 0;
                        }

                        if (expiracion > System.currentTimeMillis() && valores[3].length() > 0) {

                            // Si la cooike sigue vigente, se conecta directamente
                            Variables.session_id = valores[3];
                            conectar = true;
                        } else if (expiracion < System.currentTimeMillis() && Variables.contra.length() > 0) {

                            // Si la coodie ha vencido se revalida
                            Variables.session_id = "";
                            revalidar = true;
                        }
                    }
                }
            }
        }

        final TextInputEditText txtUrl = findViewById(R.id.txtUrl);
        final TextInputLayout txtUrlLayout = findViewById(R.id.txtUrlLayout);

        txtUrl.setText(Variables.url);
        ((TextInputEditText) findViewById(R.id.txtUsuario)).setText(Variables.usuario);

        txtUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Patterns.WEB_URL.matcher(editable).matches()) {
                    txtUrlLayout.setError(null);
                } else {
                    txtUrlLayout.setError(getString(R.string.txt_url_empresa));
                }
            }
        });

        if (conectar) {
            // Conectar con la cookie guardada
            findViewById(R.id.pnlSesion).setVisibility(View.INVISIBLE);
            Funciones.abrirEspera(R.string.validando_sesion, activity);
            Intent intent = new Intent(getBaseContext(), AplicacionWeb.class);
            startActivity(intent);
            finish();
        } else if (revalidar) {
            // Revalidad credenciales para recuperar una cooke nueva
            findViewById(R.id.pnlSesion).setVisibility(View.INVISIBLE);
            conectar();
        } else {
            // Se inicia el login de manera normal. Esto ocurre cuando se abre por primera vez o se ha cerrado la sesión
            findViewById(R.id.pnlSesion).setVisibility(View.VISIBLE);

            Variables.contra = "";
            Variables.session_id = "";

            // Se almacena el valor de la sesión para borrar cualquier otro dato anterior que pueda ya no ser válido
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String datosConexon = Variables.url + "|*|" + Variables.usuario + "|*|" + Variables.contra + "|*|" + Variables.session_id + "|*|0";
            editor.putString("datosConexion", Funciones.cifrar(datosConexon));
            editor.apply();
            editor.commit();

        }


        // Comprobación de tablets
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        if (width > 1023) {
            LinearLayout layout = findViewById(R.id.pnlSesion);
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.width = 1023;
            layout.setLayoutParams(params);
        }
    }

    @Override
    public void onClick(View v) {
        TextView txtUsuario = (TextInputEditText) findViewById(R.id.txtUsuario);
        txtContra = (TextInputEditText) findViewById(R.id.txtContra);
        TextView txtUrl = (TextInputEditText) findViewById(R.id.txtUrl);

        Funciones.ocultarTeclado(activity);

        if (txtUsuario.getText().toString().trim().length() == 0 || txtContra.getText().toString().trim().length() == 0 || txtUrl.getText().toString().trim().length() == 0) {
            Funciones.mostrarAlerta(R.string.datos_sesion_incorrectos, activity);
            return;
        }

        try {
            new URL(Funciones.armarDominio(txtUrl.getText().toString().trim()));
        } catch (Exception ex) {
            Funciones.mostrarAlerta(R.string.datos_sesion_incorrectos, activity);
            return;
        }

        Variables.url = txtUrl.getText().toString().trim().toLowerCase();
        Variables.usuario = txtUsuario.getText().toString().trim();
        Variables.contra = txtContra.getText().toString().trim();

        conectar();
    }

    private void conectar() {
        if (!Funciones.isConnected(this)) {
            Funciones.mostrarAlerta(R.string.sin_internet, activity);
            return;
        }

        String url = Funciones.armarDominio(Variables.url) + getString(R.string.authenticatepath);

        Autenticacion autenticacion = new Autenticacion();

        autenticacion.getParams().setDb(getString(R.string.database));
        autenticacion.getParams().setLogin(Variables.usuario);
        autenticacion.getParams().setPassword(Variables.contra);

        ObtenerToken obtenerToken = new ObtenerToken();
        obtenerToken.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, Funciones.json(autenticacion));

        Funciones.abrirEspera(R.string.validando_sesion, activity);
    }

    private class ObtenerToken extends AsyncTask<String, int[], Respuesta> {

        @Override
        protected Respuesta doInBackground(String... params) {

            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;
            Respuesta respuesta = new Respuesta();

            try {

                Uri builtUri = Uri.parse(params[0]).buildUpon().build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setConnectTimeout(30000);
                urlConnection.setReadTimeout(30000);
                urlConnection.addRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                OutputStreamWriter writer =  new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(params[1]);
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    respuesta.setError(new Respuesta.Errores());
                    respuesta.getError().setCode(2);
                    respuesta.getError().setMessage(getString(R.string.error_web));
                    return respuesta;
                }

                if (urlConnection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    respuesta.setError(new Respuesta.Errores());
                    respuesta.getError().setCode(urlConnection.getResponseCode());
                    respuesta.getError().setMessage(urlConnection.getResponseMessage());
                    return respuesta;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    respuesta.setError(new Respuesta.Errores());
                    respuesta.getError().setCode(2);
                    respuesta.getError().setMessage(getString(R.string.error_web));
                    return respuesta;
                }

                try {
                    String respuestaCadena = buffer.toString();

                    respuesta = Respuesta.json(respuestaCadena);

                    String parametros[] = urlConnection.getHeaderField("Set-Cookie").trim().split("\\;");
                    for (String parametro:parametros) {
                        String valor[] = parametro.split("\\=");
                        if (valor[0].trim().equalsIgnoreCase("session_id")) {

                            // Se recupera la cooke del header de la llamada al web service
                            respuesta.setCookie_id(valor[0] + "=" + valor[1]);

                            // Se da un tiempo de expiración de 2 días. Es menor a la cookie y a la sesión, pero en la práctica se comprobó que era lo mejor
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.HOUR, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            cal.add(Calendar.DAY_OF_MONTH, 2);
                            respuesta.setExpiration(cal.getTimeInMillis());
                        }
                    }
                    return respuesta;
                } catch (Exception ex) {
                    respuesta.setError(new Respuesta.Errores());
                    respuesta.getError().setCode(3);
                    respuesta.getError().setMessage(ex.getMessage());
                    return respuesta;
                }

            } catch (IOException e) {
                respuesta.setError(new Respuesta.Errores());
                respuesta.getError().setCode(4);
                respuesta.getError().setMessage(e.getMessage());
                return respuesta;

            } catch (Exception e) {
                respuesta.setError(new Respuesta.Errores());
                respuesta.getError().setCode(5);
                respuesta.getError().setMessage(e.getMessage());
                return respuesta;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {

                    }
                }
            }
        }

        @Override
        protected void onProgressUpdate(int[]... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Respuesta respuesta) {
            Funciones.cerrarEspera();
            if (respuesta.getResult() != null) {
                // Si la sesión es correcta, se reciben el resultado de la sesión
                Variables.session_id = respuesta.getCookie_id();
                findViewById(R.id.pnlSesion).setVisibility(View.INVISIBLE);

                // Se almacenan los valores de sesión, incluyendo la contraseña del usuario
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String datosConexon = Variables.url + "|*|" + Variables.usuario + "|*|" + Variables.contra + "|*|" + Variables.session_id + "|*|" + respuesta.getExpiration();
                // Los datos van cifrados
                editor.putString("datosConexion", Funciones.cifrar(datosConexon));
                editor.apply();
                editor.commit();

                Intent intent = new Intent(getBaseContext(), AplicacionWeb.class);
                startActivity(intent);
                finish();
            } else {
                findViewById(R.id.pnlSesion).setVisibility(View.VISIBLE);
                if (respuesta.getError() != null && respuesta.getError().getData() != null && respuesta.getError().getData().getException_type() != null && respuesta.getError().getData().getException_type().equalsIgnoreCase("access_denied")) {
                    // Si la sesión es inoorrecta, se valida si es un error por usuario o contraseña incorrectos.
                    Funciones.mostrarAlerta(R.string.datos_sesion_incorrectos, activity);
                } else {
                    // Cualquier otro error se muestra de manera genérica
                    Funciones.mostrarAlerta(respuesta.getError().getMessage(), activity);
                }
                txtContra.setText("");
            }
        }
    }

}
