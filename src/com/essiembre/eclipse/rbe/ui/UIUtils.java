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
package com.essiembre.eclipse.rbe.ui;

import java.util.Locale;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * Utility methods related to application UI.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public final class UIUtils {

    /** Image registry. */
    private static final ImageRegistry imageRegistry = new ImageRegistry();
    
    /**
     * Constructor.
     */
    private UIUtils() {
        super();
    }

    /**
     * Creates a font by altering the font associated with the given control
     * and applying the provided style (size is unaffected).
     * @param control control we base our font data on
     * @param style   style to apply to the new font
     * @return newly created font
     */
    public static Font createFont(Control control, int style) {
        //TODO consider dropping in favor of control-less version?
        return createFont(control, style, 0);
    }

    
    /**
     * Creates a font by altering the font associated with the given control
     * and applying the provided style and relative size.
     * @param control control we base our font data on
     * @param style   style to apply to the new font
     * @param relSize size to add or remove from the control size
     * @return newly created font
     */
    public static Font createFont(Control control, int style, int relSize) {
        //TODO consider dropping in favor of control-less version?
        FontData[] fontData = control.getFont().getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(fontData[i].getHeight() + relSize);
            fontData[i].setStyle(style);
        }
        return new Font(control.getDisplay(), fontData);
    }

    /**
     * Creates a font by altering the system font
     * and applying the provided style and relative size.
     * @param style   style to apply to the new font
     * @return newly created font
     */
    public static Font createFont(int style) {
        return createFont(style, 0);
    }
    
    /**
     * Creates a font by altering the system font
     * and applying the provided style and relative size.
     * @param style   style to apply to the new font
     * @param relSize size to add or remove from the control size
     * @return newly created font
     */
    public static Font createFont(int style, int relSize) {
        Display display = RBEPlugin.getDefault().getWorkbench().getDisplay();
        FontData[] fontData = display.getSystemFont().getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(fontData[i].getHeight() + relSize);
            fontData[i].setStyle(style);
        }
        return new Font(display, fontData);
    }

    /**
     * Gets a system color.
     * @param colorId SWT constant
     * @return system color
     */
    public static Color getSystemColor(int colorId) {
        return RBEPlugin.getDefault().getWorkbench()
                .getDisplay().getSystemColor(colorId);
    }
    
    /**
     * Gets the approximate width required to display a given number of
     * characters in a control.
     * @param control the control on which to get width
     * @param widthInChars the number of chars
     * @return
     */    
    public static int getWidthInChars(Control control, int widthInChars) {
        GC gc = new GC(control);
        Point extent = gc.textExtent("W");//$NON-NLS-1$
        gc.dispose();
        return widthInChars * extent.x;
    }
    
    /**
     * Shows an error dialog based on the supplied arguments.
     * @param shell the shell
     * @param exception the core exception
     * @param msgKey key to the plugin message text
     */
    public static void showErrorDialog(
            Shell shell, CoreException exception, String msgKey) {
        exception.printStackTrace();
        ErrorDialog.openError(
                shell,
                RBEPlugin.getString(msgKey),
                exception.getLocalizedMessage(),
                exception.getStatus());
    }
    
    /**
     * Shows an error dialog based on the supplied arguments.
     * @param shell the shell
     * @param exception the core exception
     * @param msgKey key to the plugin message text
     */
    public static void showErrorDialog(
            Shell shell, Exception exception, String msgKey) {
        exception.printStackTrace();
        IStatus status = new Status(
                IStatus.ERROR, 
                RBEPlugin.ID,
                0, 
                RBEPlugin.getString(msgKey) + " "
                        + RBEPlugin.getString("error.seeLogs"),
                exception);
        ErrorDialog.openError(
                shell,
                RBEPlugin.getString(msgKey),
                exception.getLocalizedMessage(),
                status);
    }
    

    public static String getDisplayName(Locale locale) {
        if (locale == null) {
            return RBEPlugin.getString("editor.default");
        }
        return locale.getDisplayName();
    }
    
    
    public static Image getImage(String imageName) {
        Image image = imageRegistry.get(imageName);
        if (image == null) {
            image = RBEPlugin.getImageDescriptor(imageName).createImage();
            imageRegistry.put(imageName, image);
        }
        return image;
    }
}
