package me.devtec.simplecraft.data;

import me.devtec.simplecraft.data.collections.UnsortedList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class Ref {
	public static void set(Object main, Field field, Object o) {
		try {
			field.setAccessible(true);
			field.set(main, o);
		} catch (Exception e) {
		}
	}

	public static void set(Object main, String field, Object o) {
		try {
			Field f = field(main.getClass(), field);
			f.setAccessible(true);
			f.set(main, o);
		} catch (Exception e) {
		}
	}

	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean existsMethod(Class<?> c, String name) {
		boolean a = false;
		for (Method d : getMethods(c))
			if (d.getName().equals(name)) {
				a = true;
				break;
			}
		return a;
	}

	public static Object cast(Class<?> c, Object item) {
		try {
			return c.cast(item);
		} catch (Exception e) {
			return null;
		}
	}

	public static Constructor<?> constructor(Class<?> main, Class<?>... bricks) {
		try {
			return main.getDeclaredConstructor(bricks);
		} catch (Exception es) {
			return null;
		}
	}

	public static Class<?>[] getClasses(Class<?> main) {
		try {
			return main.getClasses();
		} catch (Exception es) {
			return new Class<?>[0];
		}
	}

	public static Class<?>[] getDeclaredClasses(Class<?> main) {
		try {
			return main.getDeclaredClasses();
		} catch (Exception es) {
			return new Class<?>[0];
		}
	}

	public static Field[] getFields(Class<?> main) {
		try {
			return main.getFields();
		} catch (Exception es) {
			return new Field[0];
		}
	}

	public static List<Field> getAllFields(Class<?> main) {
		List<Field> f = new UnsortedList<>();
		Class<?> superclass = main;
		while (superclass != null) {
			for (Field fw : getDeclaredFields(superclass))
				if (!f.contains(fw))
					f.add(fw);
			superclass = superclass.getSuperclass();
		}
		return f;
	}

	public static Field[] getDeclaredFields(Class<?> main) {
		try {
			return main.getDeclaredFields();
		} catch (Exception es) {
			return new Field[0];
		}
	}

	public static Method[] getMethods(Class<?> main) {
		try {
			return main.getMethods();
		} catch (Exception es) {
			return new Method[0];
		}
	}

	public static Method[] getDeclaredMethods(Class<?> main) {
		try {
			return main.getDeclaredMethods();
		} catch (Exception es) {
			return null;
		}
	}

	public static Constructor<?>[] getConstructors(Class<?> main) {
		try {
			return main.getConstructors();
		} catch (Exception es) {
			return null;
		}
	}

	public static Constructor<?>[] getDeclaredConstructors(Class<?> main) {
		try {
			return main.getDeclaredConstructors();
		} catch (Exception es) {
			return null;
		}
	}

	public static Method method(Class<?> main, String name, Class<?>... bricks) {
		try {
			Method a = main.getDeclaredMethod(name, bricks);
			Class<?> d = main;
			while (d != null && a == null) {
				for (Method m : getDeclaredMethods(d)) {
					if (m.getName().equals(name) && areSame(m.getParameterTypes(), bricks)) {
						a = m;
						break;
					}
				}
				d = d.getSuperclass();
			}
			if (a != null)
				a.setAccessible(true);
			return a;
		} catch (Exception e) {
			return null;
		}
	}

	public static Field field(Class<?> main, String name) {
		try {
			Field f = main.getDeclaredField(name);
			f.setAccessible(true);
			return f;
		} catch (Exception e) {
			try {
				Field f = null;
				Class<?> c = main.getSuperclass();
				while (c != null) {
					try {
						f = c.getDeclaredField(name);
					} catch (Exception err) {
					}
					if (f != null)
						break;
					try {
						c = c.getSuperclass();
					} catch (Exception err) {
						break;
					}
				}
				if (f != null)
					f.setAccessible(true);
				return f;
			} catch (Exception er) {
			}
			return null;
		}
	}

	public static Object get(Object main, Field field) {
		try {
			field.setAccessible(true);
			return field.get(main);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object getNulled(Field field) {
		try {
			field.setAccessible(true);
			return field.get(null);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object getNulled(Class<?> clas, String field) {
		try {
			return field(clas, field).get(null);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object get(Object main, String field) {
		return get(main, field(main.getClass(), field));
	}

	public static Object handle(Object item) {
		try {
			return invoke(item, method(item.getClass(), "getHandle"));
		} catch (Exception e) {
			return null;
		}
	}

	public static Object invoke(Object main, Method method, Object... bricks) {
		try {
			method.setAccessible(true);
			return method.invoke(main, bricks);
		} catch (Exception | NoSuchMethodError es) {
			return null;
		}
	}

	public static Object invoke(Object main, String method, Object... bricks) {
		try {
			return findMethod(main.getClass(), method, bricks).invoke(main, bricks);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object invokeNulled(Class<?> classInMethod, String method, Object... bricks) {
		try {
			return findMethod(classInMethod, method, bricks).invoke(null, bricks);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object invokeNulled(Method method, Object... bricks) {
		try {
			return method.invoke(null, bricks);
		} catch (Exception es) {
			return null;
		}
	}

	public static Method findMethod(Object c, String name, Object... bricks) {
		return findMethod(c.getClass(), name, bricks);
	}

	public static Method findMethodByName(Class<?> c, String name) {
		Method a = null;
		Class<?> d = c;
		while (d != null) {
			for (Method m : getDeclaredMethods(d)) {
				if (m.getName().equals(name)) {
					a = m;
					break;
				}
			}
			if (a != null)
				break;
			try {
				d = d.getSuperclass();
			} catch (Exception err) {
				break;
			}
		}
		if (a != null)
			a.setAccessible(true);
		return a;
	}

	public static Method findMethod(Class<?> c, String name, Object... bricks) {
		Method a = null;
		Class<?> d = c;
		Class<?>[] param = new Class<?>[bricks.length];
		int i = 0;
		for (Object o : bricks) {
			if (o != null)
				param[i++] = o instanceof Class ? (Class<?>) o : o.getClass();
		}
		while (d != null) {
			for (Method m : getDeclaredMethods(d)) {
				if (m.getName().equals(name) && areSame(m.getParameterTypes(), param)) {
					a = m;
					break;
				}
			}
			if (a != null)
				break;
			try {
				d = d.getSuperclass();
			} catch (Exception err) {
				break;
			}
		}
		if (a != null)
			a.setAccessible(true);
		return a;
	}

	public static Constructor<?> findConstructor(Class<?> c, Object... bricks) {
		Constructor<?> a = null;
		Class<?>[] param = new Class<?>[bricks.length];
		int i = 0;
		for (Object o : bricks) {
			if (o != null)
				param[i++] = o instanceof Class ? (Class<?>) o : o.getClass();
		}
		for (Constructor<?> m : getDeclaredConstructors(c)) {
			if (areSame(m.getParameterTypes(), param)) {
				a = m;
				break;
			}
		}
		if (a != null)
			a.setAccessible(true);
		return a;
	}

	private static boolean areSame(Class<?>[] a, Class<?>[] b) {
		return Arrays.asList(a).containsAll(Arrays.asList(b));
	}

	public static Object newInstance(Constructor<?> constructor, Object... bricks) {
		try {
			constructor.setAccessible(true);
			return constructor.newInstance(bricks);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object newInstanceByClass(String className, Object... bricks) {
		return newInstance(findConstructor(getClass(className), bricks), bricks);
	}

	public static Object newInstanceByClass(Class<?> clazz, Object... bricks) {
		return newInstance(findConstructor(clazz, bricks), bricks);
	}
}