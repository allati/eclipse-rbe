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
package com.essiembre.eclipse.rbe.ui.editor;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.essiembre.eclipse.rbe.ui.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.UIUtils;
import com.essiembre.eclipse.rbe.ui.editor.i18n.I18nPage;
import com.essiembre.eclipse.rbe.ui.editor.locale.NewLocalePage;
import com.essiembre.eclipse.rbe.ui.editor.resources.ResourceManager;
import com.essiembre.eclipse.rbe.ui.editor.resources.SourceEditor;

/**
 * Multi-page editor for editing resource bundles.
 */
public class ResourceBundleEditor extends MultiPageEditorPart {

    private ResourceManager resourceMediator;
    private I18nPage i18nPage;
    /** New locale page. */
    private NewLocalePage newLocalePage;
    /** Bundle image. */
    private Image bundleImage;
    /** Property image. */
    private Image propertyImage;
    /** New Property image. */
    private Image newPropertyImage;
    
    /**
     * Creates a multi-page editor example.
     */
    public ResourceBundleEditor() {
        super();
    }

    /**
     * The <code>MultiPageEditorExample</code> implementation of this method
     * checks that the input is an instance of <code>IFileEditorInput</code>.
     */
    public void init(IEditorSite site, IEditorInput editorInput)
        throws PartInitException {
        if (editorInput instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) editorInput).getFile();
            try {
                resourceMediator = new ResourceManager(site, file);
            } catch (CoreException e) {
                UIUtils.showErrorDialog(
                        getSite().getShell(), e, "error.init.ui");
            }
            
            // Load images
            bundleImage = RBEPlugin.getImageDescriptor(
                    "resourcebundle.gif").createImage();
            propertyImage = RBEPlugin.getImageDescriptor(
                    "propertiesfile.gif").createImage();
            newPropertyImage = RBEPlugin.getImageDescriptor(
                    "newpropertiesfile.gif").createImage();
            
            setPartName(resourceMediator.getEditorDisplayName());
            setContentDescription(
                    RBEPlugin.getString("editor.content.desc")
                  + resourceMediator.getEditorDisplayName() + "."); 
            setTitleImage(bundleImage);
            closeIfAreadyOpen(site, file);
            super.init(site, editorInput);
        } else {
            throw new PartInitException(
                    "Invalid Input: Must be IFileEditorInput");
        }
    }
    
    /**
     * Creates the pages of the multi-page editor.
     */
    protected void createPages() {
        // Create I18N page
        i18nPage = new I18nPage(
                getContainer(), SWT.H_SCROLL | SWT.V_SCROLL, resourceMediator);
        int index = addPage(i18nPage);
        setPageText(index, RBEPlugin.getString(
                "editor.properties"));
        setPageImage(index, bundleImage);
        
        // Create text editor pages for each locales
        try {
            SourceEditor[] sourceEditors = resourceMediator.getSourceEditors();
            for (int i = 0; i < sourceEditors.length; i++) {
                SourceEditor sourceEditor = (SourceEditor) sourceEditors[i];
                index = addPage(
                        sourceEditor.getEditor(), 
                        sourceEditor.getEditor().getEditorInput());
                setPageText(index, UIUtils.getDisplayName(
                        sourceEditor.getLocale()));
                setPageImage(index, propertyImage);
            }
        } catch (PartInitException e) {
            ErrorDialog.openError(
                getSite().getShell(), "Error creating text editor page.",
                null, e.getStatus());
        }
        
        // Add "new locale" page
        newLocalePage = new NewLocalePage(getContainer(), resourceMediator);
        index = addPage(newLocalePage);
        setPageText(index, RBEPlugin.getString(
                "editor.new.tab"));
        setPageImage(index, newPropertyImage);
    }

    /**
     * The <code>MultiPageEditorPart</code> implementation of this 
     * <code>IWorkbenchPart</code> method disposes all nested editors.
     * Subclasses may extend.
     */
    public void dispose() {
        super.dispose();
        propertyImage.dispose();
        newPropertyImage.dispose();
        bundleImage.dispose();
    }
    /**
     * Saves the multi-page editor's document.
     */
    public void doSave(IProgressMonitor monitor) {
        i18nPage.refreshEditorOnChanges();
        resourceMediator.save(monitor);
    }
    
    /**
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs() {
        // Save As not allowed.
    }
    
    /**
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * Calculates the contents of page GUI page when it is activated.
     */
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        if (newPageIndex == 0) {
            resourceMediator.reloadProperties();
            i18nPage.refreshTextBoxes();
        }
    }

    protected boolean isBundleMember(IFile file) {
        return resourceMediator.isResource(file);
    }

    private void closeIfAreadyOpen(IEditorSite site, IFile file) {
        IWorkbenchPage[] pages = site.getWorkbenchWindow().getPages();
        for (int i = 0; i < pages.length; i++) {
            IWorkbenchPage page = pages[i];
            IEditorReference[] editors = page.getEditorReferences();
            for (int j = 0; j < editors.length; j++) {
                IEditorPart editor = editors[j].getEditor(false);
                if (editor instanceof ResourceBundleEditor) {
                    ResourceBundleEditor rbe = (ResourceBundleEditor) editor;
                    if (rbe.isBundleMember(file)) {
                        page.closeEditor(editor, true);
                    }
                }
            }
        }
    }
    
    
    

}
