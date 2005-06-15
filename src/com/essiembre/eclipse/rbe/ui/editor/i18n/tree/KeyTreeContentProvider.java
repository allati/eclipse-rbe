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
package com.essiembre.eclipse.rbe.ui.editor.i18n.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.essiembre.eclipse.rbe.model.DeltaEvent;
import com.essiembre.eclipse.rbe.model.IDeltaListener;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;

/**
 * Content provider for key tree viewer.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class KeyTreeContentProvider implements ITreeContentProvider, IDeltaListener {

    /** Represents empty objects. */
    private static Object[] EMPTY_ARRAY = new Object[0];
    /** Viewer this provided act upon. */
    protected TreeViewer viewer;
    /** Key tree used to provide content. */
    private KeyTree keyTree;
    
    /**
     * @see IContentProvider#dispose()
     */
    public void dispose() {}

    /**
     * @see IContentProvider#inputChanged(Viewer, Object, Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (TreeViewer)viewer;
        if(oldInput != null) {
            ((KeyTree) oldInput).removeListener(this);
        }
        if(newInput != null) {
            ((KeyTree) newInput).addListener(this);
        }
    }

    /**
     * @see ITreeContentProvider#getChildren(Object)
     */
    public Object[] getChildren(Object parentElement) {
        String key = null;
        if(parentElement instanceof KeyTree) {
            return ((KeyTree) parentElement).getRootKeyItems().toArray();
        } else if (parentElement instanceof KeyTreeItem) {
            return ((KeyTreeItem) parentElement).getChildren().toArray();
        }
        return EMPTY_ARRAY;
    }
    
    /**
     * @see ITreeContentProvider#getParent(Object)
     */
    public Object getParent(Object element) {
        if(element instanceof KeyTreeItem) {
            return ((KeyTreeItem) element).getParent();
        }
        return null;
    }

    /**
     * @see ITreeContentProvider#hasChildren(Object)
     */
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /**
     * @see IStructuredContentProvider#getElements(Object)
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    /**
     * @see IDeltaListener#add(DeltaEvent)
     */
    public void add(DeltaEvent event) {
        viewer.refresh(true);
    }

    /**
     * @see IDeltaListener#remove(DeltaEvent)
     */
    public void remove(DeltaEvent event) {
        viewer.refresh(true);
    }

    /**
     * @see IDeltaListener#modify(DeltaEvent)
     */
    public void modify(DeltaEvent event) {
        KeyTreeItem treeItem = (KeyTreeItem) event.receiver();
        Object parentTreeItem = treeItem.getParent();
        viewer.refresh(parentTreeItem, true);
        viewer.setSelection(new StructuredSelection(treeItem), true);

    }
}
