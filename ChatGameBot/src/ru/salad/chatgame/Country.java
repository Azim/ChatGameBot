package ru.salad.chatgame;

import java.util.List;

import ru.salad.chatgame.abilities.ActiveAbility;
import ru.salad.chatgame.abilities.PassiveAbility;

public class Country {
	private Long userId;
	private String name;
	private Object icon;//TODO: find out that will be saved as icon
	private List<ActiveAbility> activeAbilities;
	private List<PassiveAbility> passiveAbilities;
	
	public Country(Long userId, String name, Object icon, List<ActiveAbility> activeAbilities,
			List<PassiveAbility> passiveAbilities) {
		this.userId = userId;
		this.name = name;
		this.icon = icon;
		this.activeAbilities = activeAbilities;
		this.passiveAbilities = passiveAbilities;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getIcon() {
		return icon;
	}

	public void setIcon(Object icon) {
		this.icon = icon;
	}

	public Long getUserId() {
		return userId;
	}

	public List<ActiveAbility> getActiveAbilities() {
		return activeAbilities;
	}

	public List<PassiveAbility> getPassiveAbilities() {
		return passiveAbilities;
	}
	
	public boolean addActiveAbility(ActiveAbility aa) {
		if(!this.activeAbilities.contains(aa)) {
			this.activeAbilities.add(aa);
			return true;
		}
		return false;
	}
	
	public boolean addPassiveAbility(PassiveAbility pa) {
		if(!this.passiveAbilities.contains(pa)) {
			this.passiveAbilities.add(pa);
			return true;
		}
		return false;
	}
	
}
