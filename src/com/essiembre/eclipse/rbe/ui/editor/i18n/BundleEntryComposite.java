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

import java.util.Iterator;
import java.util.Locale;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.texteditor.ITextEditor;

import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.bundle.visitors.DuplicateValuesVisitor;
import com.essiembre.eclipse.rbe.model.bundle.visitors.SimilarValuesVisitor;
import com.essiembre.eclipse.rbe.model.utils.LevenshteinDistanceAnalyzer;
import com.essiembre.eclipse.rbe.model.utils.ProximityAnalyzer;
import com.essiembre.eclipse.rbe.model.utils.WordCountAnalyzer;
import com.essiembre.eclipse.rbe.ui.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.ui.editor.ResourceBundleEditor;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;
import com.essiembre.eclipse.rbe.ui.editor.resources.SourceEditor;
import com.essiembre.eclipse.rbe.ui.preferences.RBEPreferences;

/**
 * Represents a data entry section for a bundle entry.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class BundleEntryComposite extends Composite {

    private final ResourceManager resourceManager;
    private final Locale locale;
    private final Font boldFont;
    private final Font smallFont;
    private final Color yellowColor = UIUtils.getSystemColor(SWT.COLOR_YELLOW);
    
    private ImageRegistry imageRegistry = new ImageRegistry();

    private Text textBox;
    private Button commentedCheckbox;
    private Button gotoButton;
    private Button duplButton;
    private Button simButton;
    
    private String activeKey;
    private String textBeforeUpdate;

    private DuplicateValuesVisitor duplVisitor;
    private SimilarValuesVisitor similarVisitor;
    
    
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
        this.smallFont = UIUtils.createFont(SWT.NONE, -1);
        
        GridLayout gridLayout = new GridLayout(1, false);        
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 2;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);
        setLayoutData(new GridData(GridData.FILL_BOTH));

        createLabelRow();
        createTextRow();
    }

    /**
     * Update bundles if the value of the active key changed.
     */
    public void updateBundleOnChanges(){
        if (activeKey != null) {
            BundleGroup bundleGroup = resourceManager.getBundleGroup();
            BundleEntry entry = bundleGroup.getBundleEntry(locale, activeKey);
            boolean commentedSelected = commentedCheckbox.getSelection();
            if (entry == null || !textBox.getText().equals(entry.getValue())
                   || entry.isCommented() != commentedSelected) {
                String comment = null;
                if (entry != null) {
                    comment = entry.getComment();
                }
                bundleGroup.addBundleEntry(locale, new BundleEntry(
                        activeKey, 
                        textBox.getText(), 
                        comment, 
                        commentedSelected));
            }
        }
    }
    
    /**
     * @see org.eclipse.swt.widgets.Widget#dispose()
     */
    public void dispose() {
        super.dispose();
        boldFont.dispose();
        smallFont.dispose();
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
                commentedCheckbox.setSelection(false);
            } else {
                commentedCheckbox.setSelection(bundleEntry.isCommented());
                textBox.setText(bundleEntry.getValue());
            }
            commentedCheckbox.setEnabled(!sourceEditor.isReadOnly());
            textBox.setEnabled(!sourceEditor.isReadOnly());
            gotoButton.setEnabled(true);
            if (RBEPreferences.getReportDuplicateValues()) {
                findDuplicates(bundleEntry);
            } else {
                duplVisitor = null;
            }
            if (RBEPreferences.getReportSimilarValues()) {
                findSimilar(bundleEntry);
            } else {
                similarVisitor = null;
            }
        } else {
            commentedCheckbox.setSelection(false);
            commentedCheckbox.setEnabled(false);
            textBox.setText("");
            textBox.setEnabled(false);
            gotoButton.setEnabled(false);
            duplButton.setVisible(false);
            simButton.setVisible(false);
        }
        resetCommented();
    }
        
    private void findSimilar(BundleEntry bundleEntry) {
        ProximityAnalyzer analyzer;
        if (RBEPreferences.getReportSimilarValuesLevensthein()) {
            analyzer = LevenshteinDistanceAnalyzer.getInstance();
        } else {
            analyzer = WordCountAnalyzer.getInstance();
        }
        BundleGroup bundleGroup = resourceManager.getBundleGroup();
        if (similarVisitor == null) {
            similarVisitor = new SimilarValuesVisitor();
        }
        similarVisitor.setProximityAnalyzer(analyzer);
        similarVisitor.clear();
        bundleGroup.getBundle(locale).accept(similarVisitor, bundleEntry);
        if (duplVisitor != null) {
            similarVisitor.getSimilars().removeAll(duplVisitor.getDuplicates());
        }
        simButton.setVisible(similarVisitor.getSimilars().size() > 0);
    }
    
    private void findDuplicates(BundleEntry bundleEntry) {
        BundleGroup bundleGroup = resourceManager.getBundleGroup();
        if (duplVisitor == null) {
            duplVisitor = new DuplicateValuesVisitor();
        }
        duplVisitor.clear();
        bundleGroup.getBundle(locale).accept(duplVisitor, bundleEntry);
        duplButton.setVisible(duplVisitor.getDuplicates().size() > 0);
    }
    
    
    /**
     * Creates the text field label, icon, and commented check box.
     */
    private void createLabelRow() {
        Composite labelComposite = new Composite(this, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        gridLayout.horizontalSpacing = 5;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        labelComposite.setLayout(gridLayout);
        labelComposite.setLayoutData(
                new GridData(GridData.FILL_HORIZONTAL));

        // Locale text
        Label txtLabel = new Label(labelComposite, SWT.NONE);
        txtLabel.setText(" " + UIUtils.getDisplayName(locale) + " ");
        txtLabel.setFont(boldFont);

        GridData gridData = new GridData();

        // Similar button
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gridData.grabExcessHorizontalSpace = true;
        simButton = new Button(labelComposite, SWT.PUSH | SWT.FLAT);
        simButton.setImage(UIUtils.getImage("similar.gif"));
        simButton.setLayoutData(gridData);
        simButton.setVisible(false);
        simButton.setToolTipText(
                "Similar value(s) found. Click for details");
        simButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                String msg = "Below are keys having similar value as key \""
                        + activeKey + "\" within the locale \""
                        + UIUtils.getDisplayName(locale) + "\":\n\n";
                for (Iterator iter = similarVisitor.getSimilars().iterator();
                        iter.hasNext();) {
                    msg += "        " + ((BundleEntry) iter.next()).getKey()
                        + "\n";
                }
                MessageDialog.openInformation(
                        getShell(), "Similar value(s) found.", msg); 
            }
        });

        // Duplicate button
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        duplButton = new Button(labelComposite, SWT.PUSH | SWT.FLAT);
        duplButton.setImage(UIUtils.getImage("duplicate.gif"));
        duplButton.setLayoutData(gridData);
        duplButton.setVisible(false);
        duplButton.setToolTipText(
                "Duplicate value(s) found. Click for details");
        duplButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                String msg = "Below are keys having the same value as key \""
                        + activeKey + "\" within the locale \""
                        + UIUtils.getDisplayName(locale) + "\":\n\n";
                for (Iterator iter = duplVisitor.getDuplicates().iterator();
                        iter.hasNext();) {
                    msg += "        " + ((BundleEntry) iter.next()).getKey()
                        + "\n";
                }
                MessageDialog.openInformation(
                        getShell(), "Duplicate value(s) found.", msg); 
            }
        });
        
        // Commented checkbox
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        //gridData.grabExcessHorizontalSpace = true;
        commentedCheckbox = new Button(
                labelComposite, SWT.CHECK);
        commentedCheckbox.setText("#");//TODO translate
        commentedCheckbox.setFont(smallFont);
        commentedCheckbox.setLayoutData(gridData);
        commentedCheckbox.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                resetCommented();
                updateBundleOnChanges();
            }
        });
        commentedCheckbox.setEnabled(false);
        
        // Country flag
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        Label imgLabel = new Label(labelComposite, SWT.NONE);
        imgLabel.setLayoutData(gridData);
        imgLabel.setImage(loadCountryIcon(locale));

        // Goto button
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gotoButton = new Button(
                labelComposite, SWT.ARROW | SWT.RIGHT);
        gotoButton.setToolTipText(
                "Click to go to corresponding properties file");
        gotoButton.setEnabled(false);
        gotoButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                ITextEditor editor = resourceManager.getSourceEditor(
                        locale).getEditor();
                Object activeEditor = 
                        editor.getSite().getPage().getActiveEditor();
                if (activeEditor instanceof ResourceBundleEditor) {
                    ((ResourceBundleEditor) activeEditor).setActivePage(locale);
                }
            }
        });
        gotoButton.setLayoutData(gridData);
    }
    
    /**
     * Creates the text row.
     */
    private void createTextRow() {
        textBox = new Text(this, SWT.MULTI | SWT.WRAP | 
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        textBox.setEnabled(false);
        GridData gridData = new GridData();
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
        //TODO add a preference property listener and add/remove this listener
        textBox.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent event) {
                if (!RBEPreferences.getFieldTabInserts() 
                        && event.character == SWT.TAB) {
                    event.doit = true;
                }
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
                        int caretPosition = textBox.getCaretPosition();
                        updateBundleOnChanges();
                        textBox.setSelection(caretPosition);
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
     * Loads country icon based on locale country.
     * @param locale the locale on which to grab the country
     * @return an image, or <code>null</code> if no match could be made
     */
    private Image loadCountryIcon(Locale locale) {
        Image image = null;
        String countryCode = null;
        if (locale != null && locale.getCountry() != null) {
            countryCode = locale.getCountry().toLowerCase();
        }
        if (countryCode != null && countryCode.length() > 0) {
            String imageName = 
                    "countries/" + countryCode.toLowerCase() + ".gif";
            image = imageRegistry.get(imageName);
            if (image == null) {
                image = RBEPlugin.getImageDescriptor(imageName).createImage();
                imageRegistry.put(imageName, image);
            }
        }
        if (image == null) {
            image = imageRegistry.get("countries/blank.gif");
            if (image == null) {
                String imageName = "countries/blank.gif"; 
                image = RBEPlugin.getImageDescriptor(imageName).createImage();
                imageRegistry.put(imageName, image);
            }
        }
        return image;
    }
    
    private void resetCommented() {
        if (commentedCheckbox.getSelection()) {
            commentedCheckbox.setToolTipText(
                    "Uncheck to uncomment this entry.");
            textBox.setForeground(
                    getDisplay().getSystemColor(SWT.COLOR_GRAY));
        } else {
            commentedCheckbox.setToolTipText(
                    "Check to comment this entry.");
            textBox.setForeground(null);
        }
    }
}
