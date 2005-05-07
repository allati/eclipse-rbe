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
package com.essiembre.eclipse.i18n.resourcebundle.wizards;

import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * The "New" wizard page allows setting the container for
 * the new file as well as the file name. The page
 * will only accept file name without the extension OR
 * with the extension that matches the expected one (properties).
 */

public class ResourceBundleNewWizardPage extends WizardPage {
    private Text containerText;
    private Text fileText;
    private ISelection selection;
    
    
    private Locale[] availableLocales;
    private String[] availableLanguages;
    private String[] availableCountries;
    
    
    private Combo availCombo;
    private Combo availLangCombo;
    private Combo availCountryCombo;
    private Text availVariantText;
    
    private Button addButton;
    private Button removeButton;
    
    private List bundleLocalesList;
    
    

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public ResourceBundleNewWizardPage(ISelection selection) {
        super("wizardPage");
        setTitle("Resource Bundle (Properties Files)");
        setDescription("This wizard creates a set of new files with "
                + "*.properties extension that can be opened by a "
                + "ResourceBundle editor.");
        this.selection = selection;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        // Set available locales
        // TODO sort values (implement a comparator)
        availableLocales = Locale.getAvailableLocales();
        availableLanguages = Locale.getISOLanguages();
        availableCountries = Locale.getISOCountries();
        
        // Move on...
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        layout.verticalSpacing = 20;
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        container.setLayoutData(gd);
        
        // Bundle name + location        
        createTopComposite(container);

        // Locales        
        createBottomComposite(container);
        
                
        initialize();
        dialogChanged();
        setControl(container);
    }


    /**
     * Creates the bottom part of this wizard, which is the locales to add.
     * @param parent parent container
     */
    private void createBottomComposite(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        container.setLayoutData(gd);
        
        // Available locales
        createBottomAvailableLocalesComposite(container);

        // Buttons
        createBottomButtonsComposite(container);
    
        // Selected locales
        createBottomSelectedLocalesComposite(container);
    }

    /**
     * Creates the bottom part of this wizard where selected locales 
     * are stored.
     * @param parent parent container
     */
    private void createBottomSelectedLocalesComposite(Composite parent) {

        // Selected locales Group
        Group selectedGroup = new Group(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout = new GridLayout();
        layout.numColumns = 1;
        selectedGroup.setLayout(layout);
        GridData gd = new GridData(GridData.FILL_BOTH);
        selectedGroup.setLayoutData(gd);
        selectedGroup.setText("Bundle Locales");
        bundleLocalesList = 
                new List(selectedGroup, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER);
        gd = new GridData(GridData.FILL_BOTH);
        bundleLocalesList.setLayoutData(gd);
        bundleLocalesList.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                removeButton.setEnabled(
                        bundleLocalesList.getSelectionIndices().length != 0);
                setAddButtonState();
            }
        });
    }
    
    /**
     * Creates the bottom part of this wizard where buttons to add/remove
     * locales are located.
     * @param parent parent container
     */
    private void createBottomButtonsComposite(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        container.setLayoutData(gd);

        addButton = new Button(container, SWT.NULL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        addButton.setLayoutData(gd);
        addButton.setText("Add   -->");
        addButton.setEnabled(false);
        addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                bundleLocalesList.add(getComboLocaleString());
                setAddButtonState();
            }
        });

        removeButton = new Button(container, SWT.NULL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        removeButton.setLayoutData(gd);
        removeButton.setText("<-- Remove");
        removeButton.setEnabled(false);
        removeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                bundleLocalesList.remove(
                        bundleLocalesList.getSelectionIndices());
                removeButton.setEnabled(false);
                setAddButtonState();
            }
        });
    }
        
    /**
     * Creates the bottom part of this wizard where locales can be chosen
     * or created
     * @param parent parent container
     */
    private void createBottomAvailableLocalesComposite(Composite parent) {

        // Available locales Group
        Group availGroup = new Group(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout = new GridLayout();
        layout.numColumns = 3;
        availGroup.setLayout(layout);
        availGroup.setText("Choose or type a Locale to add");
    
        availCombo = new Combo(availGroup, SWT.READ_ONLY);
        availLangCombo = new Combo(availGroup, SWT.NULL);
        availCountryCombo = new Combo(availGroup, SWT.NULL);
        availVariantText = new Text(availGroup, SWT.BORDER);

        // Text representations of locales
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        availCombo.setLayoutData(gd);
        for (int i = 0; i < availableLocales.length; i++) {
            availCombo.add(availableLocales[i].getDisplayName());
        }
        availCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Locale locale = 
                        availableLocales[availCombo.getSelectionIndex()];
                availLangCombo.setText(locale.getLanguage());
                availCountryCombo.setText(locale.getCountry());
                availVariantText.setText("");
            }
        });

        // Languages
        gd = new GridData();
        availLangCombo.setLayoutData(gd);
        for (int i = 0; i < availableLanguages.length; i++) {
            availLangCombo.add(availableLanguages[i]);
        }
        availLangCombo.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                availLangCombo.setText(availLangCombo.getText().toLowerCase());
                setLocaleOnAvailCombo();
            }
        });
        availLangCombo.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                setAddButtonState();
            }
        });

        // Countries
        gd = new GridData();
        availCountryCombo.setLayoutData(gd);
        for (int i = 0; i < availableCountries.length; i++) {
            availCountryCombo.add(availableCountries[i]);
        }
        availCountryCombo.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                availCountryCombo.setText(
                        availCountryCombo.getText().toUpperCase());
                setLocaleOnAvailCombo();
            }
        });
        availCountryCombo.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                setAddButtonState();
            }
        });

        // Variant
        gd = new GridData();
        availVariantText.setLayoutData(gd);
        availVariantText.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                setLocaleOnAvailCombo();
            }
        });
        availVariantText.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                setAddButtonState();
            }
        });

        
        // Labels
        gd = new GridData();
        gd.horizontalAlignment = GridData.CENTER;
        Label lblLang = new Label(availGroup, SWT.NULL);
        lblLang.setText("Lang.");
        lblLang.setLayoutData(gd);

        gd = new GridData();
        gd.horizontalAlignment = GridData.CENTER;
        Label lblCountry = new Label(availGroup, SWT.NULL);
        lblCountry.setText("Country");
        lblCountry.setLayoutData(gd);

        gd = new GridData();
        gd.horizontalAlignment = GridData.CENTER;
        Label lblVariant = new Label(availGroup, SWT.NULL);
        lblVariant.setText("Variant");
        lblVariant.setLayoutData(gd);
    }
    
    /**
     * Sets an available locale on the available locales combo box.
     */
    private void setLocaleOnAvailCombo() {
        Locale locale = new Locale(
                availLangCombo.getText(),
                availCountryCombo.getText(),
                availVariantText.getText());
        int index = -1;
        for (int i = 0; i < availableLocales.length; i++) {
            Locale availLocale = availableLocales[i];
            if (availLocale.equals(locale)) {
                index = i;
            }
        }
        if (index >= 0) {
            availCombo.select(index);
        } else {
            availCombo.clearSelection();
        }
    }
    
    /**
     * Creates the top part of this wizard, which is the bundle name
     * and location.
     * @param parent parent container
     */
    private void createTopComposite(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        container.setLayoutData(gd);
        
        // Folder
        Label label = new Label(container, SWT.NULL);
        label.setText("&Folder:");

        containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        containerText.setLayoutData(gd);
        containerText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        Button button = new Button(container, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleBrowse();
            }
        });
        
        // Bundle name
        label = new Label(container, SWT.NULL);
        label.setText("&Bundle name:");

        fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileText.setLayoutData(gd);
        fileText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        label = new Label(container, SWT.NULL);
        label.setText("[locale].properties");
    }
    
    
    /**
     * Tests if the current workbench selection is a suitable
     * container to use.
     */
    private void initialize() {
        if (selection!=null && selection.isEmpty()==false && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection)selection;
            if (ssel.size()>1) return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource) {
                IContainer container;
                if (obj instanceof IContainer)
                    container = (IContainer)obj;
                else
                    container = ((IResource)obj).getParent();
                containerText.setText(container.getFullPath().toString());
            }
        }
        fileText.setText("ApplicationResources");
    }
    
    /**
     * Uses the standard container selection dialog to
     * choose the new value for the container field.
     */

    private void handleBrowse() {
        ContainerSelectionDialog dialog =
            new ContainerSelectionDialog(
                getShell(),
                ResourcesPlugin.getWorkspace().getRoot(),
                false,
                "Select a new folder");
        if (dialog.open() == ContainerSelectionDialog.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                containerText.setText(((Path)result[0]).toOSString());
            }
        }
    }
    
    /**
     * Ensures that both text fields are set.
     */

    private void dialogChanged() {
        String container = getContainerName();
        String fileName = getFileName();

        if (container.length() == 0) {
            updateStatus("File container must be specified");
            return;
        }
        if (fileName.length() == 0) {
            updateStatus("File name must be specified");
            return;
        }
        int dotLoc = fileName.lastIndexOf('.');
        if (dotLoc != -1) {
            String ext = fileName.substring(dotLoc + 1);
            if (ext.equalsIgnoreCase("properties") == false) {
                updateStatus("File extension must be \"properties\"");
                return;
            }
        }
        updateStatus(null);
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getContainerName() {
        return containerText.getText();
    }
    public String getFileName() {
        return fileText.getText();
    }
    
    /**
     * Gets string representation of locale based on combo boxes.
     * @return string representation of locale
     */
    private String getComboLocaleString() {
        String localeText = availLangCombo.getText();
        if (availCountryCombo.getText().length() > 0) {
            localeText += "_" + availCountryCombo.getText();
        }
        if (availVariantText.getText().length() > 0) {
            localeText += "_" + availVariantText.getText();
        }
        return localeText;
    }
    
    private void setAddButtonState() {
        addButton.setEnabled(
                availLangCombo.getText().length() > 0
             && bundleLocalesList.indexOf(getComboLocaleString()) == -1);
    }
    
    /**
     * Gets the user selected locales.
     * @return locales
     */
    String[] getLocaleStrings() {
        return bundleLocalesList.getItems();
    }
}