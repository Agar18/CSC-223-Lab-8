package preprocessor;
import java.util.Queue;
import java.util.LinkedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import preprocessor.delegates.ImplicitPointPreprocessor;
import geometry_objects.Segment;

public class Preprocessor
{
	// The explicit points provided to us by the user.
	// This database will also be modified to include the implicit
	// points (i.e., all points in the figure).
	protected PointDatabase _pointDatabase;

	// Minimal ('Base') segments provided by the user
	protected Set<Segment> _givenSegments;

	// The set of implicitly defined points caused by segments
	// at implicit points.
	protected Set<Point> _implicitPoints;

	// The set of implicitly defined segments resulting from implicit points.
	protected Set<Segment> _implicitSegments;

	// Given all explicit and implicit points, we have a set of
	// segments that contain no other subsegments; these are minimal ('base') segments
	// That is, minimal segments uniquely define the figure.
	protected Set<Segment> _allMinimalSegments;

	// A collection of non-basic segments
	protected Set<Segment> _nonMinimalSegments;

	// A collection of all possible segments: maximal, minimal, and everything in between
	// For lookup capability, we use a map; each <key, value> has the same segment object
	// That is, key == value. 
	protected Map<Segment, Segment> _segmentDatabase;
	public Map<Segment, Segment> getAllSegments() { return _segmentDatabase; }

	public Preprocessor(PointDatabase points, Set<Segment> segments)
	{
		_pointDatabase  = points;
		_givenSegments = segments;

		_segmentDatabase = new HashMap<Segment, Segment>();

		analyze();
	}

	//
	// Construct all segments inductively from the base segments
	//
	public Set<Segment> constructAllNonMinimalSegments(Set<Segment> allMinimalSegments) {
	    Set<Segment> allNonMinimalSegments = new HashSet<>();
	    Queue<Segment> queue = new LinkedList<>();
	    
	    // Iterate over minimal segments
	    for (Segment seg1 : allMinimalSegments) {
	        // Check for shared vertices and coinciding segments with other minimal segments
	        for (Segment seg2 : allMinimalSegments) {
	            if (seg1 != seg2) { // Skip same segment
	                Point pt = seg1.sharedVertex(seg2);
	                if (pt != null && seg1.coincideWithoutOverlap(seg2)) {
	                    Point seg1Pt = seg1.other(pt);
	                    Point seg2Pt = seg2.other(pt);
	                    
	                    Segment newSegment = new Segment(seg1Pt, seg2Pt);
	                    // Check if the new segment is already in the set of all non-minimal segments
	                    if (!allNonMinimalSegments.contains(newSegment)) {
	                        allNonMinimalSegments.add(newSegment);
	                        queue.offer(newSegment); // Add to queue for further processing
	                    }
	                }
	            }
	        }
	        
	        //create different method
	        // Process queue for additional segments
	        while (!queue.isEmpty()) {
	            Segment segment = queue.poll();
	            for (Segment seg2 : allMinimalSegments) {
	                if (!segment.HasSubSegment(seg2)) { // Skip segments already in the current segment
	                    Point pt = segment.sharedVertex(seg2);
	                    if (pt != null && segment.coincideWithoutOverlap(seg2)) {
	                        Point seg1Pt = segment.other(pt);
	                        Point seg2Pt = seg2.other(pt);
	                        
	                        Segment newSegment = new Segment(seg1Pt, seg2Pt);
	                        // Check if the new segment is already in the set of all non-minimal segments
	                        if (!allNonMinimalSegments.contains(newSegment)) {
	                            allNonMinimalSegments.add(newSegment);
	                            queue.offer(newSegment);
	                        }
	                    }
	                }
	            }
	        }
	    }
	    
	    return allNonMinimalSegments;
	}




	//
	// Combine the given minimal segments and implicit segments into a true set of minimal segments
	//     *givenSegments may not be minimal
	//     * implicitSegmen
	//

	public Set<Segment> identifyAllMinimalSegments(Set<Point> implicitPoints, Set<Segment> givenSegments, Set<Segment> implicitSegments) {
		{
			Set<Segment> allMinimalSegments = new HashSet<Segment>();

			allMinimalSegments.addAll(implicitSegments);
			allMinimalSegments.addAll(givenSegments);


			// remove a seg from givenSeg if an implicit pt is on it (not end pt)
			for (Segment seg : givenSegments)
			{
				for (Point pt : implicitPoints)
				{
					if (seg.pointLiesBetweenEndpoints(pt)) allMinimalSegments.remove(seg);
				}	
			}

			return allMinimalSegments;
		}
	}



	// Compute all implicit segments attributed to implicit points
	public Set<Segment> computeImplicitBaseSegments(Set<Point> implicitPoints) {
	    Set<Segment> implicitBaseSegments = new LinkedHashSet<Segment>(); // Set to store the implicit base segments, using LinkedHashSet to preserve order

	    for (Segment seg : _givenSegments) { // Loop through the given segments
	        SortedSet<Point> ptList = new TreeSet<Point>(); // Sorted set to store the points on the segment

	        Point left = seg.getPoint1(); // Get the left endpoint of the segment
	        Point right = seg.getPoint2(); // Get the right endpoint of the segment

	        ptList.add(left); // Add the left endpoint to the set
	        ptList.addAll(seg.collectOrderedPointsOnSegment(implicitPoints)); // Add all the points on the segment from the given set of implicit points
	        ptList.add(right); // Add the right endpoint to the set

	        Point[] ptArr = ptList.toArray(new Point[ptList.size()]); // Convert the sorted set of points to an array for iteration

	        for (int x = 0; x < ptArr.length - 1; x++) { // Loop through the points array to create segments
	            Segment newSeg = new Segment(ptArr[x], ptArr[x+1]); // Create a new segment from the current and next points
	            if(!_givenSegments.contains(newSeg)) // Check if the new segment is not in the given segments (avoid duplicates)
	                implicitBaseSegments.add(newSeg); // Add the new segment to the implicit base segments set
	        }
	    }
	    return implicitBaseSegments; // Return the set of implicit base segments
	}



	/**
	 * Invoke the precomputation procedure.
	 */
	public void analyze() {
		//
		// Implicit Points
		//
		_implicitPoints = ImplicitPointPreprocessor.compute(_pointDatabase, _givenSegments.stream().collect(Collectors.toList()));

		//
		// Implicit Segments attributed to implicit points
		//
		_implicitSegments = computeImplicitBaseSegments(_implicitPoints);

		//
		// Combine the given minimal segments and implicit segments into a true set of minimal segments
		//     *givenSegments may not be minimal
		//     * implicitSegments
		//
		_allMinimalSegments = identifyAllMinimalSegments(_implicitPoints, _givenSegments, _implicitSegments);

		//
		// Construct all segments inductively from the base segments
		//
		_nonMinimalSegments = constructAllNonMinimalSegments(_allMinimalSegments);

		//
		// Combine minimal and non-minimal into one package: our database
		//
		_allMinimalSegments.forEach((segment) -> _segmentDatabase.put(segment, segment));
		_nonMinimalSegments.forEach((segment) -> _segmentDatabase.put(segment, segment));
	}
}
