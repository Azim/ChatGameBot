package ru.salad.chatgame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Session {
	private List<Country> players = new ArrayList<Country>();
	private Long chatId;
	private String map;
	
	/** Draws the new object above given image
	 * 
	 * @param img - image; set null to get the default image
	 * @param x x coord - set <0 to draw nothing
	 * @param y y coord - set <0 to draw nothing
	 * @param icon object to draw; set null to draw nothing
	 * @return inputStream with image in it
	 * @throws IOException
	 */
	public InputStream drawMap(BufferedImage img, int x, int y, Image icon) throws IOException {
		if(img == null) {
			img = ImageIO.read(new File("map_basic.jpg"));
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		if(icon != null && x >= 0 && y >= 0) {
			Graphics2D g = img.createGraphics();
			g.drawImage(icon,x,y,null);
			g.dispose();
		}
		ImageIO.write(img, "jpg", os); 
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		return is;
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
