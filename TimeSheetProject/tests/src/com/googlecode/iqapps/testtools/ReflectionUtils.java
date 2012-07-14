package com.googlecode.iqapps.testtools;

import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
	public static String rRawName(String targetPackage, int id) {
		return getName(rRaw(targetPackage), id);
	}

	public static String getName(Class<?> clazz, int id) {
		try {
			for (Field resource : clazz.getFields()) {
				if (!isStatic(resource.getModifiers()) || resource.getType() != Integer.TYPE) continue;
				if (resource.getInt(null) != id) continue;

				return "R.raw." + resource.getName();
			}

			return "Unknown";
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static Class<?> rRaw(String pkg) {
		return forName(pkg + ".R$raw");
	}

	public static int rRaw(String pkg, String id) {
		return staticInt(rRaw(pkg), id);
	}
	
	public static Class<?> rId(String pkg) {
		return forName(pkg + ".R$id");		
	}
	
	public static int rId(String pkg, String id) {
		return staticInt(rId(pkg), id);
	}
	
	public static Class<?> forName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/** Find and call a getter method for the named property. */
	public static Object getter(Object target, String property) {
		Method getter = findGetter(target, property);
		
		if (getter == null) {
			throw new IllegalArgumentException("Unknown property " + target.getClass().getName() + "." + property + "  Spelling?");
		}
			
		return invoke(getter, target);
	}

	public static Method findGetter(Object target, String property) {
		String [] getterNames = new String [] {
				property,
				"get" + property.substring(0, 1).toUpperCase() + property.substring(1)
		};
		
		for (String getterName : getterNames) {
			try {
				return target.getClass().getMethod(getterName);
			} catch (SecurityException e) { throw new RuntimeException(e.getMessage(), e);
			} catch (NoSuchMethodException e) { /* try another. */ }			
		}
		
		return null;
	}
	
	public static Method getMethod(Class<?> target, String name, Class<?>... args) {
		try {
			return target.getMethod(name, args);
		} catch (SecurityException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("" + target.getName() + " doesn't have a " + name + ".");
		}
	}
	
	public static Object invoke(Method method, Object target, Object... args) {
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static Field getField(Class<?> target, String property) {
		try {
			return target.getField(property);
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException("Unknown property " + target.getName() + "." + property + ".  Spelling?");
		} catch (SecurityException e) { throw new RuntimeException(e.getMessage(), e); }
	}
	
	public static int staticInt(Class<?> target, String staticProperty) {
		try {
			return getField(target, staticProperty).getInt(null);
		} catch (IllegalArgumentException e) { throw new RuntimeException(e.getMessage(), e);
		} catch (IllegalAccessException e) { throw new RuntimeException(e.getMessage(), e); }
	}
		
	public static List<String> staticIntFields(Class<?> target) {
		List<String> fields = new ArrayList<String>();
		
		for (Field field : target.getFields()) {
			if (!Modifier.isStatic(field.getModifiers())) continue;
			if (field.getType() != Integer.TYPE) continue;
			fields.add(field.getName());
		}
		
		return fields;
	}
	
	public static Method getMethod(Class<?> target, String name, int parameterCount) {
		for (Method method : target.getMethods()) {
			if (!method.getName().equals(name)) continue;
			if (method.getParameterTypes().length != parameterCount) continue;
			return method;
		}
		
		return null;
	}
}
