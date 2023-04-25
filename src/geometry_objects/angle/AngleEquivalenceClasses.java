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
		// Find the index of the equivalence class to which the angle belongs.
		int eqIndex = indexOfClass(angle);

		// If the angle belongs to an existing equivalence class, add it to that class and return true.
		if(eqIndex != -1) return _classes.get(eqIndex).add(angle);

		// If the angle does not belong to an existing equivalence class, create a new class and add it to the list of classes.
		AngleLinkedEquivalenceClass list = new AngleLinkedEquivalenceClass();
		_classes.add(list);

		// Add the angle to the new equivalence class and return true.
		return list.add(angle);
	}
}

