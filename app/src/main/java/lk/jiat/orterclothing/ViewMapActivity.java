package lk.jiat.orterclothing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class ViewMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        Intent intent = getIntent();
        String storeName = intent.getStringExtra("storeName");
        double latitude = intent.getDoubleExtra("latitude", 0.0);
        double longitude = intent.getDoubleExtra("longitude", 0.0);
        String image = intent.getStringExtra("image");
        String contact = intent.getStringExtra("contact");


        Button call = findViewById(R.id.button18);
        TextView storeNameView = findViewById(R.id.textView63);
        storeNameView.setText(storeName);
        Glide.with(this).load(image).into((ImageView) findViewById(R.id.proImage3));
        TextView contactView = findViewById(R.id.textView64);
        contactView.setText(contact);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contact));
                startActivity(intent);
            }
        });

        MapsFragment mapsFragment = new MapsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("storeName", storeName);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        bundle.putString("image", image);


        TextView text = findViewById(R.id.textView64);
        text.setText(contact);

     mapsFragment.setArguments(bundle);
     getSupportFragmentManager().beginTransaction()
         .replace(R.id.fragmentContainerView, mapsFragment)
         .commit();

        Button back = findViewById(R.id.button16);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}