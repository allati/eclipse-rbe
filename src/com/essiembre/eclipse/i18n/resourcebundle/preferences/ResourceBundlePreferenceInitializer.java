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

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.essiembre.eclipse.i18n.resourcebundle.ResourceBundlePlugin;

/**
 * Initializes default preferences.
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public class ResourceBundlePreferenceInitializer extends
        AbstractPreferenceInitializer {

    /**
     * Constructor.
     */
    public ResourceBundlePreferenceInitializer() {
        super();
    }

    /**
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer
     *      #initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
        Preferences prefs = 
                ResourceBundlePlugin.getDefault().getPluginPreferences();        
        prefs.setDefault(RBPreferences.KEY_GROUP_SEPARATOR, ".");
        prefs.setDefault(RBPreferences.ALIGN_EQUAL_SIGNS, true);
        prefs.setDefault(RBPreferences.SHOW_GENERATOR, true);
        prefs.setDefault(RBPreferences.KEY_TREE_HIERARCHICAL, true);
        
        prefs.setDefault(RBPreferences.GROUP_KEYS, true);
        prefs.setDefault(RBPreferences.GROUP_LEVEL_DEEP, 1);
        prefs.setDefault(RBPreferences.GROUP_LINE_BREAKS, 1);
        prefs.setDefault(RBPreferences.GROUP_ALIGN_EQUAL_SIGNS, true);

        prefs.setDefault(RBPreferences.WRAP_CHAR_LIMIT, 80);
        prefs.setDefault(RBPreferences.WRAP_INDENT_SPACES, 8);
    }

}
