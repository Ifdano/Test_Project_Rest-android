//Класс с реализацией паттерна "ќдиночка"

package com.example.testdemorest.weather;

//дл¤ Retrofit 
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class WeatherService {
	//базовый url дл¤ получени¤ данных с сервера
	private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
	
	//единичный экземпл¤р данного класса
	private static WeatherService mInstance;
	//сам Retrofit
	private Retrofit retrofit;
	
	//в конструкторе создаем основу Retrofit, указав использование RxJava
	private WeatherService(){
		retrofit = new Retrofit.Builder()
							   .baseUrl(BASE_URL)
							   .addConverterFactory(GsonConverterFactory.create())
							   .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
							   .build();
	}
	
	//создание/получение единичного экземпл¤ра данного класса
	public static WeatherService getService(){
		if(mInstance == null)
			mInstance = new WeatherService();
		
		return mInstance;
	}
	
	//получение рабочего api, чтобы потом делать запросы на сервер
	public WeatherApi getApi(){
		return retrofit.create(WeatherApi.class);
	}
}
