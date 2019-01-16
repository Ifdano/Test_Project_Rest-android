//Класс POJO с общими данными JSON, которые мы будем получать с сервера

package com.example.testdemorest.weather;

import com.google.gson.annotations.SerializedName;

public class TempWind {
	@SerializedName("speed")
	private float speed;
	
	//геттеры
	public float getSpeed(){ return speed; }
}
