package ru.salad.chatgame.bot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import ru.salad.chatgame.util.Config;

public class ChatBot extends TelegramLongPollingBot{
	private final String botUsername;
	private final String botToken;
	
	public ChatBot(String botUsername, String botToken) {
		this.botUsername = botUsername;
		this.botToken = botToken;
	}

	public ChatBot(Config config) {
		this.botUsername = config.getBotUsername();
		this.botToken = config.getBotToken();
	}


	@Override
	public void onUpdateReceived(Update update) {
		if(update.hasMessage()&&update.getMessage().hasText()) {
			String text = update.getMessage().getText();
			if(text.startsWith("/next")){
				InputStream is = drawSymbol(0,0," ",Color.red);
				SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId()).setText("ХОД-1 (@username)");
				SendPhoto map = new SendPhoto().setNewPhoto("map-"+update.getMessage().getChatId(), is).setChatId(update.getMessage().getChatId()).setCaption("предстоящие события, \nнесколько \nстрок, вроде максимум - 4000 символов");
				try {
					execute(msg);
					sendPhoto(map);
					msg.setText("Для вывода пассивок можно делать отдельную комманду");
					execute(msg);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
				
				
			}else if(text.startsWith("/draw")&&text.contains(":")) {

				String[] cords = text.split(" ")[1].split(":");
				InputStream is = drawSymbol(Integer.valueOf(cords[0]),Integer.valueOf(cords[1]),cords[2],Color.red);
				if(is == null) return;
				SendPhoto map = new SendPhoto().setNewPhoto("map-"+update.getMessage().getChatId(), is).setChatId(update.getMessage().getChatId());
				try {
					sendPhoto(map);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getBotToken() {
		return this.botToken;
	}

	@Override
	public String getBotUsername() {
		return this.botUsername;
	}
	
	
	private InputStream drawSymbol(int x, int y, String symbols, Color color) {
		try {
			BufferedImage img = ImageIO.read(new File("map_basic.jpg"));

			// Obtain the Graphics2D context associated with the BufferedImage.
			Graphics2D g = img.createGraphics();
			g.setColor(color);
			// Draw on the BufferedImage via the graphics context.
			//g.drawOval(50, 50, 12, 12);

			//g.drawLine(42, 14, 80, 80);
			//g.drawLine(80, 80, 118, 145);
			g.drawString(symbols,x,y);
		// Clean up -- dispose the graphics context that was created.
			g.dispose();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			ImageIO.write(img, "jpg", os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			return is;
			
		}catch(IOException e){
			e.printStackTrace();
			
			return null;
		}
	}
}
