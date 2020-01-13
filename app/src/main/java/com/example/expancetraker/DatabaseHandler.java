package com.example.expancetraker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

import static android.provider.BaseColumns._ID;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ext.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "db_table";
    private static final String SPINNER_COL = "select_catagory";
    private static final String EXPENSE_AMOUNT_COL = "expense_amount";
    private static final String EXPENSE_DATE_COL = "expense_date";
    private static final String EXPENSE_TIME_COL = "expense_time";
    private static final String DOCUMENT_COL = "document";

    private static final String createTable ="CREATE TABLE "+TABLE_NAME+"("+ _ID +" INTEGER PRIMARY KEY AUTOINCREMENT , "+SPINNER_COL+" TEXT, "+EXPENSE_AMOUNT_COL+" INTEGER, "+EXPENSE_DATE_COL+" TEXT, "+EXPENSE_TIME_COL+" TEXT, "+DOCUMENT_COL+" BLOB NOT NULL "+")";



    DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

    }

    long insertToDB(String spinner_val, String exp_amount, String exp_date, String exp_time, byte[] document){

        SQLiteDatabase liteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(SPINNER_COL,spinner_val);
        contentValues.put(EXPENSE_AMOUNT_COL,exp_amount);
        contentValues.put(EXPENSE_DATE_COL,exp_date);
        contentValues.put(EXPENSE_TIME_COL,exp_time);
        contentValues.put(DOCUMENT_COL,document);

       long id = liteDatabase.insert(TABLE_NAME,null,contentValues);

       liteDatabase.close();

       return id;
    }
}
