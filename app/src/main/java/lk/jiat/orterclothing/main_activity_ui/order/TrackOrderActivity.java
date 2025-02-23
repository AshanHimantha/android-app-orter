package lk.jiat.orterclothing.main_activity_ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import lk.jiat.orterclothing.R;

public class TrackOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_track_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        Intent intent = getIntent();
        String url = intent.getStringExtra("tracking_url");

        Toast.makeText(this, "wada naa"+ url, Toast.LENGTH_SHORT).show();

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
             view.evaluateJavascript("document.querySelector('main[role=\"main\"]').style.display='block'; document.body.innerHTML = document.querySelector('main[role=\"main\"]').outerHTML;", null);

            }
        });

        Button back = findViewById(R.id.button110);
        back.setOnClickListener(v -> {
            finish();
        });


    }
}