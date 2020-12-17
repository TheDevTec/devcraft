package me.devtec.fang.data;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.devtec.fang.Loader;
import me.devtec.fang.data.collections.UnsortedList;
import me.devtec.fang.data.collections.UnsortedSet;
import me.devtec.fang.data.json.Maker;
import me.devtec.fang.data.json.Writer;
import me.devtec.fang.data.loader.DataLoader;
import me.devtec.fang.data.loader.EmptyLoader;
import me.devtec.fang.utils.StreamUtils;
import me.devtec.fang.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class Data implements me.devtec.fang.data.datas.Data {
	public static class DataHolder {
		private Object o;
		private final List<String> lines = new UnsortedList<>();

		public DataHolder() {
		}

		public DataHolder(Object object) {
			o = object;
		}

		public DataHolder(Object object, List<String> unusedLines) {
			this(object);
			lines.addAll(unusedLines);
		}

		public Object getValue() {
			return o;
		}

		public void setValue(Object o) {
			this.o = o;
		}

		public List<String> getComments() {
			return lines;
		}
	}

	private DataLoader loader = new EmptyLoader();
	private final Set<String> aw = new UnsortedSet<>();
	private File a;

	public Data() {
	}

	public Data(String filePath) {
		this(new File(filePath.startsWith("/") ? filePath.substring(1) : filePath), true);
	}

	public Data(String filePath, boolean load) {
		this(new File(filePath.startsWith("/") ? filePath.substring(1) : filePath), load);
	}

	public Data(File f) {
		this(f, true);
	}

	public Data(File f, boolean load) {
		a = f;
		if (load)
			reload(a);
	}

	public void addDefault(String key, Object value) {
		if (!exists(key))
			set(key, value);
	}

	public void addDefault(String key, Object value, Collection<String> comments) {
		if (!exists(key)) {
			set(key, value);
			setComments(key, comments);
		}
	}

	public boolean exists(String path) {
		int a = 0;
		for (String k : loader.get().keySet()) {
			if (k.startsWith(path)) {
				a = 1;
				break;
			}
		}
		return a == 1;
	}

	public Data setFile(File f) {
		a = f;
		return this;
	}

	private DataHolder getOrCreateData(String key) {
		DataHolder h = loader.get().getOrDefault(key, null);
		if (h == null) {
			h = new DataHolder();
			aw.add(key.split("\\.")[0]);
			loader.set(key, h);
		}
		return h;
	}

	public Data set(String key, Object value) {
		if (key == null)
			return this;
		if (value == null) {
			if (key.split("\\.").length <= 1)
				aw.remove(key.split("\\.")[0]);
			loader.remove(key);
			return this;
		}
		getOrCreateData(key).setValue(value);
		return this;
	}

	public Data remove(String key) {
		if (key == null)
			return this;
		if (key.split("\\.").length <= 1)
			aw.remove(key.split("\\.")[0]);
		loader.remove(key);
		for (String keys : getKeys(key))
			loader.remove(key + "." + keys);
		return this;
	}

	public List<String> getComments(String key) {
		if (key == null)
			return null;
		return getOrCreateData(key).lines;
	}

	public Data setComments(String key, Collection<String> value) {
		if (key == null)
			return this;
		getOrCreateData(key).lines.clear();
		if (value == null)
			return this;
		getOrCreateData(key).lines.addAll(value);
		return this;
	}

	public Data addComments(String key, Collection<String> value) {
		if (value == null || key == null)
			return this;
		getOrCreateData(key).lines.addAll(value);
		return this;
	}

	public Data addComment(String key, String value) {
		if (value == null || key == null)
			return this;
		DataHolder h = getOrCreateData(key);
		h.lines.add(value);
		return this;
	}

	public Data removeComments(String key, Collection<String> value) {
		if (value == null || key == null)
			return this;
		getOrCreateData(key).lines.removeAll(value);
		return this;
	}

	public Data removeComment(String key, String value) {
		if (value == null || key == null)
			return this;
		DataHolder h = getOrCreateData(key);
		if (h.lines != null)
			h.lines.remove(value);
		return this;
	}

	public Data removeComment(String key, int line) {
		if (line < 0 || key == null)
			return this;
		DataHolder h = getOrCreateData(key);
		if (h.lines != null)
			h.lines.remove(line);
		return this;
	}

	public File getFile() {
		return a;
	}

	public Data setHeader(Collection<String> lines) {
		loader.getHeader().clear();
		loader.getHeader().addAll(lines);
		return this;
	}

	public Data setFooter(Collection<String> lines) {
		loader.getFooter().clear();
		loader.getFooter().addAll(lines);
		return this;
	}

	public Collection<String> getHeader() {
		return loader.getHeader();
	}

	public Collection<String> getFooter() {
		return loader.getFooter();
	}

	public Data reload(String input) {
		aw.clear();
		loader = DataLoader.findLoaderFor(input); // get & load
		for (String k : loader.getKeys())
			aw.add(k.split("\\.")[0]);
		return this;
	}

	public Data reload(File f) {
		if (!f.exists()) {

			try {
				f.getParentFile().mkdirs();
			} catch (Exception e) {
			}
			try {
				f.createNewFile();
			} catch (Exception e) {
			}
		}
		return reload(StreamUtils.fromStream(f));
	}

	public Object get(String key) {
		try {
			return loader.get().get(key).getValue();
		} catch (Exception e) {
			return null;
		}
	}

	public <E> E getAs(String key, Class<? extends E> clazz) {
		try {
			return clazz.cast(loader.get().get(key).getValue());
		} catch (Exception e) {
		}
		return null;
	}

	public String getString(String key) {
		return get(key) != null ? String.valueOf(get(key)) : null;
	}

	public int getInt(String key) {
		try {
			return getAs(key, int.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Integer.class);
			} catch (Exception notNumber2) {
				return StringUtils.getInt(getString(key));
			}
		}
	}

	public double getDouble(String key) {
		try {
			return getAs(key, double.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Double.class);
			} catch (Exception notNumber2) {
				return StringUtils.getDouble(getString(key));
			}
		}
	}

	public long getLong(String key) {
		try {
			return getAs(key, long.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Long.class);
			} catch (Exception notNumber2) {
				return StringUtils.getLong(getString(key));
			}
		}
	}

	public float getFloat(String key) {
		try {
			return getAs(key, float.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Float.class);
			} catch (Exception notNumber2) {
				return StringUtils.getFloat(getString(key));
			}
		}
	}

	public byte getByte(String key) {
		try {
			return getAs(key, byte.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Byte.class);
			} catch (Exception notNumber2) {
				return StringUtils.getByte(getString(key));
			}
		}
	}

	public boolean getBoolean(String key) {
		try {
			return getAs(key, boolean.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Boolean.class);
			} catch (Exception notNumber2) {
				return StringUtils.getBoolean(getString(key));
			}
		}
	}

	public short getShort(String key) {
		try {
			return getAs(key, short.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Short.class);
			} catch (Exception notNumber2) {
				return StringUtils.getShort(getString(key));
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<Object> getList(String key) {
		return get(key) != null && get(key) instanceof Collection ? (Collection) get(key) : new UnsortedList<>(3);
	}

	public <E> List<E> getListAs(String key, Class<?> clazz) {
		// Cast everything to <E>
		Collection<Object> items = getList(key);
		List<E> list = new UnsortedList<>(items.size());
		for (Object o : items)
			try {
				if (o != null)
					list.add(o == null ? null : (E)clazz.cast(o));
				else
					list.add(null);
			} catch (Exception er) {
			}
		return list;
	}

	public List<String> getStringList(String key) {
		// Cast everything to String
		Collection<Object> items = getList(key);
		List<String> list = new UnsortedList<>(items.size());
		for (Object o : items)
			if (o != null)
				list.add("" + o);
			else
				list.add(null);
		return list;
	}

	public List<Boolean> getBooleanList(String key) {
		// Cast everything to Boolean
		Collection<Object> items = getList(key);
		List<Boolean> list = new UnsortedList<>(items.size());
		for (Object o : items)
			list.add(o != null && StringUtils.getBoolean(o.toString()));
		return list;
	}

	public List<Integer> getIntegerList(String key) {
		// Cast everything to Integer
		Collection<Object> items = getList(key);
		List<Integer> list = new UnsortedList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getInt(o.toString()));
		return list;
	}

	public List<Double> getDoubleList(String key) {
		// Cast everything to Double
		Collection<Object> items = getList(key);
		List<Double> list = new UnsortedList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0.0 : StringUtils.getDouble(o.toString()));
		return list;
	}

	public List<Short> getShortList(String key) {
		// Cast everything to Short
		Collection<Object> items = getList(key);
		List<Short> list = new UnsortedList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getShort(o.toString()));
		return list;
	}

	public List<Byte> getByteList(String key) {
		// Cast everything to Byte
		Collection<Object> items = getList(key);
		List<Byte> list = new UnsortedList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getByte(o.toString()));
		return list;
	}

	public List<Float> getFloatList(String key) {
		// Cast everything to Float
		Collection<Object> items = getList(key);
		List<Float> list = new UnsortedList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getFloat(o.toString()));
		return list;
	}

	public List<Long> getLongList(String key) {
		// Cast everything to Byte
		Collection<Object> items = getList(key);
		List<Long> list = new UnsortedList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getLong(o.toString()));
		return list;
	}

	public List<Map<?, ?>> getMapList(String key) {
		return getListAs(key, Map.class);
	}

	public Data save(DataType type) {
			try {
				if (a == null)
					return this;
				OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(a), StandardCharsets.UTF_8);
				if (type == DataType.BYTE) {
					try {
						ByteArrayDataOutput bos = ByteStreams.newDataOutput(loader.get().size());
						for (Entry<String, DataHolder> key : loader.get().entrySet())
							try {
								bos.writeUTF(key.getKey());
								bos.writeUTF(Writer.write(key.getValue().getValue()));
							} catch (Exception er) {
							}
						w.write(Base64.getEncoder().encodeToString(bos.toByteArray()));
						w.close();
					} catch (Exception e) {
						w.write("");
						w.close();
					}
					return this;
				}
				if (type == DataType.JSON) {
					Maker maker = new Maker();
					for (String key : aw)
						addKeys(maker, key);
					w.write(maker.toString(false).replace("\\n", System.lineSeparator()));
					w.close();
					return this;
				}
				try {
					for (String h : loader.getHeader())
						w.write(h + System.lineSeparator());
				} catch (Exception er) {
				}
				Loader.log(aw.toString());
				for (String key : aw)
					preparePath(key, key + ":", 0, w);
				try {
					for (String h : loader.getFooter())
						w.write(h + System.lineSeparator());
				} catch (Exception er) {
				}
				w.close();
			} catch (Exception er) {
			}
		return this;
	}

	/**
	 * Wraps string in double quotes, unless the string is already in double or
	 * single quotes.
	 *
	 * New-line characters are removed from the returned string.
	 *
	 * Example input -> output: : 5 -> "5" : '5' -> '5' : "5" -> "5" : 5" -> "5""
	 */
	private String addQuotes(boolean raw, String text) {
		if (text == null)
			return null;
		boolean quotedString = (text.trim().startsWith("'") && text.trim().endsWith("'"))
				|| (text.trim().startsWith("\"") && text.trim().endsWith("\""));
		if (raw && !quotedString) {
			return (StringUtils.isBoolean(text)?text:StringUtils.isNumber(text)?"'"+text+"'":"\"" + text + "\"").replace("\n", "\\n").replace(System.lineSeparator(), "\\n");
		}
		return text.replace("\n", "\\n").replace(System.lineSeparator(), "\\n");
	}
	
	public void save() {
		save(DataType.YAML);
	}

	public Set<String> getKeys() {
		return new UnsortedSet<>(aw);
	}

	public Set<String> getKeys(boolean subkeys) {
		if (subkeys)
			return loader.getKeys();
		return new UnsortedSet<>(aw);
	}

	public Set<String> getKeys(String key) {
		return getKeys(key, false);
	}

	public boolean isKey(String key) {
		boolean is = false;
		for (String k : loader.getKeys()) {
			if (k.startsWith(key)) {
				String r = k.replaceFirst(key, "");
				if (r.startsWith(".") || r.trim().isEmpty()) {
					is = true;
					break;
				}
			}
		}

		return is;
	}

	public Set<String> getKeys(String key, boolean subkeys) {
		Set<String> a = new UnsortedSet<>();
		for (String d : loader.getKeys())
			if (d.startsWith(key)) {
				String c = d.replaceFirst(Pattern.quote(key), "");
				if (!c.startsWith("."))
					continue;
				c = subkeys ? c : c.replaceFirst("\\.", "").split("\\.")[0];
				if (c.trim().isEmpty())
					continue;
				a.add(c);
			}
		return a;
	}

	public String toString() {
		return toString(DataType.BYTE);
	}

	private void addKeys(Maker main, String key) {
		Object o = get(key);
		if (o != null)
			main.add(main.create().put(key, o));
		for (String keyer : getKeys(key))
			addKeys(main, key + "." + keyer);
	}

	private void preparePath(String path, String pathName, int spaces, java.io.Writer b) {
			try {
				DataHolder aw = loader.get().get(path);
				Object o = aw != null ? aw.getValue() : null;
				String space = cs(spaces, 1);
				pathName = space + pathName;
				if (aw != null)
					for (String s : aw.getComments())
						b.write(space + s + System.lineSeparator());
				if (o == null) {
					b.write(pathName + System.lineSeparator());
				} else {
					if (o instanceof Collection || o instanceof Object[]) {
						b.write(pathName + System.lineSeparator());
						String splitted = space + "- ";
						if (o instanceof Collection) {
							for (Object a : (Collection<?>) o) {
								b.write(splitted+addQuotes(a instanceof String, Writer.write(a))
										+ System.lineSeparator());
							}
						} else {
							for (Object a : (Object[]) o)
								b.write(splitted+addQuotes(a instanceof String, Writer.write(a))
										+ System.lineSeparator());
						}
					} else
						b.write(pathName + " "
								+ addQuotes(o instanceof String, Writer.write(o))
								+ System.lineSeparator());
				}
				for (String key : getKeys(path, false))
					preparePath(path + "." + key, key + ":", spaces + 1, b);
			} catch (Exception er) {
			}
	}

	public String toString(DataType type) {
		synchronized (loader) {
			if (type == DataType.BYTE) {
				try {
					ByteArrayDataOutput bos = ByteStreams.newDataOutput(loader.get().size());
					for (Entry<String, DataHolder> key : loader.get().entrySet())
						try {
							bos.writeUTF(key.getKey());
							bos.writeUTF(Writer.write(key.getValue().getValue()));
						} catch (Exception er) {
						}
					return Base64.getEncoder().encodeToString(bos.toByteArray());
				} catch (Exception e) {
				}
				return "";
			}
			if (type == DataType.JSON) {
				Maker main = new Maker();
				for (String key : aw)
					addKeys(main, key);
				return main.toString();
			}

			StringWriter d = new StringWriter();
			try {
				for (String h : loader.getHeader())
					d.write(h + System.lineSeparator());
			} catch (Exception er) {
			}
			for (String key : aw)
				preparePath(key, key + ":", 0, d);
			try {
				for (String h : loader.getFooter())
					d.write(h + System.lineSeparator());
			} catch (Exception er) {
			}
			return d.toString();
		}
	}

	private static String cs(int s, int doubleSpace) {
		StringWriter i = new StringWriter();
		String space = doubleSpace == 1 ? "  " : " ";
		for (int c = 0; c < s; ++c)
			i.write(space);
		return i.toString();
	}

	@Override
	public String getDataName() {
		return "Data(" + (a != null ? "'" + a.getName() + "'" : "") + ")";
	}

	public Data clear() {
		loader.get().clear();
		return this;
	}

	public Data reset() {
		loader.reset();
		return this;
	}

	public Data merge(Data f, boolean addHeader, boolean addFooter) {
		for(Entry<String, DataHolder> s : f.loader.get().entrySet()) {
			if(get(s.getKey())==null && s.getValue().getValue()!=null)
				set(s.getKey(), s.getValue().getValue());
			if(getComments(s.getKey()).isEmpty() && !s.getValue().getComments().isEmpty())
				setComments(s.getKey(), s.getValue().getComments());
		}
		if(addHeader && !loader.getHeader().containsAll(f.loader.getHeader()))
			setHeader(f.loader.getHeader());
		if(addFooter && !loader.getFooter().containsAll(f.loader.getFooter()))
			setFooter(f.loader.getFooter());
		return this;
	}
}