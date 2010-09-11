package com.googlecode.iqapps;

import java.util.EventObject;

/**
 * Simple event dispatcher that maintains a list of listeners to notify when the
 * event is fired.
 */
public class EventDispatcher {
	// Create the listener list
	protected EventListenerList listenerList = new EventListenerList();

	// These methods allow classes to register for events
	public void addEventListener(EventInterface listener) {
		listenerList.add(EventInterface.class, listener);
	}

	// This methods allows classes to unregister for events
	public void removeEventListener(EventInterface listener) {
		listenerList.remove(EventInterface.class, listener);
	}

	// This method fires the specified event
	public void fireEvent(EventObject evt) {
		Object[] listeners = listenerList.getListenerList();
		// Each listener occupies two elements - the first is the listener class
		// and the second is the listener instance
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == EventInterface.class) {
				((EventInterface) listeners[i + 1]).eventOccurred(evt);
			}
		}
	}
}
