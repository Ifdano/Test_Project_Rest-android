//Класс для создания отдельной таблицы для данных с пользователями github

package com.example.testdemorest.sql;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.testdemorest.sql.SqlContracts.GithubEntry;

public class GithubDbHelper extends SQLiteOpenHelper{

	//название базы данных
	public static final String DATABASE_NAME = "github.db";
	
	//версия
	public static final int DATABASE_VERSION = 1;
	
	//запрос для github
	public static final String SQL_GITHUB_TABLE = "CREATE TABLE " + 
							GithubEntry.GITHUB_TABLE_NAME + " (" +
							GithubEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
							GithubEntry.COLUMN_USERNAME + " TEXT NOT NULL," +
							GithubEntry.COLUMN_NAME + " TEXT NOT NULL," + 
							GithubEntry.COLUMN_URL + " TEXT NOT NULL," +
							GithubEntry.COLUMN_AVATAR_URL + " TEXT NOT NULL," +
							GithubEntry.COLUMN_CREATED_DATE + " TEXT NOT NULL);"
							;
	
	//конструктор
	public GithubDbHelper(Context context){
		super(
			context, 
			DATABASE_NAME, 
			null, 
			DATABASE_VERSION
		);
	}
	
	//метод при создании базы данных
	public void onCreate(SQLiteDatabase db){
		//запускаем создание таблицы github
		db.execSQL(SQL_GITHUB_TABLE);
	}
	
	//метод при обновлении базы данных
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		//удаляем таблицу
		db.execSQL("DROP TABLE IF EXISTS " + GithubEntry.GITHUB_TABLE_NAME + ";");
		
		//и создаем по новой
		onCreate(db);
	}
}
