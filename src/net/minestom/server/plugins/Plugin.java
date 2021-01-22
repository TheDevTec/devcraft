package net.minestom.server.plugins;

import net.minestom.server.MinecraftServer;

public class Plugin {
	protected Description desctiption;
	protected boolean enabled;
	
	public void onEnable() {}
	
	public void onDisable() {}
	
	public void onLoad() {}

	public Description getDesctiption() {
		return desctiption;
	}
	
	public String getName() {
		return desctiption.getName();
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setNaggable() {
		MinecraftServer.getPluginManager().disable(this);
	}
}
