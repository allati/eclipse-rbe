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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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

    /** Number of pixels per field indentation  */
    private final int indentPixels = 20;
    
    /* Preference fields. */
    private Text keyGroupSeparator;
    private Button alignEqualSigns;

    private Button groupKeys;
    private Text groupLevelDeep;
    private Text groupLineBreaks;
    private Button groupAlignEqualSigns;
    
    private Button wrapLines;
    private Text wrapCharLimit;
    private Button wrapAlignEqualSigns;
    private Text wrapIndentSpaces;

    /** Controls with errors in them. */
    private final Map errors = new HashMap();
    
    /**
     * Constructor.
     */
    public ResourceBundlePreferencePage() {
        super();
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent) {
        IPreferenceStore prefs = getPreferenceStore();
        Composite field = null;
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        
        // Key group separator
        field = createFieldComposite(composite);
        new Label(field, SWT.NONE).setText("Key group separator:");
        keyGroupSeparator = new Text(field, SWT.BORDER);
        keyGroupSeparator.setText(
                prefs.getString(RBPreferences.KEY_GROUP_SEPARATOR));
        keyGroupSeparator.setTextLimit(2);
        
        // Format group
        Group formatGroup = new Group(composite, SWT.NONE);
        formatGroup.setText("Formatting options:");
        formatGroup.setLayout(new GridLayout(1, false));
        formatGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Align equal signs?
        field = createFieldComposite(formatGroup);
        alignEqualSigns = new Button(field, SWT.CHECK);
        alignEqualSigns.setSelection(
                prefs.getBoolean(RBPreferences.ALIGN_EQUAL_SIGNS));
        alignEqualSigns.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                refreshEnabledStatuses();
            }
        });
        new Label(field, SWT.NONE).setText("Align equal signs.");

        // Group keys?
        field = createFieldComposite(formatGroup);
        groupKeys = new Button(field, SWT.CHECK);
        groupKeys.setSelection(prefs.getBoolean(RBPreferences.GROUP_KEYS));
        groupKeys.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                refreshEnabledStatuses();
            }
        });
        new Label(field, SWT.NONE).setText("Group keys.");

        // Group keys by how many level deep?
        field = createFieldComposite(formatGroup, indentPixels);
        new Label(field, SWT.NONE).setText("How many level deep:");
        groupLevelDeep = new Text(field, SWT.BORDER);
        groupLevelDeep.setText(prefs.getString(RBPreferences.GROUP_LEVEL_DEEP));
        groupLevelDeep.setTextLimit(2);
        setWidthInChars(groupLevelDeep, 2);
        groupLevelDeep.addKeyListener(new IntTextValidatorKeyListener(
                "The 'How many level deep' field must be numeric."));
        
        // How many lines between groups?
        field = createFieldComposite(formatGroup, indentPixels);
        new Label(field, SWT.NONE).setText("How many lines between groups:");
        groupLineBreaks = new Text(field, SWT.BORDER);
        groupLineBreaks.setText(
                prefs.getString(RBPreferences.GROUP_LINE_BREAKS));
        groupLineBreaks.setTextLimit(2);
        setWidthInChars(groupLineBreaks, 2);
        groupLineBreaks.addKeyListener(new IntTextValidatorKeyListener(
                "The 'How many lines between groups' field must be numeric."));

        // Align equal signs within groups?
        field = createFieldComposite(formatGroup, indentPixels);
        groupAlignEqualSigns = new Button(field, SWT.CHECK);
        groupAlignEqualSigns.setSelection(
                prefs.getBoolean(RBPreferences.GROUP_ALIGN_EQUAL_SIGNS));
        new Label(field, SWT.NONE).setText("Align equal signs within groups.");

        // Wrap lines?
        field = createFieldComposite(formatGroup);
        wrapLines = new Button(field, SWT.CHECK);
        wrapLines.setSelection(prefs.getBoolean(RBPreferences.WRAP_LINES));
        wrapLines.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                refreshEnabledStatuses();
            }
        });
        new Label(field, SWT.NONE).setText("Wrap lines.");
        
        // After how many characters should we wrap?
        field = createFieldComposite(formatGroup, indentPixels);
        new Label(field, SWT.NONE).setText(
                "Wrap lines after how many characters:");
        wrapCharLimit = new Text(field, SWT.BORDER);
        wrapCharLimit.setText(prefs.getString(RBPreferences.WRAP_CHAR_LIMIT));
        wrapCharLimit.setTextLimit(4);
        setWidthInChars(wrapCharLimit, 4);
        wrapCharLimit.addKeyListener(new IntTextValidatorKeyListener(
                "The 'Wrap lines after...' field must be numeric."));
        
        // Align wrapped lines with equal signs?
        field = createFieldComposite(formatGroup, indentPixels);
        wrapAlignEqualSigns = new Button(field, SWT.CHECK);
        wrapAlignEqualSigns.setSelection(
                prefs.getBoolean(RBPreferences.WRAP_ALIGN_EQUAL_SIGNS));
        wrapAlignEqualSigns.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                refreshEnabledStatuses();
            }
        });
        new Label(field, SWT.NONE).setText(
                "Align wrapped lines with equal signs.");

        // How many spaces/tabs to use for indenting?
        field = createFieldComposite(formatGroup, indentPixels);
        new Label(field, SWT.NONE).setText(
                "How many spaces to use for indentation:");
        wrapIndentSpaces = new Text(field, SWT.BORDER);
        wrapIndentSpaces.setText(
                prefs.getString(RBPreferences.WRAP_INDENT_SPACES));
        wrapIndentSpaces.setTextLimit(2);
        setWidthInChars(wrapIndentSpaces, 2);
        wrapIndentSpaces.addKeyListener(new IntTextValidatorKeyListener(
                "The 'How many spaces to use...' field must be numeric."));

        refreshEnabledStatuses();
        
        return composite;
    }

    
    /**
     * @see org.eclipse.ui.IWorkbenchPreferencePage
     *      #init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        setPreferenceStore(
                ResourceBundlePlugin.getDefault().getPreferenceStore());
    }


    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        IPreferenceStore prefs = getPreferenceStore();
        prefs.setValue(RBPreferences.KEY_GROUP_SEPARATOR,
                keyGroupSeparator.getText());
        prefs.setValue(RBPreferences.ALIGN_EQUAL_SIGNS,
                alignEqualSigns.getSelection());
        prefs.setValue(RBPreferences.GROUP_KEYS,
                groupKeys.getSelection());
        prefs.setValue(RBPreferences.GROUP_LEVEL_DEEP,
                groupLevelDeep.getText());
        prefs.setValue(RBPreferences.GROUP_LINE_BREAKS,
                groupLineBreaks.getText());
        prefs.setValue(RBPreferences.GROUP_ALIGN_EQUAL_SIGNS,
                groupAlignEqualSigns.getSelection());
        prefs.setValue(RBPreferences.WRAP_LINES,
                wrapLines.getSelection());
        prefs.setValue(RBPreferences.WRAP_CHAR_LIMIT,
                wrapCharLimit.getText());
        prefs.setValue(RBPreferences.WRAP_ALIGN_EQUAL_SIGNS,
                wrapAlignEqualSigns.getSelection());
        prefs.setValue(RBPreferences.WRAP_INDENT_SPACES,
                wrapIndentSpaces.getText());
        refreshEnabledStatuses();
        return super.performOk();
    }
    
    
    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        IPreferenceStore prefs = getPreferenceStore();
        keyGroupSeparator.setText(
                prefs.getDefaultString(RBPreferences.KEY_GROUP_SEPARATOR));
        alignEqualSigns.setSelection(
                prefs.getDefaultBoolean(RBPreferences.ALIGN_EQUAL_SIGNS));
        groupKeys.setSelection(
                prefs.getDefaultBoolean(RBPreferences.GROUP_KEYS));
        groupLevelDeep.setText(
                prefs.getDefaultString(RBPreferences.GROUP_LEVEL_DEEP));
        groupLineBreaks.setText(
                prefs.getDefaultString(RBPreferences.GROUP_LINE_BREAKS));
        groupAlignEqualSigns.setSelection(prefs.getDefaultBoolean(
                RBPreferences.GROUP_ALIGN_EQUAL_SIGNS));
        wrapLines.setSelection(
                prefs.getDefaultBoolean(RBPreferences.WRAP_LINES));
        wrapCharLimit.setText(
                prefs.getDefaultString(RBPreferences.WRAP_CHAR_LIMIT));
        wrapAlignEqualSigns.setSelection(
                prefs.getDefaultBoolean(RBPreferences.WRAP_ALIGN_EQUAL_SIGNS));
        wrapIndentSpaces.setText(
                prefs.getDefaultString(RBPreferences.WRAP_INDENT_SPACES));
        refreshEnabledStatuses();
        super.performDefaults();
    }

    private Composite createFieldComposite(Composite parent) {
        return createFieldComposite(parent, 0);
    }
    private Composite createFieldComposite(Composite parent, int indent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = indent;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;
        composite.setLayout(gridLayout);
        return composite;
    }

    private void refreshEnabledStatuses() {
        boolean isGroupKeyEnabled = groupKeys.getSelection();
        boolean isAlignEqualsEnabled = alignEqualSigns.getSelection();
        boolean isWrapEnabled = wrapLines.getSelection();
        boolean isWrapAlignEqualsEnabled = wrapAlignEqualSigns.getSelection();

        groupLevelDeep.setEnabled(isGroupKeyEnabled);
        groupLineBreaks.setEnabled(isGroupKeyEnabled);
        groupAlignEqualSigns.setEnabled(
                isGroupKeyEnabled && alignEqualSigns.getSelection());
        wrapCharLimit.setEnabled(isWrapEnabled);
        wrapAlignEqualSigns.setEnabled(isWrapEnabled);
        wrapIndentSpaces.setEnabled(isWrapEnabled && !isWrapAlignEqualsEnabled);
    }
    
    private class IntTextValidatorKeyListener extends KeyAdapter {
        
        private String errMsg = null;
        
        
        /**
         * Constructor.
         * @param errMsg error message
         */
        public IntTextValidatorKeyListener(String errMsg) {
            super();
            this.errMsg = errMsg;
        }
        /**
         * @see org.eclipse.swt.events.KeyAdapter#keyPressed(
         *          org.eclipse.swt.events.KeyEvent)
         */
        public void keyReleased(KeyEvent event) {
            Text text = (Text) event.widget;
            String value = text.getText(); 
            event.doit = value.matches("^\\d*$");
            if (event.doit) {
                errors.remove(text);
                if (errors.isEmpty()) {
                    setErrorMessage(null);
                    setValid(true);
                } else {
                    setErrorMessage(
                            (String) errors.values().iterator().next());
                }
            } else {
                errors.put(text, errMsg);
                setErrorMessage(errMsg);
                setValid(false);
            }
        }
    }
    
    private void setWidthInChars(Control field, int widthInChars) {
        GridData gd = new GridData();
        GC gc = new GC(field);
        try {
            Point extent = gc.textExtent("X");//$NON-NLS-1$
            gd.widthHint = widthInChars * extent.x;
        } finally {
            gc.dispose();
        }
        field.setLayoutData(gd);
    }
    
}
