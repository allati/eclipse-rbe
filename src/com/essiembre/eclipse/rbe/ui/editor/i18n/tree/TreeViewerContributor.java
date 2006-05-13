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


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.jface.dialogs.InputDialog;

import org.eclipse.jface.window.Window;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;

import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.swt.SWT;

import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.updater.FlatKeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.GroupedKeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.IncompletionUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.KeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.RBEPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * Helper class which is used to provide menu functions to the used TreeViewer instances
 * (outline and the treeview within the editor).
 */
public class TreeViewerContributor {

	
	public static final int KT_FLAT         = 0;  // 0th bit unset
	public static final int KT_HIERARCHICAL = 1;  // 0th bit set
	public static final int KT_INCOMPLETE   = 2;  // 1th bit set
	
	
	public  static final int MENU_NEW       = 0 ;
	public  static final int MENU_RENAME    = 1 ;
	public  static final int MENU_DELETE    = 2 ;
	public  static final int MENU_COPY      = 3 ;
	public  static final int MENU_COMMENT   = 4 ;
	public  static final int MENU_UNCOMMENT = 5 ;
	public  static final int MENU_EXPAND    = 6 ;
	public  static final int MENU_COLLAPSE  = 7 ;
	private static final int MENU_COUNT     = 8 ;
	
	
	/** the tree which is controlled through this manager.    */
	private KeyTree            tree;
	
	/** the component which displays the tree.                */
	private TreeViewer         treeviewer;
    
	private MenuItem           separator;
    
	/** items for the context menu.                           */
	private MenuItem[]         menuitems;
    
	/** the updater which is used for structural information. */
	private KeyTreeUpdater     structuralupdater;
	
	/** holds the information about the current state.        */
	private int                mode;

	/** some cursors to indicate progress                     */
    private Cursor             waitcursor;
    private Cursor             defaultcursor;
    
	
    /**
     * Initializes this contributor using the supplied model structure
     * and the viewer which is used to access the model.
     * 
     * @param keytree   Out tree model.
     * @param viewer    The viewer used to display the supplied model.
     */
	public TreeViewerContributor(KeyTree keytree, TreeViewer viewer) {
		tree              = keytree;
		treeviewer        = viewer;
		menuitems         = new MenuItem[MENU_COUNT];
		mode              = KT_HIERARCHICAL;
        waitcursor        = UIUtils.createCursor(SWT.CURSOR_WAIT);
        defaultcursor     = UIUtils.createCursor(SWT.CURSOR_ARROW);
		if(RBEPreferences.getKeyTreeHierarchical()) {
			structuralupdater = new GroupedKeyTreeUpdater(RBEPreferences.getKeyGroupSeparator());
		} else {
			structuralupdater = new FlatKeyTreeUpdater();
		}        
	}


	/**
	 * Returns the menuitem which is associated with the supplied code.
	 * 
	 * @param code   One of the {@link #MENU_COLLAPSE ...} constants declared above.
	 * 
	 * @return   The menuitem instance or null in case of an invalid code.
	 */
	public MenuItem getMenuItem(int code) {
		if((code >= 0) && (code < menuitems.length)) {
			return(menuitems[code]);
		}
		return(null);
	}
	
	
	/**
	 * Releases all associated resources.
	 */
	public void dispose() {
		for(int i = 0; i < menuitems.length; i++) {
			menuitems[i].dispose();
		}
		separator.dispose();
		waitcursor.dispose();
		defaultcursor.dispose();		
	}
	
	
	/**
	 * Creates the menu contribution for the supplied parental component.
	 * 
	 * @param parent   The component which is receiving the menu.
	 */
	public void createControl(Composite parent) {
		
        // Add popup menu
        Menu menu = new Menu (parent);

		menuitems[MENU_NEW] = new MenuItem (menu, SWT.PUSH);
		menuitems[MENU_NEW].setText(RBEPlugin.getString("key.new")); //$NON-NLS-1$
		menuitems[MENU_NEW].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				newKey();
			}
		});
		
        menuitems[MENU_RENAME] = new MenuItem (menu, SWT.PUSH);
        menuitems[MENU_RENAME].setText(RBEPlugin.getString("key.rename")); //$NON-NLS-1$
        menuitems[MENU_RENAME].addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                renameKeyOrGroup();
            }
        });
        
        menuitems[MENU_DELETE] = new MenuItem (menu, SWT.PUSH);
        menuitems[MENU_DELETE].setText(RBEPlugin.getString("key.delete")); //$NON-NLS-1$
        menuitems[MENU_DELETE].addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                deleteKeyOrGroup();
            }
        });
        
        menuitems[MENU_COPY] = new MenuItem (menu, SWT.PUSH);
        menuitems[MENU_COPY].setText(RBEPlugin.getString("key.duplicate")); //$NON-NLS-1$
        menuitems[MENU_COPY].addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                copyKeyOrGroup();
            }
        });
        
        menuitems[MENU_COMMENT] = new MenuItem (menu, SWT.PUSH);
        menuitems[MENU_COMMENT].setText(RBEPlugin.getString("key.comment")); //$NON-NLS-1$
        menuitems[MENU_COMMENT].addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                commentKey();
            }
        });
        
        menuitems[MENU_UNCOMMENT] = new MenuItem (menu, SWT.PUSH);
        menuitems[MENU_UNCOMMENT].setText(
                RBEPlugin.getString("key.uncomment")); //$NON-NLS-1$
        menuitems[MENU_UNCOMMENT].addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                uncommentKey();
            }
        });	
        
        separator = new MenuItem(menu, SWT.SEPARATOR);
      
        menuitems[MENU_EXPAND] = new MenuItem (menu, SWT.PUSH);
        menuitems[MENU_EXPAND].setText(RBEPlugin.getString("key.expandAll")); //$NON-NLS-1$
        menuitems[MENU_EXPAND].addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		treeviewer.expandAll();
        	}
        });
        
        menuitems[MENU_COLLAPSE] = new MenuItem (menu, SWT.PUSH);
        menuitems[MENU_COLLAPSE].setText(RBEPlugin.getString("key.collapseAll")); //$NON-NLS-1$
        menuitems[MENU_COLLAPSE].addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		treeviewer.collapseAll();
        	}
        });

        treeviewer.getTree().setMenu(menu);
		
	}
	

    /**
     * Gets the selected key tree item.
     * @return key tree item
     */
    public KeyTreeItem getSelection() {
        IStructuredSelection selection = (IStructuredSelection) treeviewer.getSelection();
        return (KeyTreeItem) selection.getFirstElement();
    }
	

	/**
	 * Creates a new key in case it isn't existing yet.
	 */
	protected void newKey() {
	    KeyTreeItem selectedItem = getSelection();
	    String key = selectedItem.getId();
	    String msgHead = RBEPlugin.getString("dialog.new.head"); //$NON-NLS-1$
	    String msgBody = RBEPlugin.getString("dialog.new.body", key); //$NON-NLS-1$
	    InputDialog dialog = new InputDialog(getShell(), msgHead, msgBody, key, null);
	    dialog.open();
	    if (dialog.getReturnCode() == Window.OK ) {
	        String newKey = dialog.getValue();
	        BundleGroup bundleGroup = tree.getBundleGroup();
	        if (!bundleGroup.containsKey(newKey)) {
            	bundleGroup.addKey(newKey);
	        }
	    }
	}
    
    
	/**
	 * Renames a key or group of key.
	 */
	protected void renameKeyOrGroup() {
	    KeyTreeItem selectedItem = getSelection();
	    String key = selectedItem.getId();
	    String msgHead = null;
	    String msgBody = null;
	    if (selectedItem.getChildren().size() == 0) {
	        msgHead = RBEPlugin.getString(
	                "dialog.rename.head.single"); //$NON-NLS-1$
	        msgBody = RBEPlugin.getString(
	                "dialog.rename.body.single", key); //$NON-NLS-1$
	    } else {
	        msgHead = RBEPlugin.getString(
	                "dialog.rename.head.multiple"); //$NON-NLS-1$
	        msgBody = RBEPlugin.getString(
	                "dialog.rename.body.multiple", //$NON-NLS-1$
	                selectedItem.getName());
	    }
	    // Rename single item
	    InputDialog dialog = new InputDialog(getShell(), msgHead, msgBody, key, null);
	    dialog.open();
	    if (dialog.getReturnCode() == Window.OK ) {
	        String newKey = dialog.getValue();
	        BundleGroup bundleGroup = tree.getBundleGroup();
	        Collection items = new ArrayList();
	        items.add(selectedItem);
	        items.addAll(selectedItem.getNestedChildren());
	        for (Iterator iter = items.iterator(); iter.hasNext();) {
	            KeyTreeItem item = (KeyTreeItem) iter.next();
	            String oldItemKey = item.getId();
	            if (oldItemKey.startsWith(key)) {
	                String newItemKey = newKey + oldItemKey.substring(key.length());
	                bundleGroup.renameKey(oldItemKey, newItemKey);
	            }
	        }
	    }
	}


	/**
	 * Uncomments a key or group of key.
	 */
	protected void uncommentKey() {
	    KeyTreeItem selectedItem = getSelection();
	    BundleGroup bundleGroup = tree.getBundleGroup();
	    Collection items = new ArrayList();
	    items.add(selectedItem);
	    items.addAll(selectedItem.getNestedChildren());
	    for (Iterator iter = items.iterator(); iter.hasNext();) {
	        KeyTreeItem item = (KeyTreeItem) iter.next();
	        bundleGroup.uncommentKey(item.getId());
	    }
	}


	/**
	 * Deletes a key or group of key.
	 */
	protected void deleteKeyOrGroup() {
	    KeyTreeItem selectedItem = getSelection();
	    String key = selectedItem.getId();
	    String msgHead = null;
	    String msgBody = null;
	    if (selectedItem.getChildren().size() == 0) {
	        msgHead = RBEPlugin.getString(
	                "dialog.delete.head.single"); //$NON-NLS-1$
	        msgBody = RBEPlugin.getString(
	                "dialog.delete.body.single", key); //$NON-NLS-1$
	    } else {
	        msgHead = RBEPlugin.getString(
	                "dialog.delete.head.multiple"); //$NON-NLS-1$
	        msgBody = RBEPlugin.getString(
	                "dialog.delete.body.multiple", //$NON-NLS-1$ 
	                selectedItem.getName());
	    }
	    MessageBox msgBox = new MessageBox(getShell(), SWT.ICON_QUESTION|SWT.OK|SWT.CANCEL);
	    msgBox.setMessage(msgBody);
	    msgBox.setText(msgHead);
	    if (msgBox.open() == SWT.OK) {
	        BundleGroup bundleGroup = tree.getBundleGroup();
	        Collection items = new ArrayList();
	        items.add(selectedItem);
	        items.addAll(selectedItem.getNestedChildren());
	        for (Iterator iter = items.iterator(); iter.hasNext();) {
	            KeyTreeItem item = (KeyTreeItem) iter.next();
	            bundleGroup.removeKey(item.getId());
	        }
	    }
	}


	/**
	 * Comments a key or group of key.
	 */
	protected void commentKey() {
	    KeyTreeItem selectedItem = getSelection();
	    BundleGroup bundleGroup = tree.getBundleGroup();
	    Collection items = new ArrayList();
	    items.add(selectedItem);
	    items.addAll(selectedItem.getNestedChildren());
	    for (Iterator iter = items.iterator(); iter.hasNext();) {
	        KeyTreeItem item = (KeyTreeItem) iter.next();
	        bundleGroup.commentKey(item.getId());
	    }
	    
	}


	/**
	 * Copies a key or group of key.
	 */
	protected void copyKeyOrGroup() {
	    KeyTreeItem selectedItem = getSelection();
	    String key = selectedItem.getId();
	    String msgHead = null;
	    String msgBody = null;
	    if (selectedItem.getChildren().size() == 0) {
	        msgHead = RBEPlugin.getString(
	                "dialog.duplicate.head.single"); //$NON-NLS-1$
	        msgBody = RBEPlugin.getString(
	                "dialog.duplicate.body.single", key); //$NON-NLS-1$
	    } else {
	        msgHead = RBEPlugin.getString(
	                "dialog.duplicate.head.multiple"); //$NON-NLS-1$
	        msgBody = RBEPlugin.getString(
	                "dialog.duplicate.body.multiple", //$NON-NLS-1$ 
	                selectedItem.getName());
	    }
	    // Rename single item
	    InputDialog dialog = new InputDialog(getShell(), msgHead, msgBody, key, null);
	    dialog.open();
	    if (dialog.getReturnCode() == Window.OK ) {
	        String newKey = dialog.getValue();
	        BundleGroup bundleGroup = tree.getBundleGroup();
	        Collection items = new ArrayList();
	        items.add(selectedItem);
	        items.addAll(selectedItem.getNestedChildren());
	        for (Iterator iter = items.iterator(); iter.hasNext();) {
	            KeyTreeItem item = (KeyTreeItem) iter.next();
	            String origItemKey = item.getId();
	            if (origItemKey.startsWith(key)) {
	                String newItemKey = 
	                        newKey + origItemKey.substring(key.length());
	                bundleGroup.copyKey(origItemKey, newItemKey);
	            }
	        }
	    }
	}
	

	/**
	 * Returns the currently used Shell instance.
	 * 
	 * @return   The currently used Shell instance.
	 */
	private Shell getShell() {
		return(RBEPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
	}

	
	/**
	 * Modifies the current filter according to a selected activity.
	 * 
	 * @param action     One of the KT_??? constants declared above.
	 * @param activate   true <=> Enable this activity.
	 */
	public void update(int action, boolean activate) {
		treeviewer.getTree().setCursor(waitcursor);
		if(action == KT_INCOMPLETE) {
			if(activate) {
				// we're setting a filter which uses the structural updater
				tree.setUpdater(
					new IncompletionUpdater(tree.getBundleGroup(), structuralupdater)
				);
	            mode = mode | KT_INCOMPLETE;
			} else {
				// disabled, so we can reuse the structural updater
				tree.setUpdater(structuralupdater);
				mode = mode & (~KT_INCOMPLETE);
			}
            if(structuralupdater instanceof GroupedKeyTreeUpdater) {
				if(RBEPreferences.getKeyTreeExpanded()) {
	                treeviewer.expandAll();
	            }			
            }
		} else if(action == KT_FLAT) {
			structuralupdater = new FlatKeyTreeUpdater(); 
			if((mode & KT_INCOMPLETE) != 0) {
				// we need to activate the filter
				tree.setUpdater(
					new IncompletionUpdater(tree.getBundleGroup(), structuralupdater)
				);				
			} else {
				tree.setUpdater(structuralupdater);
			}
			mode = mode & (~KT_HIERARCHICAL);
		} else if(action == KT_HIERARCHICAL) {
			structuralupdater = new GroupedKeyTreeUpdater(RBEPreferences.getKeyGroupSeparator()); 
			if((mode & KT_INCOMPLETE) != 0) {
				// we need to activate the filter
				tree.setUpdater(
					new IncompletionUpdater(tree.getBundleGroup(), structuralupdater)
				);				
			} else {
				tree.setUpdater(structuralupdater);
			}
            if(RBEPreferences.getKeyTreeExpanded()) {
                treeviewer.expandAll();
            }			
            mode = mode | KT_HIERARCHICAL;
		}
        treeviewer.getTree().setCursor(defaultcursor);
	}
	

	/**
	 * Returns the currently used mode.
	 * 
	 * @return   The currently used mode.
	 */
	public int getMode() {
		return(mode);
	}
	
	
} /* ENDCLASS */
