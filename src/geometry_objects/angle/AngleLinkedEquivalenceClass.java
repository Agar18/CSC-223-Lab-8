package geometry_objects.angle;

import java.util.Comparator;

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
 * @author Khalid
 */
public class AngleLinkedEquivalenceClass extends LinkedEquivalenceClass<Angle>
{
	public AngleLinkedEquivalenceClass()
    {
    	super(new AngleStructureComparator());
    }
	
	@Override
	 public boolean add(Angle angle) {
	     boolean added = super.add(angle);
	     System.out.println("added: " +  added);
	     System.out.println("fir canonical: " + _canonical);
	     
	     if (_comparator.compare(angle,  _canonical) < 0) {
	    	 demoteAndSetCanonical(angle);
	    	 System.out.println("sec canonical: " + _canonical);
	     }
	    
	     return added;
	 }
	 
	 
   
  
    
    
}

