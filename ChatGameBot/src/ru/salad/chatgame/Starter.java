package ru.salad.chatgame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import ru.salad.chatgame.bot.ChatBot;
import ru.salad.chatgame.util.Config;
import ru.salad.chatgame.util.Node;

public class Starter {
	public static Gson gson;
	private static Config config;
	
	public static void main(String[] args) {
		System.out.println("Preparing gson");
		gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
		
		System.out.println("Attempting to load configs");
		
		if(loadConfigs()) {
			System.out.println("Successfully loaded configs!\nPreparing the bot");
		}else {
			System.out.println("Fatal error occured while loading configs, shutting down!");
			System.exit(2);
		}

		System.out.println("Loading Telegramm libraries (offline)");
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        ChatBot bot = new ChatBot(config);
        try {
    		System.out.println("Starting the bot");
			telegramBotsApi.registerBot(bot);
		} catch (TelegramApiRequestException e) {
			e.printStackTrace();
			System.out.println("unable to register bot");
		}
		System.out.println("Successfully started the bot!");
		
	}
	private static boolean loadConfigs() {
		try {
			File cfg = new File("config.json");
			if(!cfg.exists()) {
				cfg.createNewFile();
				System.out.println("No config file found, creating a new one...");
				if(!writeDefaults(cfg,new Config())) {
					System.out.println("Unable to create config file, shuttong down...");
					System.exit(1);
				}
				Node root = new Node("beginning","first Message");
				root.addChild("second", "secondMessage");
				root.addChild("third", "thirdMessage");
				Node ch2 = new Node("30","300");
				ch2.addChild(new Node("deeper","test"));
				root.addChild(ch2);
				File test = new File("test.json");
				if(!writeDefaults(test, root)) {
					System.out.println("Unable to create test");
				}
				
				System.out.println("Successfully created default config file, please, enter valid bot username and bot token and then try again.");
				System.exit(0);
			}
			
			config = gson.fromJson(readJsonFile(cfg), Config.class);
			
			
		}catch (IOException ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	private static String readJsonFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
		BufferedReader bufferedReader = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
		    sb.append(line);
		}
		fis.close();
		isr.close();
		bufferedReader.close();
		String json = sb.toString();
		return json;
	}
	
	private static boolean writeDefaults(File file, Object obj) {
		try {
			FileWriter fw = new FileWriter(file);
			gson.toJson(obj, fw);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
		return true;
	}

}
