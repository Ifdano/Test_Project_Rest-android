//Класс POJO с общими данными JSON, которые мы будем получать с сервера

package com.example.testdemorest.weather;

import com.google.gson.annotations.SerializedName;

public class TempFinal {
	@SerializedName("weather")
	private TempWeather[] weather;
	
	@SerializedName("main")
	private TempMain main;
	
	@SerializedName("wind")
	private TempWind wind;
	
	@SerializedName("name")
	private String name;
	
	//геттеры
	public TempWeather[] getWeather(){ return weather; }
	public TempMain getMain(){ return main; }
	public TempWind getWind(){ return wind; }
	public String getName(){ return name; }
}
