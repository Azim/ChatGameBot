package ru.salad.chatgame.util;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import ru.salad.chatgame.GameSession;

public class Utils {
	 /** makes all white pixels transparent
	  * 
	  * @param img 
	  * @return BufferedImage img
	  */
	public static BufferedImage makeWhiteTransparent(Image img) {
		BufferedImage dst = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		dst.getGraphics().drawImage(img, 0, 0, null);
		int markerRGB = Color.WHITE.getRGB() | 0xFF000000;
		int width = dst.getWidth();
		int height = dst.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = dst.getRGB(x, y);
				if ((rgb | 0xFF000000) == markerRGB) {
					int value = 0x00FFFFFF & rgb;
					dst.setRGB(x, y, value);
				}
			}
		}
		return dst;
	}
	/** transforms coords from cell x:y to pixel x:y
	 * 
	 * @param x - x coord
	 * @param y - y coord
	 * @return int[x,y] - transformed coords
	 */
	public static int[] transformCoords(int x, int y) {
		int[] data = {x,y};
		
		

		x = x*21+21;
		if(x%2==0) {
			y = 21;
		}else {
			y = 32;
		}
		y = data[1]*22+y;
		data[0]=x;
		data[1]=y;
		
		return data;
	}
	
	public static GameSession getSessionById(List<GameSession> sessions,Long id) {
		for(GameSession session:sessions) {
			if(session.getChatId().equals(id)) {
				return session;
			}
		}
		return null;
	}
	
	public static boolean containsSessionWithId(List<GameSession> sessions, Long id) {
		for(GameSession session:sessions) {
			if(session.getChatId().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
}
