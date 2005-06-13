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
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.essiembre.eclipse.rbe.workbench.PropertiesFileCreator;
import com.essiembre.eclipse.rbe.workbench.StandardPropertiesFileCreator;

/**
 * Responsible for creating resources related to a standard
 * directory structure.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class StandardResourceFactory extends StructuredResourceFactory {

    private final SourceEditor[] sourceEditors;
    private final PropertiesFileCreator fileCreator;
    private final String displayName;

    /**
     * Constructor.
     * 
     */
    public StandardResourceFactory(IEditorSite site, IFile file) 
             throws CoreException {
        super();
        List editors = new ArrayList();
        String bundleName = getBundleName(file);
        IResource[] resources = null;
        try {
            resources = file.getParent().members();
        } catch (CoreException e) {
            throw new PartInitException(
                    "Can't initialize resource bundle editor.", e);
        }
        for (int i = 0; i < resources.length; i++) {
            IResource resource = resources[i];
            String regex = "^(" + bundleName + ")"
                    + "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})"
                    + "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\."
                    + file.getFileExtension() + ")$";
            String resourceName = resource.getName();
            if (resource instanceof IFile && resourceName.matches(regex)) {
                
                // Build local title
                String localeText = resourceName.replaceFirst(regex, "$2");
                StringTokenizer tokens = 
                    new StringTokenizer(localeText, "_");
                List localeSections = new ArrayList();
                while (tokens.hasMoreTokens()) {
                    localeSections.add(tokens.nextToken());
                }
                Locale locale = null;
                switch (localeSections.size()) {
                case 1:
                    locale = new Locale((String) localeSections.get(0));
                    break;
                case 2:
                    locale = new Locale(
                            (String) localeSections.get(0),
                            (String) localeSections.get(1));
                    break;
                case 3:
                    locale = new Locale(
                            (String) localeSections.get(0),
                            (String) localeSections.get(1),
                            (String) localeSections.get(2));
                    break;
                default:
                    break;
                }
                SourceEditor sourceEditor = 
                        createEditor(site, resource, locale);
                if (sourceEditor != null) {
                    editors.add(sourceEditor);
                }
            }
        }
        
        sourceEditors = (SourceEditor[]) editors.toArray(new SourceEditor[] {});
        fileCreator = new StandardPropertiesFileCreator(
                file.getParent().getFullPath().toString(),
                bundleName,
                file.getFileExtension());
        displayName = bundleName + "[...]." + file.getFileExtension();
    }
    
    /**
     * @see com.essiembre.eclipse.rbe.ui.editor.resources.StructuredResourceFactory#getEditorDisplayName()
     */
    public String getEditorDisplayName() {
        return displayName;
    }

    /**
     * @see com.essiembre.eclipse.rbe.ui.editor.resources.StructuredResourceFactory#getSourceEditors()
     */
    public SourceEditor[] getSourceEditors() {
        return sourceEditors;
    }

    /**
     * @see com.essiembre.eclipse.rbe.ui.editor.resources.StructuredResourceFactory#getPropertiesFileCreator()
     */
    public PropertiesFileCreator getPropertiesFileCreator() {
        return fileCreator;
    }

    private String getBundleName(IFile file) {
        String name = file.getName();
        String regex = "^(.*?)"
                + "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})"
                + "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\."
                + file.getFileExtension() + ")$";
        return name.replaceFirst(regex, "$1");
    }

}
