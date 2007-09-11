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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.essiembre.eclipse.rbe.model.workbench.files.NLPropertiesFileCreator;
import com.essiembre.eclipse.rbe.model.workbench.files.PropertiesFileCreator;

/**
 * Responsible for creating resources related to an Eclipse "NL"
 * directory structure.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class NLResourceFactory extends ResourceFactory {

    private final Map sourceEditors;
    private final PropertiesFileCreator fileCreator;
    private final String displayName;
    private final IEditorSite site;
    
    /**
     * Constructor.
     * @param site editor site
     * @param file file used to open all related files
     * @throws CoreException problem creating factory
     */
    protected NLResourceFactory(IEditorSite site, IFile file) 
            throws CoreException {
        super();
        this.site = site;
        sourceEditors = new HashMap();
        String filename = file.getName();
        
        // Locate "nl" directory (if any)
        IContainer container = file.getParent();
        IResource nlDir = null;
        while (container != null 
                && (nlDir == null || !(nlDir instanceof Folder))) {
            nlDir = container.findMember("nl"); //$NON-NLS-1$
            container = container.getParent();
        }
        
        // Load properties files in "nl" structure.
        if (nlDir != null && nlDir instanceof Folder) {
            
            // Load root file, if exists.
            IResource resource = nlDir.getParent().findMember(filename);
            SourceEditor sourceEditor = createEditor(site, resource, null);
            if (sourceEditor != null) {
                sourceEditors.put(sourceEditor.getLocale(), sourceEditor);
            }
            
            // Load "language" matching files in "nl" tree.
            IResource[] langResources = ((Folder) nlDir).members();
            for (int i = 0; i < langResources.length; i++) {
                String language = null;
                IResource langResource = langResources[i];
                if (langResource instanceof IFolder) {
                    IFolder langFolder = (IFolder) langResource;
                    language = langFolder.getName();
                    sourceEditor = createEditor(
                            site, 
                            langFolder.findMember(filename),
                            new Locale(language));
                    if (sourceEditor != null) {
                    	sourceEditors.put(sourceEditor.getLocale(), sourceEditor);
                    }

                    // Load "country" matching files in "nl" tree.
                    String country = null;
                    IResource[] cntryResources = langFolder.members();
                    for (int j = 0; j < cntryResources.length; j++) {
                        IResource cntryResource = cntryResources[j];
                        if (cntryResource instanceof IFolder) {
                            IFolder cntryFolder = (IFolder) cntryResource;
                            country = cntryFolder.getName();
                            sourceEditor = createEditor(
                                    site, 
                                    cntryFolder.findMember(filename),
                                    new Locale(language, country));
                            if (sourceEditor != null) {
                            	sourceEditors.put(sourceEditor.getLocale(), sourceEditor);
                            }
                            
                            // Load "variant" matching files in "nl" tree.
                            IResource[] vrntResources = cntryFolder.members();
                            for (int k = 0; k < vrntResources.length; k++) {
                                IResource vrntResource = vrntResources[k];
                                if (vrntResource instanceof IFolder) {
                                    IFolder vrntFolder = (IFolder) vrntResource;
                                    sourceEditor = createEditor(
                                            site, 
                                            vrntFolder.findMember(filename),
                                            new Locale(language, country,
                                                    vrntFolder.getName()));
                                    if (sourceEditor != null) {
                                    	sourceEditors.put(sourceEditor.getLocale(), sourceEditor);
                                    }
                                }
                            }
                        }
                    }                        
                }
            }            
            fileCreator = 
                new NLPropertiesFileCreator(nlDir.toString(), filename);
            displayName = filename;
        } else {
            fileCreator = null;
            displayName = null;
        }
    }

    /**
     * @see com.essiembre.eclipse.rbe.ui.editor.resources
     *         .ResourceFactory#getEditorDisplayName()
     */
    public String getEditorDisplayName() {
        return displayName;
    }

    /**
     * @see com.essiembre.eclipse.rbe.ui.editor.resources
     *         .ResourceFactory#getSourceEditors()
     */
    public SourceEditor[] getSourceEditors() {
        return (SourceEditor[]) sourceEditors.values().toArray();
    }

    /**
     * @see com.essiembre.eclipse.rbe.ui.editor.resources
     *         .ResourceFactory#getPropertiesFileCreator()
     */
    public PropertiesFileCreator getPropertiesFileCreator() {
        return fileCreator;
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