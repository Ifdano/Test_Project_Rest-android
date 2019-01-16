//Класс POJO с общими данными JSON, которые мы будем получать с сервера

package com.example.testdemorest.weather;

import com.google.gson.annotations.SerializedName;

public class TempMain {
	@SerializedName("temp")
	private float temp;
	
	@SerializedName("pressure")
	private float pressure;
	
	@SerializedName("humidity")
	private float humidity;
	
	//геттеры
	public float getTemp(){ return temp; }
	public float getPressure(){ return pressure; }
	public float getHumidity(){ return humidity; }
}
