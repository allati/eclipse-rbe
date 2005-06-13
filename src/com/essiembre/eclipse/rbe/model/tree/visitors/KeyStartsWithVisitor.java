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
package com.essiembre.eclipse.rbe.model.tree.visitors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.essiembre.eclipse.rbe.model.tree.IKeyTreeVisitor;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;

/**
 * Visitor for finding keys starting with the <code>passAlongArgument</code>,
 * which must be a <code>String</code>.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class KeyStartsWithVisitor implements IKeyTreeVisitor {

    /** Holder for matching keys. */
    List items = new ArrayList();
    
    /**
     * Constructor.
     */
    public KeyStartsWithVisitor() {
        super();
    }

    /**
     * @see com.essiembre.eclipse.rbe.model.tree.IKeyTreeVisitor#visitKeyTree(
     *         com.essiembre.eclipse.rbe.model.tree.KeyTree, java.lang.Object)
     */
    public void visitKeyTree(KeyTree keyTree, Object passAlongArgument) {
        // TODO implement me?
    }

    /**
     * @see com.essiembre.eclipse.rbe.model.tree.IKeyTreeVisitor
     *         #visitKeyTreeItem(
     *                 com.essiembre.eclipse.rbe.model.tree.KeyTreeItem,
     *                 java.lang.Object)
     */
    public void visitKeyTreeItem(KeyTreeItem item, Object passAlongArgument) {
        String keyStart = (String) passAlongArgument;
        if (item.getId().startsWith(keyStart)) {
            items.add(item);
        }
    }

    /**
     * Gets matching key tree items.
     * @return matching key tree items
     */
    public Collection getKeyTreeItems() {
        return items;
    }
    
    /**
     * Gets the first item matched.
     * @return first item matched, or <code>null</code> if none was found
     */
    public KeyTreeItem getKeyTreeItem() {
        if (items.size() > 0) {
            return (KeyTreeItem) items.get(0);
        }
        return null;
    }
}
