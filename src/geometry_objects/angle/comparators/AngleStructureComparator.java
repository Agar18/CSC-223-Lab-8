package geometry_objects.angle.comparators;
import java.util.Set;


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import geometry_objects.Segment;
import geometry_objects.angle.Angle;
import geometry_objects.points.Point;
import utilities.math.MathUtilities;
import utilities.math.analytic_geometry.GeometryUtilities;

/**
 * Write a succinct, meaningful description of the class here. You should avoid wordiness    
 * and redundancy. If necessary, additional paragraphs should be preceded by <p>,
 * the html tag for a new paragraph.
 *
 * <p>Bugs: (a list of bugs and / or other problems)
 *
 * @author <your name>
 * @date   <date of completion>
 **/


public class AngleStructureComparator implements Comparator<Angle>
{
	public static final int STRUCTURALLY_INCOMPARABLE = Integer.MAX_VALUE;
	
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
	 * What we care about is the fact that angle BAE is the smallest angle (structurally)
	 * and DAF is the largest angle (structurally). 
	 * 
	 * If one angle X has both rays (segments) that are subsegments of an angle Y, then X < Y.
	 * 
	 * If only one segment of an angle is a subsegment, then no conclusion can be made.
	 * 
	 * So:
	 *     BAE < CAE
   	 *     BAE < DAF
   	 *     CAF < DAF

   	 *     CAE inconclusive BAF
	 * 
	 * @param left -- an angle
	 * @param right -- an angle
	 * @return -- according to the algorithm above:
 	 *              Integer.MAX_VALUE will refer to our error result
 	 *              0 indicates an inconclusive result
	 *              -1 for less than
	 *              1 for greater than
	 */
	
	
	
	@Override
	public int compare(Angle left, Angle right) {
	    // check if left and right are the same angle
	    if (left.equals(right)) {
	        return 0;
	    }
	    
	    if (right == null) {
	        return -STRUCTURALLY_INCOMPARABLE;
	    }
	    
	    // get the sets of segments that define each angle
	    Set<Segment> leftSegments = new HashSet<>(Arrays.asList(left.getRay1(), left.getRay2()));
	    
	    Set<Segment> rightSegments = new HashSet<>(Arrays.asList(right.getRay1(), right.getRay2()));
	    
	    // check if left is a subset of right
	    if (rightSegments.containsAll(leftSegments)) {
	        return -1;
	    }
	    
	    // check if right is a subset of left
	    if (leftSegments.containsAll(rightSegments)) {
	        return 1;
	    }
	    
	    // check if there is an inconclusive relationship between the two angles
	    Set<Segment> intersection = new HashSet<>(leftSegments);
	    intersection.retainAll(rightSegments);
	    if (intersection.isEmpty()) {
	        return 0;
	    }
	    
	    // inconclusive relationship
	    return STRUCTURALLY_INCOMPARABLE;
	}
	
}
