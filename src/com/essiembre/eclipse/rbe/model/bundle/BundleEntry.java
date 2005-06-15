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
package com.essiembre.eclipse.rbe.model.bundle;

import java.util.Locale;

import com.essiembre.eclipse.rbe.model.Model;

/**
 * Represents an entry in a properties file.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public final class BundleEntry implements IBundleVisitable {

    /** Entry Locale. */
    private Locale locale;
    /** Entry unique identifier. */
    private String key;
    /** Entry comment. */
    private String comment;
    /** Whehter this entry is commented out or not. */
    private boolean commented;
    /** Entry value. */
    private String value;
    /** Associated bundle (parent). */
    private Bundle bundle;

    /**
     * Constructor.  Keys and value are <code>null</code> safe.
     * @param key unique identifier within bundle
     * @param value entry value
     * @param comment entry comment
     */
    public BundleEntry(
            String key, String value, String comment, boolean commented) {
        super();
        this.key = key;
        this.value = value;
        this.comment = comment;
        if (key == null) {
            this.key = "";
        }
        if (value == null) {
            this.value = "";
        }
        this.commented = commented;
    }

    
    /**
     * Constructor.  Keys and value are <code>null</code> safe.
     * @param key unique identifier within bundle
     * @param value entry value
     * @param comment entry comment
     */
    public BundleEntry(String key, String value, String comment) {
        this(key, value, comment, false);
    }

    /**
     * @see Model#accept(ModelVisitorI, Object)
     */
    public void accept(IBundleVisitor visitor, Object passAlongArgument) {
        visitor.visitBundleEntry(this, passAlongArgument);
        visitor.visitBundle(bundle, passAlongArgument);
        if (bundle != null) {
            visitor.visitBundleGroup(
                    bundle.getBundleGroup(), passAlongArgument);
        }
    }

    /**
     * Gets the "comment" attribute.
     * @return Returns the comment.
     */
    public String getComment() {
        return comment;
    }
    /**
     * Gets the "key" attribute.
     * @return Returns the key.
     */
    public String getKey() {
        return key;
    }
        
    /**
     * Gets the "value" attribute.
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the "commented" attribute.
     * @return <code>true</code> if this entry is commented out.
     */
    public boolean isCommented() {
        return commented;
    }

    /**
     * Gets associated bundle (parent).
     * @return parent bundle
     */
    public Bundle getBundle() {
        return bundle;
    }
    /**
     * Sets associated bundle (parent).
     * @param bundle the parent bundle
     */
    protected void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
    
    /**
     * Gets associated locale.
     * @return locale
     */
    public Locale getLocale() {
        return locale;
    }
    /**
     * Gets associated locale.
     * @param locale the entry locale
     */
    protected void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof BundleEntry)) {
            return false;
        }
        BundleEntry entry = (BundleEntry) obj;
        return key.equals(entry.getKey())
                && commented == entry.isCommented()
                && value.equals(entry.getValue())
                && (comment == null && entry.getComment() == null
                        || comment != null && comment.equals(
                                entry.getComment()));
    }
}
