package com.example.sqliteexample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DBNAME="Student";
    public static final String TBNAME="data";
    public static final String col_1="id";
    public static final String col_2="name";
    public static final String col_3="roll";
    public static final String col_4="stream";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create="create table "+TBNAME+"("+col_1+" integer primary key AUTOINCREMENT,"+col_2+" text,"+col_3+" text,"+col_4+" text)";
        db.execSQL(create);
    }

    public DatabaseHandler(@Nullable Context context) {
        super(context,DBNAME,null,1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TBNAME);
        onCreate(db);
    }
    public long insert(String name,String roll,String str)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(col_2,name);
        values.put(col_3,roll);
        values.put(col_4,str);
        long i=db.insert(TBNAME,null,values);

        db.close();
        return i;
    }
    public Cursor getdata()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        String query="select * from "+TBNAME+" where rowid=4";
        Cursor dt=db.rawQuery(query,null);
        return dt;
    }
}
