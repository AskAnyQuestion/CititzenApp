package com.example.application;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DBName = "register.db";

    public DbHelper(@Nullable Context context) {
        super(context, DBName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users(phone INTEGER primary key, login TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists users");
    }

    public boolean insertData(Long phone, String login, String password) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", phone);
        contentValues.put("login", login);
        contentValues.put("password", password);
        long result = myDB.insert("users", null, contentValues);
        return result != -1;
    }

    public boolean checkLogin(String login) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = myDB.rawQuery("select * from users where login = ?", new String[]{login});
        return cursor.getCount() > 0;
    }

    public boolean checkUser(Long phone, String password) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = myDB.rawQuery("select * from users where phone = ? and password = ?", new String[]{String.valueOf(phone), password});
        return cursor.getCount() > 0;
    }
}
