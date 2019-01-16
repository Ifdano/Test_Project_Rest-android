//Класс для отображения полной информации профиля github из списка.

package com.example.testdemorest.currentItem;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import android.view.View;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

import android.content.Intent;

//для базы данных
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.testdemorest.sql.SqlContracts.GithubEntry;
import com.example.testdemorest.sql.GithubDbHelper;

import com.example.testdemorest.R;
import com.example.testdemorest.github.MainGithub;
import com.example.testdemorest.editItem.EditGithub;

//для отображения изображения по url
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

public class CurrentGithub extends Activity implements OnTouchListener{
	//статичные значения ключей, с помощью которых мы передаем/получаем данные.
	//в данном случае, мы получаем информацию, которую нам передали из экрана со списком
	public static final String KEY_ID = "id";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_NAME = "name";
	public static final String KEY_URL = "url";
	public static final String KEY_AVATAR_URL = "avatarUrl";
	public static final String KEY_CREATED_DATE = "createdDate";
	
	private TextView displayInfo;
	private ImageView imageAvatar;
	private Button buttonUpdate;
	private Button buttonDelete;
	private Button buttonBack;
	
	//для отображения данных базы данных
	private GithubDbHelper DbHelper;
	
	//удалять будем с предупреждением
	//чтобы удалить данные, нужно будет дважды подряд нажать на кнопку удаления
	private int countOfClickDelete;
	
	//данные, которые мы будем получать из базы данных и передавать на экран для изменения
	private int currentId;
	private String currentUsername;
	private String currentName;
	private String currentUrl;
	private String currentAvatarUrl;
	private String currentCreatedDate;
	
	//наш id, по которому мы узнаем какую информацию нужно использовать
	private int id;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_github);
		
		init();
		getIdItem();
	}
	
	//находим компоненты и устанавливаем слушателей
	public void init(){
		displayInfo = (TextView)findViewById(R.id.textInfoData);
		imageAvatar = (ImageView)findViewById(R.id.imageAvatar);
		buttonUpdate = (Button)findViewById(R.id.buttonUpdate);
		buttonDelete = (Button)findViewById(R.id.buttonDelete);
		buttonBack = (Button)findViewById(R.id.buttonBack);
		
		//для базы данных
		DbHelper = new GithubDbHelper(this);
		
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
				GithubEntry._ID, 
				GithubEntry.COLUMN_USERNAME,
				GithubEntry.COLUMN_NAME,
				GithubEntry.COLUMN_URL,
				GithubEntry.COLUMN_AVATAR_URL,
				GithubEntry.COLUMN_CREATED_DATE
		};
		
		//указываем данные с каким id нам нужны
		String selection = GithubEntry._ID + "=?";
		String[] selectionArgs = {Integer.toString(id)};
		
		//делаем запрос
		Cursor cursor = db.query(
					GithubEntry.GITHUB_TABLE_NAME,
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
			int idIndex = cursor.getColumnIndex(GithubEntry._ID);
			int usernameIndex = cursor.getColumnIndex(GithubEntry.COLUMN_USERNAME);
			int nameIndex = cursor.getColumnIndex(GithubEntry.COLUMN_NAME);
			int urlIndex = cursor.getColumnIndex(GithubEntry.COLUMN_URL);
			int avatarUrlIndex = cursor.getColumnIndex(GithubEntry.COLUMN_AVATAR_URL);
			int createdDateIndex = cursor.getColumnIndex(GithubEntry.COLUMN_CREATED_DATE);
			
			//пробегаемся по всем данным и получаем их
			while(cursor.moveToNext()){
				currentId = cursor.getInt(idIndex);
				currentUsername = cursor.getString(usernameIndex);
				currentName = cursor.getString(nameIndex);
				currentUrl = cursor.getString(urlIndex);
				currentAvatarUrl = cursor.getString(avatarUrlIndex);
				currentCreatedDate = cursor.getString(createdDateIndex);
				
				//вызываем метод загрузки изображения
				loadAvatarImage();
				
				//выведим данные на экран
				displayInfo.setText(
							"ID: " + currentId + "\n\n" + 
							"Username: " + currentUsername + "\n" +
							"Name: " + currentName + "\n" +
							"Url: " + currentUrl + "\n\n" +
							"Created date: " + currentCreatedDate
						);
			}
		}finally{
			//закрываем базу данных
			cursor.close();
			db.close();
		}
	}
	
	//загрузка изображения аватарки
	public void loadAvatarImage(){
		//наш Callback
		Callback callback = new Callback(){
			//изображение удачно загрузилось
			public void onSuccess(){}
			
			//вышла ошибка
			public void onError(){
				//выведим сообщение об ошибке
				Toast.makeText(
							getApplicationContext(),
							"Image is not loaded! Check your internet connection!",
							Toast.LENGTH_LONG)
				     .show();
			}
		};
		
		//загрузка изображения по Url
		Picasso.with(getApplicationContext())
		       .load(currentAvatarUrl)
		       .placeholder(R.drawable.loading)
		       .error(R.drawable.error)
		       .into(imageAvatar, callback);
	}
	
	//удаление данных
	public void deleteCurrentData(){
		//открываем базу данных для записи
		SQLiteDatabase db = DbHelper.getWritableDatabase();
			
		//удаляем по id, который мы получили при переходе на этот экран
		db.delete(
					GithubEntry.GITHUB_TABLE_NAME,
					GithubEntry._ID + "=?",
					new String[]{Integer.toString(id)}
				);
	}
	
	//касание экрана
	public boolean onTouch(View view, MotionEvent event){
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//кнопка для обновления данных
			if(view == buttonUpdate){
				Intent intent = new Intent(this, EditGithub.class);
				//передаем данные для изменения
				intent.putExtra(KEY_ID, id);
				intent.putExtra(KEY_USERNAME, currentUsername);
				intent.putExtra(KEY_NAME, currentName);
				intent.putExtra(KEY_URL, currentUrl);
				intent.putExtra(KEY_AVATAR_URL, currentAvatarUrl);
				intent.putExtra(KEY_CREATED_DATE, currentCreatedDate);
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
					Intent intent = new Intent(this, MainGithub.class);
					startActivity(intent);
				}
			}
			
			//кнопка для возвращения на общий экран
			if(view == buttonBack){
				Intent intent = new Intent(this, MainGithub.class);
				startActivity(intent);
			}
		}
		
		return false;
	}
}
