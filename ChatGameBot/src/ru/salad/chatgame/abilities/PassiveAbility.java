package ru.salad.chatgame.abilities;

public class PassiveAbility {
	private String name;
	private Object action;  //TODO: getters and setters, action and logic
	private String description;
	
	public PassiveAbility(String name, String description, Object action) {
		this.name = name;
		this.description = description;
		this.action = action;
	}
}
