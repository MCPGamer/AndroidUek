package ch.duartemendes.paircade.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class PaircadePersistedData {
    // So that no one Instantiates
    private PaircadePersistedData() {
    }

    public static class PaircadeData implements BaseColumns {
        public static final String TABLE_NAME = "data";
        public static final String COLUMN_NAME_USERNAME = "username";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PaircadeData.TABLE_NAME + " (" +
                    PaircadeData._ID + " INTEGER PRIMARY KEY," +
                    PaircadeData.COLUMN_NAME_USERNAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PaircadeData.TABLE_NAME;

    public static class PaircadeDataHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "PaircadeData.db";

        public PaircadeDataHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        public String getUsername() {
            SQLiteDatabase db = getReadableDatabase();

            Cursor c = db.rawQuery("SELECT * FROM " + PaircadeData.TABLE_NAME + " order by " + PaircadeData._ID + " desc", null);
            if(c.getCount() > 0){
                c.moveToFirst();
                if (c.getString(c.getColumnIndex(PaircadeData.COLUMN_NAME_USERNAME)) != null) {
                    String username = c.getString(c.getColumnIndex(PaircadeData.COLUMN_NAME_USERNAME));
                    return username;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        public void saveUsername(String username) {
            // Gets the data repository in write mode
            SQLiteDatabase db = getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(PaircadeData.COLUMN_NAME_USERNAME, username);

            if (getUsername() == null) {
                // Insert the new row
                db.insert(PaircadeData.TABLE_NAME, null, values);
            } else {
                String selection = PaircadeData._ID + " LIKE ?";
                String[] selectionArgs = {"1"};

                // Update
                db.update(PaircadeData.TABLE_NAME, values, selection, selectionArgs);
            }
        }
    }
}

