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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Text;

/**
 * Grouping of all related resource bundles.
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public class Bundles {

    /** All bundles in this group. */
    private List bundles = new ArrayList();
    
    /**
     * Constructor.
     */
    public Bundles() {
        super();
    }

    /**
     * Gets the bundle at specified index.
     * @parm index position assigned to bundle
     * @return Returns the bundle.
     */
    public Bundle getBundle(int index) {
        return (Bundle) bundles.get(index);
    }

    /**
     * Gets the bundle associated to given text box.
     * @parm textBox a <code>Text</code> widget
     * @return Returns the bundle.
     */
    public Bundle getBundle(Text textBox) {
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            Bundle bundle = (Bundle) iter.next();
            if (bundle.getTextBox().equals(textBox)) {
                return bundle;
            }
        }
        return null;
    }

    /**
     * Gets the bundle index.
     * @parm bundle The bundle to get its index.
     * @return Returns the index
     */
    public int getBundleIndex(Bundle bundle) {
        return bundles.indexOf(bundle);
    }
    
    /**
     * Add a bundle at given index.
     * @parm index position assigned to bundle
     * @param bundle The bundle to add.
     */
    public void addBundle(int index, Bundle bundle) {
        this.bundles.add(index, bundle);
    }

    /**
     * Adds a bundle after last existing bundle.
     * @param bundle The bundle to add.
     */
    public void addBundle(Bundle bundle) {
        this.bundles.add(bundle);
    }
    
    /**
     * Refreshes text boxes contained in all bundles with value found
     * under the given resource bundle key.
     * @param key resource bundle key to get value from.
     */
    public void refreshTextBoxes(String key) {
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            ((Bundle) iter.next()).refreshTextBox(key);
        }
    }

    /**
     * Refreshes all bundle data based on editors content.
     */
    public void refreshData() {
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            ((Bundle) iter.next()).refreshData();
        }
    }

    
    /**
     * Gets all resource bundle keys.
     * @return <code>List</code> of resource bundle keys.
     */
    public List getKeys() {
        Set keys = new TreeSet();
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            keys.addAll(((Bundle) iter.next()).getKeys());
        }
        return Collections.unmodifiableList(new ArrayList(keys));
    }

    /**
     * Add a resource bundle key.  If a bundle already has the supplied
     * key, it is left unchanged.
     * @param key resource bundle key
     */
    public void addKey(String key) {
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            Bundle bundle = (Bundle) iter.next();
            Map data = bundle.getData();
            if (!data.containsKey(key)) {
                data.put(key, null);
                bundle.refreshEditor();
            }
        }
    }

    /**
     * Removes a resource bundle key from all bundles.
     * @param key resource bundle key
     */
    public void removeKey(String key) {
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            Bundle bundle = (Bundle) iter.next();
            bundle.getData().remove(key);
            bundle.refreshEditor();
        }
    }

    /**
     * Modifies a resource bundle key in all bundles.
     * @param oldKey resource bundle key to modify
     * @param newKey replacement key for resource bundle key to modify
     */
    public void modifyKey(String oldKey, String newKey) {
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            Bundle bundle = (Bundle) iter.next();
            String value = (String) bundle.getData().get(oldKey);
            bundle.getData().remove(oldKey);
            bundle.getData().put(newKey, value);
            bundle.refreshEditor();
        }
    }

    
    /**
     * Gets the number of bundles in this bundle group.
     * @return The number of bundles in this bundle group.
     */
    public int count() {
        return bundles.size();
    }
}
