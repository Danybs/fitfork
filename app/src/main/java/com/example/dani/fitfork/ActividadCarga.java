package com.example.dani.fitfork;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

        import android.content.Intent;
        import android.content.pm.ActivityInfo;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;

        import com.google.firebase.auth.FirebaseAuth;

        import pl.tajchert.waitingdots.DotsTextView;

public class ActividadCarga extends AppCompatActivity {



    public DotsTextView dots;
    Carga carga;
    Intent intent;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


    /*
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }*/



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_carga);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //mAuth = FirebaseAuth.getInstance();

        intent = new Intent(this, EmailPasswordActivity.class);
        dots = (DotsTextView) findViewById(R.id.dots);
        carga = new Carga();
        carga.execute();


    }

    private class Carga extends AsyncTask<String, String, String> {

        private String resp = "prueba";

        protected void onPreExecute() {
            dots.showAndPlay();
        }

        protected String doInBackground(String... resp) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser() != null){
                        dots.hideAndStop();
                        startActivity(new Intent(ActividadCarga.this, ActividadPrincipal.class));
                        finish();
                    }
                }
            };*/
            return String.valueOf(resp);
        }

        protected void onPostExecute(String result) {
            dots.hideAndStop();
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
}