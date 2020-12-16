package me.devtec.simplecraft.data.loader;

import me.devtec.simplecraft.data.Data;
import me.devtec.simplecraft.data.collections.UnsortedList;
import me.devtec.simplecraft.data.maps.UnsortedMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmptyLoader extends DataLoader {
	private Map<String, Data.DataHolder> data = new UnsortedMap<>();
	private List<String> header = new UnsortedList<>(), footer = new UnsortedList<>();

	@Override
	public Map<String, Data.DataHolder> get() {
		return data;
	}

	public Set<String> getKeys() {
		return data.keySet();
	}

	public void set(String key, Data.DataHolder holder) {
		if (key == null)
			return;
		if (holder == null) {
			remove(key);
			return;
		}
		data.put(key, holder);
	}

	public void remove(String key) {
		if (key == null)
			return;
		data.remove(key);
	}

	public void reset() {
		data.clear();
		header.clear();
		footer.clear();
	}

	@Override
	public void load(String input) {
		reset();
	}

	@Override
	public Collection<String> getHeader() {
		return header;
	}

	@Override
	public Collection<String> getFooter() {
		return footer;
	}

	@Override
	public boolean isLoaded() {
		return true;
	}
	
	public String toString() {
		return getDataName();
	}

	@Override
	public String getDataName() {
		return "Data(EmptyLoader:" + data.size() + ")";
	}
}
