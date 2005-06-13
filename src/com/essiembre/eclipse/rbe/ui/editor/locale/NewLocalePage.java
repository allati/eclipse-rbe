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
package com.essiembre.eclipse.rbe.ui.editor.locale;

import java.io.IOException;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.essiembre.eclipse.rbe.ui.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;
import com.essiembre.eclipse.rbe.ui.widgets.LocaleSelector;

/**
 * Page for adding a new locale (new localized properties file).
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class NewLocalePage extends Composite {

    /**
     * Constructor.
     * @param parent parent component.
     * @param basePathAddFileName base path and file name.
     * @param baseFileName base file name.
     * @param nlDir path to "nl" directory. <code>null</code> if regular bundle. 
     */
    public NewLocalePage(
            final Composite parent, 
            final ResourceManager resourceMediator) {
        super(parent, SWT.NONE);
        
        setLayout(new GridLayout());

        Composite block = new Composite(this, SWT.NONE);
        block.setLayout(new GridLayout());
        
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        block.setLayoutData(gridData);
        
        // Title label
        Label label = new Label(block, SWT.NONE);
        label.setText(RBEPlugin.getString(
                "editor.new.title"));
        label.setFont(UIUtils.createFont(this, SWT.BOLD, 5));
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        label.setLayoutData(gridData);

        // Locale selector
        final LocaleSelector localeSelector = 
                new LocaleSelector(block);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        localeSelector.setLayoutData(gridData);
        
        // Create button
        Button createButton = new Button(block, SWT.NULL);
        createButton.setText(RBEPlugin.getString(
                "editor.new.create"));
        createButton.setFont(UIUtils.createFont(this, SWT.BOLD, 1));
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        createButton.setLayoutData(gridData);
        createButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                Locale locale = localeSelector.getSelectedLocale();
                try {
                    // Create the new file
                    try {
                        //TODO add "newPropertiesFile" method to seGroup.
                        final IFile file = 
                                resourceMediator.createPropertiesFile(locale);
                        // Reopen
                        getShell().getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                IWorkbenchPage page = PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow().getActivePage();
                                try {
                                    IDE.openEditor(page, file, true);
                                } catch (PartInitException e) {
                                    UIUtils.showErrorDialog(getShell(), e,
                                            "error.newfile.cannotCreate");
                                }
                            }
                        });
                    } catch (NullPointerException e) {
                        UIUtils.showErrorDialog(getShell(), e, 
                                "error.newfile.cannotCreate");
                        throw e;
                    }
                } catch (CoreException e) {
                    UIUtils.showErrorDialog(
                            getShell(), e, "error.newfile.cannotCreate");
                } catch (IOException e) {
                    UIUtils.showErrorDialog(
                            getShell(), e, "error.newfile.cannotCreate");
                }
            }
        });
        this.layout();
    }

}
