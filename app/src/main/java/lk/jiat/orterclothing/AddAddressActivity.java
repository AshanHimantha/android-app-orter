package lk.jiat.orterclothing;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

public class AddAddressActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        // Set window flags for keyboard handling
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        dbHelper = new DatabaseHelper(this);
        scrollView = findViewById(R.id.scrollView2);

        Button btnBack = findViewById(R.id.button12);
        btnBack.setOnClickListener(v -> finish());

        EditText displayName = findViewById(R.id.editTextText);
        EditText f_name = findViewById(R.id.editTextText3);
        EditText l_name = findViewById(R.id.editTextText2);
        EditText address1 = findViewById(R.id.editTextText5);
        EditText address2 = findViewById(R.id.editTextText6);
        EditText zip = findViewById(R.id.editTextText7);
        EditText contact = findViewById(R.id.editTextContact);

        // Set up focus listeners for EditText fields
        setupFocusListener(displayName);
        setupFocusListener(f_name);
        setupFocusListener(l_name);
        setupFocusListener(address1);
        setupFocusListener(address2);
        setupFocusListener(zip);
        setupFocusListener(contact);

        Button btnSave = findViewById(R.id.button9);
        btnSave.setOnClickListener(v -> saveAddress(
                displayName, f_name, l_name,
                address1, address2, zip, contact
        ));
    }

    private void setupFocusListener(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollView.postDelayed(() ->
                        scrollView.smoothScrollTo(0, v.getTop() - 100), 200);
            }
        });
    }

    private void saveAddress(EditText displayName, EditText f_name,
                             EditText l_name, EditText address1,
                             EditText address2, EditText zip,
                             EditText contact) {
        String displayNameText = displayName.getText().toString();
        String fNameText = f_name.getText().toString();
        String lNameText = l_name.getText().toString();
        String address1Text = address1.getText().toString();
        String address2Text = address2.getText().toString();
        String zipText = zip.getText().toString();
        String contactText = contact.getText().toString();

        if (displayNameText.isEmpty() || fNameText.isEmpty() ||
                address1Text.isEmpty() || address2Text.isEmpty() ||
                zipText.isEmpty() || contactText.isEmpty() ||
                lNameText.isEmpty()) {
            Toast.makeText(this, "All fields are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_NAME +
                        " WHERE " + DatabaseHelper.COLUMN_DISPLAY_NAME + " = ?",
                new String[]{displayNameText}
        );

        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            Toast.makeText(this, "Display name already exists",
                    Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }
        cursor.close();

        dbHelper.insertAddress(displayNameText, fNameText, lNameText,
                address1Text, address2Text, zipText, contactText);
        Toast.makeText(this, "Address saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}