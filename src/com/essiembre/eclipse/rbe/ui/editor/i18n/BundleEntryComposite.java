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

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.texteditor.ITextEditor;

import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.ui.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;
import com.essiembre.eclipse.rbe.ui.editor.resources.SourceEditor;

/**
 * Represents a data entry section for a bundle entry.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class BundleEntryComposite extends Composite {

    private final ResourceManager resourceManager;
    private final Locale locale;
    private final Font boldFont;
    private final Image countryImage;
    private final Text textBox;
    
    private String activeKey;
    private String textBeforeUpdate;

    
    /**
     * Constructor.
     */
    public BundleEntryComposite(
            final Composite parent, 
            final ResourceManager resourceManager, 
            final Locale locale) {

        super(parent, SWT.NONE);
        this.resourceManager = resourceManager;
        this.locale = locale;
        this.boldFont = UIUtils.createFont(this, SWT.BOLD, 0);

        GridLayout gridLayout = new GridLayout(1, false);        
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 2;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);
        setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite labelComposite = new Composite(this, SWT.NONE);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        labelComposite.setLayout(gridLayout);
        labelComposite.setLayoutData(
                new GridData(GridData.FILL_HORIZONTAL));
        Label txtLabel = new Label(labelComposite, SWT.NONE);
        txtLabel.setText(UIUtils.getDisplayName(locale));
        txtLabel.setFont(boldFont);
        countryImage = loadCountryIcon(locale);
        Label imgLabel = new Label(labelComposite, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gridData.grabExcessHorizontalSpace = true;
        imgLabel.setLayoutData(gridData);
        imgLabel.setImage(countryImage);
        
        // Textbox row
        textBox = new Text(this, SWT.MULTI | SWT.WRAP | 
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        textBox.setEnabled(false);
        gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        textBox.setLayoutData(gridData);
        textBox.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent event) {
                textBeforeUpdate = textBox.getText();
            }
            public void focusLost(FocusEvent event) {
                updateBundleOnChanges();
            }
        });
        textBox.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                Text textBox = (Text) event.widget;
                final ITextEditor editor = resourceManager.getSourceEditor(
                        locale).getEditor();
                // Text field has changed: make editor dirty if not already
                if (textBeforeUpdate != null 
                        && !textBeforeUpdate.equals(textBox.getText())) {
                    // Make the editor dirty if not already.  If it is, 
                    // we wait until field focus lost (or save) to 
                    // update it completely.
                    if (!editor.isDirty()) {
                        updateBundleOnChanges();
                    }
                // Text field is the same as original (make non-dirty)
                } else {
                    if (editor.isDirty()) {
                        getShell().getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                editor.doRevertToSaved();
                            }
                        });
                    }                        
                }
            }
        });

    
    }

    /**
     * Update bundles if the value of the active key changed.
     */
    public void updateBundleOnChanges(){
        if (activeKey != null) {
            BundleGroup bundleGroup = resourceManager.getBundleGroup();
            BundleEntry entry = bundleGroup.getBundleEntry(locale, activeKey);
            if (entry == null || !textBox.getText().equals(entry.getValue())) {
                String comment = null;
                if (entry != null) {
                    comment = entry.getComment();
                }
                bundleGroup.addBundleEntry(locale, new BundleEntry(
                        activeKey, textBox.getText(), comment));
            }
        }
    }
    
    /**
     * @see org.eclipse.swt.widgets.Widget#dispose()
     */
    public void dispose() {
        super.dispose();
        boldFont.dispose();
        if (countryImage != null) {
            countryImage.dispose();
        }
    }

    /**
     * Refreshes the text field value with value matching given key.
     * @param key key used to grab value
     */
    public void refresh(String key) {
        activeKey = key;
        BundleGroup bundleGroup = resourceManager.getBundleGroup();
        if (key != null && bundleGroup.isKey(key)) {
            BundleEntry bundleEntry = bundleGroup.getBundleEntry(locale, key);
            SourceEditor sourceEditor = resourceManager.getSourceEditor(locale);
            if (bundleEntry == null) {
                textBox.setText("");
            } else {
                textBox.setText(bundleEntry.getValue());
            }
            textBox.setEnabled(!sourceEditor.isReadOnly());
        } else {
            textBox.setText("");
            textBox.setEnabled(false);
        }
    }
    
    /**
     * Gets the active key.
     * @return key
     */
    public String getActiveKey() {
        return activeKey;
    }
    
    /**
     * Loads country icon based on locale country.
     * @param locale the locale on which to grab the country
     * @return an image, or <code>null</code> if no match could be made
     */
    private Image loadCountryIcon(Locale locale) {
        String countryCode = null;
        if (locale != null && locale.getCountry() != null) {
            countryCode = locale.getCountry().toLowerCase();
        }
        if (countryCode != null && countryCode.length() > 0) {
            return RBEPlugin.getImageDescriptor(
                    "countries/" + countryCode + ".gif").createImage();
        }
        return null;
    }
}
