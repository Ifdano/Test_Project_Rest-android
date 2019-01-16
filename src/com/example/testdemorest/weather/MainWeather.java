//Класс с получением данных с сервера и хранением их в виде списка.

package com.example.testdemorest.weather;

import android.os.Bundle;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;

//для списка
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.view.View;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

import android.content.Intent;
import android.content.ContentValues;

//для фрагмента
import android.support.v4.app.FragmentActivity;

import com.example.testdemorest.Main;
import com.example.testdemorest.R;

//для базы данных
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.testdemorest.sql.SqlContracts.WeatherEntry;
import com.example.testdemorest.sql.WeatherDbHelper;

//для использования RxJava, ибо данные мы получаем в виде Flowable
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

import android.widget.Toast;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.testdemorest.currentItem.CurrentWeather;

public class MainWeather extends FragmentActivity implements OnTouchListener, OnItemClickListener{
	//статичные значения ключей, с помощью которых мы передаем/получаем данные.
	//в данном случае, мы будем передавать id, чтобы знать - какие данных из базы данных использовать
	public static final String KEY_ID = "id";
	
	private EditText editWeather;
	private Button buttonGetWeather;
	private Button buttonBack;
	
	//для списка
	private ListView listWeather;
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> weatherList;
	
	//для базы данных
	private WeatherDbHelper DbHelper;
	
	//данные с сервера
	private String weatherCity;
	private String weatherDescription;
	private String weatherDate;
	private float weatherTemperature;
	private float weatherPressure;
	private float weatherHumidity;
	private float weatherSpeed;
	
	//хранение id элементов из базы данных.
	//при выборе элемента из списка, мы будем переходить на новый экран,
	//где будут показаны все данные. Это нужно, чтобы понять, какие именно данные нужно показать.
	//будем передавать на новый экран id строки из базы данных
	private ArrayList<Integer> idSqlData;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		init();
		//выводим текущие данные из базы данных
		displayDatabaseInfo();
	}
	
	//находим компоненты и устанавливаем слушателей
	public void init(){
		editWeather = (EditText)findViewById(R.id.weatherEdit);
		buttonGetWeather = (Button)findViewById(R.id.buttonGetWeather);
		buttonBack = (Button)findViewById(R.id.buttonBack);
		listWeather = (ListView)findViewById(R.id.list);
		
		//для списка
		weatherList = new ArrayList<String>();
		
		//для базы данных
		DbHelper = new WeatherDbHelper(this);
		
		//для id элементов базы данных
		idSqlData = new ArrayList<Integer>();
		
		buttonGetWeather.setOnTouchListener(this);
		buttonBack.setOnTouchListener(this);
		listWeather.setOnItemClickListener(this);
	}
	
	//для отображения данных базы данных
	public void displayDatabaseInfo(){
		//открываем базу данных для чтения
		SQLiteDatabase db = DbHelper.getReadableDatabase();
		
		//условие выборки, какие столбцы будем получать
		String[] projection = {
				WeatherEntry._ID,
				WeatherEntry.COLUMN_CITY,
				WeatherEntry.COLUMN_DATE
		};
		
		//делаем запрос
		Cursor cursor = db.query(
					WeatherEntry.WEATHER_TABLE_NAME,
					projection,
					null,
					null,
					null,
					null,
					null
				);
		
		try{
			//узнаем индексы нужных нам столбцов
			int idIndex = cursor.getColumnIndex(WeatherEntry._ID);
			int cityIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_CITY);
			int dateIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_DATE);
			
			//получаем данные из таблицы
			while(cursor.moveToNext()){
				int currentId = cursor.getInt(idIndex);
				String currentCity = cursor.getString(cityIndex);
				String currentDate = cursor.getString(dateIndex);
				
				//создаем общую строку, которую добавив в общий список
				String tempList = "" + currentId + ". " + currentCity + "\n" + currentDate;
				
				//добавим строку в список, а потом и в ListView
				weatherList.add(tempList);
				mAdapter = new ArrayAdapter<String>(
						this,
						android.R.layout.simple_list_item_1,
						weatherList);

				listWeather.setAdapter(mAdapter);
				
				//добавляем id элементов в список, что потом передавать их на другие экраны
				//это нужно, чтобы знать какие именно данные нужно использовать [какой id]
				idSqlData.add(currentId);
			}
			
		}finally{
			//закрываем базу данных
			cursor.close();
			db.close();
		}
	}
	
	//получение данных с сервера
	public void getWeatherData(){
		//создаем подписчика для Flowable
		Subscriber<TempFinal> subscriber = new Subscriber<TempFinal>(){
			public void onNext(@NonNull TempFinal tempFinal){
				//очищаем поле ввода
				editWeather.setText("");
				
				//получаем название города
				weatherCity = tempFinal.getName();
				
				//получаем температуру
				TempMain tempMain = tempFinal.getMain();
				weatherTemperature = tempMain.getTemp();
				weatherPressure = tempMain.getPressure();
				weatherHumidity = tempMain.getHumidity();
				
				//получаем общие данные погоды
				TempWeather[] tempWeather = tempFinal.getWeather();
				TempWeather weather = tempWeather[0];
				weatherDescription = weather.getDescription();
				
				//получаем данные ветра
				TempWind tempWind = tempFinal.getWind();
				weatherSpeed = tempWind.getSpeed();
				
				//получаем текущее время
				Date date = new Date();
				//или можно так
				//Date date = Calendar.getInstance().getTime();
				//конвертируем в строку
				DateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
				weatherDate = dateFormat.format(date);

				//добавляем полученные данные в базу данных
				insertWeather();
			}
			
			//в случае ошибки/исключения
			public void onError(Throwable e){
				//выведем сообщение об ошибке
				Toast.makeText(
							getApplicationContext(),
							"Please check a city name or your internet connection!",
							Toast.LENGTH_LONG)
				     .show();
			}
			
			//в случае удачного завершения
			public void onComplete(){
				weatherCity = "";
			}
			
			//для подписки/отписки
			public void onSubscribe(Subscription s){
				//в случае с Subscriber, нужно сделать явный запрос, чтобы Flowable начал делиться данными
				//иначе - никаких данных не будет
				s.request(Long.MAX_VALUE);
			}
		};
		
		//общие запросы
		//сначала получаем тот самый единичный экземпляр класса с "Одиночкой"
		//получаем сам api
		//делаем запрос, который мы указали в api
		//указываем в каком потоке будут получаться данные
		//и подписываем подписчика к источнику
		WeatherService.getService()
					  .getApi()
					  .getWeather(weatherCity, "b29d2de8bb725fc8d2d3b61749af00b8", "metric")
					  .subscribeOn(Schedulers.io())
					  .observeOn(AndroidSchedulers.mainThread())
					  .subscribe(subscriber);
	}
	
	//для добавления в базу данных
	public void insertWeather(){
		//открываем базу данных для записи
		SQLiteDatabase db = DbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		//добавляем полученые данные из сервера в базу данных
		values.put(WeatherEntry.COLUMN_CITY, weatherCity);
		values.put(WeatherEntry.COLUMN_DESCRIPTION, weatherDescription);
		values.put(WeatherEntry.COLUMN_DATE, weatherDate);
		values.put(WeatherEntry.COLUMN_TEMPERATURE, weatherTemperature);
		values.put(WeatherEntry.COLUMN_PRESSURE, weatherPressure);
		values.put(WeatherEntry.COLUMN_HUMIDITY, weatherHumidity);
		values.put(WeatherEntry.COLUMN_SPEED, weatherSpeed);

		//размещаем данные
		long newRowId = db.insert(
				WeatherEntry.WEATHER_TABLE_NAME, 
				null, 
				values
		);
		
		//обнуляем количество данных и сами данные в списках
		//это нужно, чтобы при повторных вызовах метода displayDatabaseInfo - списки не добавляли 
		//повторные данные
		idSqlData.clear();
		weatherList.clear();
		
		//обновляем содержимое ListView
		displayDatabaseInfo();
	}
	
	//для нажатий на список
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		//переходим на страницу с выбранной информацией
		Intent intent = new Intent(this, CurrentWeather.class);
		
		//передаем на другой экран id[из базы данных] выбранного элемента, чтобы
		//знать что именно показывать
		intent.putExtra(KEY_ID, idSqlData.get(position));
		
		//запускаем другой экран
		startActivity(intent);
	}
	
	//касание экрана
	public boolean onTouch(View view, MotionEvent event){
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//кнопка получения данных с сервера
			if(view == buttonGetWeather){
				//если поле для ввода пустое, то выводим сообщение об этом
				//иначе, получаем название города и вызываем метод для получения данных с сервера
				if(editWeather.getText().toString().isEmpty()){
					//сообщение, что нужно ввести название города
					Toast.makeText(
								getApplicationContext(),
								"Please enter a city name!",
								Toast.LENGTH_LONG)
					     .show();
				}else{
					//иначе, получаем название города и вызываем метод для получения данных
					weatherCity = editWeather.getText().toString().trim();
					getWeatherData();
				}
			}
			
			//кнопка для возвращения в общее меню
			if(view == buttonBack){
				Intent intent = new Intent(this, Main.class);
				startActivity(intent);
			}
		}
		
		return false;
	}
}
