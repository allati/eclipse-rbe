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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.essiembre.eclipse.i18n.resourcebundle.ResourceBundlePlugin;

/**
 * Plugin preference page.
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public class ResourceBundlePreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {

//    private StringFieldEditor groupSeparator;
    private BooleanFieldEditor alignEqualSigns;
    private IntegerFieldEditor groupLevelDeep;
    private IntegerFieldEditor groupLinesAfter;
    private BooleanFieldEditor groupAlignEqualSigns;
    private Collection fields = new ArrayList();
    
    /**
     * Constructor.
     */
    public ResourceBundlePreferencePage() {
        super();
    }

    /**
     * @see org.eclipse.jface.preference.
     * PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {

        Label label = new Label(parent, SWT.NONE);
        label.setText("Coming soon...");
        return label;
        
        
//        return createFormatComposite(parent);
        
//        final TabFolder tabs = new TabFolder(parent, SWT.NONE);
//
//        // General
//       TabItem generalTab = new TabItem(tabs, SWT.NONE);
//        generalTab.setText("General");
//        generalTab.setControl(createGeneralComposite(tabs));
//
//        // Formatting
//        TabItem formatTab = new TabItem(tabs, SWT.NONE);
//        formatTab.setText("Format");
//        formatTab.setControl(createFormatComposite(tabs));
//        
//        initializeFieldEditors();
//        return tabs;
        
    }
//
//    /**
//     * Create general composite.
//     */
//    public Composite createGeneralComposite(Composite parent) {
//        Composite general = new Composite(parent, SWT.NONE);        
//        GridLayout gridLayout = new GridLayout(1, false);
//        gridLayout.marginWidth = 10;
//        gridLayout.marginHeight = 10;
//        general.setLayout(gridLayout);
//        general.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//        
//        groupSeparator = new StringFieldEditor(
//                ResourceBundlePlugin.P_GROUP_SEPARATOR, "Group character separator:", 1, general);
//        groupSeparator.setTextLimit(1);
//        fields.add(groupSeparator);
//        return general;
//    }

//    /**
//     * Create formatting composite.
//     */
//    public Composite createFormatComposite(Composite parent) {
//        Composite format = new Composite(parent, SWT.NONE);
//        GridLayout gridLayout = new GridLayout(1, false);
//        gridLayout.marginWidth = 10;
//        gridLayout.marginHeight = 10;
//        format.setLayout(gridLayout);
//        format.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//        
//        // Align equal signs?
//        alignEqualSigns = new BooleanFieldEditor(
//                ResourceBundlePlugin.P_ALIGN_EQUAL_SIGNS, "Align equal signs.", format);
//        fields.add(alignEqualSigns);
//        
//        // Group keys?
//        groupKeys = new BooleanFieldEditor(
//                ResourceBundlePlugin.P_GROUP_KEYS, "Group keys.", format);
//        fields.add(groupKeys);
//        
//        Group group = new Group(format, SWT.NONE);
//        group.setText("Key grouping options");
//        gridLayout = new GridLayout(1, false);
//        gridLayout.marginWidth = 0;
//        gridLayout.marginHeight = 0;
//        gridLayout.verticalSpacing = 0;
//        group.setLayout(gridLayout);
//        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//        
//        // How many level deep?
//        Composite oneCell = createFieldComposite(group);
//        groupLevelDeep = new IntegerFieldEditor(
//                ResourceBundlePlugin.P_GROUP_LEVEL_DEEP, "Level deep:",
//                oneCell, 2);
//        setFieldWidth(groupLevelDeep.getTextControl(oneCell), 20);
//        fields.add(groupLevelDeep);
//        
//        // How many lines after each group?
//        oneCell = createFieldComposite(group);
//        groupLinesAfter = new IntegerFieldEditor(
//                ResourceBundlePlugin.P_GROUP_LINE_BREAKS, 
//                "Blank lines between each group:", 
//                oneCell, 2);
//        setFieldWidth(groupLinesAfter.getTextControl(oneCell), 20);
//        fields.add(groupLinesAfter);
//
//        // Align equal signs within groups?
//        oneCell = createFieldComposite(group);
//        groupAlignEqualSigns = new BooleanFieldEditor(
//                ResourceBundlePlugin.P_GROUP_ALIGN_EQUAL_SIGNS, 
//                "Align equal signs within groups.",
//                oneCell);
//        fields.add(groupAlignEqualSigns);
//        
//        return format;
//    }

    
    /**
     * @see IWorkbenchPreferencePage#init
     */ 
    public void init(IWorkbench wb) {   
        // Set the preference store for the preference page.
        IPreferenceStore store =
            ResourceBundlePlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
//        store.setDefault(ResourceBundlePlugin.P_GROUP_SEPARATOR, ".");
//        store.setDefault(ResourceBundlePlugin.P_GROUP_LEVEL_DEEP, 1);
//        store.setDefault(ResourceBundlePlugin.P_GROUP_LINE_BREAKS, 1);
    }

    /*
     * Initializes field editors.
     */
    protected void initializeFieldEditors() {
        for (Iterator iter = fields.iterator(); iter.hasNext();) {
            FieldEditor fieldEditor = (FieldEditor) iter.next();
            fieldEditor.setPreferencePage(this);
            fieldEditor.setPreferenceStore(getPreferenceStore());
            fieldEditor.load();
        }
    }

    
    /*
     * The user has pressed "Restore defaults".
     * Restore all default preferences.
     */
    protected void performDefaults() {
        for (Iterator iter = fields.iterator(); iter.hasNext();) {
            FieldEditor fieldEditor = (FieldEditor) iter.next();
            fieldEditor.loadDefault();
        }
        super.performDefaults();
    }
    
    /*
     * The user has pressed Ok or Apply. Store/apply 
     * this page's values appropriately.
     */ 
    public boolean performOk() {
        for (Iterator iter = fields.iterator(); iter.hasNext();) {
            FieldEditor fieldEditor = (FieldEditor) iter.next();
            fieldEditor.store();
        }
        return super.performOk();
    }

    private void setFieldWidth(Control field, int width) {
        GridData gridData = new GridData();
        gridData.widthHint = width;
        field.setLayoutData(gridData);
    }
    
    private Composite createFieldComposite(Composite parent) {
        Composite fieldComposite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;
        fieldComposite.setLayout(gridLayout);
        GridData gridData = new GridData();
        fieldComposite.setLayoutData(gridData);
        return fieldComposite;
    }
    
}
