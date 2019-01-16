// ласс POJO с общими данными JSON, которые мы будем получать с сервера

package com.example.testdemorest.jokes;

import com.google.gson.annotations.SerializedName;

public class RandomJoke {
	@SerializedName("id")
	private int id;
	
	@SerializedName("setup")
	private String setup;
	
	@SerializedName("punchline")
	private String punchline;
	
	//геттеры
	public int getId(){ return id; }
	public String getSetup(){ return setup; }
	public String getPunchline(){ return punchline; }
}
