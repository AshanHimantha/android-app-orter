
package lk.jiat.orterclothing;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "address.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "addresses";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    public static final String COLUMN_OWNER_NAME = "owner_name";
    public static final String COLUMN_ADDRESS1 = "address1";
    public static final String COLUMN_ADDRESS2 = "address2";
    public static final String COLUMN_ZIP = "zip";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DISPLAY_NAME + " TEXT, " +
            COLUMN_OWNER_NAME + " TEXT, " +
            COLUMN_ADDRESS1 + " TEXT, " +
            COLUMN_ADDRESS2 + " TEXT, " +
            COLUMN_ZIP + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertAddress(String displayName, String ownerName, String address1, String address2, String zip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISPLAY_NAME, displayName);
        values.put(COLUMN_OWNER_NAME, ownerName);
        values.put(COLUMN_ADDRESS1, address1);
        values.put(COLUMN_ADDRESS2, address2);
        values.put(COLUMN_ZIP, zip);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

}