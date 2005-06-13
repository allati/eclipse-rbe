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
package com.essiembre.eclipse.rbe.model.workbench;

import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Creates a properties file under an "NL" structure.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class NLPropertiesFileCreator extends PropertiesFileCreator {

    private String nlDir;
    private String fileName;
    
    /**
     * Constructor.
     */
    public NLPropertiesFileCreator(String nlDir, String fileName) {
        super();
        this.nlDir = nlDir;
        this.fileName = fileName;
    }

    /**
     * @throws CoreException
     * @see com.essiembre.eclipse.rbe.model.workbench.PropertiesFileCreator#buildFilePath(java.util.Locale)
     */
    protected IPath buildFilePath(Locale locale) throws CoreException {
        String folderPath = "";
        IWorkspaceRoot root = 
                ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(nlDir);
        IContainer container = (IContainer) resource;

        if (locale != null) {
            if (!locale.getLanguage().equals("")) {
                folderPath += locale.getLanguage() + "/"; 
                IFolder folder = container.getFolder(
                        new Path(folderPath));
                if (!folder.exists()) {
                    folder.create(true, true, null);
                }
            }
            if (!locale.getCountry().equals("")) {
                folderPath += locale.getCountry() + "/"; 
                IFolder folder = container.getFolder(
                        new Path(folderPath));
                if (!folder.exists()) {
                    folder.create(true, true, null);
                }
            }
            if (!locale.getVariant().equals("")) {
                folderPath += locale.getVariant() + "/"; 
                IFolder folder = container.getFolder(
                        new Path(folderPath));
                if (!folder.exists()) {
                    folder.create(true, true, null);
                }
            }
            folderPath = nlDir + "/" + folderPath;
        } else {
            folderPath = nlDir.substring(
                    0, nlDir.length() - "/nl".length())
                  + "/" + folderPath;
        }
        return new Path(folderPath + fileName);
    }


}
