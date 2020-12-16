package me.devtec.simplecraft.data.json;

public interface JsonReader {
	public Object deserilize(java.io.Reader reader);

	public Object deserilize(String json);
}
