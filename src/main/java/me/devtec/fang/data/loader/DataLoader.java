package me.devtec.fang.data.loader;

import me.devtec.fang.data.datas.Data;
import me.devtec.fang.utils.StreamUtils;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class DataLoader implements Data {
	public abstract Map<String, me.devtec.fang.data.Data.DataHolder> get();

	public abstract void set(String key, me.devtec.fang.data.Data.DataHolder value);

	public abstract void remove(String key);

	public abstract Collection<String> getHeader();

	public abstract Collection<String> getFooter();

	public abstract Set<String> getKeys();

	public abstract void reset();
	
	public abstract void load(String input);
	
	public abstract boolean isLoaded();

	public void load(File f) {
		load(StreamUtils.fromStream(f));
	}

	public static DataLoader findLoaderFor(File a) {
		return findLoaderFor(StreamUtils.fromStream(a));
	}

	public static DataLoader findLoaderFor(String input) {
		DataLoader data = new ByteLoader();
		try {
		data.load(input);
		if(data.isLoaded())return data;
		}catch(Exception err) {}
		data = new JsonLoader();
		try {
		data.load(input);
		if(data.isLoaded())return data;
		}catch(Exception err) {}
		data = new YamlLoader();
		try {
		data.load(input);
		if(data.isLoaded())return data;
		}catch(Exception err) {}
		return new EmptyLoader();
	}
}
