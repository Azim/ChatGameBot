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
import java.util.Random;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import ru.salad.chatgame.Country;
import ru.salad.chatgame.GameSession;
import ru.salad.chatgame.util.Cell;
import ru.salad.chatgame.util.Config;
import ru.salad.chatgame.util.Utils;

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
			Long chatId = update.getMessage().getChatId();
			Integer fromId = update.getMessage().getFrom().getId();
			Integer messageId = update.getMessage().getMessageId();
			/*if(text.startsWith("/next")){
				InputStream is = drawSymbol(0,0,Color.red);
				SendMessage msg = new SendMessage().setChatId(update.getMessage().getChatId()).setText("���-1 (@username)");
				SendPhoto map = new SendPhoto().setNewPhoto("map-"+update.getMessage().getChatId(), is).setChatId(update.getMessage().getChatId()).setCaption("����������� �������, \n��������� \n�����, ����� �������� - 4000 ��������");
				try {
					execute(msg);
					sendPhoto(map);
					msg.setText("��� ������ �������� ����� ������ ��������� ��������");
					execute(msg);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
				
				
			}else */
			if(text.startsWith("/go")) {
				if(Utils.containsSessionWithId(sessions, chatId)) {
				GameSession session = Utils.getSessionById(sessions, chatId);
				
					if(session.containsPlayer(fromId)) {
						Country pl = session.getPlayerById(fromId);
						String[] data = text.split(" ");
						if(data.length!=3) {
							return;
						}
						Cell toGo = new Cell(Integer.valueOf(data[1]), Integer.valueOf(data[2]));
						
						if(!pl.canGo(toGo.getX(),toGo.getY())) {
							System.out.println("can't go");
							return;
						}
						
						pl.addCell(toGo);
						
						int[]cords = Utils.transformCoords(toGo.getX(),toGo.getY());
						InputStream is = null;//drawSymbol(Utils.getSessionById(this.sessions, update.getMessage().getChatId()),cords[0],cords[1],Color.red);
						try {
							session.drawImageOnMap(cords[0], cords[1], ImageIO.read(new File("images/2nd.jpg")));
							is = session.getCurrentMapAsStream();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						if(is == null) return;
					
						SendPhoto map = new SendPhoto().setNewPhoto("map-"+update.getMessage().getChatId(), is).setChatId(update.getMessage().getChatId()).setCaption("X: "+toGo.getX()+"\nY: "+toGo.getY());
						try {
							sendPhoto(map);
						} catch (TelegramApiException e) {
							e.printStackTrace();
						}
					}else {
						//not a player
					}
				}else {
					//create new game first
				}
			}else if(text.toLowerCase().startsWith("/newsession")) {
				if(!Utils.containsSessionWithId(sessions, chatId)) {
					GameSession ses;
					try {
						ses = new GameSession(update.getMessage().getChatId());
					this.sessions.add(ses);
					System.out.println("added session "+chatId);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else if(text.toLowerCase().startsWith("/joingame")) {
				if(Utils.containsSessionWithId(sessions, chatId)) {
					GameSession ses = Utils.getSessionById(sessions, chatId);
					if(!ses.containsPlayer(fromId)) {
						Random rnd = new Random();
						rnd.setSeed(System.currentTimeMillis());
						Cell sc = new Cell(rnd.nextInt(48),rnd.nextInt(30));
						Country nc = new Country(fromId,"test",null,null,null,sc);
						
						ses.addPlayer(nc);
						System.out.println("added player "+fromId+" to session "+chatId+" with starting pos '"+sc.getX()+"' & '"+sc.getY()+"'");
						
						int[]cords = Utils.transformCoords(sc.getX(), sc.getY());
						InputStream is = null;// = drawSymbol(Utils.getSessionById(this.sessions, update.getMessage().getChatId()),cords[0],cords[1],Color.GREEN);
						try {
							ses.drawImageOnMap(cords[0], cords[1], ImageIO.read(new File("images/start.jpg")));
							is = ses.getCurrentMapAsStream();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						if(is == null) {
							System.out.println("empty image");
							return;
						}
					
						SendPhoto map = new SendPhoto().setNewPhoto("map-"+update.getMessage().getChatId(), is).setChatId(update.getMessage().getChatId()).setCaption("X: "+sc.getX()+"\nY: "+sc.getY());
						try {
							sendPhoto(map);
						} catch (TelegramApiException e) {
							e.printStackTrace();
						}
						
					}else {
						//already exists
					}
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
	
	
	private InputStream drawSymbol(GameSession s,int x, int y, Color color) {
		try {
			BufferedImage img = s.getCurrentMap();
			//BufferedImage img = ImageIO.read(new File("images/map_basic_num.jpg"));

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
