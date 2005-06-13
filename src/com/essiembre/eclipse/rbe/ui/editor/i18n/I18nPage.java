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
package com.essiembre.eclipse.rbe.ui.editor.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.essiembre.eclipse.rbe.ui.editor.i18n.tree.KeyTreeComposite;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;

/**
 * Internationalization page where one can edit all resource bundle entries 
 * at once for all supported locales.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class I18nPage extends ScrolledComposite {

    /** Minimum height of text fields. */
    private static final int TEXT_MIN_HEIGHT = 60;

    private final ResourceManager resourceMediator;
    private final KeyTreeComposite keysComposite;
    private final Collection entryComposites = new ArrayList(); 
    
    private BundleEntryComposite activeEntry;
    
    /**
     * Constructor.
     * @param parent parent component.
     * @param style  style to apply to this component
     * @param resourceMediator resource manager
     */
    public I18nPage(
            Composite parent, int style, 
            final ResourceManager resourceMediator) {
        super(parent, style);
        this.resourceMediator = resourceMediator; 

        // Create screen        
        SashForm sashForm = new SashForm(this, SWT.NONE);

        setContent(sashForm);
        setExpandHorizontal(true);
        setExpandVertical(true);
        setMinWidth(400);
        setMinHeight(resourceMediator.getLocales().size() * TEXT_MIN_HEIGHT);

        keysComposite = new KeyTreeComposite(
                sashForm, resourceMediator.getKeyTree());
        keysComposite.getTreeViewer().addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        refreshTextBoxes();
                    }
        });
        
        createSashRightSide(sashForm);
                
        sashForm.setWeights(new int[]{25, 75});
    }


    /**
     * Creates right side of main sash form.
     * @param sashForm parent sash form
     */
    private void createSashRightSide(SashForm sashForm) {
        Composite rightComposite = new Composite(sashForm, SWT.BORDER);
        
        rightComposite.setLayout(new GridLayout(1, false));
        for (Iterator iter = resourceMediator.getLocales().iterator();
                iter.hasNext();) {
            Locale locale = (Locale) iter.next();
            BundleEntryComposite entryComposite = new BundleEntryComposite(
                    rightComposite, resourceMediator, locale);
            entryComposite.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent event) {
                    activeEntry = (BundleEntryComposite) event.widget;
                }
                public void focusLost(FocusEvent event) {
                    activeEntry = null;
                }
            });
            entryComposites.add(entryComposite);
        }
    }
    
    /**
//     * Gets the currently active property key.
//     * @return active property key 
//     */
//    public String getSelectedKey(){
//        return keysComposite.getSelectedKey();
//    }

    /**
     * Refreshes all fields and data linked to this page. This includes
     * resource bundle data, text boxes, and key tree.
     */
    public void refresh(){

        System.out.println("REFresh!!" + System.currentTimeMillis());
        //        bundles.refreshData();
//        bundles.refreshTextBoxes(keysComposite.getSelectedKey());
//        keysComposite.refresh(keysComposite.getSelectedKey());
    }

	/**
	 * Refreshes the editor associated with the active text box (if any)
     * if it has changed.
	 */
	public void refreshEditorOnChanges(){
//        if (activeTextBox != null) {
//            String text = activeTextBox.getText();
//            if (!text.equals(textBeforeUpdate)) {
//                Bundle bundle = bundles.getBundle(activeTextBox);                        
//                Map data = bundle.getData();
//                String selectedKey = 
//                        (String) activeTextBox.getData(SELECTED_KEY);
//                data.put(selectedKey, activeTextBox.getText());
//                bundle.refreshEditor();
//                if (text == null || text.trim().length() == 0
//                        || textBeforeUpdate == null 
//                        || textBeforeUpdate.trim().length() == 0) {
//                    keysComposite.refreshBranchIcons(selectedKey);
//                }
//            }
//        }
	}
	    
    public void refreshTextBoxes() {
        String key = keysComposite.getSelectedKey();
        for (Iterator iter = entryComposites.iterator(); iter.hasNext();) {
            BundleEntryComposite entryComposite = 
                    (BundleEntryComposite) iter.next();
            entryComposite.refresh(key);
        }
    }
}
