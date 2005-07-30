package com.essiembre.eclipse.rbe.model.utils;

/**
 * Analyse the proximity of two objects (i.e., how similar they are) and return
 * a proximity level between zero and one.  The higher the return value is, 
 * the closer the two objects are to each other.  "One" does not need to mean 
 * "identical", but it has to be the closest match and analyser can 
 * potentially acheive.
 * @author Pascal Essiembre
 * @version $Author$ $Revision$ $Date$
 */
public interface ProximityAnalyzer {
    /**
     * Analyses two objects and return the proximity level.
     * @param obj1 first object to analyse
     * @param obj2 second object to analyse
     * @return proximity level
     */
    double analyse(Object obj1, Object obj2);
}
