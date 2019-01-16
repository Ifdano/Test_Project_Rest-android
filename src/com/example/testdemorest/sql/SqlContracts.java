//Класс для таблиц базы данных. 

package com.example.testdemorest.sql;

//для контроля id и его автоматического увеличения
import android.provider.BaseColumns;

public final class SqlContracts{
	
	//таблица для weather
	public static final class WeatherEntry implements BaseColumns{
		//название таблицы
		public final static String WEATHER_TABLE_NAME = "weather";
		
		//названия столбцов
		public final static String _ID = BaseColumns._ID;
		public final static String COLUMN_CITY = "city";
		public final static String COLUMN_DESCRIPTION = "description";
		public final static String COLUMN_TEMPERATURE = "temperature";
		public final static String COLUMN_PRESSURE = "pressure";
		public final static String COLUMN_HUMIDITY = "humidity";
		public final static String COLUMN_SPEED = "speed";
		public final static String COLUMN_DATE = "date";
	}
	
	//таблица для joke
	public static final class JokesEntry implements BaseColumns{
		//название таблицы
		public final static String JOKES_TABLE_NAME = "jokes";
		
		//названия столбцов
		public final static String _ID = BaseColumns._ID;
		public final static String COLUMN_SETUP = "setup";
		public final static String COLUMN_PUNCHLINE = "punchline";
	}
	
	//таблица для github
	public static final class GithubEntry implements BaseColumns{
		//название таблицы
		public final static String GITHUB_TABLE_NAME = "github";
		
		//название столбцов
		public final static String _ID = BaseColumns._ID;
		public final static String COLUMN_USERNAME = "username";
		public final static String COLUMN_NAME = "name";
		public final static String COLUMN_URL = "url";
		public final static String COLUMN_AVATAR_URL = "avatar_url";
		public final static String COLUMN_CREATED_DATE = "created_date";
	}
	
}