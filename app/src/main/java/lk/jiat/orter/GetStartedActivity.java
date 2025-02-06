package lk.jiat.orter;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GetStartedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );


        TextView animatedTextView = findViewById(R.id.textView1);
        TextView animatedTextView2 = findViewById(R.id.textView2);
        TextView animatedTextView3 = findViewById(R.id.textView3);
        TextView animatedTextView4 = findViewById(R.id.textView4);

        // Animate the TextView horizontally
        ObjectAnimator animator = ObjectAnimator.ofFloat(animatedTextView, "translationX", 0f, 500f);
        animator.setDuration(10000); // 2000 milliseconds = 2 seconds
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();


        ObjectAnimator animator2 = ObjectAnimator.ofFloat(animatedTextView2, "translationX", 500f, 0f);
        animator2.setDuration(10000); // 10000 milliseconds = 10 seconds
        animator2.setRepeatCount(ObjectAnimator.INFINITE);
        animator2.setRepeatMode(ObjectAnimator.REVERSE);
        animator2.start();

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(animatedTextView3, "translationX", 0f, 200f);
        animator3.setDuration(10000); // 10000 milliseconds = 10 seconds
        animator3.setRepeatCount(ObjectAnimator.INFINITE);
        animator3.setRepeatMode(ObjectAnimator.REVERSE);
        animator3.start();

        ObjectAnimator animator4 = ObjectAnimator.ofFloat(animatedTextView4, "translationX", 0f, 100f);
        animator4.setDuration(10000); // 10000 milliseconds = 10 seconds
        animator4.setRepeatCount(ObjectAnimator.INFINITE);
        animator4.setRepeatMode(ObjectAnimator.REVERSE);
        animator4.start();

        Button button = findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetStartedActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}