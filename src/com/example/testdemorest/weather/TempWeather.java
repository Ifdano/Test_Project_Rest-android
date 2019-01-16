//Класс POJO с общими данными JSON, которые мы будем получать с сервера

package com.example.testdemorest.weather;

import com.google.gson.annotations.SerializedName;

public class TempWeather {
	@SerializedName("description")
	private String description;
	
	//геттеры
	public String getDescription(){ return description; }
}
