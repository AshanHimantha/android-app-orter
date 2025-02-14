package lk.jiat.orterclothing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pl.droidsonroids.gif.GifImageView;


public class SplashActivity extends AppCompatActivity {


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

        GifImageView gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this)
                .load(R.drawable.truck)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(gifImageView);

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

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Log.d(TAG, "Calling checkUserAuthentication after delay");
                    checkUserAuthentication();
                }, 200); // Small delay after the animation
            }
        });
        animator.start(); //Start the animation here

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
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Log.e(TAG, "Error getting ID token", task.getException());
                    runOnUiThread(() -> {
                        Toast.makeText(SplashActivity.this, "Authentication error.", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(SplashActivity.this, GetStartedActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }
            });
        } else {
            Log.d(TAG, "No user signed in. Navigating to GetStartedActivity.");
            Intent intent = new Intent(SplashActivity.this, GetStartedActivity.class);
            startActivity(intent);
            finish();
        }
    }






}