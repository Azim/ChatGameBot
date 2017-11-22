package ru.salad.chatgame;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import ru.salad.chatgame.abilities.ActiveAbility;
import ru.salad.chatgame.abilities.PassiveAbility;
import ru.salad.chatgame.util.Cell;

public class Country {
	private int userId;
	private String name;
	private Image icon;//TODO: find out that will be saved as icon
	private List<ActiveAbility> activeAbilities;
	private List<PassiveAbility> passiveAbilities;
	private List<Cell> cells;
	
	public Country(int userId, String name, Image icon, List<ActiveAbility> activeAbilities,
			List<PassiveAbility> passiveAbilities, Cell startingCell) {
		this.userId = userId;
		this.name = name;
		this.icon = icon;
		this.activeAbilities = activeAbilities;
		this.passiveAbilities = passiveAbilities;
		this.cells = new ArrayList<Cell>();
		this.cells.add(startingCell);
	}
	
	public boolean canGo(int x2, int y2) {
		if(x2<0||y2<0||x2>29||y2>29) {
			return false;
		}
		for(Cell loc:this.cells) {
			if(loc.getX()==x2&&loc.getY()==y2) {
				continue;
			}
			
			int x1 = loc.getX();
			int y1 = loc.getY();
			int dx = Math.abs(x1-x2);
			if(y1==y2) {
				if(dx==1) {
					return true;
				}
			}
		
			if((x1 % 2 == 0)) {
				if(((y1==y2-1)&&(dx<=1))||((y1 == y2+1)&&(x1==x2))){
					return true;
				}
			} else {
				if (((y1 == y2 + 1) && (dx <= 1)) || ((y1 == y2 - 1) && (x1 == x2))) {
					return true;
				}
				/*
				 * if(y1==y2-1){ if(dx<=1) { return true; } } if(y1 == y2+1) { if(x1==x2) {
				 * return true; } }
				 * 
				 * }else{
				 * 
				 * if(y1==y2+1){ if(dx<=1) { return true; } } if(y1 == y2-1) { if(x1==x2) {
				 * return true; } }
				 */
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public int getUserId() {
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
	
	public List<Cell> getCells(){
		return this.cells;
	}
	public boolean addCell(Cell c) {
		if(!this.cells.contains(c)) {
			this.cells.add(c);
		}
		return false;
	}
	
}
