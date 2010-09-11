package com.googlecode.iqapps;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Simple interface to identify events.
 */
public interface EventInterface extends EventListener {
	public void eventOccurred(EventObject evt);
}
