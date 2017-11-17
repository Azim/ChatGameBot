package ru.salad.chatgame.abilities;

public class ActiveAbility {
	private String name;
	private Object action;  //TODO: getters and setters, action and logic
	private String description;
	
	public ActiveAbility(String name, String description, Object action) {
		this.name = name;
		this.description = description;
		this.action = action;
	}
	
}
