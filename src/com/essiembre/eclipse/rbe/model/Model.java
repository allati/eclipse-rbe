/*
 * Copyright (C) 2003, 2004  Pascal Essiembre, Essiembre Consultant Inc.
 * 
 * This file is part of Essiembre ResourceBundle Editor.
 * 
 * Essiembre ResourceBundle Editor is free software; you can redistribute it 
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * Essiembre ResourceBundle Editor is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with Essiembre ResourceBundle Editor; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA  02111-1307  USA
 */
package com.essiembre.eclipse.rbe.model;

import org.eclipse.core.internal.runtime.ListenerList;

/**
 * Base class for core model objects.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public abstract class Model {

    /** Listeners for this object. */
    private final ListenerList listeners = new ListenerList();
    
    /**
     * Fires an "add" event.
     * @param added object added
     */
	protected void fireAdd(Object added) {
        for (int i = 0; i < listeners.getListeners().length; i++) {
            IDeltaListener listener = 
                    (IDeltaListener) listeners.getListeners()[i];
            listener.add(new DeltaEvent(added));
        }
	}

    /**
     * Fires a "remove" event.
     * @param removed object removed
     */
	protected void fireRemove(Object removed) {
        for (int i = 0; i < listeners.getListeners().length; i++) {
            IDeltaListener listener = 
                    (IDeltaListener) listeners.getListeners()[i];
            listener.remove(new DeltaEvent(removed));
        }
	}
	
    /**
     * Fires a "modify" event.
     * @param modified object modified
     */
    protected void fireModify(Object modified) {
        for (int i = 0; i < listeners.getListeners().length; i++) {
            IDeltaListener listener = 
                    (IDeltaListener) listeners.getListeners()[i];
            listener.modify(new DeltaEvent(modified));
        }
    }

    /**
     * Adds a listener to this instance.
     * @param listener listener to add
     */
    public void addListener(IDeltaListener listener) {
        listeners.add(listener);
	}
    /**
     * Removes a listener from this instance.
     * @param listener listener to remove
     */
	public void removeListener(IDeltaListener listener) {
        listeners.remove(listener);
	}
}
