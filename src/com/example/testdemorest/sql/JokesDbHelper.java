//Класс для создания отдельной таблицы для данных с шутками

package com.example.testdemorest.sql;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.testdemorest.sql.SqlContracts.JokesEntry;

public class JokesDbHelper extends SQLiteOpenHelper{

	//название базы данных
	public static final String DATABASE_NAME = "jokes.db";
	
	//версия
	public static final int DATABASE_VERSION = 1;
	
	//запрос для jokes
	public static final String SQL_JOKES_TABLE = "CREATE TABLE " +
							JokesEntry.JOKES_TABLE_NAME + " (" +
							JokesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
							JokesEntry.COLUMN_SETUP + " TEXT NOT NULL," +
							JokesEntry.COLUMN_PUNCHLINE + " TEXT NOT NULL);"
							;
	
	//конструктор
	public JokesDbHelper(Context context){
		super(
			context, 
			DATABASE_NAME, 
			null, 
			DATABASE_VERSION
		);
	}
	
	//метод при создании базы данных
	public void onCreate(SQLiteDatabase db){
		//запускаем создание таблицы jokes
		db.execSQL(SQL_JOKES_TABLE);
	}
	
	//метод при обновлении базы данных
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		//удаление таблицы при обновлении
		db.execSQL("DROP TABLE IF EXISTS " + JokesEntry.JOKES_TABLE_NAME + ";");
		
		//и создание новой
		onCreate(db);
	}
}
