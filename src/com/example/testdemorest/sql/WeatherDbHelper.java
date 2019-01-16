//Класс для создания отдельной таблицы для данных с погодой

package com.example.testdemorest.sql;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.testdemorest.sql.SqlContracts.WeatherEntry;

public class WeatherDbHelper extends SQLiteOpenHelper{

	//название базы данных
	public static final String DATABASE_NAME = "weather.db";
	
	//версия
	public static final int DATABASE_VERSION = 1;

	//запрос для weather
	public static final String SQL_WEATHER_TABLE = "CREATE TABLE " + 
							WeatherEntry.WEATHER_TABLE_NAME + " (" +
							WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
							WeatherEntry.COLUMN_CITY + " TEXT NOT NULL," +
							WeatherEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
							WeatherEntry.COLUMN_TEMPERATURE + " REAL NOT NULL," +
							WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL," +
							WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL," +
							WeatherEntry.COLUMN_SPEED + " REAL NOT NULL," +
							WeatherEntry.COLUMN_DATE + " TEXT NOT NULL);"
							;
	
	//конструктор
	public WeatherDbHelper(Context context){
		super(
			context, 
			DATABASE_NAME, 
			null, 
			DATABASE_VERSION
		);
	}
	
	//метод при создании базы данных
	public void onCreate(SQLiteDatabase db){
		//запускаем создание таблицы weather
		db.execSQL(SQL_WEATHER_TABLE);
	}
	
	//метод при обновлении базы данных
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		//удаляем таблицу
		db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.WEATHER_TABLE_NAME + ";");
		
		//и создаем по новой
		onCreate(db);
	}
}
