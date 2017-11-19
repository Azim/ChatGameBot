package ru.salad.chatgame.util;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImgUtils {
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
		
		if(x%2==0) {
			x = x*41/2+26;
			y = 44;
		}else {
			x = (x-1)/2*41+46;
			y = 32;
		}
		if(Integer.valueOf(data[1])%2==0) {
			y = y  + 47*Integer.valueOf(data[1])/2;
		}else {
			y = y + 24 + 47*(Integer.valueOf(data[1])-1)/2;
		}
		data[0]=x;
		data[1]=y;
		
		return data;
	}
}
