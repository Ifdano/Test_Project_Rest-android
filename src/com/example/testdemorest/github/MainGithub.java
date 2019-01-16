//Класс с получением данных с сервера и хранением их в виде списка.

package com.example.testdemorest.github;

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
import com.example.testdemorest.sql.SqlContracts.GithubEntry;
import com.example.testdemorest.sql.GithubDbHelper;

//для использования RxJava, ибо данные мы получаем в виде Flowable
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

import android.widget.Toast;

import com.example.testdemorest.currentItem.CurrentGithub;

public class MainGithub extends FragmentActivity implements OnTouchListener, OnItemClickListener{
	//статичные значения ключей, с помощью которых мы передаем/получаем данные.
	//в данном случае, мы будем передавать id, чтобы знать - какие данных из базы данных использовать
	public static final String KEY_ID = "id";
	
	private EditText editGithub;
	private Button buttonGetGithub;
	private Button buttonBack;
	
	//для списка
	private ListView listGithub;
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> githubList;
	
	//для базы данных
	private GithubDbHelper DbHelper;
	
	//данные с сервера
	private String githubUsername;
	private String githubName;
	private String githubUrl;
	private String githubAvatarUrl;
	private String githubCreatedDate;
	
	//хранение id элементов из базы данных.
	//при выборе элемента из списка, мы будем переходить на новый экран,
	//где будут показаны все данные. Это нужно, чтобы понять, какие именно данные нужно показать.
	//будем передавать на новый экран id строки из базы данных
	private ArrayList<Integer> idSqlData;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_github);
		
		init();
		//выводим текущие данные из базы данных
		displayDatabaseInfo();
	}
	
	public void init(){
		editGithub = (EditText)findViewById(R.id.githubEdit);
		buttonGetGithub = (Button)findViewById(R.id.buttonGetGithub);
		buttonBack = (Button)findViewById(R.id.buttonBack);
		listGithub = (ListView)findViewById(R.id.list);
		
		//для списка
		githubList = new ArrayList<String>();
		
		//для базы данных
		DbHelper = new GithubDbHelper(this);
		
		//для id элементов базы данных
		idSqlData = new ArrayList<Integer>();
		
		buttonGetGithub.setOnTouchListener(this);
		buttonBack.setOnTouchListener(this);
		listGithub.setOnItemClickListener(this);
	}
	
	//для отображения данных базы данных
	public void displayDatabaseInfo(){
		//открываем базу данных для чтения
		SQLiteDatabase db = DbHelper.getReadableDatabase();
			
		//условие выборки, какие столбцы будем получать
		String[] projection = {
				GithubEntry._ID,
				GithubEntry.COLUMN_USERNAME,
		};
			
		//делаем запрос
		Cursor cursor = db.query(
					GithubEntry.GITHUB_TABLE_NAME,
					projection,
					null,
					null,
					null,
					null,
					null
				);
			
		try{
			//узнаем индексы нужных нам столбцов
			int idIndex = cursor.getColumnIndex(GithubEntry._ID);
			int usernameIndex = cursor.getColumnIndex(GithubEntry.COLUMN_USERNAME);
				
			//получаем данные из таблицы
			while(cursor.moveToNext()){
				int currentId = cursor.getInt(idIndex);
				String currentUsername = cursor.getString(usernameIndex);
					
				//создаем общую строку, которую добавив в общий список
				String tempList = "" + currentId + ". " + currentUsername;
					
				//добавим строку в список, а потом и в ListView
				githubList.add(tempList);
				mAdapter = new ArrayAdapter<String>(
						this,
						android.R.layout.simple_list_item_1,
						githubList);

				listGithub.setAdapter(mAdapter);
				
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
	
	//получаем данные пользователя
	public void getUserData(){
		//создаем подписчика для Flowable
		Subscriber<GithubUser> subscriber = new Subscriber<GithubUser>(){
			public void onNext(@NonNull GithubUser githubUser){
				//очищаем поле ввода
				editGithub.setText("");
				
				//получаем данные
				githubUsername = githubUser.getLogin();
				githubName = githubUser.getName();
				githubUrl = githubUser.getHtmlUrl();
				githubAvatarUrl = githubUser.getAvatarUrl();
				githubCreatedDate = githubUser.getCreatedDate();
				
				//добавляем данные в базу данных
				insertGithubUser();
			}
			
			//в случае ошибки/исключения
			public void onError(Throwable e){
				//сообщим, чтобы проверили корректность имени пользователя
				Toast.makeText(
							getApplicationContext(),
							"Please check username or your internet connection!",
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
		GithubService.getService()
					 .getApi()
					 .getUser(githubUsername)
					 .subscribeOn(Schedulers.io())
					 .observeOn(AndroidSchedulers.mainThread())
					 .subscribe(subscriber);
	}
	
	//для добавления в базу данных
	public void insertGithubUser(){
		//открываем базу данных для записи
		SQLiteDatabase db = DbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		//добавляем полученые данные из сервера в базу данных
		values.put(GithubEntry.COLUMN_USERNAME, githubUsername);
		values.put(GithubEntry.COLUMN_NAME, githubName);
		values.put(GithubEntry.COLUMN_URL, githubUrl);
		values.put(GithubEntry.COLUMN_AVATAR_URL, githubAvatarUrl);
		values.put(GithubEntry.COLUMN_CREATED_DATE, githubCreatedDate);
		
		//размещаем данные
		long newRowId = db.insert(
				GithubEntry.GITHUB_TABLE_NAME, 
				null, 
				values
		);
		
		//обнуляем количество данных и сами данные в списках
		//это нужно, чтобы при повторных вызовах метода displayDatabaseInfo - списки не добавляли 
		//повторные данные
		idSqlData.clear();
		githubList.clear();
		
		//обновляем содержимое ListView
		displayDatabaseInfo();
	}
	
	//для нажатий на список
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){		
		//переходим на страницу с выбранной информацией
		Intent intent = new Intent(this, CurrentGithub.class);
		
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
			if(view == buttonGetGithub){
				//если поле для ввода пустое, то выводим сообщение об этом
				//иначе, получаем имя пользователя и вызываем метод для получения данных с сервера
				if(editGithub.getText().toString().isEmpty()){
					//сообщение, что поле для ввода пустое
					Toast.makeText(
								getApplicationContext(),
								"Please enter username!",
								Toast.LENGTH_LONG)
						 .show();
				}else{
					//иначе, получаем имя пользователя и вызываем метод для получения данных с сервера
					githubUsername = editGithub.getText().toString().trim();
					getUserData();
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
