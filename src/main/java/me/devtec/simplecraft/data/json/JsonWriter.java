package me.devtec.simplecraft.data.json;

public interface JsonWriter {
	String serilize(java.io.Writer writer, Object item);

	String serilize(Object item);

	String serilize(Object item, boolean fancy);
}
