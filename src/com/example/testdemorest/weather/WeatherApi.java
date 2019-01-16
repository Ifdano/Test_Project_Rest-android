//Интерфейс дл¤ создани¤ запросов к серверу

package com.example.testdemorest.weather;

import retrofit2.http.GET;
import retrofit2.http.Query;

//данные будут получены в виде Flowable
import io.reactivex.Flowable;

public interface WeatherApi {
	//получаем данные, указав условия
	@GET("weather")
	Flowable<TempFinal> getWeather(
				@Query("q") String city,
				@Query("appid") String key,
				@Query("units") String units
			);
}
