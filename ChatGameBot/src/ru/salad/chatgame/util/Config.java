package ru.salad.chatgame.util;

import com.google.gson.annotations.Expose;

public class Config {

	@Expose()
	private String botUsername;

	@Expose()
	private String botToken;
	
	public String getBotUsername() {
		return botUsername;
	}
	public void setBotUsername(String botUsername) {
		this.botUsername = botUsername;
	}
	public String getBotToken() {
		return botToken;
	}
	public void setBotToken(String botToken) {
		this.botToken = botToken;
	}

	
	public Config() {
		this.botUsername = "Your Bot Username here";
		this.botToken = "Your Bot Token here";
	}
}
