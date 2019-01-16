//Основной класс. Переход на экран с нужной информацией.

package com.example.testdemorest;

import android.app.Activity;
import android.os.Bundle;

import android.widget.Button;

import android.view.View;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

import android.content.Intent;

import com.example.testdemorest.weather.MainWeather;
import com.example.testdemorest.jokes.MainJokes;
import com.example.testdemorest.github.MainGithub;

public class Main extends Activity implements OnTouchListener{
	
	//кнопки для перехода на нужны экран
	private Button buttonWeather;
	private Button buttonJokes;
	private Button buttonGithub;
	
	private Intent intent;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}
	
	//находим компоненты и устанавливаем слушателей
	public void init(){
		buttonWeather = (Button)findViewById(R.id.buttonWeather);
		buttonJokes = (Button)findViewById(R.id.buttonJoke);
		buttonGithub = (Button)findViewById(R.id.buttonGithub);
		
		buttonWeather.setOnTouchListener(this);
		buttonJokes.setOnTouchListener(this);
		buttonGithub.setOnTouchListener(this);
	}
	
	//касание экрана
	public boolean onTouch(View view, MotionEvent event){
		
		//при касании нужной нам кнопки - переходим на нужный нам экран
		//варианты: погода, шутки или профиль github.
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(view == buttonWeather){
				intent = new Intent(this, MainWeather.class);
				startActivity(intent);
			}
			
			if(view == buttonJokes){
				intent = new Intent(this, MainJokes.class);
				startActivity(intent);
			}
			
			if(view == buttonGithub){
				intent = new Intent(this, MainGithub.class);
				startActivity(intent);
			}
		}
		
		return false;
	}
}