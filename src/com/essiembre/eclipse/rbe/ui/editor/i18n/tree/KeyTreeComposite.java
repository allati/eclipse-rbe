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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;
import com.essiembre.eclipse.rbe.model.tree.updater.FlatKeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.GroupedKeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.tree.visitors.KeyStartsWithVisitor;
import com.essiembre.eclipse.rbe.ui.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.preferences.RBEPreferences;

/**
 * Tree for displaying and navigating through resource bundle keys.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class KeyTreeComposite extends Composite {

    /** Image for tree mode toggle button. */
    private Image treeToggleImage;
    /** Image for flat mode toggle button. */
    private Image flatToggleImage;

    /** "Expand All" menu item. */
    MenuItem expandItem; 
    /** "Collapse All" menu item. */
    MenuItem collapseItem; 
    
    /** Key Tree Viewer. */
    private TreeViewer treeViewer;
    /** TreeViewer label provider. */
    protected KeyTreeLabelProvider labelProvider;
    
    /** Flat or Tree mode? */
    private boolean keyTreeHierarchical = 
            RBEPreferences.getKeyTreeHierarchical();
    
    /** Text box to add a new key. */
    private Text addTextBox;
    
    /** Key tree. */
    private KeyTree keyTree;
    
    /** Whether to synchronize the add text box with tree key selection. */
    private boolean syncAddTextBox = true;
    
    /**
     * Constructor.
     * @param parent parent composite
     * @param keyTree key tree
     */
    public KeyTreeComposite(Composite parent, final KeyTree keyTree) {
        super(parent, SWT.BORDER);
        this.keyTree = keyTree;

        treeToggleImage = RBEPlugin.getImageDescriptor(
                "hierarchicalLayout.gif").createImage();
        flatToggleImage = RBEPlugin.getImageDescriptor(
                "flatLayout.gif").createImage();

        setLayout(new GridLayout(1, false));
        createTopSection();
        createMiddleSection();
        createBottomSection();
    }

    /**
     * Gets the tree viewer.
     * @return tree viewer
     */
    public TreeViewer getTreeViewer() {
        return treeViewer;
    }
    
    /**
     * Gets the selected key tree item.
     * @return key tree item
     */
    public KeyTreeItem getSelection() {
        IStructuredSelection selection = 
                (IStructuredSelection) treeViewer.getSelection();
        return (KeyTreeItem) selection.getFirstElement();
    }

    /**
     * Gets selected key.
     * @return selected key
     */
    public String getSelectedKey() {
        String key = null;
        KeyTreeItem item = getSelection();
        if (item != null) {
            key = (String) item.getId();
        }
        return key;
    }

    /**
     * @see org.eclipse.swt.widgets.Widget#dispose()
     */
    public void dispose() {
        super.dispose();
        treeToggleImage.dispose();
        flatToggleImage.dispose();
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
            //TODO translate
            msgHead = "Rename key";
            msgBody = "Rename \"" + key + "\" to:";
        } else {
            msgHead = "Rename key group";
            msgBody = "Rename key group \"" + selectedItem.getName() 
                    + "\" to (all nested keys will be renamed):";
        }
        // Rename single item
        InputDialog dialog = new InputDialog(
                getShell(), msgHead, msgBody, key, null);
        dialog.open();
        if (dialog.getReturnCode() == Window.OK ) {
            String newKey = dialog.getValue();
            BundleGroup bundleGroup = keyTree.getBundleGroup();
            Collection items = new ArrayList();
            items.add(selectedItem);
            items.addAll(selectedItem.getNestedChildren());
            for (Iterator iter = items.iterator(); iter.hasNext();) {
                KeyTreeItem item = (KeyTreeItem) iter.next();
                String oldItemKey = item.getId();
                String newItemKey = oldItemKey.replaceFirst("^" + key, newKey);
                bundleGroup.renameKey(oldItemKey, newItemKey);
            }
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
            //TODO translate
            msgHead = "Copy key";
            msgBody = "Copy \"" + key + "\" to:";
        } else {
            msgHead = "Copy key group";
            msgBody = "Copy key group \"" + selectedItem.getName() 
                    + "\" to (all nested keys will be copied):";
        }
        // Rename single item
        InputDialog dialog = new InputDialog(
                getShell(), msgHead, msgBody, key, null);
        dialog.open();
        if (dialog.getReturnCode() == Window.OK ) {
            String newKey = dialog.getValue();
            BundleGroup bundleGroup = keyTree.getBundleGroup();
            Collection items = new ArrayList();
            items.add(selectedItem);
            items.addAll(selectedItem.getNestedChildren());
            for (Iterator iter = items.iterator(); iter.hasNext();) {
                KeyTreeItem item = (KeyTreeItem) iter.next();
                String origItemKey = item.getId();
                String newItemKey = origItemKey.replaceFirst("^" + key, newKey);
                bundleGroup.copyKey(origItemKey, newItemKey);
            }
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
            //TODO translate
            msgHead = "Delete key?";
            msgBody = "Are you sure you want to delete \"" + key + "\"?";
        } else {
            msgHead = "Delete key group?";
            msgBody = "Are you sure you want to delete all keys in \"" 
                    + selectedItem.getName() + "\" group?";
        }
        MessageBox msgBox = new MessageBox(
                getShell(), SWT.ICON_QUESTION|SWT.OK|SWT.CANCEL);
        msgBox.setMessage(msgBody);
        msgBox.setText(msgHead);
        if (msgBox.open() == SWT.OK) {
            BundleGroup bundleGroup = keyTree.getBundleGroup();
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
     * Creates the top section (toggle buttons) of this composite.
     */
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
        hierModeButton.setImage(treeToggleImage);
        hierModeButton.setToolTipText("Tree mode");
        final Button flatModeButton = new Button(topComposite, SWT.TOGGLE);
        flatModeButton.setImage(flatToggleImage);
        flatModeButton.setToolTipText("Flat mode");
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
                    keyTree.setUpdater(new GroupedKeyTreeUpdater(
                            RBEPreferences.getKeyGroupSeparator()));
                    expandItem.setEnabled(true);
                    collapseItem.setEnabled(true);
                    treeViewer.expandAll();
                    selectKeyTreeItem(addTextBox.getText());
                }
            }
        });
        flatModeButton.addSelectionListener(new SelectionAdapter () {
            public void widgetSelected(SelectionEvent event) {
                if (flatModeButton.getSelection()) {
                    hierModeButton.setSelection(false);
                    hierModeButton.setEnabled(true);
                    flatModeButton.setEnabled(false);
                    keyTree.setUpdater(new FlatKeyTreeUpdater());
                    expandItem.setEnabled(false);
                    collapseItem.setEnabled(false);
                    selectKeyTreeItem(addTextBox.getText());
                }
            }
        });
    }
    
    /**
     * Creates the middle (tree) section of this composite.
     */
    private void createMiddleSection() {

        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        treeViewer = new TreeViewer(this,
                SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        treeViewer.setContentProvider(new KeyTreeContentProvider());
        labelProvider = new KeyTreeLabelProvider();
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.setUseHashlookup(true);
        treeViewer.setInput(keyTree);
        treeViewer.expandAll();
        treeViewer.getTree().setLayoutData(gridData);      
        treeViewer.getTree().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                if (event.character == SWT.DEL) {
                    deleteKeyOrGroup();
                }
            }
        });
        treeViewer.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        if (syncAddTextBox && getSelectedKey() != null) {
                            addTextBox.setText(getSelectedKey());
                        }
                        syncAddTextBox = true;
                    }
        });
        treeViewer.getTree().addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent event) {
                Object element = getSelection();
                if (treeViewer.isExpandable(element)) {
                    if (treeViewer.getExpandedState(element)) {
                        treeViewer.collapseToLevel(element, 1);
                    } else {
                        treeViewer.expandToLevel(element, 1);
                    }
                }
            }
        });
        
        // Add popup menu
        Menu menu = new Menu (this);
        MenuItem renameItem = new MenuItem (menu, SWT.PUSH);
        renameItem.setText(RBEPlugin.getString("key.rename"));
        renameItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                renameKeyOrGroup();
            }
        });
        MenuItem deleteItem = new MenuItem (menu, SWT.PUSH);
        deleteItem.setText(RBEPlugin.getString("key.delete"));
        deleteItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                deleteKeyOrGroup();
            }
        });
        MenuItem copyItem = new MenuItem (menu, SWT.PUSH);
        copyItem.setText("&Copy"); //TODO translate
        copyItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                copyKeyOrGroup();
            }
        });
        MenuItem commentItem = new MenuItem (menu, SWT.PUSH);
        commentItem.setText("Co&mment"); //TODO translate
        commentItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                //TODO implement comment
                System.out.println("Key commenting not implemented.");
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);
        
        expandItem = new MenuItem (menu, SWT.PUSH);
        expandItem.setText("&Expand All"); //TODO translate
        expandItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                treeViewer.expandAll();
            }
        });
        collapseItem = new MenuItem (menu, SWT.PUSH);
        collapseItem.setText("Co&llapse All"); //TODO translate
        collapseItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                treeViewer.collapseAll();
            }
        });

        treeViewer.getTree().setMenu(menu);
    }
    
    /**
     * Creates the botton section (add field/button) of this composite.
     */
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

        // Add button
        final Button addButton = new Button(bottomComposite, SWT.PUSH);
        addButton.setText(RBEPlugin.getString("key.add"));
        addButton.setEnabled(false);
        addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                addKey();
            }
        });

        addTextBox.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                String key = addTextBox.getText();
                if (event.character == SWT.CR) {
                    addKey();
                } else if (!key.equals("")){
                    KeyStartsWithVisitor visitor = new KeyStartsWithVisitor();
                    keyTree.accept(visitor, key);
                    KeyTreeItem item = visitor.getKeyTreeItem();
                    if (item != null) {
                        syncAddTextBox = false;
                        selectKeyTreeItem(item);
                    }
                }
            }
        });
        addTextBox.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                String key = addTextBox.getText();
                boolean keyExist = keyTree.getBundleGroup().isKey(key);
                if (keyExist || key.equals("")) {
                    addButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                }
            }
        });
    }
    
    /**
     * Adds a key to the tree, based on content from add field.
     */
    private void addKey() {
        String key = addTextBox.getText();
        keyTree.getBundleGroup().addKey(key);
        selectKeyTreeItem(key);
    }
    
    /**
     * Selected the key tree item matching given key.
     * @param key key to select
     */
    private void selectKeyTreeItem(String key) {
        selectKeyTreeItem(keyTree.getKeyTreeItem(key));
    }
    
    /**
     * Selected the key tree item matching given key tree item.
     * @param key key tree item to select
     */
    private void selectKeyTreeItem(KeyTreeItem item) {
        if (item != null) {
            treeViewer.setSelection(new StructuredSelection(item), true);
        }
    }
}
