package me.devtec.simplecraft.data.json;

import com.google.common.collect.Multimap;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.devtec.simplecraft.data.Ref;
import me.devtec.simplecraft.data.collections.UnsortedList;
import me.devtec.simplecraft.data.maps.UnsortedMap;
import me.devtec.simplecraft.utils.StringUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader implements JsonReader {
	//Do you want your own JsonParser? Override this one! Ref.set(Reader.class, "reader", <NewReaderJson>)
	private static JsonReader reader = new Reader();

	public static Object read(String json) {
		return reader.deserilize(json);
	}

	private static sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref
			.getNulled(Ref.field(sun.misc.Unsafe.class, "theUnsafe"));
	private static Gson parser = new GsonBuilder().setLenient().create();

	public Object object(String json) {
		if (json == null)
			return null;
		Object parsed = json;
		try {
			parsed = map(json);
			if (parsed == null)
				parsed = collection(json);
			if (parsed == null)
				parsed = json;
		} catch (Exception e) {
		}
		if (parsed instanceof Comparable)
			return parsed;
		return parseR(parsed);
	}

	public Collection<?> collection(String json) {
		if (json == null)
			return null;
		try {
			return (Collection<?>) parser.fromJson(json, UnsortedList.class);
		} catch (Exception e1) {
		}
		return null;
	}

	public List<?> list(String json) {
		if (json == null)
			return null;
		try {
			return (List<?>) parser.fromJson(json, UnsortedList.class);
		} catch (Exception e1) {
		}
		return null;
	}

	public Object[] array(String json) {
		if (json == null)
			return null;
		return collection(json).toArray();
	}

	public Map<?, ?> map(String json) {
		if (json == null)
			return null;
		try {
			return (Map<?,?>) parser.fromJson(json, UnsortedMap.class);
		} catch (Exception e1) {
		}
		return null;
	}

	private Object parse(Class<?> clazz, Object object) {
		Object o = Ref.newInstance(Ref.constructor(clazz));
		try {
			o = unsafe.allocateInstance(clazz);
		} catch (Exception errr) {
		}
		if (o == null)
			return null;
		if (object instanceof Map<?, ?>)
			for (Entry<?, ?> s : ((Map<?, ?>) object).entrySet())
				setObject(o, s.getKey() + "", s.getValue());
		return o;
	}

	@SuppressWarnings("unchecked")
	private void setObject(Object o, String f, Object v) {
		if (v == null)
			Ref.set(o, f, v);
		Type c = Ref.field(o.getClass(), f).getGenericType();
		v = cast(v, Ref.getClass(c.getTypeName()));
		Matcher ma = Pattern.compile("Map<(.*?), (.*?)>").matcher(c.getTypeName());
		if (ma.find()) {
			@SuppressWarnings("rawtypes")
			UnsortedMap uknown = new UnsortedMap<>();
			for (Entry<?, ?> e : ((Map<?, ?>) v).entrySet())
				uknown.put(cast(e.getKey(), Ref.getClass(ma.group(1))), cast(e.getValue(), Ref.getClass(ma.group(2))));
			v = uknown;
		}
		ma = Pattern.compile("List<(.*?)>").matcher(c.getTypeName());
		if (ma.find()) {
			@SuppressWarnings("rawtypes")
			UnsortedList uknown = new UnsortedList<>();
			for (Object e : ((List<?>) v))
				uknown.add(cast(e, Ref.getClass(ma.group(1))));
			v = uknown;
		}
		ma = Pattern.compile("Set<(.*?)>").matcher(c.getTypeName());
		if (ma.find()) {
			@SuppressWarnings("rawtypes")
			HashSet uknown = new HashSet<>();
			for (Object e : ((Set<?>) v))
				uknown.add(cast(e, Ref.getClass(ma.group(1))));
			v = uknown;
		}
		Ref.set(o, f, v);
	}

	private Object cast(Object v, Class<?> c) {
		if (v instanceof Comparable) {
			if (c == boolean.class | c == Boolean.class)
				v = StringUtils.getBoolean(v + "");
			if (c == String.class)
				v = v.toString();
			if (c == double.class | c == Double.class)
				v = StringUtils.getDouble(v + "");
			if (c == float.class | c == Float.class)
				v = StringUtils.getFloat(v + "");
			if (v instanceof Double)
				v = (int) (double) v;
			if (c == int.class | c == Integer.class)
				v = StringUtils.getInt(v + "");
			if (c == long.class | c == Long.class)
				v = StringUtils.getLong(v + "");
			if (c == byte.class | c == Byte.class)
				v = StringUtils.getByte(v + "");
			if (c == short.class | c == Short.class)
				v = StringUtils.getShort(v + "");
			if (c == BigDecimal.class)
				v = new BigDecimal(v + "");
		}

		return v;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object parseR(Object o) {
		if (o instanceof Collection) {
			Collection<Object> aw = new UnsortedList<>();
			for (Object f : ((Collection<?>) o))
				aw.add(f instanceof String ? object((String) f) : f);
			o = aw;
			List<Object> cloneOfList = new UnsortedList<>((Collection<Object>) o);
			((Collection<Object>) o).clear();
			for (Object s : cloneOfList)
				((Collection<Object>) o).add(parseR(s));
			return o;
		}
		if (o instanceof Map) {
			Map<Object, Object> aw = new UnsortedMap<>();
			for (Entry<?, ?> f : ((Map<?, ?>) o).entrySet())
				aw.put(f.getKey() instanceof String ? object((String) f.getKey()) : f.getKey(), f.getValue());
			o = aw;
			Map<Object, Object> a = new UnsortedMap<>();
			boolean c = false;
			for (Entry<?, ?> s : ((Map<?, ?>) o).entrySet()) {
				if (s.getKey().toString().startsWith("enum ")) {
					a.put(s.getKey(), Ref.getNulled(Ref.getClass((s.getKey().toString()).replaceFirst("enum ", "")),
							s.getValue() + ""));
					c = true;
				} else if (s.getKey().toString().startsWith("class ")) {
					a.put(s.getKey(), parse(Ref.getClass((s.getKey().toString()).replaceFirst("class ", "")),
							parseR(s.getValue())));
					c = true;
				} else if (s.getKey().toString().startsWith("Map ")) {
					Map map = (Map) Ref.newInstance(
							Ref.constructor(Ref.getClass((s.getKey().toString()).replaceFirst("Map ", ""))));
					for (Entry<?, ?> er : ((Map<?, ?>) s.getValue()).entrySet())
						map.put(parseR(er.getKey()), parseR(er.getValue()));
					a.put(s.getKey(), parseR(map));
					c = true;
				} else if (s.getKey().toString().startsWith("Multimap ")) {
					Multimap map = (Multimap) Ref.newInstance(
							Ref.constructor(Ref.getClass((s.getKey().toString()).replaceFirst("Multimap ", ""))));
					for (Entry<?, ?> er : ((Multimap<?, ?>) s.getValue()).entries())
						map.put(parseR(er.getKey()), parseR(er.getValue()));
					a.put(s.getKey(), parseR(map));
					c = true;
				} else if (s.getKey().toString().startsWith("Collection ")) {
					Collection map = (Collection) Ref.newInstance(
							Ref.constructor(Ref.getClass((s.getKey().toString()).replaceFirst("Collection ", ""))));
					for (Object er : (Collection) s.getValue())
						map.add(parseR(er));
					a.put(s.getKey(), parseR(map));
					c = true;
				} else {
					Object aa = s.getKey();
					if (!aa.equals(parseR(s.getKey()))) {
						aa = parseR(s.getKey());
						c = true;
					}
					a.put(aa, parseR(s.getValue()));
				}
			}
			if (a.size() == 1 && c)
				for (Object oa : a.values())
					return parseR(oa);
			return a;
		}
		return o;
	}

	@Override
	public Object deserilize(java.io.Reader reader) {
		try {
			return object(CharStreams.toString(reader));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object deserilize(String json) {
		return object(json);
	}
}
