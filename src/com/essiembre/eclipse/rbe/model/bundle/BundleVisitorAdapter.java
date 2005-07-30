package com.essiembre.eclipse.rbe.model.bundle;

/**
 * Convenience implementation of <code>IBundleVisitor</code> allowing to 
 * override only required methods.
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public class BundleVisitorAdapter implements IBundleVisitor {

    /**
     * Constructor.
     */
    public BundleVisitorAdapter() {
        super();
    }

    /**
     * @see com.essiembre.eclipse.rbe.model.bundle.IBundleVisitor
     *         #visitBundleGroup(
     *                 com.essiembre.eclipse.rbe.model.bundle.BundleGroup, 
     *                 java.lang.Object)
     */
    public void visitBundleGroup(BundleGroup group, Object passAlongArgument) {
    }

    /**
     * @see com.essiembre.eclipse.rbe.model.bundle.IBundleVisitor#visitBundle(
     *         com.essiembre.eclipse.rbe.model.bundle.Bundle, java.lang.Object)
     */
    public void visitBundle(Bundle bundle, Object passAlongArgument) {
    }

    /**
     * @see com.essiembre.eclipse.rbe.model.bundle.IBundleVisitor
     *         #visitBundleEntry(
     *                 com.essiembre.eclipse.rbe.model.bundle.BundleEntry,
     *                 java.lang.Object)
     */
    public void visitBundleEntry(BundleEntry entry, Object passAlongArgument) {
    }
}
