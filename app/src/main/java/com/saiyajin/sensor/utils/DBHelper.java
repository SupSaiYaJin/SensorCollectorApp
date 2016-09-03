package com.saiyajin.sensor.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper {
	private static final String DATABASE_NAME = "sensorresult.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "result";
	private static final String[] COLUMNS = { "_id", "sensortype", "time",
			"dataA", "dataB", "dataC" };
	private DBOpenHelper helper;
	private SQLiteDatabase db;

	private static class DBOpenHelper extends SQLiteOpenHelper {
		private static final String CREATE_TABLE = 
		"CREATE TABLE " + TABLE_NAME + " (" + COLUMNS[0] + " INTEGER primary key autoincrement, " + COLUMNS[1] + " TEXT, "+ COLUMNS[2] +" TEXT, "+ COLUMNS[3] +" TEXT, "+ COLUMNS[4] +" TEXT, "+ COLUMNS[5] +" TEXT);";
		public DBOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}

	}

	public DBHelper(Context context) {
		helper = new DBOpenHelper(context);
		db = helper.getWritableDatabase();
	}

	public void insert(int type, long time, float[] data) {
		ContentValues values = new ContentValues();
		values.put(COLUMNS[1], type);
		values.put(COLUMNS[2], time);
		for(int i = 0; i != data.length; ++i)
			values.put(COLUMNS[i + 3], data[i]);
		db.insert(TABLE_NAME, null, values);
	}
	public void clearall(){
		db.delete(TABLE_NAME, null, null);
		db.delete("sqlite_sequence", null, null);
	}
}
