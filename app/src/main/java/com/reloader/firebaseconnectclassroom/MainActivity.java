package com.reloader.firebaseconnectclassroom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.reloader.firebaseconnectclassroom.Servicios.Constantes;
import com.reloader.firebaseconnectclassroom.Servicios.HelperWs;
import com.reloader.firebaseconnectclassroom.Servicios.MethodWs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    ImageView imageView;
    TextView name, email, id;
    Button signOut;
    GoogleApiClient googleApiClient;
    String idToken;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        name = findViewById(R.id.txtName);
        email = findViewById(R.id.txtEmail);
        id = findViewById(R.id.txtID);
        signOut = findViewById(R.id.button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                 .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    setUserData(user);
                } else {
                    goLogInScreen();
                }
            }
        };

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            // Send token to your backend via HTTPS

                            Log.v("tokeFirebase", idToken);

                            String KeyApi = Constantes.API_KEY;
                            String coursesStates = "ACTIVE";
                            String studentId = "classroom.dev@sacooliveros.edu.pe";
                            String teacherId = "112956248399671464321"; //5to sm

                            MethodWs methodWs = HelperWs.getConfiguration(getApplicationContext()).create(MethodWs.class);
                            Call<ResponseBody> responseBodyCall = methodWs.getCursosMail(coursesStates, studentId, teacherId, idToken);
                            responseBodyCall.enqueue(new Callback<ResponseBody>() {

                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    ResponseBody informacion = response.body();

                                    if (response.isSuccessful()) {
                                        try {
                                            String cadena_respuesta = informacion.string();
                                            Log.v("RsptaResponse", cadena_respuesta);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.e("infoResponseFalse", t.getMessage());
                                }
                            });

                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });
    }

    private void setUserData(FirebaseUser user) {


        Glide.with(this).load(user.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        id.setText(user.getUid());
        String tokenID = String.valueOf(user.getIdToken(true));

        Log.v("tokenID", tokenID);
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(firebaseAuthListener);
        // firebaseAuth.addIdTokenListener(idToken);
    }


    private void goLogInScreen() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }

    }
}
