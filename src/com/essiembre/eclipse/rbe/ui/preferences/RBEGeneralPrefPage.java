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
package com.essiembre.eclipse.rbe.ui.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.essiembre.eclipse.rbe.ui.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.UIUtils;

/**
 * Plugin generic preference page.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class RBEGeneralPrefPage extends PreferencePage implements
        IWorkbenchPreferencePage {
    
    /* Preference fields. */
    private Text keyGroupSeparator;

    private Button convertEncodedToUnicode;

    private Button supportNL;

    //TODO defaults to tree mode when opening editor
    
    /** Controls with errors in them. */
    private final Map errors = new HashMap();
    
    /**
     * Constructor.
     */
    public RBEGeneralPrefPage() {
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
        new Label(field, SWT.NONE).setText(
                RBEPlugin.getString("prefs.groupSep"));
        keyGroupSeparator = new Text(field, SWT.BORDER);
        keyGroupSeparator.setText(
                prefs.getString(RBEPreferences.KEY_GROUP_SEPARATOR));
        keyGroupSeparator.setTextLimit(2);
        
        // Convert encoded to unicode?
        field = createFieldComposite(composite);
        convertEncodedToUnicode = new Button(field, SWT.CHECK);
        convertEncodedToUnicode.setSelection(
                prefs.getBoolean(RBEPreferences.CONVERT_ENCODED_TO_UNICODE));
        new Label(field, SWT.NONE).setText(
                RBEPlugin.getString("prefs.convertEncoded"));

        // Support "NL" localization structure
        field = createFieldComposite(composite);
        supportNL = new Button(field, SWT.CHECK);
        supportNL.setSelection(prefs.getBoolean(RBEPreferences.SUPPORT_NL));
        new Label(field, SWT.NONE).setText(
                RBEPlugin.getString(
                        "prefs.supportNL"));
        
        return composite;
    }

    
    /**
     * @see org.eclipse.ui.IWorkbenchPreferencePage
     *      #init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        setPreferenceStore(
                RBEPlugin.getDefault().getPreferenceStore());
    }


    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        IPreferenceStore prefs = getPreferenceStore();
        prefs.setValue(RBEPreferences.KEY_GROUP_SEPARATOR,
                keyGroupSeparator.getText());
        prefs.setValue(RBEPreferences.CONVERT_ENCODED_TO_UNICODE,
                convertEncodedToUnicode.getSelection());
        prefs.setValue(RBEPreferences.SUPPORT_NL,
                supportNL.getSelection());
        return super.performOk();
    }
    
    
    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        IPreferenceStore prefs = getPreferenceStore();
        keyGroupSeparator.setText(
                prefs.getDefaultString(RBEPreferences.KEY_GROUP_SEPARATOR));
        convertEncodedToUnicode.setSelection(prefs.getDefaultBoolean(
                RBEPreferences.CONVERT_ENCODED_TO_UNICODE));
        supportNL.setSelection(prefs.getDefaultBoolean(
                RBEPreferences.SUPPORT_NL));
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
        gd.widthHint = UIUtils.getWidthInChars(field, widthInChars);
        field.setLayoutData(gd);
    }
    
}
