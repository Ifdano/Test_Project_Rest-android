//Класс для изменения данных

package com.example.testdemorest.editItem;

import android.app.Activity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.view.View;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

import android.content.Intent;
import android.content.ContentValues;

//для базы данных
import android.database.sqlite.SQLiteDatabase;
import com.example.testdemorest.sql.SqlContracts.WeatherEntry;
import com.example.testdemorest.sql.WeatherDbHelper;

import com.example.testdemorest.R;
import com.example.testdemorest.currentItem.CurrentWeather;

public class EditWeather extends Activity implements OnTouchListener{
	//статичные значения ключей, с помощью которых мы передаем/получаем данные.
	//в данном случае, мы получаем информацию, которую нам передали из экрана с раскрытой информацией
	public static final String KEY_ID = "id";
	public static final String KEY_CITY = "city";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_DATE = "date";
	public static final String KEY_TEMPERATURE = "temperature";
	public static final String KEY_PRESSURE = "pressure";
	public static final String KEY_HUMIDITY = "humidity";
	public static final String KEY_SPEED = "speed";
	
	private Button buttonSubmit;
	private Button buttonCancel;
	
	//поля для изменения данных
	private EditText editCity;
	private EditText editDate;
	private EditText editDescription;
	private EditText editTemperature;
	private EditText editPressure;
	private EditText editHumidity;
	private EditText editSpeed;
	
	//наш id, по которому мы будем знать какую информацию изменять
	private int id;
	
	//измененные данные, которые мы получаем из полей
	private String currentCity;
	private String currentDescription;
	private String currentDate;
	private String currentTemperature;
	private String currentPressure;
	private String currentHumidity;
	private String currentSpeed;
	
	//для базы данных
	private WeatherDbHelper DbHelper;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_weather);
		
		init();
		getIdItem();
	}
	
	//находим компоненты и устанавливаем слушателей
	public void init(){
		buttonSubmit = (Button)findViewById(R.id.buttonSubmit);
		buttonCancel = (Button)findViewById(R.id.buttonCancel);
		
		editCity = (EditText)findViewById(R.id.editCity);
		editDate = (EditText)findViewById(R.id.editDate);
		editDescription = (EditText)findViewById(R.id.editDescription);
		editTemperature = (EditText)findViewById(R.id.editTemperature);
		editPressure = (EditText)findViewById(R.id.editPressure);
		editHumidity = (EditText)findViewById(R.id.editHumidity);
		editSpeed = (EditText)findViewById(R.id.editSpeed);
		
		//для базы данных
		DbHelper = new WeatherDbHelper(this);
		
		buttonSubmit.setOnTouchListener(this);
		buttonCancel.setOnTouchListener(this);
	}
	
	//получение данных, которые мы передали из общего экрана
	public void getIdItem(){
		Intent intent = getIntent();
		//получаем id
		//если его нет, то будет -1
		id = intent.getIntExtra(KEY_ID, -1);
		
		//получаем все данные
		currentCity = intent.getStringExtra(KEY_CITY);
		currentDescription = intent.getStringExtra(KEY_DESCRIPTION);
		currentDate = intent.getStringExtra(KEY_DATE);
		currentTemperature = intent.getStringExtra(KEY_TEMPERATURE);
		currentPressure = intent.getStringExtra(KEY_PRESSURE);
		currentHumidity = intent.getStringExtra(KEY_HUMIDITY);
		currentSpeed = intent.getStringExtra(KEY_SPEED);

		//если мы получили id, то вызываем метод для изменения информации
		//иначе, сообщаем об ошибке
		if(id >= 0 )
			editCurrentData();
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
	
	//для изменения данных
	public void editCurrentData(){
		//заполняем поля полученные данными
		editCity.setText(currentCity);
		editDescription.setText(currentDescription);
		editDate.setText(currentDate);
		editTemperature.setText(currentTemperature);
		editPressure.setText(currentPressure);
		editHumidity.setText(currentHumidity);
		editSpeed.setText(currentSpeed);
	}
	
	//для получения измененных данных из полей и добавление их в базу данных
	public void submitCurrentData(){
		//получаем данные с полей
		String tempCity = editCity.getText().toString().trim();
		String tempDescription = editDescription.getText().toString().trim();
		String tempDate = editDate.getText().toString().trim();
		String tempTemperature = editTemperature.getText().toString().trim();
		String tempPressure = editPressure.getText().toString().trim();
		String tempHumidity = editHumidity.getText().toString().trim();
		String tempSpeed = editSpeed.getText().toString().trim();
		
		//загружаем в базу данных
		SQLiteDatabase db = DbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		//данные, которые нужно обновить
		values.put(WeatherEntry.COLUMN_CITY, tempCity);
		values.put(WeatherEntry.COLUMN_DESCRIPTION, tempDescription);
		values.put(WeatherEntry.COLUMN_DATE, tempDate);
		values.put(WeatherEntry.COLUMN_TEMPERATURE, Float.parseFloat(tempTemperature));
		values.put(WeatherEntry.COLUMN_PRESSURE, Float.parseFloat(tempPressure));
		values.put(WeatherEntry.COLUMN_HUMIDITY, Float.parseFloat(tempHumidity));
		values.put(WeatherEntry.COLUMN_SPEED, Float.parseFloat(tempSpeed));
		
		//делаем запрос, указывая по какому id нужно изменить информацию
		db.update(
					WeatherEntry.WEATHER_TABLE_NAME,
					values,
					WeatherEntry._ID + "=?",
					new String[]{Integer.toString(id)}
				);
	}
	
	public boolean onTouch(View view, MotionEvent event){
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//кнопка для подтверждения изменений
			if(view == buttonSubmit){
				//сначала вызываем метод для внесения изменений в базу данных
				submitCurrentData();
				
				//а затем - возвращаемся на общий экран
				Intent intent = new Intent(this, CurrentWeather.class);
				intent.putExtra(KEY_ID, id);
				startActivity(intent);
			}

			//кнопка отмены и возвращения на общий экран
			if(view == buttonCancel){
				Intent intent = new Intent(this, CurrentWeather.class);
				intent.putExtra(KEY_ID, id);
				startActivity(intent);
			}
		}
		
		return false;
	}
}
