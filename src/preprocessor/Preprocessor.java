package preprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
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
        Set<Segment> allSegments = new HashSet<>(allMinimalSegments);
        Set<Segment> newSegments = new HashSet<>();

        // For each minimal segment, generate all possible subsegments
        for (Segment s1 : allMinimalSegments) {
            for (Segment s2 : allMinimalSegments) {
                if (s1.equals(s2)) {
                    continue;
                }

                Point p1 = s1.getPoint1();
                Point p2 = s1.getPoint2();
                Point p3 = s2.getPoint1();
                Point p4 = s2.getPoint2();

                // If two minimal segments share a common point, they must be non-minimal
                if (p1.equals(p3)) {
                    newSegments.add(new Segment(p2, p4));
                } else if (p1.equals(p4)) {
                    newSegments.add(new Segment(p2, p3));
                } else if (p2.equals(p3)) {
                    newSegments.add(new Segment(p1, p4));
                } else if (p2.equals(p4)) {
                    newSegments.add(new Segment(p1, p3));
                }
            }
        }
        allSegments.addAll(newSegments);
        return allSegments;
	}
	//
	// Combine the given minimal segments and implicit segments into a true set of minimal segments
	//     *givenSegments may not be minimal
	//     * implicitSegmen
	//

	public Set<Segment> identifyAllMinimalSegments(Set<Point> implicitPoints, Set<Segment> givenSegments, Set<Segment> implicitSegments) {
	    Set<Segment> minimalSegments = new HashSet<>();

	    // Check all implicit segments
	    for (Segment s : implicitSegments) {
	        boolean isMinimal = true;

	        // Check if any point of the segment is an implicit point
	        for (Point p : s.getPoints()) {
	            if (implicitPoints.contains(p)) {
	                isMinimal = false;
	                break;
	            }
	        }

	        // Check if the segment is not already in givenSegments
	        if (isMinimal && !givenSegments.contains(s)) {
	            // Check if the segment is minimal
	            for (Segment gs : givenSegments) {
	                if (gs.HasSubSegment(s) && gs.length() < s.length()) {
	                    isMinimal = false;
	                    break;
	                }
	            }
	            if (isMinimal) {
	                minimalSegments.add(s);
	            }
	        }
	    }

	    return minimalSegments;
	}



	// Compute all implicit segments attributed to implicit points
	public Set<Segment> computeImplicitBaseSegments(Set<Point> implicitPoints) {
	    Set<Segment> implicitSegments = new HashSet<>();

	    // For each pair of implicit points, create a segment
	    for (Point p1 : implicitPoints) {
	        for (Point p2 : implicitPoints) {
	        	implicitSegments.add(new Segment(p1, p2));
	        	}
	    }
	    return implicitSegments;
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
	
	public static void main(String[] args) {
        // Create a point database
        PointDatabase pointDatabase = new PointDatabase();

        // Create some points
        Point p1 = new Point("A",1, 1);
        Point p2 = new Point("B",2, 2);
        Point p3 = new Point("C",3, 3);
        Point p4 = new Point("D",4, 4);

        // Add points to the point database
        pointDatabase.put("A", 1, 1);
        pointDatabase.put("B", 2, 2);
        pointDatabase.put("C", 3, 3);
        pointDatabase.put("D", 4, 4);

        // Create some segments
        Segment s1 = new Segment(p1, p2);
        Segment s2 = new Segment(p2, p3);
        Segment s3 = new Segment(p3, p4);

        // Create a set of given segments
        Set<Segment> givenSegments = new HashSet<>();
        givenSegments.add(s1);
        givenSegments.add(s2);
        givenSegments.add(s3);
        
        System.out.print("pointnode: " + pointDatabase.size());
        System.out.print("segments: " + givenSegments.size());

        // Create a preprocessor object
        Preprocessor preprocessor = new Preprocessor(pointDatabase, givenSegments);

        // Invoke the precomputation procedure
        preprocessor.analyze();

        // Retrieve the minimal segments
        Set<Segment> minimalSegments = _allMinimalSegments;

        // Print the minimal segments
        System.out.println("Minimal segments:");
        for (Segment s : minimalSegments) {
            System.out.println(s.toString());
        }
    }

	
	
	
}
