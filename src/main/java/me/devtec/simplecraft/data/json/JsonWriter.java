package me.devtec.simplecraft.data.json;

public interface JsonWriter {
	public String serilize(java.io.Writer writer, Object item);

	public String serilize(Object item);

	public String serilize(Object item, boolean fancy);
}
