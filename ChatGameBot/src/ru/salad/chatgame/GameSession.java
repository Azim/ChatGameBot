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

import ru.salad.chatgame.util.ImgUtils;

public class GameSession {
	private List<Country> players = new ArrayList<Country>();
	private Long chatId;
	private String mapName;
	private BufferedImage map;
	private int[][] location;
	
	public GameSession(Long chatId, String map) {
		this.chatId = chatId;
		this.mapName = map;
		this.location = new int[48][30];
	}
	
	public GameSession(Long chatId) {
		this(chatId,"map_basic.jpg");
	}

	/** Draws the new object above given image
	 * 
	 * @param img - image; set null to get the default image
	 * @param x x coord - set <0 to draw nothing
	 * @param y y coord - set <0 to draw nothing
	 * @param icon object to draw; set null to draw nothing
	 * @return input stream with image in it
	 * @throws IOException
	 */
	public BufferedImage drawImageOnMap(int x, int y, Image icon) throws IOException {
		BufferedImage transparentWhite = ImgUtils.makeWhiteTransparent(icon);
		
		
		if(icon != null && x >= 0 && y >= 0 &&
				(x+transparentWhite.getWidth()<this.map.getWidth()) &&
				(y+transparentWhite.getHeight()<this.map.getHeight())
				) {
			
			Graphics2D g = this.map.createGraphics();
			g.drawImage(transparentWhite,x,y,null);
			g.dispose();
		}
		return this.map;
	}
	
	public InputStream getCurrentMapAsStream() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(this.map, "jpg", os); 
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		os.close();
		return is;
	}
	
	public BufferedImage getCurrentMap() throws IOException {
		if(this.map == null) {
			this.map = ImageIO.read(new File("images/"+mapName));
		}
		return this.map;
	}
	
	
	
	
	/** Adds player to playerlist.
	 * 
	 * @param co player to add
	 */
	public void addPlayer(Country co) {
		if(!this.players.contains(co)) {
			this.players.add(co);
		}
	}
	/** Removes player from playerlist.
	 * 
	 * @param co player toremove
	 * @return false if player not found, otherwise - true;
	 */
	public boolean removePlayer(Country co) {
		if(this.players.contains(co)) {
			this.players.remove(co);
			return true;
		}
		return false;
	}
	/** Get the session's chat id
	 * 
	 * @return Long chat id
	 */
	public Long getChatId() {
		return this.chatId;
	}
}
