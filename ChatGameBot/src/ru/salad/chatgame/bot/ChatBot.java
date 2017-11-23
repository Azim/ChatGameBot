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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import ru.salad.chatgame.Player;
import ru.salad.chatgame.GameSession;
import ru.salad.chatgame.util.Cell;
import ru.salad.chatgame.util.Config;
import ru.salad.chatgame.util.Utils;

public class ChatBot extends TelegramLongPollingBot{
	private final String botUsername;
	private final String botToken;
	private List<GameSession> sessions;
	private Map<Long,List<Integer>> messagesToDelete;
	
	public ChatBot(String botUsername, String botToken) {
		this.botUsername = botUsername;
		this.botToken = botToken;
		this.sessions = new ArrayList<GameSession>();
		this.messagesToDelete = new HashMap<Long,List<Integer>>();
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
				
				
			}else */
			if(text.startsWith("/go")) {
				SendMessage msg = new SendMessage().setChatId(chatId);
				if(Utils.containsSessionWithId(sessions, chatId)) {
					GameSession session = Utils.getSessionById(sessions, chatId);
				
					if(session.containsPlayer(fromId)) {
						if(session.getCurrentTurn().getUserId()==fromId) {
							Player pl = session.getPlayerById(fromId);
							String[] data = text.split(" ");
							if(data.length<3) {
								return;
							}
							Cell toGo = new Cell(Integer.valueOf(data[1]), Integer.valueOf(data[2]));
						
							if(!pl.canGo(toGo.getX(),toGo.getY())) {
								System.out.println("can't go");

								msg.setText("Can't go there!");
								return;
							}
							
						
							//int[]cords = Utils.transformCoords(toGo.getX(),toGo.getY());
							InputStream is = null;//drawSymbol(Utils.getSessionById(this.sessions, update.getMessage().getChatId()),cords[0],cords[1],Color.red);
							try {
								session.go(pl,toGo);
								is = session.getCurrentMapAsStream();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							if(is == null) return;
					
							SendPhoto map = new SendPhoto().setNewPhoto("map-"+update.getMessage().getChatId(), is).setChatId(update.getMessage().getChatId()).setCaption("X: "+toGo.getX()+"\nY: "+toGo.getY());
							session.nextTurn();
							try {
								delMessages(chatId);
								Message sm = sendPhoto(map);
								List<Integer> temp = this.messagesToDelete.get(chatId);
								temp.add(sm.getMessageId());
								this.messagesToDelete.put(chatId, temp);
							} catch (TelegramApiException e) {
								e.printStackTrace();
							}
						}else {
							msg.setText("It's not your turn!");
						}
					}else {
						msg.setText("You must join the game first\nTo do so, use /joingame");
					}
				}else {
					msg.setText("Here are no game running now\nTo start one, use /newgame");
				}
				
				try {
					if(!(msg.getText()==null||msg.getText().isEmpty())) {
						execute(msg);
					}
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}else if(text.toLowerCase().startsWith("/newgame")) {
				try {
					GameSession ses;
					ses = new GameSession(update.getMessage().getChatId());
					SendMessage msg = new SendMessage().setChatId(chatId);
					if(!Utils.containsSessionWithId(sessions, chatId)) {
						this.sessions.add(ses);
						System.out.println("added session "+chatId);
						msg.setText("Created a new game! To join it, use /joingame");
					}else {
						this.sessions.remove(Utils.getSessionById(sessions, chatId));
						msg.setText("Game was reset!");
					}
					delMessages(chatId);
					execute(msg);
				} catch (IOException | TelegramApiException e) {
					e.printStackTrace();
				}
				
			}else if(text.toLowerCase().startsWith("/joingame")&&text.split(" ").length==2) {
				if(Utils.containsSessionWithId(sessions, chatId)) {
					GameSession ses = Utils.getSessionById(sessions, chatId);
					if(!ses.containsPlayer(fromId)) {
						String name = text.split(" ")[1];
						Random rnd = new Random();
						rnd.setSeed(System.currentTimeMillis());
						Cell sc = new Cell(rnd.nextInt(30),rnd.nextInt(30));
						Player nc = new Player(fromId,name,null,null,sc);
						
						ses.addPlayer(nc);
						System.out.println("added player "+fromId+" to session "+chatId+" with starting pos '"+sc.getX()+"' & '"+sc.getY()+"'");
						
						int[]cords = Utils.transformCoords(sc.getX(), sc.getY());
						InputStream is = null;// = drawSymbol(Utils.getSessionById(this.sessions, update.getMessage().getChatId()),cords[0],cords[1],Color.GREEN);
						try {
							ses.drawImageOnMap(cords[0], cords[1], ImageIO.read(new File("images/1st.png")));
							is = ses.getCurrentMapAsStream();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						if(is == null) {
							System.out.println("empty image");
							return;
						}
					
						SendPhoto map = new SendPhoto().setNewPhoto("map-"+update.getMessage().getChatId(), is).setChatId(update.getMessage().getChatId()).setCaption("Joined the game! your spawn location is:\nX: "+sc.getX()+"\nY: "+sc.getY()+"\n\nUse /go X Y to expand");
						try {
							delMessages(chatId);
							Message sm = sendPhoto(map);
							List<Integer> temp = this.messagesToDelete.get(chatId);
							temp.add(sm.getMessageId());
							this.messagesToDelete.put(chatId, temp);
						} catch (TelegramApiException e) {
							e.printStackTrace();
						}
						
					}else {
						SendMessage msg = new SendMessage().setChatId(chatId).setText("You are already in game");
						try {
							execute(msg);
						} catch (TelegramApiException e) {
							e.printStackTrace();
						}
					}
				}else {
					SendMessage msg = new SendMessage().setChatId(chatId).setText("Here are no running game right now, you can create new one using command '/newgame'");
					try {
						execute(msg);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}
			}else if(text.toLowerCase().startsWith("/leavegame")) {
				SendMessage msg = new SendMessage().setChatId(chatId).setText("Here are no running game right now, you can create new one using command '/newgame'").setReplyToMessageId(messageId);
				if(Utils.containsSessionWithId(sessions, chatId)) {
					GameSession ses = Utils.getSessionById(sessions, chatId);
					if(ses.containsPlayer(fromId)) {
						ses.removePlayer(ses.getPlayerById(fromId));
						msg.setText("You have left the game");
					}else {
						msg.setText("You are not in the game yet");
					}
				}
				try {
					execute(msg);
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
	
	/** Deletes messages with outdated map
	 * 
	 * @param chatId the chat where messages will be removed
	 */
	private void delMessages(Long chatId) {
		List<Integer> msgs = this.messagesToDelete.get(chatId);
		for(int mid:msgs) {
			try {
				DeleteMessage dm = new DeleteMessage(chatId, mid);
				execute(dm);
				Thread.sleep(300);
			}catch(InterruptedException|TelegramApiException e) {
				e.printStackTrace();
			}
		}
		
	}
	

	@Deprecated
	/** Debug function to draw X symbol on the specific cell
	 * 
	 * @param s session
	 * @param x 
	 * @param y
	 * @param color 
	 * @return
	 */
	private InputStream drawX(GameSession s,int x, int y, Color color) {
		try {
			BufferedImage img = s.getCurrentMap();

			Graphics2D g = img.createGraphics();
			g.setColor(color);
			g.drawLine(x, y, x+21, y+21);
			g.drawLine(x+21, y, x, y+21);
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
