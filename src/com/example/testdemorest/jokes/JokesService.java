// ласс с реализацией паттерна "ќдиночка"

package com.example.testdemorest.jokes;

//дл¤ Retrofit 
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class JokesService {
	//базовый url дл¤ получени¤ данных с сервера
	private static final String BASE_URL = "https://official-joke-api.appspot.com/";
	
	//единичный экземпл¤р данного класса
	private static JokesService mInstance;
	//сам Retrofit
	private Retrofit retrofit;
	
	//в конструкторе создаем основу Retrofit, указав использование RxJava
	private JokesService(){
		retrofit = new Retrofit.Builder()
							   .baseUrl(BASE_URL)
							   .addConverterFactory(GsonConverterFactory.create())
							   .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
							   .build();
	}
	
	//создание/получение единичного экземпл¤ра данного класса
	public static JokesService getService(){
		if(mInstance == null)
			mInstance = new JokesService();
		
		return mInstance;
	}
	
	//получение рабочего api, чтобы потом делать запросы на сервер
	public JokesApi getApi(){
		return retrofit.create(JokesApi.class);
	}
}
