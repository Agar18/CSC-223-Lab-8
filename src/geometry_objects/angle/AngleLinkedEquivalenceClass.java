package geometry_objects.angle;

import geometry_objects.angle.comparators.AngleStructureComparator;
import utilities.eq_classes.LinkedEquivalenceClass;

/**
 * This implementation requires greater knowledge of the implementing Comparator.
 * 
 * According to our specifications for the AngleStructureComparator, we have
 * the following cases:
 *
 *    Consider Angles A and B
 *    * Integer.MAX_VALUE -- indicates that A and B are completely incomparable
                             STRUCTURALLY (have different measure, don't share sides, etc. )
 *    * 0 -- The result is indeterminate:
 *           A and B are structurally the same, but it is not clear one is structurally
 *           smaller (or larger) than another
 *    * 1 -- A > B structurally
 *    * -1 -- A < B structurally
 *    
 *    We want the 'smallest' angle structurally to be the canonical element of an
 *    equivalence class.
 * 
 * @author Alex and Khalid
 */
public class AngleLinkedEquivalenceClass extends LinkedEquivalenceClass<Angle>
{
    public AngleLinkedEquivalenceClass()
    {
    	super(new AngleStructureComparator());
    }
    
    @Override
    public boolean belongs(Angle angle)
    { 
    	// Check to make sure the angle is not already in the list.
    	if(contains(angle)) return false;
    		
    	// Check if the angle is structurally comparable to the canonical
    	return _comparator.compare(angle, _canonical) != AngleStructureComparator.STRUCTURALLY_INCOMPARABLE;
    }
    
    /**
	 *	Adds an element to the equivalence class if it belongs. 
	 *	Returns whether is was successfully added.
	 */
    @Override
    public boolean add(Angle angle) {
        if (isEmpty()) {
            _canonical = angle;
            return true;
        } else if (belongs(angle)) {
            if (_comparator.compare(angle, _canonical) < 0) {
                // Update the canonical to be the structurally smaller angle.
                demoteAndSetCanonical(angle);
            } else {
                _rest.addToFront(angle);
            }
            return true;
        } else {
            return false;
        }
    }
}



