package lk.jiat.orterclothing;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "address.db";
    private static final int DATABASE_VERSION = 3;  // Increment version for schema change

    public static final String TABLE_NAME = "addresses";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    public static final String COLUMN_F_NAME = "f_name";  // Changed from owner_name
    public static final String COLUMN_L_NAME = "l_name";  // New column
    public static final String COLUMN_ADDRESS1 = "address1";
    public static final String COLUMN_ADDRESS2 = "address2";
    public static final String COLUMN_ZIP = "zip";
    public static final String COLUMN_CONTACT = "contact";

    private static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DISPLAY_NAME + " TEXT, " +
                    COLUMN_F_NAME + " TEXT, " +
                    COLUMN_L_NAME + " TEXT, " +
                    COLUMN_ADDRESS1 + " TEXT, " +
                    COLUMN_ADDRESS2 + " TEXT, " +
                    COLUMN_ZIP + " TEXT, " +
                    COLUMN_CONTACT + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.d("DatabaseHelper", "onCreate: Database and table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "onUpgrade: Upgrading database from version " + oldVersion + " to " + newVersion);

        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_CONTACT + " TEXT DEFAULT '';");
            Log.d("DatabaseHelper", "onUpgrade: Added contact column");
        }

        if (oldVersion < 3) {
            // Add l_name column
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_L_NAME + " TEXT DEFAULT '';");
            // Rename owner_name to f_name
            db.execSQL("ALTER TABLE " + TABLE_NAME + " RENAME COLUMN owner_name TO " + COLUMN_F_NAME + ";");
            Log.d("DatabaseHelper", "onUpgrade: Renamed owner_name to f_name and added l_name column");
        }
    }

    public void insertAddress(String displayName, String firstName, String lastName,
                              String address1, String address2, String zip, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISPLAY_NAME, displayName);
        values.put(COLUMN_F_NAME, firstName);
        values.put(COLUMN_L_NAME, lastName);
        values.put(COLUMN_ADDRESS1, address1);
        values.put(COLUMN_ADDRESS2, address2);
        values.put(COLUMN_ZIP, zip);
        values.put(COLUMN_CONTACT, contact);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
}