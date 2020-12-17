package me.devtec.fang.utils;

import me.devtec.fang.data.collections.UnsortedList;
import me.devtec.fang.data.json.Reader;
import me.devtec.fang.data.json.Writer;
import me.devtec.fang.data.maps.UnsortedMap;
import net.minestom.server.chat.ColoredText;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Random random = new Random();

    public interface ColormaticFactory {
        String colorize(String text);

        String getNextColor();
    }

    @SuppressWarnings("unchecked")
    public static String colorizeJson(String json) {
        return Writer.write(colorizeMap((Map<Object, Object>) Reader.read(json)));
    }

    @SuppressWarnings("unchecked")
    public static Map<Object, Object> colorizeMap(Map<Object, Object> json) {
        Map<Object, Object> colorized = new UnsortedMap<>(json.size());
        for(Entry<Object, Object> e : json.entrySet()) {
            if(e.getKey() instanceof Collection) {
                if(e.getValue() instanceof Collection) {
                    colorized.put(colorizeList((Collection<Object>) e.getKey()), colorizeList((Collection<Object>) e.getKey()));
                }
                if(e.getValue() instanceof Map) {
                    colorized.put(colorizeList((Collection<Object>) e.getKey()), colorizeMap((Map<Object, Object>) e.getKey()));
                }
                if(e.getValue() instanceof Object[]) {
                    colorized.put(colorizeList((Collection<Object>) e.getKey()), colorizeArray((Object[]) e.getKey()));
                }
                if(e.getValue() instanceof String) {
                    colorized.put(colorizeList((Collection<Object>) e.getKey()), colorize((String) e.getKey()));
                }else {
                    colorized.put(colorizeList((Collection<Object>) e.getKey()), e.getValue());
                }
            }
            if(e.getKey() instanceof Map) {
                if(e.getValue() instanceof Collection) {
                    colorized.put(colorizeMap((Map<Object, Object>) e.getKey()), colorizeList((Collection<Object>) e.getKey()));
                }
                if(e.getValue() instanceof Map) {
                    colorized.put(colorizeMap((Map<Object, Object>) e.getKey()), colorizeMap((Map<Object, Object>) e.getKey()));
                }
                if(e.getValue() instanceof Object[]) {
                    colorized.put(colorizeMap((Map<Object, Object>) e.getKey()), colorizeArray((Object[]) e.getKey()));
                }
                if(e.getValue() instanceof String) {
                    colorized.put(colorizeMap((Map<Object, Object>) e.getKey()), colorize((String) e.getKey()));
                }else {
                    colorized.put(colorizeMap((Map<Object, Object>) e.getKey()), e.getValue());
                }
            }
            if(e.getKey() instanceof Object[]) {
                if(e.getValue() instanceof Collection) {
                    colorized.put(colorizeArray((Object[]) e.getKey()), colorizeList((Collection<Object>) e.getKey()));
                }
                if(e.getValue() instanceof Map) {
                    colorized.put(colorizeArray((Object[]) e.getKey()), colorizeMap((Map<Object, Object>) e.getKey()));
                }
                if(e.getValue() instanceof Object[]) {
                    colorized.put(colorizeArray((Object[]) e.getKey()), colorizeArray((Object[]) e.getKey()));
                }
                if(e.getValue() instanceof String) {
                    colorized.put(colorizeArray((Object[]) e.getKey()), colorize((String) e.getKey()));
                }else {
                    colorized.put(colorizeArray((Object[]) e.getKey()), e.getValue());
                }
            }
            if(e.getKey() instanceof String) {
                if(e.getValue() instanceof Collection) {
                    colorized.put(colorize((String) e.getKey()), colorizeList((Collection<Object>) e.getKey()));
                }
                if(e.getValue() instanceof Map) {
                    colorized.put(colorize((String) e.getKey()), colorizeMap((Map<Object, Object>) e.getKey()));
                }
                if(e.getValue() instanceof Object[]) {
                    colorized.put(colorize((String) e.getKey()), colorizeArray((Object[]) e.getKey()));
                }
                if(e.getValue() instanceof String) {
                    colorized.put(colorize((String) e.getKey()), colorize((String) e.getKey()));
                }else {
                    colorized.put(colorize((String) e.getKey()), e.getValue());
                }
            }else {
                if(e.getValue() instanceof Collection) {
                    colorized.put(e.getKey(), colorizeList((Collection<Object>) e.getKey()));
                }
                if(e.getValue() instanceof Map) {
                    colorized.put(e.getKey(), colorizeMap((Map<Object, Object>) e.getKey()));
                }
                if(e.getValue() instanceof Object[]) {
                    colorized.put(e.getKey(), colorizeArray((Object[]) e.getKey()));
                }
                if(e.getValue() instanceof String) {
                    colorized.put(e.getKey(), colorize((String) e.getKey()));
                }else {
                    colorized.put(e.getKey(), e.getValue());
                }
            }
        }
        return colorized;
    }

    @SuppressWarnings("unchecked")
    public static Collection<Object> colorizeList(Collection<Object> json) {
        List<Object> colorized = new UnsortedList<>(json.size());
        for(Object e : json) {
            if(e instanceof Collection) {
                colorized.add(colorizeList((Collection<Object>) e));
            }
            if(e instanceof Map) {
                colorized.add(colorizeMap((Map<Object, Object>) e));
            }
            if(e instanceof Object[]) {
                colorized.add(colorizeArray((Object[]) e));
            }
            if(e instanceof String)
                colorized.add(colorize((String) e));
            else
                colorized.add(e);
        }
        return colorized;
    }

    @SuppressWarnings("unchecked")
    public static Object[] colorizeArray(Object[] json) {
        List<Object> colorized = new UnsortedList<>(json.length);
        for(Object e : json) {
            if(e instanceof Collection) {
                colorized.add(colorizeList((Collection<Object>) e));
            }
            if(e instanceof Map) {
                colorized.add(colorizeMap((Map<Object, Object>) e));
            }
            if(e instanceof Object[]) {
                colorized.add(colorizeArray((Object[]) e));
            }
            if(e instanceof String)
                colorized.add(colorize((String) e));
            else
                colorized.add(e);
        }
        return colorized.toArray();
    }

    /**
     * @return List<String>
     */
    public static List<String> copyPartialMatches(String prefix, Iterable<String> originals) {
        List<String> collection = new UnsortedList<>();
        for (String string : originals)
            if (string.length() >= prefix.length() && string.regionMatches(true, 0, prefix, 0, prefix.length()))
                collection.add(string);
        return collection;
    }

    /**
     * @return List<String>
     */
    public static List<String> copySortedPartialMatches(String prefix, Iterable<String> originals) {
        List<String> collection = new UnsortedList<>();
        for (String string : originals)
            if (string.length() >= prefix.length() && string.regionMatches(true, 0, prefix, 0, prefix.length()))
                collection.add(string);
        Collections.sort(collection);
        return collection;
    }

    /**
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
     * @return String
     */
    public static String colorize(String msg) {
        if (msg == null)
            return null;
        return ColoredText.ofLegacy(msg, '&').toString();
    }

    /**
     * 
     * @param args
     * @return String
     *
     */
    public static String buildString(String[] args) {
        return buildString(0, args);
    }

    /**
     * @param args
     * @return String
     *
     */
    public static String buildString(int start, String[] args) {
        return buildString(start, args.length, args);
    }

    /**
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
     * @return long
     */
    public static long getTimeFromString(String period) {
        return timeFromString(period);
    }

    /**
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
            if (typ.equalsIgnoreCase("m") || typ.toLowerCase().startsWith("mi")) {
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
            if (typ.equalsIgnoreCase("mon")) {
                time += num * 2629743.83;
            }
            if (typ.toLowerCase().startsWith("y")) {
                time += num * 31556926;
            }
        }
        return (long) time;
    }

    /**
     * @see Set long to string
     * @return String
     */
    public static String setTimeToString(long period) {
        return timeToString(period);
    }

    /**
     * @see Set long to string
     * @return String
     */
    public static String timeToString(long time) { // New shorter name of method
        long minutes = (time / 60) % 60;
        long hours = (time / 3600) % 24;
        long days = (time / 86400) % 7;
        long weeks = (time / 604800) % 4;
        long mounth = (long) (((float)time / 2629743.83F) % 12);
        long year = (time / 31556926);
        String date = "";
        if (year > 0)
            date = year + " year" + (year > 1 ? "s" : "");
        if (mounth > 0)
            date = (!date.equals("") ? date + " " : "") + mounth + " month" + (mounth > 1 ? "s" : "");
        if (weeks > 0)
            date = (!date.equals("") ? date + " " : "") + weeks + " week" + (weeks > 1 ? "s" : "");
        if (days > 0)
            date = (!date.equals("") ? date + " " : "") + days + " day" + (days > 1 ? "s" : "");
        if (hours > 0)
            date = (!date.equals("") ? date + " " : "") + hours + " hour" + (hours > 1 ? "s" : "");
        if (minutes > 0)
            date = (!date.equals("") ? date + " " : "") + minutes + " minute" + (minutes > 1 ? "s" : "");
        if (date.equals(""))
            date = "&e" + (time % 60) + " second" + ((time % 60) == 0 ? "" : "s");
        return date;
    }

    /**
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
     * @return long
     */
    public static long getLong(String fromString) {
        return (long) getFloat(fromString);
    }

    /**
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
     * @return int
     */
    public static int getInt(String fromString) {
        return (int) (getDouble(fromString) == 0 ? getLong(fromString) : getDouble(fromString));
    }

    /**
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
     * @return boolean
     */
    public static boolean isNumber(String fromString) {
        return isInt(fromString) || isDouble(fromString) || isLong(fromString) || isByte(fromString)
                || isShort(fromString) || isFloat(fromString);
    }

    /**
     * @return boolean
     */
    public static boolean isBoolean(String fromString) {
        if (fromString == null)
            return false;
        return fromString.equalsIgnoreCase("true") || fromString.equalsIgnoreCase("false")
                || fromString.equalsIgnoreCase("yes") || fromString.equalsIgnoreCase("no");
    }

    private static final Pattern special = Pattern.compile("[^A-Z-a-z0-9_]+");

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