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
import com.example.testdemorest.sql.SqlContracts.JokesEntry;
import com.example.testdemorest.sql.JokesDbHelper;

import com.example.testdemorest.R;
import com.example.testdemorest.currentItem.CurrentJoke;

public class EditJoke extends Activity implements OnTouchListener{
	//статичные значения ключей, с помощью которых мы передаем/получаем данные.
	//в данном случае, мы получаем информацию, которую нам передали из экрана с раскрытой информацией
	public static final String KEY_ID = "id";
	public static final String KEY_SETUP = "setup";
	public static final String KEY_PUNCHLINE = "punchline";
	
	private Button buttonSubmit;
	private Button buttonCancel;
	
	//поля для изменения данных
	private EditText editSetup;
	private EditText editPunchline;
	
	//наш id, по которому мы будем знать какую информацию изменять
	private int id;
	
	//измененные данные, которые мы получаем из полей
	private String currentSetup;
	private String currentPunchline;
	
	//для базы данных
	private JokesDbHelper DbHelper;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_joke);
		
		init();
		getIdItem();
	}
	
	//находим компоненты и устанавливаем слушателей
	public void init(){
		buttonSubmit = (Button)findViewById(R.id.buttonSubmit);
		buttonCancel = (Button)findViewById(R.id.buttonCancel);
		
		editSetup = (EditText)findViewById(R.id.editSetup);
		editPunchline = (EditText)findViewById(R.id.editPunchline);
		
		//для базы данных
		DbHelper = new JokesDbHelper(this);
		
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
		currentSetup = intent.getStringExtra(KEY_SETUP);
		currentPunchline = intent.getStringExtra(KEY_PUNCHLINE);
		
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
		editSetup.setText(currentSetup);
		editPunchline.setText(currentPunchline);
	}
	
	//для получения измененных данных из полей и добавление их в базу данных
	public void submitCurrentData(){
		//получаем данные с полей
		String tempSetup = editSetup.getText().toString().trim();
		String tempPunchline = editPunchline.getText().toString().trim();
		
		//загружаем в базу данных
		SQLiteDatabase db = DbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
				
		//данные, которые нужно обновить
		values.put(JokesEntry.COLUMN_SETUP, tempSetup);
		values.put(JokesEntry.COLUMN_PUNCHLINE, tempPunchline);
				
		//делаем запрос, указывая по какому id нужно изменить информацию
		db.update(
					JokesEntry.JOKES_TABLE_NAME,
					values,
					JokesEntry._ID + "=?",
					new String[]{Integer.toString(id)}
				);
	}
	
	public boolean onTouch(View view, MotionEvent event){
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//кнопка для подтверждения изменений
			if(view == buttonSubmit){
				//сначала вызываем метод для внесения изменений в базу данных
				submitCurrentData();
				
				//а затем - возвращаемся на общий экран, 
				Intent intent = new Intent(this, CurrentJoke.class);
				intent.putExtra(KEY_ID, id);
				startActivity(intent);
			}

			//кнопка отмены и возвращения на общий экран
			if(view == buttonCancel){
				Intent intent = new Intent(this, CurrentJoke.class);
				intent.putExtra(KEY_ID, id);
				startActivity(intent);
			}
		}
		
		return false;
	}
}
