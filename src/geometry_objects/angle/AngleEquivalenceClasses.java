package geometry_objects.angle;

import geometry_objects.angle.comparators.AngleStructureComparator;
import utilities.eq_classes.EquivalenceClasses;

/**
 * Given the figure below:
 * 
 *    A-------B----C-----------D
 *     \
 *      \
 *       \
 *        E
 *         \
 *          \
 *           F
 * 
 * Equivalence classes structure we want:
 * 
 *   canonical = BAE
 *   rest = BAF, CAE, DAE, CAF, DAF
 */
public class AngleEquivalenceClasses extends EquivalenceClasses<Angle>
{
	public AngleEquivalenceClasses()
	{
		super(new AngleStructureComparator());
	}
	
	/**
	 *	Add a given angle to an existing class if it fits in one,
	 *  otherwise create a new AngleLinkedEquivalenceClass for the element.
	 */
	
	@Override
	public boolean add(Angle angle) 
	{
		int eqIndex = indexOfClass(angle);
		if(eqIndex != -1) return _classes.get(eqIndex).add(angle);
		
		AngleLinkedEquivalenceClass list = new AngleLinkedEquivalenceClass();
		_classes.add(list);
		
		return list.add(angle);
	}
}

