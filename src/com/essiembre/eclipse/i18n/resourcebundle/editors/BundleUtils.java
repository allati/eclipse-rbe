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
package com.essiembre.eclipse.i18n.resourcebundle.editors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.essiembre.eclipse.i18n.resourcebundle.ResourceBundlePlugin;
import com.essiembre.eclipse.i18n.resourcebundle.preferences.Preferences;

/**
 * Provides utility methods for formatting an editor content. 
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public final class BundleUtils {

    /**
     * Constructor.
     */
    private BundleUtils() {
        super();
    }

    /**
     * Formats a resource bundle data.  Converts Map to a property file,
     * according to plugin preference store.
     * @param data data to convert
     * @return property file content
     */
    public static String formatData(Map data) {
        // Get preferences
        IPreferenceStore prefs = 
                ResourceBundlePlugin.getDefault().getPreferenceStore();
        int groupLinesAfter = Preferences.getNumOfLinesBetweenGroups();
        
        // Format
        String group = null;
        int equalIndex = -1;
        StringBuffer text = new StringBuffer();
        for (Iterator iter = data.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            String value = (String) data.get(key);
            
            String newGroup = getKeyGroup(key);
            if (newGroup == null || !newGroup.equals(group)) {
                group = newGroup;
                equalIndex = getEqualIndex(group, data);
                for (int i = 0; i < groupLinesAfter; i++) {
                    text.append(System.getProperty("line.separator"));
                }
            }
            appendKey(text, key, equalIndex);
            if (value != null) {
                text.append(value);
            }
            text.append(System.getProperty("line.separator"));
        }
        return text.toString();
    }
    
    /**
     * Gets the position where the equal sign should be located for
     * the given group.
     * @param group resource bundle key group
     * @param data bundle content
     * @return position
     */
    private static int getEqualIndex(String group, Map data) {
        int equalIndex = -1;
        IPreferenceStore prefs = 
                ResourceBundlePlugin.getDefault().getPreferenceStore();
        boolean alignEquals = Preferences.isAlignEqualSigns();
        boolean groupAlignEquals = Preferences.isAlignGroupEqualSigns();

        // Exit now if we are not aligning equals
        if (!alignEquals && !groupAlignEquals || group == null) {
            return equalIndex;
        }
        
        // Get equal index
        for (Iterator iter = data.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if (groupAlignEquals && key.startsWith(group)) {
                int index = key.length();
                if (index > equalIndex) {
                    equalIndex = index;
                }
            }
        }        
        return equalIndex;
    }
    
    /**
     * Appends a key to resource bundle content.
     * @param text the resource bundle content so far
     * @param key the key to add
     * @param equalIndex the equal sign position
     */
    private static void appendKey(
            StringBuffer text, String key, int equalIndex) {
        text.append(key);
        if (equalIndex != -1) {
            for (int i = 0; i < equalIndex - key.length(); i++) {
                text.append(' ');
            }
        }
        text.append(" = ");
    }

    /**
     * Gets the group from a resource bundle key.
     * @param key the key to get a group from
     * @return key group
     */
    public static String getKeyGroup(String key) {
        IPreferenceStore prefs = 
                ResourceBundlePlugin.getDefault().getPreferenceStore();

        String sep = Preferences.getKeyGroupSeparator();
        int deepness = Preferences.getGroupLevelDeepness();
        int endIndex = 0;
        int levelFound = 0;
        
        for (int i = 0; i < deepness; i++) {
            int sepIndex = key.indexOf(sep, endIndex);
            if (sepIndex != -1) {
                endIndex = sepIndex + 1;
                levelFound++;
            }
        }
        if (levelFound != 0) {
            if (levelFound < deepness) {
                return key;
            }
            return key.substring(0, endIndex - 1);
        }
        return null;
    }

    /**
     * Loads an image.
     * @param path image path, relative to plugin
     * @return image
     */
    public static Image loadImage(String path) {
        URL url = null;
        try {
        url = new URL(ResourceBundlePlugin.getDefault().getBundle().getEntry(
                "/"), path);
        } catch (MalformedURLException e) {
        }
        return ImageDescriptor.createFromURL(url).createImage();
    }
}
