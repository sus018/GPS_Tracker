package com.example.gps_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 07-Mar-18.
 */

public class MyDataBase {
    private  MyHelper mh;// for DDL
    private SQLiteDatabase sdb; // for DML

    public MyDataBase(Context c){
        mh = new MyHelper(c,"gps_tracker.db",null, 1);

    }
    public void open(){
        sdb = mh.getWritableDatabase();
    }

    public void closeDb(){

        if (sdb != null)
        {
            sdb.close();
        }

    }


    public void insertlatlong(int lat, int longitude){
        ContentValues cv = new ContentValues();
        cv.put("lat",lat);
        cv.put("longitude",longitude);

        sdb.insert("location",null,cv);//this will insert one row to register table
    }

    public Cursor querylocation(){
        Cursor c = null;
        c = sdb.query("location",null,null,null,null,null,null);
        return c;
    }
//    public Cursor queryregister(int _id){
//        Cursor c = null;
//        c = sdb.query("register",null,"_id = ?",new String[]{""+_id},null,null,null);
//        if (c != null) {
//            Log.d("NOT NULL","NOT NULL");
//        }
//        return c;
//    }


    public class MyHelper extends SQLiteOpenHelper {

        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            //create tables here
            sqLiteDatabase.execSQL("create table location(_id integer primary key, lat text, longitude text);");

        }
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
