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

import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Internationalization page where one can edit all resource bundle entries 
 * at once for all supported locales.
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public class I18NPage extends ScrolledComposite {

    /** Minimum height of text fields. */
    private static final int TEXT_MINIMUM_HEIGHT = 50;

    /** All bundles. */
    private Bundles bundles;
    
    /** Keys related controls. */
    private KeysComposite keysComposite;
    
    /** Text before it is updated in a field having focus. */
    private String textBeforeUpdate;

    /** Bold font. */
    private Font boldFont;
    
    /**
     * Constructor.
     * @param parent parent component.
     * @param style  style to apply to this component
     */
    public I18NPage(Composite parent, int style, Bundles bundles) {
        super(parent, style);
        this.bundles = bundles; 

        // Compute fonts
        FontData[] fontData = getFont().getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setStyle(SWT.BOLD);
        }
        boldFont = new Font(getDisplay(), fontData);

        // Create screen        
        SashForm sashForm = new SashForm(this, SWT.NONE);
        setContent(sashForm);
        setExpandHorizontal(true);
        setExpandVertical(true);
        setMinWidth(400);
        setMinHeight(bundles.count() * TEXT_MINIMUM_HEIGHT);

        keysComposite = new KeysComposite(sashForm, bundles);
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
        for (int i = 0; i <  bundles.count(); i++) {
            Bundle bundle = bundles.getBundle(i);

            // Label row
            Composite labelComposite = new Composite(rightComposite, SWT.NONE);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 2;
            gridLayout.horizontalSpacing = 0;
            gridLayout.verticalSpacing = 0;
            gridLayout.marginWidth = 0;
            gridLayout.marginHeight = 0;
            labelComposite.setLayout(gridLayout);
            labelComposite.setLayoutData(
                    new GridData(GridData.FILL_HORIZONTAL));
            Label txtLabel = new Label(labelComposite, SWT.NONE);
            txtLabel.setText(bundle.getTitle() + ":");
            txtLabel.setFont(boldFont);
            Image image = loadCountryIcon(bundle.getLocale());
            Label imgLabel = new Label(labelComposite, SWT.NONE);
            GridData gridData = new GridData();
            gridData.horizontalAlignment = GridData.END;
            gridData.grabExcessHorizontalSpace = true;
            imgLabel.setLayoutData(gridData);
            imgLabel.setImage(image);
            
            // Textbox row
            Text textBox = new Text(rightComposite, SWT.MULTI | SWT.WRAP | 
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
                    Text textBox = (Text) event.widget;
                    textBeforeUpdate = textBox.getText();
                    textBox.setData(
                            "selectedKey", keysComposite.getSelectedKey());
                }
                public void focusLost(FocusEvent event) {
                    Text textBox = (Text) event.widget;
                    String text = textBox.getText();
                    if (!text.equals(textBeforeUpdate)) {
                        Bundle bundle = bundles.getBundle(textBox);                        
                        Map data = bundle.getData();
                        String selectedKey = 
                                (String) textBox.getData("selectedKey");
                        data.put(selectedKey, textBox.getText());
                        bundle.refreshEditor();
                        if (text == null || text.trim().length() == 0
                                || textBeforeUpdate == null 
                                || textBeforeUpdate.trim().length() == 0) {
                            keysComposite.refreshBranchIcons(selectedKey);
                        }
                    }
                }
            });
            bundle.setTextBox(textBox);
        }
    }
    
    /**
     * Gets the currently active property key.
     * @return active property key 
     */
    public String getActivePropertyKey(){
        return keysComposite.getSelectedKey();
    }

    /**
     * Refreshes all fields and data linked to this page. This includes
     * resource bundle data, text boxes, and key tree.
     */
    public void refresh(){
        bundles.refreshData();
        bundles.refreshTextBoxes(keysComposite.getSelectedKey());
        keysComposite.refresh(keysComposite.getSelectedKey());
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
            return BundleUtils.loadImage(
                    "icons/countries/" + countryCode + ".gif");
        }
        return null;
    }
}
