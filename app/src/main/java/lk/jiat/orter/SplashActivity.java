package lk.jiat.orter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.*;

public class SplashActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();

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
        animator.setDuration(1000); // 2000 milliseconds = 2 seconds
        animator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            progressBar.setProgress(progress);
        });
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("SplashActivity", "Progress completed successfully");
                checkUserAuthentication();
            }
        });
    }

    private void checkUserAuthentication() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();

                    Log.d("SplashActivity", "ID token: " + idToken);
                    Log.d("SplashActivity", "ID token result: " + currentUser.getUid());

                    // User is signed in, navigate to MainActivity
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    verifyUser(idToken);
                    startActivity(intent);
                } else {
                    Log.e("SplashActivity", "Error getting ID token", task.getException());
                    // Handle error
                }
            });
        } else {
            // No user is signed in, navigate to GetStartedActivity
            Intent intent = new Intent(SplashActivity.this, GetStartedActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void verifyUser(String idToken) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8000/api/verify");
                RequestBody body = RequestBody.create("{}", MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Content-Type", "application/json; utf-8")
                        .addHeader("Authorization", "Bearer " + idToken)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("SplashActivity", "Error verifying user", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
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

