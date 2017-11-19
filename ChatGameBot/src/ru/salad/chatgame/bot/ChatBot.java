package ru.salad.chatgame.bot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import ru.salad.chatgame.Country;
import ru.salad.chatgame.GameSession;
import ru.salad.chatgame.util.Config;
import ru.salad.chatgame.util.ImgUtils;

public class ChatBot extends TelegramLongPollingBot{
	private final String botUsername;
	private final String botToken;
	private List<GameSession> sessions;
	
	public ChatBot(String botUsername, String botToken) {
		this.botUsername = botUsername;
		this.botToken = botToken;
		this.sessions = new ArrayList<GameSession>();
	}

	public ChatBot(Config config) {
		this(config.getBotUsername(),config.getBotToken());
	}


	@Override
	public void onUpdateReceived(Update update) {
		if(update.hasMessage()&&update.getMessage().hasText()) {
			String text = update.getMessage().getText();
			if(text.startsWith("/next")){
				InputStream is = drawSymbol(0,0,Color.red);
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
				
				
			}else if(text.startsWith("/draw")) {

				String[] data = text.split(" ");
				if(data.length!=3) {
					return;
				}
					
				if(!Country.canGo(3, 3, Integer.valueOf(data[1]),Integer.valueOf(data[2]))) {
					return;
				}

				int[]cords = ImgUtils.transformCoords(Integer.valueOf(data[1]), Integer.valueOf(data[2]));
				InputStream is = drawSymbol(cords[0],cords[1],Color.red);
				if(is == null) return;
				SendPhoto map = new SendPhoto().setNewPhoto("map-"+update.getMessage().getChatId(), is).setChatId(update.getMessage().getChatId());
				try {
					sendPhoto(map);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}else if(text.toLowerCase().startsWith("/newsession")) {
				GameSession ses = new GameSession(update.getMessage().getChatId());
				this.sessions.add(ses);
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
	
	
	private InputStream drawSymbol(int x, int y, Color color) {
		try {
			BufferedImage img = ImageIO.read(new File("images/map_basic_num.jpg"));

			Graphics2D g = img.createGraphics();
			g.setColor(color);
			g.drawLine(x, y, x+26, y+23);
			g.drawLine(x+26, y, x, y+23);
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
