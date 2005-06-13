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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorSite;

import com.essiembre.eclipse.rbe.model.DeltaEvent;
import com.essiembre.eclipse.rbe.model.IDeltaListener;
import com.essiembre.eclipse.rbe.model.bundle.Bundle;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.bundle.BundleUtils;
import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.updater.GroupedKeyTreeUpdater;
import com.essiembre.eclipse.rbe.ui.preferences.RBEPreferences;

/**
 * Mediator holding instances of commonly used items, dealing with 
 * important interactions within themselves.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class ResourceManager {

    private StructuredResourceFactory resourcesFactory;
    private final BundleGroup bundleGroup;
    private final KeyTree keyTree;
    private final Map sourceEditors = new HashMap(); //key=Locale;value=SourceE.
    private final Collection locales = new ArrayList();
    
    /**
     * Constructor.
     * @throws CoreException
     */
    public ResourceManager(final IEditorSite site, final IFile file)
            throws CoreException {
        super();
        if (RBEPreferences.getSupportNL()) {
            StructuredResourceFactory nlFactory = new NLResourceFactory(site, file);
            if (nlFactory.getSourceEditors().length > 0) {
                resourcesFactory = nlFactory;
            }
        }
        if (resourcesFactory == null) {
            resourcesFactory = new StandardResourceFactory(site, file);
        }
        
        bundleGroup = new BundleGroup();
        SourceEditor[] editors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < editors.length; i++) {
            SourceEditor sourceEditor = editors[i];
            Locale locale = sourceEditor.getLocale();
            sourceEditors.put(locale, sourceEditor);
            locales.add(locale);
            bundleGroup.addBundle(locale, BundleUtils.parseProperties(
                    sourceEditor.getContent())); 
        }
        bundleGroup.addListener(new IDeltaListener() {
            public void add(DeltaEvent event) {}    // do nothing
            public void remove(DeltaEvent event) {} // do nothing
            public void modify(DeltaEvent event) {
                final Bundle bundle = (Bundle) event.receiver();
                final SourceEditor editor = 
                        (SourceEditor) sourceEditors.get(bundle.getLocale());
                site.getShell().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        editor.setContent(
                                BundleUtils.generateProperties(bundle));
                    }
                });
            }
        });
        
        this.keyTree = new KeyTree(bundleGroup, new GroupedKeyTreeUpdater(
                    RBEPreferences.getKeyGroupSeparator()));
    }

    public BundleGroup getBundleGroup() {
        return bundleGroup;
    }
    public Collection getLocales() {
        return locales;
    }
    public KeyTree getKeyTree() {
        return keyTree;
    }
    public SourceEditor[] getSourceEditors() {
        return resourcesFactory.getSourceEditors();
    }
    public void save(IProgressMonitor monitor) {
        SourceEditor[] sourceEditors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < sourceEditors.length; i++) {
            ((SourceEditor) sourceEditors[i]).getEditor().doSave(monitor);
        }
    }
        
    
    public String getEditorDisplayName() {
        return resourcesFactory.getEditorDisplayName();
    }

    public boolean isResource(IFile file) {
        SourceEditor[] sourceEditors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < sourceEditors.length; i++) {
            if (((SourceEditor) sourceEditors[i]).getFile().equals(file)) {
                return true;
            }
        }
        return false;
    }
    
    public IFile createPropertiesFile(Locale locale) 
            throws CoreException, IOException {
        return resourcesFactory.getPropertiesFileCreator().createPropertiesFile(
                locale);
    }
    
    public SourceEditor getSourceEditor(Locale locale) {
        return (SourceEditor) sourceEditors.get(locale);
    }
    
    public void reloadProperties() {
        SourceEditor[] sourceEditors = resourcesFactory.getSourceEditors();
        for (int i = 0; i < sourceEditors.length; i++) {
            SourceEditor editor = (SourceEditor) sourceEditors[i];
            if (editor.isCacheDirty()) {
                bundleGroup.addBundle(
                        editor.getLocale(),
                        BundleUtils.parseProperties(editor.getContent()));
                editor.resetCache();
            }
        }
    }

}