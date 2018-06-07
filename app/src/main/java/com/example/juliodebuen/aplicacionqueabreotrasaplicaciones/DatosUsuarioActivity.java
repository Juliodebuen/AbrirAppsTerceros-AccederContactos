package com.example.juliodebuen.aplicacionqueabreotrasaplicaciones;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import static com.example.juliodebuen.aplicacionqueabreotrasaplicaciones.MainActivity.googleApiClient;

public class DatosUsuarioActivity extends AppCompatActivity {

    TextView textView;
    Button btnSignOut;
    String nombre, loggedWith;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_usuario);

        init();
        initButton();
    }

    private void init(){
        Bundle extras = getIntent().getExtras();
        if(!extras.isEmpty()) {
            nombre = extras.getString("nombre");
            loggedWith = extras.getString("log with");
            textView = findViewById(R.id.textView);
            textView.setText("Bienvenido "+nombre + "\nTe logueaste con tu cuenta "+loggedWith);
        }
    }

    private void initButton(){
        btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (loggedWith){
                    case "google":
                        logoutGoogle();
                        break;
                    case "facebook":
                        LoginManager.getInstance().logOut();
                        finish();
                        break;
                }
            }
        });
    }

    public void logoutGoogle() {
        googleApiClient.connect();
        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                if(googleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("", "User Logged out");
                                Intent intent = new Intent(DatosUsuarioActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("", "Google API Client Connection Suspended");
            }
        });
    }

}
