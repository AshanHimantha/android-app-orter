package lk.jiat.orterclothing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private static final String TAG = "EmailPassword";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText emailField, passwordField;

    private CallbackManager callbackManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Facebook SDK BEFORE setContentView
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login); //Make sure this layout exist

        TextView signUpButton = findViewById(R.id.signUp);
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);  // Use LoginActivity.this
            startActivity(intent);
        });

        callbackManager = CallbackManager.Factory.create();

        //Check for logged in User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    assert idToken != null;
                    Log.d("AuthToken", idToken);
                } else {
                    Log.w("AuthToken", "Failed to get token", task.getException());
                }
            });
        } else {
            Log.w("AuthToken", "No user is signed in");
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize UI elements
        EditText emailInputLayout = findViewById(R.id.editTextText4);
        EditText passwordInputLayout = findViewById(R.id.editTextTextPassword);

    if (emailInputLayout != null) {
        emailField = emailInputLayout;
    }
    if (passwordInputLayout != null) {
        passwordField = passwordInputLayout;
    }

        Button btnEmailSignIn = findViewById(R.id.button2);
        Button btnGoogleSignIn = findViewById(R.id.button6);
        Button btnFacebookSignIn = findViewById(R.id.button7);


        // Set onClick listeners
        btnEmailSignIn.setOnClickListener(v -> {
            if (emailField != null && passwordField != null) {  // Check for null
              String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    signIn(email, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Email or password EditText is null!");
                Toast.makeText(LoginActivity.this, "Email or password field missing.", Toast.LENGTH_SHORT).show();
            }
        });


        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

        btnFacebookSignIn.setOnClickListener(v -> signInWithFacebook());


    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        Log.d("Usertoken", loginResult.getAccessToken().getToken());
                        Log.d(TAG, "facebookAuthWithFacebook:success");
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebookAuthWithFacebook:cancel");
                    }

                    @Override
                    public void onError(@NonNull FacebookException exception) {
                        Log.w(TAG, "facebookAuthWithFacebook:failure", exception);

                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");


                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
                Log.d("Usertoken", Objects.requireNonNull(account.getIdToken()));
                Log.d(TAG, "firebaseAuthWithGoogle:success");


            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);

            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");

                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",  // Use LoginActivity.this
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void signIn(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email and password must not be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");

                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.getIdToken(true).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    String idToken = task1.getResult().getToken();
                                    verifyUser(idToken);
                                } else {
                                    Log.w(TAG, "Failed to get token", task1.getException());
                                }
                            });
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Sign in failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyUser(String idToken) {
        new Thread(() -> {
            try {
                URL url = new URL("https://testapi.ashanhimantha.com/api/verify");
                RequestBody body = RequestBody.create("{}", MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Content-Type", "application/json; utf-8")
                        .addHeader("Authorization", "Bearer " + idToken)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("SplashActivity", "Error verifying user", e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        if (response.isSuccessful()) {
                            Log.d("SplashActivity", "User verified successfully");
                        } else {
                            Log.d("SplashActivity", "User verification failed with response code: " + response.code());
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("SplashActivity", "Error verifying user", e);
            }
        }).start();
    }
}