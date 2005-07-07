package com.essiembre.eclipse.rbe.ui.editor.i18n.tree.decorators;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;
import com.essiembre.eclipse.rbe.model.tree.visitors.IsMissingValueVisitor;
import com.essiembre.eclipse.rbe.ui.RBEPlugin;
import com.essiembre.eclipse.rbe.ui.UIUtils;

/**
 * 
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public class MissingValueDecorator 
        extends LabelProvider implements ILabelDecorator {

    /** Cache for all images used by this decorator. */
    private Map imageCache = new HashMap(3);
    
    /**
     * Constructor.
     * 
     */
    public MissingValueDecorator() {
        super();
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
     */
    public Image decorateImage(Image baseImage, Object element) {
        KeyTreeItem treeItem = ((KeyTreeItem) element);
        ImageDescriptor descriptor = null;
        
        IsMissingValueVisitor misValVisitor = new IsMissingValueVisitor();
        treeItem.accept(misValVisitor, null);
        if (misValVisitor.isMissingValue()) {
            descriptor = RBEPlugin.getImageDescriptor("warning.gif");
        } else if (misValVisitor.isMissingChildValueOnly()) {
            descriptor = RBEPlugin.getImageDescriptor("warningGrey.gif");
        }
        if (descriptor != null) {
            Image overlayImage = UIUtils.getCacheImage(imageCache, descriptor);
            return UIUtils.getCacheImage(imageCache, new OverlayImageIcon(
                   baseImage, overlayImage, OverlayImageIcon.BOTTOM_RIGHT));
        }
        return baseImage;
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
     */
    public String decorateText(String text, Object element) {
        return text;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        UIUtils.disposeCacheImages(imageCache);
    }

}
