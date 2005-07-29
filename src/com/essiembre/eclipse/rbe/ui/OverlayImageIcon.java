package com.essiembre.eclipse.rbe.ui;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * 
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public class OverlayImageIcon extends CompositeImageDescriptor {

    public static final int TOP_LEFT = 0;
    public static final int TOP_RIGHT = 1;
    public static final int BOTTOM_LEFT = 2;
    public static final int BOTTOM_RIGHT = 3;

    private Image baseImage;
    private Image overlayImage;
    private int location;
    private Point imgSize;
    
    
    /**
     * Constructor.
     */
    public OverlayImageIcon(Image baseImage, Image overlayImage, int location) {
        super();
        this.baseImage = baseImage;
        this.overlayImage = overlayImage;
        this.location = location;
        this.imgSize = new Point(
                baseImage.getImageData().width, 
                baseImage.getImageData().height);
    }

    /**
     * @see org.eclipse.jface.resource.CompositeImageDescriptor
     *         #drawCompositeImage(int, int)
     */
    protected void drawCompositeImage(int width, int height) {
        // Draw the base image
        drawImage(baseImage.getImageData(), 0, 0); 
        ImageData imageData = overlayImage.getImageData();
        switch(location) {
            // Draw on the top left corner
            case TOP_LEFT:
                drawImage(imageData, 0, 0);
                break;
            
            // Draw on top right corner  
            case TOP_RIGHT:
                drawImage(imageData, imgSize.x - imageData.width, 0);
                break;
            
            // Draw on bottom left  
            case BOTTOM_LEFT:
                drawImage(imageData, 0, imgSize.y - imageData.height);
                break;
            
            // Draw on bottom right corner  
            case BOTTOM_RIGHT:
                drawImage(imageData, imgSize.x - imageData.width,
                        imgSize.y - imageData.height);
                break;
            
        }
    }

    /**
     * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
     */
    protected Point getSize() {
        return imgSize;
    }

}
