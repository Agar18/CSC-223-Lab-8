package geometry_objects.angle;

import java.util.ArrayList;
import java.util.Comparator;

import geometry_objects.angle.comparators.AngleStructureComparator;
import utilities.eq_classes.EquivalenceClasses;
import utilities.eq_classes.LinkedEquivalenceClass;

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
public class AngleEquivalenceClasses extends EquivalenceClasses<Angle> {

	public AngleEquivalenceClasses(Comparator<Angle> comparator) {
		super(new AngleStructureComparator());
	}

	@Override
	public boolean add(Angle element) {

		int eqIndex = indexOfClass(element);
		
		if(eqIndex != -1)
		{
			return _classes.get(eqIndex).add(element);
		}
		
		AngleLinkedEquivalenceClass list = new AngleLinkedEquivalenceClass(_comparator);
		
		_classes.add(list);
		
		return list.add(element);
		


	}
}

