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
package com.essiembre.eclipse.i18n.resourcebundle.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import com.essiembre.eclipse.i18n.resourcebundle.ResourceBundlePlugin;

/**
 * Application preferences, relevant to the resource bundle editor plugin.
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public final class Preferences {

    /** Preferences. */
    private static final IPreferenceStore PREFS = 
            ResourceBundlePlugin.getDefault().getPreferenceStore();
    
    /**
     * Constructor.
     */
    private Preferences() {
        super();
    }

    /**
     * Gets key group separator, based on preference;
     * @return
     */
    public static String getKeyGroupSeparator() {
        return ".";
        //TODO grab separator from preferences
        //return PREFS.getString(ResourceBundlePlugin.P_GROUP_SEPARATOR);
    }

    public static boolean isAlignEqualSigns() {
        return true;
    }

    public static int getGroupLevelDeepness() {
        return 1;
    }
    
    public static int getNumOfLinesBetweenGroups() {
        return 1;
    }

    public static boolean isAlignGroupEqualSigns() {
        return true;
    }

    public static boolean isKeyTreeFlat() {
        return false;
    }

}
