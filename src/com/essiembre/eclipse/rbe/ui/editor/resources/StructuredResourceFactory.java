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

import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.essiembre.eclipse.rbe.workbench.PropertiesFileCreator;

/**
 * Responsible for creating resources related to a given file structure.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public abstract class StructuredResourceFactory {

    /** Class name of Properties file editor (Eclipse 3.1). */
    protected static final String PROPERTIES_EDITOR_CLASS_NAME = 
            "org.eclipse.jdt.internal.ui.propertiesfileeditor."
          + "PropertiesFileEditor";

    
    public abstract String getEditorDisplayName();
    public abstract SourceEditor[] getSourceEditors();
    public abstract PropertiesFileCreator getPropertiesFileCreator();
    
    protected SourceEditor createEditor(
            IEditorSite site, IResource resource, Locale locale)
            throws PartInitException {
        
        ITextEditor textEditor = null;
        if (resource != null && resource instanceof IFile) {
            IEditorInput newEditorInput = 
                    new FileEditorInput((IFile) resource);
            textEditor = null;
            try {
                // Use PropertiesFileEditor if available
                textEditor = (TextEditor) Class.forName(
                        PROPERTIES_EDITOR_CLASS_NAME).newInstance();
            } catch (Exception e) {
                // Use default editor otherwise
                textEditor = new TextEditor();
            }
            textEditor.init(site, newEditorInput);
        }
        if (textEditor != null) {
            return new SourceEditor(textEditor, locale, (IFile) resource);
        }
        return null;
    }

}
