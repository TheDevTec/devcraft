package me.devtec.fang.data.json;

public interface JsonReader {
	Object deserilize(java.io.Reader reader);

	Object deserilize(String json);
}
