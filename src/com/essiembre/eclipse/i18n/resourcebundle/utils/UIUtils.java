package com.essiembre.eclipse.i18n.resourcebundle.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * Utility methods related to application UI.
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public final class UIUtils {

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
        FontData[] fontData = control.getFont().getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(fontData[i].getHeight() + relSize);
            fontData[i].setStyle(SWT.BOLD);
        }
        return new Font(control.getDisplay(), fontData);
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

}
