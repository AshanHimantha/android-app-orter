package lk.jiat.orter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import okhttp3.*;

public class SplashActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private final String BACKEND_URL = "http://10.0.2.2:8000/api/verify";
    private static final String TAG = "SplashActivity"; //For easy Log filtering

    private boolean authenticationCheckStarted = false;  //Tracking Variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(2000); // Adjust duration as needed
        animator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            progressBar.setProgress(progress);
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "Animation Started");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "Animation Ended");
                bringAppToForeground();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Log.d(TAG, "Calling checkUserAuthentication after delay");
                    checkUserAuthentication();
                }, 200); // Small delay after the animation
            }
        });
        animator.start(); //Start the animation here

    }

    private void bringAppToForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
            if (tasks != null && !tasks.isEmpty()) {
                tasks.get(0).moveToFront();
            }
        }
    }

    private void checkUserAuthentication() {
        if(authenticationCheckStarted){
            Log.w(TAG, "checkUserAuthentication called more than once!");
            return; //Prevent multiple calls
        }
        authenticationCheckStarted = true;
        Log.d(TAG, "Starting checkUserAuthentication...");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "User is signed in. Getting ID Token...");
            currentUser.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    Log.d(TAG, "ID token: " + idToken);
                    Log.d(TAG, "User ID: " + currentUser.getUid());
                    verifyUser(idToken);
                } else {
                    Log.e(TAG, "Error getting ID token", task.getException());
                    runOnUiThread(() -> {
                        Toast.makeText(SplashActivity.this, "Authentication error.", Toast.LENGTH_SHORT).show();
                        navigateToGetStarted();
                    });
                }
            });
        } else {
            Log.d(TAG, "No user signed in. Navigating to GetStartedActivity.");
            navigateToGetStarted();
        }
    }

    private void navigateToGetStarted() {
        Log.d(TAG, "Navigating to GetStartedActivity");
        Intent intent = new Intent(SplashActivity.this, GetStartedActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToHomeActivity() {
        Log.d(TAG, "Navigating to HomeActivity");
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void verifyUser(String idToken) {
        Log.d(TAG, "Verifying user with backend...");
        new Thread(() -> {
            try {
                URL url = new URL(BACKEND_URL);
                RequestBody body = RequestBody.create("{}".getBytes(), MediaType.parse("application/json; charset=utf-8"));

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .header("Content-Type", "application/json; charset=utf-8")
                        .header("Authorization", "Bearer " + idToken)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Error verifying user", e);
                        runOnUiThread(() -> {
                            Toast.makeText(SplashActivity.this, "Network error verifying user.", Toast.LENGTH_SHORT).show();
                            navigateToGetStarted();
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "User verified successfully");
                            runOnUiThread(() -> navigateToHomeActivity());
                        } else {
                            String responseBody = response.body() != null ? response.body().string() : "No body";
                            Log.e(TAG, "User verification failed. Response code: " + response.code() + ", Body: " + responseBody);
                            runOnUiThread(() -> {
                                Toast.makeText(SplashActivity.this, "User verification failed.", Toast.LENGTH_SHORT).show();
                                navigateToGetStarted();
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error during verification process", e);
                runOnUiThread(() -> {
                    Toast.makeText(SplashActivity.this, "Error during verification process.", Toast.LENGTH_SHORT).show();
                    navigateToGetStarted();
                });
            }
        }).start();
    }
}