//Класс с получением данных с сервера и хранением их в виде списка.

package com.example.testdemorest.jokes;

import android.os.Bundle;

import android.widget.Button;
import android.widget.ListView;

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
import com.example.testdemorest.sql.SqlContracts.JokesEntry;
import com.example.testdemorest.sql.JokesDbHelper;

//для использования RxJava, ибо данные мы получаем в виде Flowable
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

import android.widget.Toast;

import com.example.testdemorest.currentItem.CurrentJoke;

public class MainJokes extends FragmentActivity implements OnTouchListener, OnItemClickListener{
	//статичные значения ключей, с помощью которых мы передаем/получаем данные.
	//в данном случае, мы будем передавать id, чтобы знать - какие данных из базы данных использовать
	public static final String KEY_ID = "id";
	
	private Button buttonGetJoke;
	private Button buttonBack;
	
	//для списка
	private ListView listJoke;
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> jokesList;
	
	//для базы данных
	private JokesDbHelper DbHelper;
	
	//данные с сервера
	private int jokesId;
	private String jokesSetup;
	private String jokesPunchline;
	
	//хранение id элементов из базы данных.
	//при выборе элемента из списка, мы будем переходить на новый экран,
	//где будут показаны все данные. Это нужно, чтобы понять, какие именно данные нужно показать.
	//будем передавать на новый экран id строки из базы данных
	private ArrayList<Integer> idSqlData;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_joke);
		
		init();
		//выводим текущие данные из базы данных
		displayDatabaseInfo();
	}
	
	public void init(){
		buttonGetJoke = (Button)findViewById(R.id.buttonGetJoke);
		buttonBack = (Button)findViewById(R.id.buttonBack);
		listJoke = (ListView)findViewById(R.id.list);
		
		//для списка
		jokesList = new ArrayList<String>();
		
		//для базы данных
		DbHelper = new JokesDbHelper(this);
		
		//для id элементов базы данных
		idSqlData = new ArrayList<Integer>();
		
		buttonGetJoke.setOnTouchListener(this);
		buttonBack.setOnTouchListener(this);
		listJoke.setOnItemClickListener(this);
	}
	
	//для отображения данных базы данных
	public void displayDatabaseInfo(){
		//открываем базу данных для чтения
		SQLiteDatabase db = DbHelper.getReadableDatabase();
			
		//условие выборки, какие столбцы будем получать
		String[] projection = {
				JokesEntry._ID,
				JokesEntry.COLUMN_SETUP,
		};
			
		//делаем запрос
		Cursor cursor = db.query(
					JokesEntry.JOKES_TABLE_NAME,
					projection,
					null,
					null,
					null,
					null,
					null
				);
			
		try{
			//узнаем индексы нужных нам столбцов
			int idIndex = cursor.getColumnIndex(JokesEntry._ID);
			int setupIndex = cursor.getColumnIndex(JokesEntry.COLUMN_SETUP);
				
			//получаем данные из таблицы
			while(cursor.moveToNext()){
				int currentId = cursor.getInt(idIndex);
				String currentSetup = cursor.getString(setupIndex);
					
				//создаем общую строку, которую добавив в общий список
				String tempList = "" + currentId + ". " + currentSetup;
					
				//добавим строку в список, а потом и в ListView
				jokesList.add(tempList);
				mAdapter = new ArrayAdapter<String>(
						this,
						android.R.layout.simple_list_item_1,
						jokesList);

				listJoke.setAdapter(mAdapter);
				
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
	
	//получаем шутку
	public void getJokes(){
		//создаем подписчика для Flowable
		Subscriber<RandomJoke> subscriber = new Subscriber<RandomJoke>(){
			public void onNext(@NonNull RandomJoke randomJoke){
				//получаем данные
				jokesId = randomJoke.getId();
				jokesSetup = randomJoke.getSetup();
				jokesPunchline = randomJoke.getPunchline();
				
				//добавляем полученную шутку в базу данных
				insertJokes();
			}
			
			//в случае ошибки/исключения
			public void onError(Throwable e){
				//сообщаем, чтобы проверили интернете соединение
				Toast.makeText(
							getApplicationContext(),
							"Please check your internet connection!",
							Toast.LENGTH_LONG)
				     .show();
			}
			
			//в случае удачного завершения
			public void onComplete(){
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
		JokesService.getService()
				    .getApi()
				    .getJoke()
				    .subscribeOn(Schedulers.io())
				    .observeOn(AndroidSchedulers.mainThread())
				    .subscribe(subscriber);
	}
	
	//для добавления в базу данных
	public void insertJokes(){
		//открываем базу данных для записи
		SQLiteDatabase db = DbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		//добавляем полученые данные из сервера в базу данных
		values.put(JokesEntry.COLUMN_SETUP, jokesSetup);
		values.put(JokesEntry.COLUMN_PUNCHLINE, jokesPunchline);
		
		//размещаем данные
		long newRowId = db.insert(
				JokesEntry.JOKES_TABLE_NAME, 
				null, 
				values
		);
			
		//обнуляем количество данных и сами данные в списках
		//это нужно, чтобы при повторных вызовах метода displayDatabaseInfo - списки не добавляли 
		//повторные данные
		idSqlData.clear();
		jokesList.clear();
		
		//обновляем содержимое ListView
		displayDatabaseInfo();
	}
	
	//для нажатий на список
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		//переходим на страницу с выбранной информацией
		Intent intent = new Intent(this, CurrentJoke.class);
		
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
			if(view == buttonGetJoke){
				//просто вызываем метод для получения данных
				getJokes();
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
