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
}
