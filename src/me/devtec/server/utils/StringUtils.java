package me.devtec.server.utils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.ArrayList;

public class StringUtils {
	private static Random random = new Random();

	/**
	 * @see see Copy matches of String from Iterable<String> Examples:
	 *      StringUtils.copyPartialMatches("hello", Arrays.asList("helloWorld",
	 *      "hiHouska")) -> helloWorld StringUtils.copyPartialMatches("hello",
	 *      Arrays.asList("helloWorld", "hiHouska", "this_is_list_of_words",
	 *      "helloDevs", "hell")) -> helloWorld, helloDevs
	 * @return List<String>
	 */
	public static List<String> copyPartialMatches(String prefix, Iterable<String> originals) {
		List<String> collection = new ArrayList<>();
		for (String string : originals)
			if (string.length() < prefix.length() ? false : string.regionMatches(true, 0, prefix, 0, prefix.length()))
				collection.add(string);
		return collection;
	}

	/**
	 * @see see Copy matches of String from Iterable<String> Examples:
	 *      StringUtils.copyPartialMatches("hello", Arrays.asList("helloWorld",
	 *      "hiHouska")) -> helloWorld StringUtils.copyPartialMatches("hello",
	 *      Arrays.asList("helloWorld", "hiHouska", "this_is_list_of_words",
	 *      "helloDevs", "hell")) -> helloWorld, helloDevs
	 * @return List<String>
	 */
	public static List<String> copySortedPartialMatches(String prefix, Iterable<String> originals) {
		List<String> collection = new ArrayList<>();
		for (String string : originals)
			if (string.length() < prefix.length() ? false : string.regionMatches(true, 0, prefix, 0, prefix.length()))
				collection.add(string);
		Collections.sort(collection);
		return collection;
	}

	/**
	 * @see see Transfer Collection to String
	 * @return String
	 */
	public static String join(Iterable<?> toJoin, String split) {
		if (toJoin == null || split == null)
			return null;
		String r = "";
		for (Object s : toJoin)
			if (s == null)
				continue;
			else
				r = r + split + s.toString();
		r = r.replaceFirst(split, "");
		return r;
	}

	/**
	 * @see see Transfer Object[] to String
	 * @return String
	 */
	public static String join(Object[] toJoin, String split) {
		if (toJoin == null || split == null)
			return null;
		String r = "";
		for (Object s : toJoin)
			if (s == null)
				continue;
			else
				r = r + split + s.toString();
		r = r.replaceFirst(split, "");
		return r;
	}

	/**
	 * @see see Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public static String buildString(String[] args) {
		return buildString(0, args);
	}

	/**
	 * @see see Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public static String buildString(int start, String[] args) {
		return buildString(start, args.length, args);
	}

	/**
	 * @see see Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public static String buildString(int start, int end, String[] args) {
		String msg = "";
		for (int i = start; i < args.length && i < end; ++i)
			msg += (msg.equals("") ? "" : " ") + args[i];
		return msg;
	}

	/**
	 * @see see Return random object from list
	 * @param list
	 * @return
	 * @return Object
	 */
	public static <T> T getRandomFromList(List<T> list) {
		if (list.isEmpty() || list == null)
			return null;
		int r = random.nextInt(list.size());
		if (r <= 0) {
			if (list.get(0) != null) {
				return list.get(0);
			}
			return null;
		} else
			return list.get(r);
	}

	private static final Pattern periodPattern = Pattern.compile(
			"([+-]*[0-9]+)(m[o]+[n]*[t]*[h]*[s]*|m[i]*[n]*[u]*[t]*[e]*[s]*|y[e]*[a]*[r]*[s]*|w[e]*[k]*[s]*|h[o]*[u]*[r]*[s]*|s[e]*[c]*[o]*[n]*[d]*[s]*|d[a]*[y]*[s]*)",
			Pattern.CASE_INSENSITIVE);

	/**
	 * @see see Get long from string
	 * @param s String
	 * @return long
	 */
	public static long getTimeFromString(String period) {
		return timeFromString(period);
	}

	/**
	 * @see see Get long from string
	 * @param s String
	 * @return long
	 */
	public static long timeFromString(String period) { // New shorter name of method
		if (period == null || period.trim().isEmpty())
			return 0;
		period = period.toLowerCase(Locale.ENGLISH);
		if (isLong(period))
			return getLong(period);
		Matcher matcher = periodPattern.matcher(period);
		float time = 0;
		while (matcher.find()) {
			long num = getLong(matcher.group(1));
			if (num == 0)
				continue;
			String typ = matcher.group(2);
			if (typ.toLowerCase().startsWith("s")) {
				time += num;
			}
			if (typ.toLowerCase().equals("m") || typ.toLowerCase().startsWith("mi")) {
				time += num * 60;
			}
			if (typ.toLowerCase().startsWith("h")) {
				time += num * 3600;
			}
			if (typ.toLowerCase().startsWith("d")) {
				time += num * 86400;
			}
			if (typ.toLowerCase().startsWith("w")) {
				time += num * 604800;
			}
			if (typ.toLowerCase().equals("mon")) {
				time += num * 2629743.83;
			}
			if (typ.toLowerCase().startsWith("y")) {
				time += num * 31556926;
			}
		}
		return (long) time;
	}

	/**
	 * @see see Set long to string
	 * @param period long
	 * @return String
	 */
	public static String setTimeToString(long period) {
		return timeToString(period);
	}

	/**
	 * @see see Set long to string
	 * @param time long
	 * @return String
	 */
	public static String timeToString(long time) {
		long minutes = (time / 60) % 60;
		long hours = (time / 3600) % 24;
		long days = (time / 86400) % 7;
		long weeks = (time / 604800) % 4;
		long month = (time / 604800)/4 % 12;
		long year = (time / ((time / 604800)/4/12));
		String date = "";
		if (year > 0)
			date = year + " year" + (year > 1 ? "s" : "");
		if (month > 0)
			date = (!date.equals("") ? date + " " : "") + month + " month" + (month > 1 ? "s" : "");
		if (weeks > 0)
			date = (!date.equals("") ? date + " " : "") + weeks + " week" + (weeks > 1 ? "s" : "");
		if (days > 0)
			date = (!date.equals("") ? date + " " : "") + days + " day" + (days > 1 ? "s" : "");
		if (hours > 0)
			date = (!date.equals("") ? date + " " : "") + hours + " hour" + (hours > 1 ? "s" : "");
		if (minutes > 0)
			date = (!date.equals("") ? date + " " : "") + minutes + " minute" + (minutes > 1 ? "s" : "");
		if (date.equals(""))
			date = (time % 60) + " second" + ((time % 60) == 0 ? "" : "s");
		return date;
	}

	/**
	 * @see see Get boolean from string
	 * @return boolean
	 */
	public static boolean getBoolean(String fromString) {
		try {
			return fromString.equalsIgnoreCase("true") || fromString.equalsIgnoreCase("yes")
					|| fromString.equalsIgnoreCase("on");
		} catch (Exception er) {
			return false;
		}
	}

	/**
	 * @see see Convert String to Math and Calculate exempt
	 * @return double
	 */
	public static BigDecimal calculate(String fromString) {
		if (fromString == null)
			return new BigDecimal(0);
		String a = fromString.replaceAll("[^0-9E+.,()*/-]+", "").replace(",", ".");
		try {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
			if (engine == null)
				engine = new ScriptEngineManager().getEngineByName("nashorn");
			if (engine == null)
				engine = new ScriptEngineManager().getEngineByName("graal.js");
			return new BigDecimal("" + engine.eval(a));
		} catch (ScriptException e) {
		}
		return new BigDecimal(0);
	}

	/**
	 * @see see Get double from string
	 * @return double
	 */
	public static double getDouble(String fromString) {
		if (fromString == null)
			return 0.0D;
		String a = fromString.replaceAll("[^+0-9E.,-]+", "").replace(",", ".");
		if (isDouble(a)) {
			return Double.parseDouble(a);
		} else {
			return 0.0;
		}
	}

	/**
	 * @see see Is string, double ?
	 * @return boolean
	 */
	public static boolean isDouble(String fromString) {
		try {
			Double.parseDouble(fromString);
			return true;
		} catch (Exception err) {
			return false;
		}
	}

	/**
	 * @see see Get long from string
	 * @return long
	 */
	public static long getLong(String fromString) {
		return (long) getFloat(fromString);
	}

	/**
	 * @see see Is string, long ?
	 * @return
	 */
	public static boolean isLong(String fromString) {
		try {
			Long.parseLong(fromString);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * @see see Get int from string
	 * @return int
	 */
	public static int getInt(String fromString) {
		return (int) (getDouble(fromString) == 0 ? getLong(fromString) : getDouble(fromString));
	}

	/**
	 * @see see Is string, int ?
	 * @return boolean
	 */
	public static boolean isInt(String fromString) {
		try {
			Integer.parseInt(fromString);
			return true;
		} catch (Exception err) {
			return false;
		}
	}

	/**
	 * @see see Is string, float ?
	 * @return boolean
	 */
	public static boolean isFloat(String fromString) {
		try {
			Float.parseFloat(fromString);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * @see see Get float from string
	 * @return float
	 */
	public static float getFloat(String fromString) {
		if (fromString == null)
			return 0F;
		String a = fromString.replaceAll("[^+0-9E.,-]+", "").replace(",", ".");
		if (isFloat(a)) {
			return Float.parseFloat(a);
		} else {
			return 0;
		}
	}

	/**
	 * @see see Is string, float ?
	 * @return boolean
	 */
	public static boolean isByte(String fromString) {
		try {
			Byte.parseByte(fromString);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * @see see Get float from string
	 * @return float
	 */
	public static byte getByte(String fromString) {
		if (fromString == null)
			return (byte) 0;
		String a = fromString.replaceAll("[^+0-9E-]+", "");
		if (isByte(a)) {
			return Byte.parseByte(a);
		} else {
			return (byte) 0;
		}
	}

	/**
	 * @see see Is string, float ?
	 * @return boolean
	 */
	public static boolean isShort(String fromString) {
		try {
			Short.parseShort(fromString);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * @see see Get float from string
	 * @return float
	 */
	public static short getShort(String fromString) {
		if (fromString == null)
			return (short) 0;
		String a = fromString.replaceAll("[^+0-9E-]+", "");
		if (isShort(a)) {
			return Short.parseShort(a);
		} else {
			return (short) 0;
		}
	}

	/**
	 * @see see Is string, number ?
	 * @return boolean
	 */
	public static boolean isNumber(String fromString) {
		return isInt(fromString) || isDouble(fromString) || isLong(fromString) || isByte(fromString)
				|| isShort(fromString) || isFloat(fromString);
	}

	/**
	 * @see see Is string, boolean ?
	 * @return boolean
	 */
	public static boolean isBoolean(String fromString) {
		if (fromString == null)
			return false;
		return fromString.equalsIgnoreCase("true") || fromString.equalsIgnoreCase("false")
				|| fromString.equalsIgnoreCase("yes") || fromString.equalsIgnoreCase("no");
	}

	private static Pattern special = Pattern.compile("[^A-Z-a-z0-9_]+");

	public static boolean containsSpecial(String value) {
		return special.matcher(value).find();
	}

	public static Number getNumber(String o) {
		if (isInt(o))
			return getInt(o);
		if (isDouble(o))
			return getDouble(o);
		if (isLong(o))
			return getLong(o);
		if (isByte(o))
			return getByte(o);
		if (isShort(o))
			return getShort(o);
		if (isFloat(o))
			return getFloat(o);
		return null;
	}
}
