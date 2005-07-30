package com.essiembre.eclipse.rbe.model.tree;

/**
 * Convenience implementation of <code>IKeyTreeVisitor</code> allowing 
 * to override only required methods.
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public class KeyTreeVisitorAdapter implements IKeyTreeVisitor {

    /**
     * Constructor.
     */
    public KeyTreeVisitorAdapter() {
        super();
    }

    /**
     * @see com.essiembre.eclipse.rbe.model.tree.IKeyTreeVisitor#visitKeyTree(
     *         com.essiembre.eclipse.rbe.model.tree.KeyTree, java.lang.Object)
     */
    public void visitKeyTree(KeyTree keyTree, Object passAlongArgument) {
    }

    /**
     * @see com.essiembre.eclipse.rbe.model.tree.IKeyTreeVisitor
     *         #visitKeyTreeItem(
     *                 com.essiembre.eclipse.rbe.model.tree.KeyTreeItem,
     *                 java.lang.Object)
     */
    public void visitKeyTreeItem(KeyTreeItem item, Object passAlongArgument) {
    }
}
