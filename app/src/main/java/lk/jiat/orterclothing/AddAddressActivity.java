// AddAddressActivity.java
package lk.jiat.orterclothing;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddAddressActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        Button btnBack = findViewById(R.id.button12);
        btnBack.setOnClickListener(v -> finish());

        EditText displayName = findViewById(R.id.editTextText);
        EditText ownerName = findViewById(R.id.editTextText2);
        EditText address1 = findViewById(R.id.editTextText5);
        EditText address2 = findViewById(R.id.editTextText6);
        EditText zip = findViewById(R.id.editTextText7);

        Button btnSave = findViewById(R.id.button9);
        btnSave.setOnClickListener(v -> {
            String displayNameText = displayName.getText().toString();
            String ownerNameText = ownerName.getText().toString();
            String address1Text = address1.getText().toString();
            String address2Text = address2.getText().toString();
            String zipText = zip.getText().toString();

         if (displayNameText.isEmpty() || ownerNameText.isEmpty() || address1Text.isEmpty() || address2Text.isEmpty() || zipText.isEmpty()) {
             Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
             return;
         }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.COLUMN_DISPLAY_NAME + " = ?", new String[]{displayNameText});
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                Toast.makeText(this, "Display name already exists", Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }
            cursor.close();

            dbHelper.insertAddress(displayNameText, ownerNameText, address1Text, address2Text, zipText);
            Toast.makeText(this, "Address saved", Toast.LENGTH_SHORT).show();
            finish();
        });

    }
}