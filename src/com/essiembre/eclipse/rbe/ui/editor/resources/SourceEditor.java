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
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Wrapper around a properties file text editor providing extra founctionality.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public class SourceEditor {

    private final Locale locale;
    private final IFile file;
    private final ITextEditor editor;
    private String contentCache;
    
    /**
     * Constructor.
     * @param editor text editor
     * @param locale a locale
     * @param file properties file
     */
    public SourceEditor(ITextEditor editor, Locale locale, IFile file) {
        super();
        this.editor = editor;
        this.locale = locale;
        this.file = file;
        contentCache = getContent();
    }

    /**
     * Gets the locale associated with this source editor.
     * @return locale
     */
    public Locale getLocale() {
        return locale;
    }
    /**
     * Gets the file associated with this source editor.
     * @return properties file
     */
    public IFile getFile() {
        return file;
    }
    /**
     * Gets the text editor associated with this source editor.
     * @return text editor
     */
    public ITextEditor getEditor() {
        return editor;
    }
    
    /**
     * Checks whether the underlying file content differs from the cached
     * source editor content.
     * @return <code>true</code> if dirty
     */
    public boolean isCacheDirty() {
        return !getContent().equals(contentCache);
    }
    
    /**
     * Resets the source editor cache.
     */
    public void resetCache() {
        contentCache = getContent();
    }
    
    /**
     * Gets the content of this source editor.
     * @return content
     */
    public String getContent() {
        return editor.getDocumentProvider().getDocument(
                editor.getEditorInput()).get();
    }
    
    /**
     * Sets the content of this source editor (replacing existing content).
     * @param content new content
     */
    public void setContent(String content) {
        editor.getDocumentProvider().getDocument(
                editor.getEditorInput()).set(content);
        contentCache = content;
    }
    
    /**
     * Checks whether this source editor is read-only.
     * @return <code>true</code> if read-only.
     */
    public boolean isReadOnly() {
        return ((TextEditor) editor).isEditorInputReadOnly();
    }

    //TODO add save and revertToSave here (spawning a thread)
}
