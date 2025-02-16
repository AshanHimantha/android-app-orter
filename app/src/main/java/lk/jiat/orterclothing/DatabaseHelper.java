// DatabaseHelper.java
package lk.jiat.orterclothing;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Import Log

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "address.db";
    private static final int DATABASE_VERSION = 2;  // Increment if you added 'contact' after initial release.  Start at 1 if you haven't released yet.

    public static final String TABLE_NAME = "addresses";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    public static final String COLUMN_OWNER_NAME = "owner_name";
    public static final String COLUMN_ADDRESS1 = "address1";
    public static final String COLUMN_ADDRESS2 = "address2";
    public static final String COLUMN_ZIP = "zip";

    public static final String COLUMN_CONTACT = "contact"; // New column

    private static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +  // Use IF NOT EXISTS
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DISPLAY_NAME + " TEXT, " +
                    COLUMN_OWNER_NAME + " TEXT, " +
                    COLUMN_ADDRESS1 + " TEXT, " +
                    COLUMN_ADDRESS2 + " TEXT, " +
                    COLUMN_ZIP + " TEXT, " +
                    COLUMN_CONTACT + " TEXT);"; // Added to create statement


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.d("DatabaseHelper", "onCreate: Database and table created"); // Log success
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "onUpgrade: Upgrading database from version " + oldVersion + " to " + newVersion);

        if (oldVersion < 2) {  // Example upgrade from version 1 to 2 (adding the contact column).  Adjust as needed.
            // Add the contact column
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_CONTACT + " TEXT DEFAULT '';"); // Give it a default value
            Log.d("DatabaseHelper", "onUpgrade: Added contact column");
        }
        // Could add more upgrade logic here if you have further schema changes.
    }

    public void insertAddress(String displayName, String ownerName, String address1,
                              String address2, String zip, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISPLAY_NAME, displayName);
        values.put(COLUMN_OWNER_NAME, ownerName);
        values.put(COLUMN_ADDRESS1, address1);
        values.put(COLUMN_ADDRESS2, address2);
        values.put(COLUMN_ZIP, zip);
        values.put(COLUMN_CONTACT, contact);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }


}