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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.essiembre.eclipse.i18n.resourcebundle.ResourceBundlePlugin;
import com.essiembre.eclipse.i18n.resourcebundle.preferences.RBPreferences;

/**
 * Tree for displaying and navigating through resource bundle keys.
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public class KeysComposite extends Composite {

    /** Key image. */
    private static Image keyImage = 
            BundleUtils.loadImage("icons/key.gif");
    /** Key group image. */
    private static Image keyGroupImage = 
            BundleUtils.loadImage("icons/keyGroup.gif");
    /** Key warning image. */
    private static Image keyWarnImage = 
            BundleUtils.loadImage("icons/keyWarn.gif");
    /** Key group warning image. */
    private static Image keyGroupWarnImage = 
            BundleUtils.loadImage("icons/keyGroupWarn.gif");
    /** Hierarchical layout image. */
    private static Image hierarchicalImage = 
            BundleUtils.loadImage("icons/hierarchicalLayout.gif");
    /** Flat layout image. */
    private static Image flatImage = 
            BundleUtils.loadImage("icons/flatLayout.gif");
    
    /** Font when a tree item as no child. */
    private Font groupFont;
    /** Default font for tree item. */
    private Font keyFont;
    //TODO add a font for when a group is also a key (bold + black)
    
    /** Tree holding keys. */
    private Tree keyTree;
    /** All tree items, keyed by key or group key name. */
    private Map keyTreeItems = new HashMap();
    /** Flat or Tree mode? */
    private boolean keyTreeHierarchical = 
            RBPreferences.getKeyTreeHierarchical();
    
    /** Text box to add a new key. */
    private Text addTextBox;
    
    /** All bundles. */
    private Bundles bundles;
    
    /**
     * Constructor.
     * @param parent parent composite
     * @param bundles all bundles
     */
    public KeysComposite(Composite parent, final Bundles bundles) {
        super(parent, SWT.BORDER);
        this.bundles = bundles;
        // Compute fonts
        keyFont = getFont();
        FontData[] fontData = getFont().getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setStyle(SWT.BOLD);
        }
        groupFont = new Font(getDisplay(), fontData);

        setLayout(new GridLayout(1, false));
        createTopSection();
        createMiddleSection();
        createBottomSection();
    }

    private void createTopSection() {
        Composite topComposite = new Composite(this, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        topComposite.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessHorizontalSpace = true;
        topComposite.setLayoutData(gridData);

        final Button hierModeButton = new Button(topComposite, SWT.TOGGLE);
        hierModeButton.setImage(hierarchicalImage);
        final Button flatModeButton = new Button(topComposite, SWT.TOGGLE);
        flatModeButton.setImage(flatImage);
        if (keyTreeHierarchical) {
            hierModeButton.setSelection(true);
            hierModeButton.setEnabled(false);
        } else {
            flatModeButton.setSelection(true);
            flatModeButton.setEnabled(false);
        }
        //TODO merge the two listeners into one
        hierModeButton.addSelectionListener(new SelectionAdapter () {
            public void widgetSelected(SelectionEvent event) {
                if (hierModeButton.getSelection()) {
                    flatModeButton.setSelection(false);
                    flatModeButton.setEnabled(true);
                    hierModeButton.setEnabled(false);
                    setKeyTreeHierarchical(true);
                }
            }
        });
        flatModeButton.addSelectionListener(new SelectionAdapter () {
            public void widgetSelected(SelectionEvent event) {
                if (flatModeButton.getSelection()) {
                    hierModeButton.setSelection(false);
                    hierModeButton.setEnabled(true);
                    flatModeButton.setEnabled(false);
                    setKeyTreeHierarchical(false);
                }
            }
        });
    }
    
    private void createMiddleSection() {
        keyTree = new Tree(this, 
                SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        refresh();
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        keyTree.setLayoutData(gridData);
        keyTree.addSelectionListener(new SelectionAdapter () {
            public void widgetSelected(SelectionEvent event) {
                addTextBox.setText(getItemPath(getSelectedItem()));
                bundles.refreshTextBoxes(getSelectedKey());
            }
        });
        keyTree.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                if (event.character == SWT.DEL) {
                    deleteKeyOrGroup();
                }
            }
        });
        keyTree.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent event) {
                renameKeyOrGroup();
            }
        });
        // Add popup menu
        Menu menu = new Menu (this);
        MenuItem renameItem = new MenuItem (menu, SWT.PUSH);
        renameItem.setText(
                ResourceBundlePlugin.getResourceString("key.rename"));
        renameItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                renameKeyOrGroup();
            }
        });
        MenuItem deleteItem = new MenuItem (menu, SWT.PUSH);
        deleteItem.setText(
                ResourceBundlePlugin.getResourceString("key.delete"));
        deleteItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                deleteKeyOrGroup();
            }
        });
        keyTree.setMenu(menu);
    }
    
    private void createBottomSection() {
        Composite bottomComposite = new Composite(this, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        bottomComposite.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessHorizontalSpace = true;
        bottomComposite.setLayoutData(gridData);

        // Text box
        addTextBox = new Text(bottomComposite, SWT.BORDER);
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        addTextBox.setLayoutData(gridData);
        addTextBox.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                if (event.character == SWT.CR) {
                    addPropertyKey();
                }
            }
        });
        
        // Add button        
        Button addButton = new Button(bottomComposite, SWT.PUSH);
        addButton.setText(ResourceBundlePlugin.getResourceString("key.add"));
        addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                addPropertyKey();
            }
        });
    }
    
    
    public TreeItem getSelectedItem() {
        TreeItem item = null;
        if (keyTree.getSelection().length > 0) {
            item = keyTree.getSelection()[0];
        }
        return item;
    }

    public String getSelectedKey() {
        String key = null;
        TreeItem item = getSelectedItem();
        if (item != null) {
            key = (String) item.getData();
        }
        return key;
    }
    
    public void refresh() {
        refresh(null);
    }
    
    public void refresh(String selectedKey) {
        keyTreeItems.clear();
        keyTree.removeAll();
        for (Iterator iter = bundles.getKeys().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if (true) {
                // Grouped display
                addGroupKeyItem(key);
            } else {
                // Flat display
                TreeItem keyItem = null;
                keyItem = new TreeItem(keyTree, SWT.NONE);
                keyItem.setText(key);
                keyItem.setData(key);
                keyTreeItems.put(key, keyItem);
            }
        }
        if (selectedKey != null) {
            keyTree.setSelection(new TreeItem[] {
                    (TreeItem) keyTreeItems.get(selectedKey) });
            keyTree.showSelection();
        }
    }
    
    private String addGroupKeyItem(String key) {
        String escapedSeparator = getEscapedKeyGroupSeparator();
        
        boolean isValueMissing = bundles.isValueMissing(key);
        String[] groups = key.split(escapedSeparator);
        TreeItem treeItem = null;
        StringBuffer group = new StringBuffer();
        for (int i = 0; i < groups.length - 1; i++) {
            if (i > 0) {
                group.append(RBPreferences.getKeyGroupSeparator());
            }
            group.append(groups[i]);
            TreeItem groupItem = (TreeItem) keyTreeItems.get(group.toString());
            // Create new group
            if (groupItem == null) {
                if (treeItem == null) {
                    groupItem = new TreeItem(keyTree, SWT.NONE);
                } else {
                    groupItem = new TreeItem(treeItem, SWT.NONE);
                }
                groupItem.setImage(keyGroupImage);
            }
            groupItem.setText(groups[i]);
            groupItem.setFont(groupFont);
            if (isValueMissing) {
                groupItem.setImage(keyGroupWarnImage);
            }
            keyTreeItems.put(group.toString(), groupItem);
            treeItem = groupItem;
        }
        // Add leaf
        String keyLeaf = groups[groups.length - 1];
        if (treeItem == null) {
            treeItem = new TreeItem(keyTree, SWT.NONE);
        } else {
            treeItem = new TreeItem(treeItem, SWT.NONE);
        }
        treeItem.setText(keyLeaf);
        treeItem.setData(key);
        if (!isValueMissing) {
           treeItem.setImage(keyImage);
        } else {
           treeItem.setImage(keyWarnImage);
        }
        if (group.length() > 0) {
            group.append(RBPreferences.getKeyGroupSeparator());
        }
        group.append(keyLeaf);
        keyTreeItems.put(group.toString(), treeItem);
        return group.toString();
    }
    
    /**
     * Gets all keys under a group item.  This includes all descendants, not
     * just direct ones.
     * @param groupItem item under which to get all keys
     * @return all keys under given group item
     */
    public String[] getAllKeysInGroup(TreeItem groupItem) {
        Collection allItems = new ArrayList();
        allItems.add(groupItem);
        findAllItemsInGroup(allItems, groupItem);
        Collection keys = new ArrayList();
        for (Iterator iter = allItems.iterator(); iter.hasNext();) {
            TreeItem item = (TreeItem) iter.next();
            if (item.getData() != null) {
                keys.add((String) item.getData());
            }
        }
        return (String[]) keys.toArray(new String[]{});
    }
    
    /**
     * Store all intems in given group, in given collection.
     * @param treeItems all tree items found under group, plus initial content
     * @param groupItem item under which to get children
     */
    private void findAllItemsInGroup(
            Collection treeItems, TreeItem groupItem) {
        TreeItem[] items = groupItem.getItems();
        treeItems.addAll(Arrays.asList(items));
        for (int i = 0; i < items.length; i++) {
            findAllItemsInGroup(treeItems, items[i]);
        }
    }
    
    /**
     * Gets an item's path, which is all labels in a branch, separated by
     * preffered character separator.
     * @param item tree item
     * @return path
     */
    private String getItemPath(TreeItem item) {
        StringBuffer path = new StringBuffer(item.getText());
        TreeItem parentItem = item;
        while ((parentItem = parentItem.getParentItem()) != null) {
            path.insert(0, RBPreferences.getKeyGroupSeparator());
            path.insert(0, parentItem.getText());
        }
        return path.toString();
    }

   
    /**
     * Returns the root item of a branch.
     * @param treeItem tree item to get its branch root item
     * @return tree item
     */
    private TreeItem getBranchRoot(TreeItem treeItem) {
        TreeItem rootItem = treeItem;
        while (rootItem.getParentItem() != null) {
            rootItem = rootItem.getParentItem();
        }
        return rootItem;        
    }

    /**
     * Refresh all icons associated with the branch the given key is in.
     * @param key the key for which to refrench the branch
     */
    protected void refreshBranchIcons(String key) {
        TreeItem item = (TreeItem) keyTreeItems.get(key);
        refreshBranchIcons(getBranchRoot(item));
    }
    
    /**
     * Refreshes all icons in a branch identified by a branch root.
     * @param branchRoot tree item
     */
    protected void refreshBranchIcons(TreeItem branchRoot) {
        Collection branchItems = new ArrayList();
        branchItems.add(branchRoot);
        findAllItemsInGroup(branchItems, branchRoot);

        // Set all items to default icons
        for (Iterator iter = branchItems.iterator(); iter.hasNext();) {
            TreeItem branchItem = (TreeItem) iter.next();
            if (branchItem.getItemCount() == 0) {
                branchItem.setImage(keyImage);
            } else {
                branchItem.setImage(keyGroupImage);
            }
        }
        
        // Add warnings where appropriate
        String escapedSeparator = RBPreferences.getKeyGroupSeparator();
        String[] keys = getAllKeysInGroup(branchRoot);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (bundles.isValueMissing(key)) {
                TreeItem[] path = getKeyPathItems(key);
                for (int j = 0; j < path.length; j++) {
                    TreeItem item = path[j];
                    if (item.getItemCount() == 0) {
                        item.setImage(keyWarnImage);
                    } else {
                        item.setImage(keyGroupWarnImage);
                    }
                }
            }
        }
    }

    /**
     * Gets all items in a path for given key.
     * @param key key to get path from
     * @return path
     */
    private TreeItem[] getKeyPathItems(String key) {
        List items = new ArrayList();
        TreeItem item = (TreeItem) keyTreeItems.get(key);
        items.add(item);
        while (item.getParentItem() != null) {
            item = item.getParentItem();
            items.add(item);
        }
        Collections.reverse(items);
        return (TreeItem[]) items.toArray(new TreeItem[] {});
    }
    
    /**
     * Renames a key or group of key.
     */
    private void renameKeyOrGroup() {
        String key = getSelectedKey();
        TreeItem item = getSelectedItem();
        if (item.getItemCount() == 0) {
            // Rename single item
            InputDialog dialog = new InputDialog(
                    getShell(), "Rename key",
                    "Rename \"" + key + "\" to:", key, null);
            dialog.open();
            if (dialog.getReturnCode() == Window.OK ) {
                String newKey = dialog.getValue();
                bundles.modifyKey(key, newKey);
                refresh(newKey);
            }
        } else {
            // Rename all keys in group
            String path = getItemPath(item);
            InputDialog dialog = new InputDialog(
                    getShell(), "Rename key group",
                    "Rename key group \"" + path 
                          + "\" to (all nested keys wll be renamed):",
                    path, null);
            dialog.open();
            if (dialog.getReturnCode() == Window.OK ) {
                String newGroup = dialog.getValue();
                String[] keys = getAllKeysInGroup(getSelectedItem());
                for (int i = 0; i < keys.length; i++) {
                    bundles.modifyKey(keys[i], keys[i].replaceFirst(
                            "^" + path, newGroup));
                }
                refresh(newGroup);
            }
        }
    }

    /**
     * Deletes a key or group of key.
     */
    private void deleteKeyOrGroup() {
        String key = getSelectedKey();
        TreeItem item = getSelectedItem();
        MessageBox msgBox = new MessageBox(
                getShell(), SWT.ICON_QUESTION|SWT.OK|SWT.CANCEL);
        if (item.getItemCount() == 0) {
            // Delete single key
            msgBox.setMessage("Are you sure you want to "
                    + "delete \"" + key + "\"?");
            msgBox.setText("Delete key?");
            if (msgBox.open() == SWT.OK) {
                bundles.removeKey(key);
                refresh();
                bundles.refreshTextBoxes(key);
            }
        } else {
            // Delete group
            msgBox.setMessage("Are you sure you want to "
                    + "delete all keys under \"" 
                    + item.getText() + "\"?");
            msgBox.setText("Delete keys?");
            if (msgBox.open() == SWT.OK) {
                String[] keys = 
                        getAllKeysInGroup(getSelectedItem());
                for (int i = 0; i < keys.length; i++) {
                    bundles.removeKey(keys[i]);
                }
                refresh();
                bundles.refreshTextBoxes(key);
            }
        }
    }

    
    /**
     * Gets the "keyTreeHierarchical" attribute.
     * @return Returns the keyTreeHierarchical.
     */
    public boolean isKeyTreeHierarchical() {
        return keyTreeHierarchical;
    }
    /**
     * Sets the "keyTreeHierarchical" attribute.
     * @param keyTreeHierarchical The keyTreeHierarchical to set.
     */
    public void setKeyTreeHierarchical(boolean keyTreeHierarchical) {
        this.keyTreeHierarchical = keyTreeHierarchical;
        refresh();
        if (keyTree.getItemCount() > 0) {
            keyTree.setSelection(new TreeItem[] {keyTree.getItems()[0]}); 
        }
        bundles.refreshTextBoxes(getSelectedKey());

    }
    
    /**
     * Gets an escaped key group separator if we are creating groups, 
     * <code>null</code> otherwise (flat view).
     * @return group separator
     */
    private String getEscapedKeyGroupSeparator() {
        if (isKeyTreeHierarchical()) {
            return "\\Q" + RBPreferences.getKeyGroupSeparator() + "\\E";
        } else {
            return "=";  // escape on something we know won't be in a key
        }
    }
    
    /**
     * Adds a property key to resource bundle, based on content of 
     * bottom "add" text box.
     */
    private void addPropertyKey(){
        String key = addTextBox.getText();
        if (key != null) {
            bundles.addKey(key);
        }
        refresh(key);
        bundles.refreshTextBoxes(getSelectedKey());
    }

}
