package me.devtec.server.utils.json;

public interface JsonReader {
	public Object deserilize(java.io.Reader reader);

	public Object deserilize(String json);
}
