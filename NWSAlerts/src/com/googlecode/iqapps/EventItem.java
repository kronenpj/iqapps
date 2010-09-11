package com.googlecode.iqapps;

import java.util.EventObject;

/**
 * Generic instantiation of an EventObject.
 */
public class EventItem extends EventObject {
	/**
	 * Randomly generated UID.
	 */
	private static final long serialVersionUID = 1090847145539711590L;

	public EventItem(Object source) {
		super(source);
	}
}
