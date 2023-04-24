package geometry_objects.angle;

import java.util.Comparator;


import geometry_objects.angle.comparators.AngleStructureComparator;

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

	public AngleEquivalenceClasses(Comparator<Angle> comparator) {
		super(comparator);
	}


	@Override
    public boolean add(Angle element) {
        int eqIndex = indexOfClass(element);
 
        if (eqIndex != -1) {
            return _classes.get(eqIndex).add(element);
            
        }
        System.out.println("new class: ");
        AngleLinkedEquivalenceClass list = new AngleLinkedEquivalenceClass((AngleStructureComparator) _comparator);
        
        _classes.add(list);
        return list.add(element);
    }
	
	
}