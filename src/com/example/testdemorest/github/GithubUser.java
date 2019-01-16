// ласс POJO с общими данными JSON, которые мы будем получать с сервера

package com.example.testdemorest.github;

import com.google.gson.annotations.SerializedName;

public class GithubUser {
	@SerializedName("login")
	private String login;
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("avatar_url")
	private String avatar_url;
	
	@SerializedName("html_url")
	private String html_url;
	
	@SerializedName("created_at")
	private String created_at;
	
	//геттеры
	public String getLogin(){ return login; }
	public String getName(){ return name; }
	public String getAvatarUrl(){ return avatar_url; }
	public String getHtmlUrl(){ return html_url; }
	public String getCreatedDate(){ return created_at; }
}
