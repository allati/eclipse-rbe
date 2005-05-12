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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
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

import com.essiembre.eclipse.i18n.resourcebundle.composites.LocaleSelectorComposite;
import com.essiembre.eclipse.i18n.resourcebundle.preferences.RBPreferences;
import com.essiembre.eclipse.i18n.resourcebundle.utils.UIUtils;

/**
 * Page for adding a new locale (new localized properties file).
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public class NewLocalePage extends Composite {

    /**
     * Constructor.
     * @param parent parent component.
     * @param style  style to apply to this component
     */
    public NewLocalePage(
            final Composite parent, 
            final String basePathAddFileName,
            final String baseFileName) {
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
        label.setText("New properties file:");
        label.setFont(UIUtils.createFont(this, SWT.BOLD, 5));
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        label.setLayoutData(gridData);

        // Locale selector
        final LocaleSelectorComposite localeSelector = 
                new LocaleSelectorComposite(block);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        localeSelector.setLayoutData(gridData);
        
        // Create button
        Button createButton = new Button(block, SWT.NULL);
        createButton.setText("Create");
        createButton.setFont(UIUtils.createFont(this, SWT.BOLD, 1));
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        createButton.setLayoutData(gridData);
        createButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                // Create the new file
                Locale locale = localeSelector.getSelectedLocale();
                String localeSuffix = "";
                if (locale != null) {
                    localeSuffix = "_" + locale.toString();
                }
                String folderPath = basePathAddFileName.substring(0, 
                        basePathAddFileName.length() - baseFileName.length());
                String fileName = new Path(basePathAddFileName).lastSegment() 
                        + localeSuffix + ".properties";
                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                IResource resource = root.findMember(folderPath);
                IContainer container = (IContainer) resource;
                final IFile file = container.getFile(new Path(fileName));
                try {
                    String contents = "";
                    if (RBPreferences.getShowGenerator()) {
                        contents = "#" + BundleUtils.GENERATED_BY;
                    }
                    InputStream stream = 
                        new ByteArrayInputStream(contents.getBytes());
                    
                    if (file.exists()) {
                        file.setContents(stream, true, true, null);
                    } else {
                        file.create(stream, true, null);
                    }
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CoreException e) {
                    e.printStackTrace();
                }
                
                // Reopen
                getShell().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        IWorkbenchPage page =
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                    .getActivePage();
                        try {
                            IDE.openEditor(page, file, true);
                        } catch (PartInitException e) {
                        }
                    }
                });
            }
        });
        this.layout();
    }

}
