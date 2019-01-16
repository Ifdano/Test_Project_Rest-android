//»нтерфейс дл¤ создани¤ запросов к серверу

package com.example.testdemorest.github;

import retrofit2.http.GET;
import retrofit2.http.Path;

//данные будут получены в виде Flowable
import io.reactivex.Flowable;

public interface GithubUserApi {
	//получаем данные, подставл¤¤ им¤ пользовател¤ в запрос
	@GET("users/{username}")
	Flowable<GithubUser> getUser(
				@Path("username") String username
			);
}
