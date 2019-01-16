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
import com.example.testdemorest.sql.SqlContracts.GithubEntry;
import com.example.testdemorest.sql.GithubDbHelper;

import com.example.testdemorest.R;
import com.example.testdemorest.currentItem.CurrentGithub;

public class EditGithub extends Activity implements OnTouchListener{
	//статичные значения ключей, с помощью которых мы передаем/получаем данные.
	//в данном случае, мы получаем информацию, которую нам передали из экрана с раскрытой информацией
	public static final String KEY_ID = "id";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_NAME = "name";
	public static final String KEY_URL = "url";
	public static final String KEY_AVATAR_URL = "avatarUrl";
	public static final String KEY_CREATED_DATE = "createdDate";
	
	private Button buttonSubmit;
	private Button buttonCancel;
	
	//поля для изменения данных
	private EditText editUsername;
	private EditText editName;
	private EditText editUrl;
	private EditText editAvatarUrl;
	private EditText editCreatedDate;
	
	//наш id, по которому мы будем знать какую информацию изменять
	private int id;
	
	//измененные данные, которые мы получаем из полей
	private String currentUsername;
	private String currentName;
	private String currentUrl;
	private String currentAvatarUrl;
	private String currentCreatedDate;
	
	//для базы данных
	private GithubDbHelper DbHelper;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_github);
		
		init();
		getIdItem();
	}
	
	//находим компоненты и устанавливаем слушателей
	public void init(){
		buttonSubmit = (Button)findViewById(R.id.buttonSubmit);
		buttonCancel = (Button)findViewById(R.id.buttonCancel);
		
		editUsername = (EditText)findViewById(R.id.editUsername);
		editName = (EditText)findViewById(R.id.editName);
		editUrl = (EditText)findViewById(R.id.editUrl);
		editAvatarUrl = (EditText)findViewById(R.id.editAvatarUrl);
		editCreatedDate = (EditText)findViewById(R.id.editCreatedDate);
		
		//для базы данных
		DbHelper = new GithubDbHelper(this);
		
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
		currentUsername = intent.getStringExtra(KEY_USERNAME);
		currentName = intent.getStringExtra(KEY_NAME);
		currentUrl = intent.getStringExtra(KEY_URL);
		currentAvatarUrl = intent.getStringExtra(KEY_AVATAR_URL);
	    currentCreatedDate = intent.getStringExtra(KEY_CREATED_DATE);
		
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
		editUsername.setText(currentUsername);
		editName.setText(currentName);
		editUrl.setText(currentUrl);
		editAvatarUrl.setText(currentAvatarUrl);
		editCreatedDate.setText(currentCreatedDate);
	}
	
	//для получения измененных данных из полей и добавление их в базу данных
	public void submitCurrentData(){
		//получаем данные с полей
		String tempUsername = editUsername.getText().toString().trim();
		String tempName = editName.getText().toString().trim();
		String tempUrl = editUrl.getText().toString().trim();
		String tempAvatarUrl = editAvatarUrl.getText().toString().trim();
		String tempCreatedDate = editCreatedDate.getText().toString().trim();
		
		//загружаем в базу данных
		SQLiteDatabase db = DbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
				
		//данные, которые нужно обновить
		values.put(GithubEntry.COLUMN_USERNAME, tempUsername);
		values.put(GithubEntry.COLUMN_NAME, tempName);
		values.put(GithubEntry.COLUMN_URL, tempUrl);
		values.put(GithubEntry.COLUMN_AVATAR_URL, tempAvatarUrl);
		values.put(GithubEntry.COLUMN_CREATED_DATE, tempCreatedDate);
		
		//делаем запрос, указывая по какому id нужно изменить информацию
		db.update(
					GithubEntry.GITHUB_TABLE_NAME,
					values,
					GithubEntry._ID + "=?",
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
				Intent intent = new Intent(this, CurrentGithub.class);
				intent.putExtra(KEY_ID, id);
				startActivity(intent);
			}

			//кнопка отмены и возвращения на общий экран
			if(view == buttonCancel){
				Intent intent = new Intent(this, CurrentGithub.class);
				intent.putExtra(KEY_ID, id);
				startActivity(intent);
			}
		}
		
		return false;
	}
}
