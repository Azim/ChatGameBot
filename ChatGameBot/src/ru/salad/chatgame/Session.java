package ru.salad.chatgame;

import java.util.ArrayList;
import java.util.List;

public class Session {
	private List<Country> players = new ArrayList<Country>();
	private Long chatId;
	
	
	
	public void drawMap() {
		
	}
	
	public void addPlayer(Country co) {
		if(!this.players.contains(co)) {
			this.players.add(co);
		}
	}
	
	public boolean removePlayer(Country co) {
		if(this.players.contains(co)) {
			this.players.remove(co);
			return true;
		}
		return false;
	}
}
