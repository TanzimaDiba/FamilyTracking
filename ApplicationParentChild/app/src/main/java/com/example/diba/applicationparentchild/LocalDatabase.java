package com.example.diba.applicationparentchild;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDatabase {

    public static final String KEY_ROWID="_id";
    public static final String KEY_DATE = "record_date";
    public static final String KEY_EMAIL = "parent_email";
    public static final String KEY_CHILD = "child_name";
    public static final String KEY_LOCATION = "location_name";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LON = "longitude";

    private static final String DB_NAME = "Database_Local";
    private static final String DB_TABLE = "localTable";
    private static final int DB_VERSION = 1;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL("CREATE TABLE " + DB_TABLE + " (" +
                            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_DATE + " TEXT NOT NULL, " +
                            KEY_EMAIL + " TEXT NOT NULL, " +
                            KEY_CHILD + " TEXT NOT NULL, " +
                            KEY_LOCATION + " TEXT NOT NULL, " +
                            KEY_LAT + " INTEGER, " +
                            KEY_LON + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }

    }

    public LocalDatabase(Context c){
        ourContext =c;
    }

    public LocalDatabase open()throws SQLException {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;

    }

    public void close(){
        ourHelper.close();
    }

    public long initialEntry(String record_date,String email,String child_name,String location_name,double lat,double lon) {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE, record_date);
        cv.put(KEY_EMAIL, email);
        cv.put(KEY_CHILD, child_name);
        cv.put(KEY_LOCATION, location_name);
        cv.put(KEY_LAT, lat);
        cv.put(KEY_LON, lon);

        return ourDatabase.insert(DB_TABLE, null, cv);
    }

    public String getTotaldata() {
        // TODO Auto-generated method stub
        String[] columns =new String[]{KEY_ROWID, KEY_DATE, KEY_EMAIL, KEY_CHILD,KEY_LOCATION, KEY_LAT, KEY_LON};
        Cursor c= ourDatabase.query(DB_TABLE, columns, null, null, null, null,null,null);
        String result="";

        int iRow = c.getColumnIndex(KEY_ROWID);
        int iEmail = c.getColumnIndex(KEY_EMAIL);
        int iChild = c.getColumnIndex(KEY_CHILD);
        int iLocation = c.getColumnIndex(KEY_LOCATION);
        int iLat = c.getColumnIndex(KEY_LAT);
        int iLon = c.getColumnIndex(KEY_LON);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            result=result + c.getString(iRow) + " " + c.getString(iEmail) + " " + c.getString(iChild) + " " + c.getString(iLocation) + " " + c.getString(iLat) +
                    " " + c.getString(iLon)  +"\n";
        }
        return result;
    }

    public Cursor getData(String currentDate, String childName) {
        // TODO Auto-generated method stub
        String[] columns =new String[]{KEY_ROWID, KEY_DATE, KEY_EMAIL, KEY_CHILD,KEY_LOCATION, KEY_LAT, KEY_LON};
        Cursor mCursor = ourDatabase.query(DB_TABLE, columns, KEY_DATE + "=?" + " AND " + KEY_CHILD + "=?",  new String[]{currentDate, childName}, null, null,  null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void deleteEntry(int rowId) {
        // TODO Auto-generated method stub
        ourDatabase.delete(DB_TABLE, KEY_ROWID + "=" + rowId, null);
    }
}
