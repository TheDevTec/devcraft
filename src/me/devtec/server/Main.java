package me.devtec.server;

import java.io.File;

import net.minestom.server.MinecraftServer;

public class Main {

	public static void main(String[] args) {
		try {
			new MinecraftServer(new File(Main.class
			          .getProtectionDomain()
			          .getCodeSource()
			          .getLocation().toURI().getPath()));
		} catch (Exception e) {
			e.printStackTrace();
			MinecraftServer.getLogger().info("Server is closing..");
			return;
		}
	}
}
