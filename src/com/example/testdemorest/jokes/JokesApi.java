//»нтерфейс дл¤ создани¤ запросов к серверу

package com.example.testdemorest.jokes;

import retrofit2.http.GET;

//данные будут получены в виде Flowable
import io.reactivex.Flowable;

public interface JokesApi {
	//получаем данные
	@GET("random_joke")
	Flowable<RandomJoke> getJoke();
}
