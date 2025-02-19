package lk.jiat.orterclothing;

         import android.animation.Animator;
         import android.animation.AnimatorListenerAdapter;
         import android.animation.ValueAnimator;
         import android.content.Context;
         import android.content.Intent;
         import android.net.ConnectivityManager;
         import android.net.NetworkInfo;
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
             private static final String TAG = "SplashActivity";
             private boolean authenticationCheckStarted = false;
             private Handler handler;
             private ValueAnimator animator;
             private ProgressBar progressBar;

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

                 handler = new Handler(Looper.getMainLooper());
                 initViews();
                 startAnimations();
             }

             private void initViews() {
                 GifImageView gifImageView = findViewById(R.id.gifImageView);
                 Glide.with(this)
                         .asGif()
                         .load(R.drawable.truck)
                         .transition(DrawableTransitionOptions.withCrossFade())
                         .into(gifImageView);

                 progressBar = findViewById(R.id.progressBar);
                 progressBar.setIndeterminate(false);
                 progressBar.setMax(100);
             }

             private void startAnimations() {
                 animator = ValueAnimator.ofInt(0, 100);
                 animator.setDuration(2000);
                 animator.addUpdateListener(animation ->
                     progressBar.setProgress((int) animation.getAnimatedValue())
                 );

                 animator.addListener(new AnimatorListenerAdapter() {
                     @Override
                     public void onAnimationStart(Animator animation) {
                         Log.d(TAG, "Animation Started");
                     }

                     @Override
                     public void onAnimationEnd(Animator animation) {
                         Log.d(TAG, "Animation Ended");
                         handler.postDelayed(() -> {
                             if (isInternetAvailable()) {
                                 checkUserAuthentication();
                             } else {
                                 showNoInternetDialog();
                             }
                         }, 200);
                     }
                 });
                 animator.start();
             }

             private boolean isInternetAvailable() {
                 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                 if (cm == null) return false;

                 NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                 return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
             }

             private void showNoInternetDialog() {
                 Toast.makeText(this, "No internet connection. Retrying...", Toast.LENGTH_SHORT).show();
                 handler.postDelayed(() -> {
                     if (isInternetAvailable()) {
                         checkUserAuthentication();
                     } else {
                         showNoInternetDialog(); // Recursive retry
                     }
                 }, 3000);
             }

             private void checkUserAuthentication() {
                 if (authenticationCheckStarted) {
                     Log.w(TAG, "checkUserAuthentication called more than once!");
                     return;
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
                             navigateToMainActivity();
                         } else {
                             Log.e(TAG, "Error getting ID token", task.getException());
                             runOnUiThread(() -> {
                                 Toast.makeText(this, "Authentication error.", Toast.LENGTH_SHORT).show();
                                 navigateToGetStartedActivity();
                             });
                         }
                     });
                 } else {
                     Log.d(TAG, "No user signed in. Navigating to GetStartedActivity.");
                     navigateToGetStartedActivity();
                 }
             }

             private void navigateToMainActivity() {
                 Intent intent = new Intent(this, MainActivity.class);
                 startActivity(intent);
                 finish();
             }

             private void navigateToGetStartedActivity() {
                 Intent intent = new Intent(this, GetStartedActivity.class);
                 startActivity(intent);
                 finish();
             }

             @Override
             protected void onDestroy() {
                 super.onDestroy();
                 if (animator != null) {
                     animator.cancel();
                 }
                 handler.removeCallbacksAndMessages(null);
             }
         }