package com.elmpool.tallr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.elmpool.tallr.R;
import com.elmpool.tallr.services.Manager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private MaterialButton btnGoogle;
    private MaterialButton btnFacebook;
    private MaterialButton btnTwitter;
    private MaterialButton btnApple;
    private MaterialButton btnMicrosoft;

    private static int SIGN_IN_GOOGLE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Manager.setStatusBar(this);
        auth = FirebaseAuth.getInstance();
        handleButtons();
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void animate(boolean show) {
        int view = show ? View.VISIBLE : View.GONE;
        btnApple.setVisibility(view);
        btnMicrosoft.setVisibility(view);
    }

    private void handleButtons(){
        btnGoogle = findViewById(R.id.btn_google);
        btnFacebook = findViewById(R.id.btn_facebook);
        btnTwitter = findViewById(R.id.btn_twitter);
        btnApple = findViewById(R.id.btn_apple);
        btnMicrosoft = findViewById(R.id.btn_microsoft);

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginGoogle();
            }
        });
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginFacebook();
            }
        });
        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginTwitter();
            }
        });
        btnApple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginApple();
            }
        });
        btnMicrosoft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginMicrosoft();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authGoogle(account);
            } catch (ApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loginGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_GOOGLE);

    }

    private void loginFacebook(){
        loadMain();
    }

    private void loginTwitter(){
        loadMain();
    }

    private void loginApple(){
        loadMain();
    }

    private void loginMicrosoft(){
        loadMain();
    }

    private void loadMain(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void authGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            writeUser();
                            loadMain();
                        } else {
                            Log.e("ERROR", task.getException().getMessage());
                        }
                    }
                });

    }

    private void writeUser(){
        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = auth.getCurrentUser();
        refUser.child(user.getUid()).child("name").setValue(user.getDisplayName());
    }

}
