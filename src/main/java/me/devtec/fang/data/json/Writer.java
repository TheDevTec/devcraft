package me.devtec.fang.data.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import me.devtec.fang.data.Ref;
import me.devtec.fang.data.collections.UnsortedList;
import me.devtec.fang.data.collections.UnsortedSet;
import me.devtec.fang.data.maps.UnsortedMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

public class Writer implements JsonWriter {
	private static final JsonWriter writer = new Writer();

	public static String write(Object object) {
		return writer.serilize(object);
	}

	public static String write(Object object, boolean fancy) {
		return writer.serilize(object, fancy);
	}

	private Map<Object, Object> fix(Map<?, ?> o, boolean fancy, boolean addNulls) {
		Map<Object, Object> map = new UnsortedMap<>();
		for (Entry<?, ?> e : o.entrySet())
			map.put(object2(e.getKey(), fancy, addNulls), object2(e.getValue(), fancy, addNulls));
		return map;
	}

	private Collection<Object> fix(Collection<?> o, boolean fancy, boolean addNulls) {
		Collection<Object> map = new UnsortedList<>();
		for (Object e : o)
			map.add(object2(e, fancy, addNulls));
		return map;
	}

	private static final Gson pretty = new GsonBuilder().setPrettyPrinting().create();
	private static final Gson simple = new GsonBuilder().create();

	public String array(Object[] object, boolean addNulls) {
		return array(object, addNulls, false);
	}

	public String array(Object[] object, boolean addNulls, boolean fancy) {
		if (object == null)
			return null;
		return (fancy ? pretty : simple).toJson(fix(Arrays.asList(object), fancy, addNulls));
	}

	public String collection(Collection<?> object, boolean addNulls) {
		return collection(object, addNulls, false);
	}

	public String collection(Collection<?> object, boolean addNulls, boolean fancy) {
		if (object == null)
			return null;
		return (fancy ? pretty : simple).toJson(fix(Arrays.asList(object), fancy, addNulls));
	}

	public String map(Map<?, ?> object, boolean addNulls) {
		return map(object, addNulls, false);
	}

	public String map(Map<?, ?> object, boolean addNulls, boolean fancy) {
		if (object == null)
			return null;
		return (fancy ? pretty : simple).toJson(fix(Arrays.asList(object), fancy, addNulls));
	}

	/**
	 * 
	 * @param w Object to parse to String
	 * @return String Object converted to String
	 */
	public String object(Object w, boolean addNulls, boolean fancy) {
		if (w == null)
			return "null";
		if (w instanceof String) {
			return "" + w;
		}
		if (w instanceof Enum<?>) {
			Map<String, Object> enumMap = new UnsortedMap<>();
			enumMap.put("enum " + w.getClass().getName(), w.toString());
			return map(enumMap, addNulls, fancy);
		}
		if (w instanceof Comparable)
			return w.toString();
		if (w instanceof Object[])
			return array((Object[]) w, addNulls, fancy);
		if (w instanceof Collection) {
			if (w instanceof UnsortedList || w instanceof UnsortedSet || w instanceof UnsortedList
					|| w.getClass() == Ref.getClass("java.util.Arrays$UnsortedList") || w instanceof HashSet
					|| w instanceof LinkedList || w instanceof LinkedHashSet) {
				return collection((Collection<?>) w, addNulls, fancy);
			}
			Map<String, Object> enumMap = new UnsortedMap<>();
			enumMap.put("Collection " + w.getClass().getName(), w);
			return map(enumMap, addNulls, fancy);
		}
		if (w instanceof Map) {
			if (w instanceof UnsortedMap || w instanceof HashMap || w instanceof LinkedHashMap || w instanceof UnsortedMap
					|| w instanceof UnsortedMap) {
				return map((Map<?, ?>) w, addNulls, fancy);
			}
			Map<String, Object> enumMap = new UnsortedMap<>();
			enumMap.put("Map " + w.getClass().getName(), w);
			return map(enumMap, addNulls, fancy);
		}
		return (fancy ? pretty : simple).toJson(convert(w, fancy, addNulls));
	}

	public Object object2(Object w, boolean fancy, boolean addNulls) {
		if (w == null)
			return null;
		if (w instanceof String) {
			return w;
		}
		if (w instanceof Enum<?>) {
			Map<String, Object> enumMap = new UnsortedMap<>();
			enumMap.put("enum " + w.getClass().getName(), w.toString());
			return enumMap;
		}
		if (w instanceof Comparable)
			return w;
		if (w instanceof Object[])
			return fix(Arrays.asList((Object[]) w), fancy, addNulls);
		if (w instanceof Collection) {
			if (w instanceof UnsortedList || w instanceof UnsortedSet || w instanceof UnsortedList
					|| w.getClass() == Ref.getClass("java.util.Arrays$UnsortedList") || w instanceof HashSet
					|| w instanceof LinkedList || w instanceof LinkedHashSet) {
				return fix((Collection<?>) w, fancy, addNulls);
			}
			Map<String, Object> enumMap = new UnsortedMap<>();
			enumMap.put("Collection " + w.getClass().getName(), fix((Collection<?>) w, fancy, addNulls));
			return enumMap;
		}
		if (w instanceof Map) {
			if (w instanceof UnsortedMap || w instanceof HashMap || w instanceof LinkedHashMap || w instanceof TreeMap
					|| w instanceof LinkedTreeMap || w instanceof WeakHashMap)
				return fix((Map<?, ?>) w, fancy, addNulls);
			Map<String, Object> enumMap = new UnsortedMap<>();
			enumMap.put("Map " + w.getClass().getName(), map((Map<?, ?>) w, addNulls));
			return fix(enumMap, fancy, addNulls);
		}
		return convert(w, fancy, addNulls);
	}

	private Object convert(Object object, boolean fancy, boolean addNulls) {
		Map<String, Object> item = new UnsortedMap<>();
		Map<String, Object> map = new UnsortedMap<>();
		for (Field f : Ref.getAllFields(object.getClass())) {
			if (Modifier.toString(f.getModifiers()).toLowerCase().contains("static"))
				continue;
			Object w = Ref.get(object, f);
			if (w == null && !addNulls)
				continue;
			if (w == null) {
				map.put(f.getName(), null);
				continue;
			}
			map.put(f.getName(), object2(w, fancy, addNulls));
			continue;
		}
		item.put("class " + object.getClass().getName(), map);
		return item;
	}

	@Override
	public String serilize(java.io.Writer writer, Object item) {
		String write = object(item, false, true);
		try {
			writer.write(write);
		} catch (Exception e) {
		}
		return write;
	}

	@Override
	public String serilize(Object item) {
		return object(item, false, false);
	}

	@Override
	public String serilize(Object item, boolean fancy) {
		return object(item, false, fancy);
	}
}
