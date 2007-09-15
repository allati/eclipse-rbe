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
package com.essiembre.eclipse.rbe.ui.editor.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.essiembre.eclipse.rbe.model.workbench.files.PropertiesFileCreator;
import com.essiembre.eclipse.rbe.model.workbench.files.StandardPropertiesFileCreator;

/**
 * Responsible for creating resources related to a standard
 * directory structure.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class StandardResourceFactory extends ResourceFactory {

    private Map sourceEditors;
    private PropertiesFileCreator fileCreator;
    private String displayName;
    private IEditorSite site;

    @Override
    public boolean isResponsible(IFile file) throws CoreException {
    	return true;
    }
    
    /**
     * Constructor.
     * @param site editor site
     * @param file file used to open all related files
     * @throws CoreException problem creating factory
     */
    public void init(IEditorSite site, IFile file) 
             throws CoreException {
        this.site = site;
        sourceEditors = new HashMap();
        String bundleName = getBundleName(file);
        String regex = ResourceFactory.getPropertiesFileRegEx(file);
        IResource[] resources = StandardResourceFactory.getResources(file);

        for (int i = 0; i < resources.length; i++) {
            IResource resource = resources[i];
            String resourceName = resource.getName();
            // Build local title
            Locale locale = parseBundleName(resource);            
            SourceEditor sourceEditor = 
                    createEditor(site, resource, locale);
            if (sourceEditor != null) {
                sourceEditors.put(sourceEditor.getLocale(), sourceEditor);
            }
        }
        fileCreator = new StandardPropertiesFileCreator(
                file.getParent().getFullPath().toString(),
                bundleName,
                file.getFileExtension());
		setDisplayName(getDisplayName(file));
    }
    
    /**
     * @see com.essiembre.eclipse.rbe.ui.editor.resources.ResourceFactory
     *         #getEditorDisplayName()
     */
    public String getEditorDisplayName() {
        return displayName;
    }

    /**
     * @see com.essiembre.eclipse.rbe.ui.editor.resources.ResourceFactory
     *         #getSourceEditors()
     */
    public SourceEditor[] getSourceEditors() {
        // Java 5 would be better here
        SourceEditor[] editors = new SourceEditor[sourceEditors.size()];
        int i = 0;
        for (Iterator iter = sourceEditors.values().iterator(); iter.hasNext();) {
            SourceEditor editor = (SourceEditor) iter.next();
            editors[i++] = editor;
        }
        return editors;
    }

    /**
     * @see com.essiembre.eclipse.rbe.ui.editor.resources.ResourceFactory
     *         #getPropertiesFileCreator()
     */
    public PropertiesFileCreator getPropertiesFileCreator() {
        return fileCreator;
    }

    protected static IFile[] getResources(IFile file)
        throws PartInitException {
        
        String regex = ResourceFactory.getPropertiesFileRegEx(file);
        IResource[] resources = null;
        try {
            resources = file.getParent().members();
        } catch (CoreException e) {
            throw new PartInitException(
                   "Can't initialize resource bundle editor.", e); //$NON-NLS-1$
        }
        Collection validResources = new ArrayList();
        for (int i = 0; i < resources.length; i++) {
            IResource resource = resources[i];
            String resourceName = resource.getName();
            if (resource instanceof IFile && resourceName.matches(regex)) {
                validResources.add(resource);
            }
        }
        return (IFile[]) validResources.toArray(new IFile[]{});
    }

    @Override
    public SourceEditor addResource(IResource resource, Locale locale) throws PartInitException {
        if (sourceEditors.containsKey(locale))
            throw new IllegalArgumentException("ResourceFactory already contains a resource for locale "+locale);
        SourceEditor editor = createEditor(site, resource, locale);
        sourceEditors.put(editor.getLocale(), editor);
        return editor;
    }
}
