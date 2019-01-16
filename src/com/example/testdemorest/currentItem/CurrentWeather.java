//Класс для отображения полной информации погоды из списка.

package com.example.testdemorest.currentItem;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import android.view.View;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

import android.content.Intent;

//для базы данных
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.testdemorest.sql.SqlContracts.WeatherEntry;
import com.example.testdemorest.sql.WeatherDbHelper;

import com.example.testdemorest.R;
import com.example.testdemorest.weather.MainWeather;
import com.example.testdemorest.editItem.EditWeather;

public class CurrentWeather extends Activity implements OnTouchListener{
	//статичные значения ключей, с помощью которых мы передаем/получаем данные.
	//в данном случае, мы получаем информацию, которую нам передали из экрана со списком
	public static final String KEY_ID = "id";
	public static final String KEY_CITY = "city";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_DATE = "date";
	public static final String KEY_TEMPERATURE = "temperature";
	public static final String KEY_PRESSURE = "pressure";
	public static final String KEY_HUMIDITY = "humidity";
	public static final String KEY_SPEED = "speed";
	
	private TextView displayInfo;
	private Button buttonUpdate;
	private Button buttonDelete;
	private Button buttonBack;
	
	//для отображения данных базы данных
	private WeatherDbHelper DbHelper;
	
	//удалять будем с предупреждением
	//чтобы удалить данные, нужно будет дважды подряд нажать на кнопку удаления
	private int countOfClickDelete;
	
	//наш id, по которому мы узнаем какую информацию нужно использовать
	private int id;
	
	//данные, которые мы будем получать из базы данных и передавать на экран для изменения
	private int currentId;
	private String currentCity;
	private String currentDescription;
	private String currentDate;
	private float currentTemperature;
	private float currentPressure;
	private float currentHumidity;
	private float currentSpeed;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_weather);
		
		init();
		getIdItem();
	}
	
	//находим компоненты и устанавливаем слушателей
	public void init(){
		displayInfo = (TextView)findViewById(R.id.textInfoData);
		buttonUpdate = (Button)findViewById(R.id.buttonUpdate);
		buttonDelete = (Button)findViewById(R.id.buttonDelete);
		buttonBack = (Button)findViewById(R.id.buttonBack);
		
		//для базы данных
		DbHelper = new WeatherDbHelper(this);
		
		//обнуляем счетчик нажатий кнопки delete
		countOfClickDelete = 0;
		
		buttonUpdate.setOnTouchListener(this);
		buttonDelete.setOnTouchListener(this);
		buttonBack.setOnTouchListener(this);
	}
	
	//поулчаем данные id, которые передали из главное экрана
	public void getIdItem(){
		Intent intent = getIntent();
		
		//получаем id
		//если его нет, то будет -1
		id = intent.getIntExtra(KEY_ID, -1);
		
		//если мы получили id, то вызываем метод для вывода информации на экран
		//иначе, сообщаем об ошибке
		if(id >= 0 )
			displayCurrentData();
		else{
			//вывести на экрна, что что-то пошло не так
			Toast.makeText(
					getApplicationContext(),
					"Something get wrong, can't get ID of item!",
					Toast.LENGTH_LONG
					)
				 .show();
		}
	}
	
	//вывод информации на экран
	public void displayCurrentData(){
		//открываем базу данных для чтения
		SQLiteDatabase db = DbHelper.getReadableDatabase();
		
		//условие выборки, какие столбцы будем получать
		String[] projection = {
				WeatherEntry._ID, 
				WeatherEntry.COLUMN_CITY,
				WeatherEntry.COLUMN_DESCRIPTION,
				WeatherEntry.COLUMN_TEMPERATURE,
				WeatherEntry.COLUMN_PRESSURE,
				WeatherEntry.COLUMN_HUMIDITY,
				WeatherEntry.COLUMN_SPEED,
				WeatherEntry.COLUMN_DATE
		};
		
		//указываем данные с каким id нам нужны
		String selection = WeatherEntry._ID + "=?";
		String[] selectionArgs = {Integer.toString(id)};
		
		//делаем запрос
		Cursor cursor = db.query(
					WeatherEntry.WEATHER_TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					null
				);
		
		//считываем данные
		try{
			//узнаем индексы нужных нам столбцов
			int idIndex = cursor.getColumnIndex(WeatherEntry._ID);
			int cityIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_CITY);
			int descriptionIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_DESCRIPTION);
			int temperatureIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_TEMPERATURE);
			int pressureIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE);
			int humidityIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY);
			int speedIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_SPEED);
			int dateIndex = cursor.getColumnIndex(WeatherEntry.COLUMN_DATE);
			
			//пробегаемся по всем данным и получаем их
			while(cursor.moveToNext()){
				currentId = cursor.getInt(idIndex);
				currentCity = cursor.getString(cityIndex);
				currentDescription = cursor.getString(descriptionIndex);
				currentDate = cursor.getString(dateIndex);
				currentTemperature = cursor.getFloat(temperatureIndex);
				currentPressure = cursor.getFloat(pressureIndex);
				currentHumidity = cursor.getFloat(humidityIndex);
				currentSpeed = cursor.getFloat(speedIndex);
				
				//выведим данные на экран
				displayInfo.setText(
							"ID: " + currentId + "\n\n" + 
							"City: " + currentCity + "\n" +
							"Date: " + currentDate + "\n" +
							"Description: " + currentDescription + "\n\n" +
							"Temperature, °C: " + (int)currentTemperature + "\n" + 
							"Pressure, hPa: " + (int)currentPressure + "\n" +
							"Humidity, %: " + (int)currentHumidity + "\n\n" +
							"Wind speed, meter/sec: " + currentSpeed
						);
			}
		}finally{
			//закрываем базу данных
			cursor.close();
			db.close();
		}
	}
	
	//удаление данных
	public void deleteCurrentData(){
		//открываем базу данных для записи
		SQLiteDatabase db = DbHelper.getWritableDatabase();
		
		//удаляем по id, который мы получили при переходе на этот экран
		db.delete(
					WeatherEntry.WEATHER_TABLE_NAME,
					WeatherEntry._ID + "=?",
					new String[]{Integer.toString(id)}
				);
	}
	
	//касание экрана
	public boolean onTouch(View view, MotionEvent event){
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//кнопка для обновления данных
			if(view == buttonUpdate){
				Intent intent = new Intent(this, EditWeather.class);
				//передаем данные для изменения
				intent.putExtra(KEY_ID, id);
				intent.putExtra(KEY_CITY, currentCity);
				intent.putExtra(KEY_DESCRIPTION, currentDescription);
				intent.putExtra(KEY_DATE, currentDate);
				intent.putExtra(KEY_TEMPERATURE, Float.toString(currentTemperature));
				intent.putExtra(KEY_PRESSURE, Float.toString(currentPressure));
				intent.putExtra(KEY_HUMIDITY, Float.toString(currentHumidity));
				intent.putExtra(KEY_SPEED, Float.toString(currentSpeed));
				//запускаем экран для изменений
				startActivity(intent);
			}
			
			//кнопка для удаления данных
			if(view == buttonDelete){
				//если нажали первый раз, то увеличим счетчик 
				//и выведим сообщение, что, если хотите удалить - нажмите кнопку удаленяи еще раз
				if(countOfClickDelete < 1){
					++countOfClickDelete;
					
					Toast.makeText(
							getApplicationContext(),
							"Click button DELETE again and current data will be removed!",
							Toast.LENGTH_LONG
							)
					     .show();
				}else{
					//если нажали два раза, то удаляем
					deleteCurrentData();
					
					//и возвращаемся обратно
					Intent intent = new Intent(this, MainWeather.class);
					startActivity(intent);
				}
			}
			
			//кнопка для возвращения на общий экран
			if(view == buttonBack){
				Intent intent = new Intent(this, MainWeather.class);
				startActivity(intent);
			}
		}
		
		return false;
	}
}
