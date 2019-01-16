//Класс для отображения полной информации шутки из списка.

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
import com.example.testdemorest.sql.SqlContracts.JokesEntry;
import com.example.testdemorest.sql.JokesDbHelper;

import com.example.testdemorest.R;
import com.example.testdemorest.jokes.MainJokes;
import com.example.testdemorest.editItem.EditJoke;

public class CurrentJoke extends Activity implements OnTouchListener{
	//статичные значения ключей, с помощью которых мы передаем/получаем данные.
	//в данном случае, мы получаем информацию, которую нам передали из экрана со списком
	public static final String KEY_ID = "id";
	public static final String KEY_SETUP = "setup";
	public static final String KEY_PUNCHLINE = "punchline";
	
	private TextView displayInfo;
	private Button buttonUpdate;
	private Button buttonDelete;
	private Button buttonBack;
	
	//для отображения данных базы данных
	private JokesDbHelper DbHelper;
	
	//удалять будем с предупреждением
	//чтобы удалить данные, нужно будет дважды подряд нажать на кнопку удаления
	private int countOfClickDelete;
	
	//наш id, по которому мы узнаем какую информацию нужно использовать
	private int id;
	
	//данные, которые мы будем получать из базы данных и передавать на экран для изменения
	private int currentId;
	private String currentSetup;
	private String currentPunchline;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_joke);
		
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
		DbHelper = new JokesDbHelper(this);
		
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
				JokesEntry._ID, 
				JokesEntry.COLUMN_SETUP,
				JokesEntry.COLUMN_PUNCHLINE
		};
		
		//указываем данные с каким id нам нужны
		String selection = JokesEntry._ID + "=?";
		String[] selectionArgs = {Integer.toString(id)};
		
		//делаем запрос
		Cursor cursor = db.query(
					JokesEntry.JOKES_TABLE_NAME,
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
			int idIndex = cursor.getColumnIndex(JokesEntry._ID);
			int setupIndex = cursor.getColumnIndex(JokesEntry.COLUMN_SETUP);
			int punchlineIndex = cursor.getColumnIndex(JokesEntry.COLUMN_PUNCHLINE);
			
			//пробегаемся по всем данным и получаем их
			while(cursor.moveToNext()){
				currentId = cursor.getInt(idIndex);
				currentSetup = cursor.getString(setupIndex);
				currentPunchline = cursor.getString(punchlineIndex);
				
				//выведим данные на экран
				displayInfo.setText(
							"ID: " + currentId + "\n\n" + 
							"Setup: " + currentSetup + "\n\n" +
							"Punchline: " + currentPunchline
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
					JokesEntry.JOKES_TABLE_NAME,
					JokesEntry._ID + "=?",
					new String[]{Integer.toString(id)}
				);
	}
	
	//касание экрана
	public boolean onTouch(View view, MotionEvent event){
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//кнопка для обновления данных
			if(view == buttonUpdate){
				Intent intent = new Intent(this, EditJoke.class);
				//передаем данные
				intent.putExtra(KEY_ID, id);
				intent.putExtra(KEY_SETUP, currentSetup);
				intent.putExtra(KEY_PUNCHLINE, currentPunchline);
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
					Intent intent = new Intent(this, MainJokes.class);
					startActivity(intent);
				}
			}
			
			//кнопка для возвращения на общий экран
			if(view == buttonBack){
				Intent intent = new Intent(this, MainJokes.class);
				startActivity(intent);
			}
		}
		
		return false;
	}
}
