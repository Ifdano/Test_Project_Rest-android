// ласс с реализацией паттерна "ќдиночка"

package com.example.testdemorest.github;

//дл¤ Retrofit 
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class GithubService {
	//базовый url дл¤ получени¤ данных с сервера
	private static final String BASE_URL = "https://api.github.com/";
	
	//единичный экземпл¤р данного класса
	private static GithubService mInstance;
	//сам Retrofit
	private Retrofit retrofit;
	
	//в конструкторе создаем основу Retrofit, указав использование RxJava
	private GithubService(){
		retrofit = new Retrofit.Builder()
				   	   	  	   .baseUrl(BASE_URL)
				   	   	  	   .addConverterFactory(GsonConverterFactory.create())
				   	   	  	   .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				   	   	  	   .build();
	}
	
	//создание/получение единичного экземпл¤ра данного класса
	public static GithubService getService(){
		if(mInstance == null)
			mInstance = new GithubService();
		
		return mInstance;
	}
	
	//получение рабочего api, чтобы потом делать запросы на сервер
	public GithubUserApi getApi(){
		return retrofit.create(GithubUserApi.class);
	}
}
