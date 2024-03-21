package com.example.choosechef;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// Representa la actividad para modificar el perfil del usuario.
public class MainActivity_mod_perfil extends AppCompatActivity {
    private final String TAG = MainActivity_mod_perfil.class.getSimpleName();
    // Variables para los campos de entrada
    private EditText mNameInput;
    private EditText mAdressInput;
    private EditText mPhoneInput;
    private EditText mPassInput;
    private EditText mNewPassInput;
    private EditText mNewPassConfInput;
    //private Switch mChangePass;
    FastMethods mfastMethods;
    Retrofit retro;
    ProfileResponse ProfileResponse;
    String usuario;
    String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establece el diseño de la actividad.
        setContentView(R.layout.activity_main_mod_perfil);
        //Inicialización de variables
        mNameInput = findViewById(R.id.edt_nombre_mod_perfil);
        mAdressInput = findViewById(R.id.edt_direccion_mod_perfil);
        mPhoneInput = findViewById(R.id.edt_telefono_mod_perfil);


        mPassInput = findViewById(R.id.edt_contraseña_anterior_mod_perfil);
        mNewPassInput = findViewById(R.id.edt_contraseña_nueva_mod_perfil);
        mNewPassConfInput = findViewById(R.id.edt_contraseña2_nueva_mod_perfil);
        //mChangePass = findViewById(R.id.switch_cambio_contraseña);

        retro=FastClient.getClient();
        mfastMethods = retro.create(FastMethods.class);

        ProfileResponse = new ProfileResponse(); // Inicializamos profileResponse
        usuario = getIntent().getStringExtra("usuario");
        pass = getIntent().getStringExtra("pass");
        ProfileResponse.setUser(usuario);
        ProfileResponse.setPassword(pass);

        recuperarDatos();

    }
    public void recuperarDatos(){
        //Comprueba el estado de la conexión de red.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        } else {
            Utils.showToast(MainActivity_mod_perfil.this,"No hay conexión");
        }

        if (networkInfo != null) {
            //call HTTP client recuperar datos
            Call<List<String>> call = mfastMethods.recuperar_info(ProfileResponse.getUser());

            //Ejecutar la llamada de manera asíncrona
            call.enqueue(new Callback<List<String>>() {
                public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                    if (response.isSuccessful()) {
                        List obtenerDatos = response.body();
                        if (obtenerDatos != null) {
                            //Mostrar datos en pantalla
                            mNameInput.setText(obtenerDatos.get(0).toString());
                            mAdressInput.setText(obtenerDatos.get(2).toString());
                            mPhoneInput.setText(obtenerDatos.get(1).toString());

                        } else {
                            // Inicio de sesión fallido, muestra un mensaje de error
                            Utils.showToast(MainActivity_mod_perfil.this, "Obtención de datos incorrecta");
                        }
                    } else {
                        // La llamada no fue exitosa, muestra un mensaje de error
                        Utils.showToast(MainActivity_mod_perfil.this, "Error de conexión");
                    }
                }
                @Override
                public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                    // Error en la llamada, muestra el mensaje de error y registra la excepción
                    t.printStackTrace();
                    Log.e(TAG, "Error en la llamada:" + t.getMessage());
                }
            });
        }


    }

    /**
     * Método para manejar el clic del botón de confirmar cambios de modificación del perfil.
     * Guarda los cambios y dirige al usuario a la pantalla de contenido si es exitoso,
     * muestra un mensaje de error si falla.
     * @param view La vista (Button) que se hizo clic.
     */
    public void confirmaCambios(View view) {
        String queryNameString = mNameInput.getText().toString();
        String queryAdressString = mAdressInput.getText().toString();
        String queryPhoneString = mPhoneInput.getText().toString();
        String queryPassString = mPassInput.getText().toString();
        String queryNewPassString = mNewPassInput.getText().toString();
        String queryNewPassConfString = mNewPassConfInput.getText().toString();



        //construir el objeto ModificarUsuarioRequest con los datos ingresados
        ModificarUsuarioRequest request = new ModificarUsuarioRequest();
        request.setNombre(queryNameString);
        request.setUbicacion(queryAdressString);
        request.setTelefono(queryPhoneString);
        request.setId(request.getId());
        request.setDescripcion(" ");
        request.setEmail(" ");

        request.setUsuario(ProfileResponse.getUser());
        request.setTipo(" ");
        if(queryPassString.equals(ProfileResponse.getPassword()) ){
            if (queryNewPassString.equals(queryNewPassConfString)){
                request.setPassword(queryNewPassString);
            }else{
                Utils.showToast(MainActivity_mod_perfil.this,"La contraseña nueva no coincide");
                request.setPassword(ProfileResponse.getPassword());
            }
        }else{
            Utils.showToast(MainActivity_mod_perfil.this,"La contraseña anterior no es correcta");
            request.setPassword(ProfileResponse.getPassword());
        }

        //Comprueba el estado de la conexión de red.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        } else {
            Utils.showToast(MainActivity_mod_perfil.this,"No hay conexión");
        }

        // Comprueba si el campo nombre tiene texto
        if (networkInfo != null) {
            // call HTTP client's modify method
            Call<String> call = mfastMethods.modificarUsuario(request);
            // Ejecutar la llamada de manera asíncrona
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body();

                       // Utils.gotoActivity(MainActivity_mod_perfil.this, MainActivity_contenido.class);
                        Utils.showToast(MainActivity_mod_perfil.this, responseBody);
                    } else {
                        Utils.showToast(MainActivity_mod_perfil.this, "Error al modificar usuario");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                    Log.e(TAG, "Error en la llamada: " + t.getMessage());
                    Utils.showToast(MainActivity_mod_perfil.this, "Error en la llamada: " + t.getMessage());
                }
            });
        } else {
            Utils.showToast(MainActivity_mod_perfil.this, "No hay conexión a internet");
        }

            //Onresponse + onfailure
            //Utils.gotoActivity(MainActivity_mod_perfil.this, MainActivity_contenido.class);
            //Call<ProfileResponse> call = mfastMethods.login(queryPhoneString);



        // Comprueba si el campo dirección tiene texto
        /*if ((networkInfo != null) && (queryAdressString.length() != 0)) {

        }

        // Comprueba si el campo teléfono tiene texto
        if ((networkInfo != null) && (queryPhoneString.length() != 0)) {

        }

        // Comprueba si el check está activo
       // if ((networkInfo != null) && (mChangePass.isChecked())) {

        //}*/
    }
}