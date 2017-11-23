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

import ru.salad.chatgame.util.Cell;
import ru.salad.chatgame.util.Utils;

public class GameSession {
	private List<Player> players = new ArrayList<Player>();
	private Long chatId;
	private String mapName;
	private BufferedImage map;
	private Integer[][] location;
	private Player turn;
	private int fullTurns; 
	
	public GameSession(Long chatId, String map) throws IOException {
		this.chatId = chatId;
		this.mapName = map;
		this.location = new Integer[30][30];
		for(int i=0;i<this.location.length;i++) {
			for(int j=0;j<this.location[0].length;j++) {
				location[i][j]=0;
			}
		}
		this.fullTurns = 0;
		this.map = ImageIO.read(new File("images/"+map));
	}
	
	public GameSession(Long chatId) throws IOException {
		this(chatId,"test.jpg");
	}

	public void nextTurn() {
		int currentIndex = this.players.indexOf(this.turn);
		if(currentIndex+1 >= this.players.size()) {
			currentIndex = 0;
			this.fullTurns ++;
		}else {
			currentIndex++;
		}
		this.turn = this.players.get(currentIndex);
	}
	

	public void go(Player pl, Cell toGo) {
		this.location[toGo.getX()][toGo.getY()] = pl.getUserId();
		pl.addCell(toGo);
		int[]dataPixels = Utils.transformCoords(toGo.getX(),toGo.getY());
		this.drawImageOnMap(dataPixels[0], dataPixels[1], pl.getIcon());
		
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
	public BufferedImage drawImageOnMap(int x, int y, Image icon) {
		BufferedImage transparentWhite = Utils.makeWhiteTransparent(icon);
		BufferedImage tmap = null;
		try {
			tmap = getCurrentMap();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Unable to get current map");
		}
		
		if(icon != null && x >= 0 && y >= 0 &&
				(x+transparentWhite.getWidth()<
						tmap.getWidth()) &&
				(y+transparentWhite.getHeight()<tmap.getHeight())
				) {
			
			Graphics2D g = tmap.createGraphics();
			g.drawImage(transparentWhite,x,y,null);
			g.dispose();
			this.map = tmap;
		}
		return this.map;
	}
	/** Returns current map as InputStream; if not created, generates new one;
	 * 
	 * @return InputStream map 
	 * @throws IOException
	 */
	public InputStream getCurrentMapAsStream() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(this.getCurrentMap(), "jpg", os); 
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		os.close();
		return is;
	}
	
	public void reDraw(){
		for(int i=0;i<this.location.length;i++) {
			for(int j=0;j<this.location[0].length;j++) {
				if(location[i][j]==0) {
					//empty
					continue;
				}
				if(location[i][j]==1) {
					//water
					continue;
				}
				if(location[i][j]==2) {
					//rock
					continue;
				}
				int[]data = Utils.transformCoords(i, j);
				drawImageOnMap(data[0], data[1], this.getPlayerById(location[i][j]).getIcon());
			}
		}
	}
	
	/** Returns current map as BufferedImage; if not created, generates new one;
	 * 
	 * @return BufferedImage map
	 * @throws IOException - no file found
	 */
	public BufferedImage getCurrentMap() throws IOException {
		if(this.map == null) {
			this.map = ImageIO.read(new File("images/"+mapName));
		}
		return this.map;
	}
	
	public boolean containsPlayer(Integer id) {
		for(Player co:this.players) {
			if(co.getUserId()==id) {
				return true;
			}
		}
		return false;
	}
	
	public Player getCurrentTurn() {
		if(this.turn == null&&!this.players.isEmpty()) {
			this.turn = this.players.get(0);
		}
		return this.turn;
	}
	
	public Player getPlayerById(Integer id) {
		for(Player co:this.players) {
			if(co.getUserId()==id) {
				return co;
			}
		}
		return null;
	}
	/** Adds player to playerlist.
	 * 
	 * @param co player to add
	 */
	public void addPlayer(Player co) {
		if(!this.players.contains(co)) {
			this.players.add(co);
		}
		if(this.turn==null) {
			this.turn = co;
		}
	}
	/** Removes player from playerlist.
	 * 
	 * @param co player toremove
	 * @return false if player not found, otherwise - true;
	 */
	public boolean removePlayer(Player co) {
		if(this.players.contains(co)) {
			if(this.turn.getUserId()==co.getUserId()) {
				this.nextTurn();
			}
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
	
	public int getFullTurns() {
		return this.fullTurns;
	}

}
