package me.devtec.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import me.devtec.server.configs.Data;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.utils.callback.CommandCallback;

public class Main {
	static Logger logger = Logger.getLogger("Fang");
	protected static Data data = new Data("Configs/Fang.yml");

	public static Logger getLogger() {
		return logger;
	}
	
	public static void main(String[] args) {
		
		//CONFIG
		if(!data.exists("Complex-Logger"))
		data.set("Complex-Logger", true);
		
		//LOGGER
		Thread.currentThread().setName("Fang-Server");
		logger.setUseParentHandlers(false);
		ConsoleHandler h = new ConsoleHandler();
		h.setFormatter(new Formatter() {
			SimpleDateFormat s= new SimpleDateFormat("[HH:mm:ss]");
			boolean l = data.getBoolean("Complex-Logger");
			@Override
			public String format(LogRecord record) {
				return s.format(new Date())+(l?" ["+Thread.currentThread().getName()+"] ":" ")+record.getMessage()+System.lineSeparator();
				}
		});
		logger.addHandler(h);
		
		Collection<String> a = new ArrayList<>();
		for(int i = 0; i < 100; ++i)
		a.add(""+i);
		long time = System.nanoTime();
		a.remove("50");
		time-=System.nanoTime();
		logger.info((time*-1)+"");
		
		a = new ArrayList<>();
		for(int i = 0; i < 100; ++i)
		a.add(""+i);
		time = System.nanoTime();
		a.remove("50");
		time-=System.nanoTime();
		logger.info((time*-1)+"");
		
		//LOAD SERVER
		logger.info("Server loading..");
		MinecraftServer load = MinecraftServer.init();
		MinecraftServer.getCommandManager().setUnknownCommandCallback(new CommandCallback() {
			public void apply(CommandSender sender, String command) {
				sender.sendMessage("No such command");
			}
		});
		load.start("localhost", 25565);
		logger.info("Server loaded");
	}
}
