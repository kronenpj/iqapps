package com.googlecode.iqapps.testtools;

import static com.googlecode.iqapps.testtools.ReflectionUtils.findGetter;
import static com.googlecode.iqapps.testtools.ReflectionUtils.getMethod;
import static com.googlecode.iqapps.testtools.ReflectionUtils.invoke;
import static com.googlecode.iqapps.testtools.ReflectionUtils.rId;
import static java.lang.Integer.parseInt;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;

import android.app.Instrumentation;
import android.view.ViewGroup;

/**
 * Use dot notation to navigate getters and array/child indexing.
 * 
 * All access is done through method reflection; no public properties are
 * examined.
 * 
 * Getter getXxx may be specified as ".getXxx" or ".xxx". Note for that getters
 * that don't start with "get" just use their names: for String.length() use
 * "length".
 * 
 * Arrays, Maps and ViewGroups may be indexed. To index a ViewGroup, getChildAt
 * is called. Array indexes are specified with a dot: ".listView.2" on a
 * ListActivity would yield getListView().getChildAt(2). Maps are indexed with
 * get(key). If the object implementing Map also has a matching method, the
 * method will be called.
 * 
 * Use a # prefix to do a child view lookup by id. This is only legal when the
 * current receiving object is an Activity (though this could be extended; make
 * some noise or patch if you really want it.) The ids themselves should be the
 * id strings just as they appear in your layout xml.
 * 
 * So "#my_text_view.text" would findViewById(R.id.my_text_view).getText().
 * 
 * Clearly all this reflection bypasses type checking; you will get
 * ClassCastExceptions, etc, if you are not careful.
 * 
 * @author philhsmith
 */
public class ViewShorthand {
	private final Positron positron;

	public ViewShorthand(Positron positron) {
		this.positron = positron;
	}

	public ViewShorthand(Instrumentation instr) {
		positron = new Positron(instr);
	}

	@SuppressWarnings("unchecked")
	public <T> T evaluate(Class<T> type, Object target, String query) {
		return (T) evaluate(target, query);
	}

	public Object evaluate(Object target, String query) {
		if (query == null || query.length() == 0)
			return target;
		for (String term : splitQuery(query))
			target = evaluateTerm(target, term);
		return target;
	}

	String[] splitQuery(String query) {
		String[] parts = query.replaceAll("X", "XX")
				.replaceAll("\\.\\.", " X ").split("\\.");
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].replaceAll(" X ", ".").replaceAll("XX", "X");
		}
		return parts;
	}

	private Object evaluateTerm(Object target, String term) {
		try {
			return index(target, parseInt(term));
		} catch (NumberFormatException notAnInt) {
		}

		if (term.startsWith("#"))
			return getById(target, term.substring(1));

		Method getter = findGetter(target, term);

		if (getter != null) {
			return invoke(getter, target);
		} else if (target instanceof Map) {
			return ((Map<?, ?>) target).get(term);
		} else {
			throw new IllegalArgumentException("Unknown property "
					+ target.getClass().getName() + "." + term + "  Spelling?");
		}
	}

	private Object index(Object target, int position) {
		if (target.getClass().isArray())
			return Array.get(target, position);
		if (target instanceof ViewGroup)
			return ((ViewGroup) target).getChildAt(position);

		throw new IllegalArgumentException("Don't know how to index into a "
				+ target.getClass().getSimpleName() + ".");
	}

	private Object getById(Object target, String id) {
		/*
		 * If there's demand for more sophisticated id searching, this could
		 * recursively walk ViewGroups while calling getId, looking for matches.
		 * I'm not going to bother yet, since it doesn't seem worth it.
		 */

		Method findViewById = getMethod(target.getClass(), "findViewById",
				Integer.TYPE);
		return invoke(findViewById, target,
				rId(positron.getTargetContext().getPackageName(), id));
	}
}
