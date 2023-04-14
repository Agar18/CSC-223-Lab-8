package preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
	public Set<Segment> constructAllNonMinimalSegments(Set<Segment> allMinimalSegments) 
	{
		Set<Segment> allNonMinimalSegments = new HashSet<Segment>();

		for (Segment segment1 : allMinimalSegments)
		{
			HashSet<Segment> collinearMinimalSegments = getCollinearMinimalSegments(segment1, allMinimalSegments);

			allNonMinimalSegments.addAll(getNonMinimalSegments(collinearMinimalSegments));
		}

		return allNonMinimalSegments;
	}

	private HashSet<Segment> getCollinearMinimalSegments(Segment segment1, Set<Segment> allMinimalSegments) 
	{
		HashSet<Segment> collinearMinimalSegments = new HashSet<Segment>();

		for(Segment segment2 : allMinimalSegments)
		{
			if(segment1.coincideWithoutOverlap(segment2))
			{
				collinearMinimalSegments.add(segment2);
			}
		}

		return collinearMinimalSegments;
	}

	private Set<Segment> getNonMinimalSegments(Set<Segment> collinearMinimalSegments) 
	{
		Set<Segment> nonMinimalSegments = new HashSet<>();

		Set<Segment> segments = new HashSet<>(collinearMinimalSegments);

		while(!segments.isEmpty())
		{
			Segment segment1 = segments.iterator().next();
			segments.remove(segment1);

			// Check for shared vertices and non-overlapping segments between segment1 and each segment2 in the set

			for(Segment segment2 : collinearMinimalSegments)
			{
				Point point = segment1.sharedVertex(segment2);

				if(point != null && segment1.coincideWithoutOverlap(segment2))
				{
					// Construct a new segment between the non-shared points of segment1 and segment2
					Segment constructedSeg = new Segment(segment1.other(point), segment2.other(point));

					// Only add the new segment if it is not already in the set of non-minimal or minimal segments
					if(!nonMinimalSegments.contains(constructedSeg) && !collinearMinimalSegments.contains(constructedSeg) )
					{
						nonMinimalSegments.add(constructedSeg);

						// Add the new segment to the set of segments to be checked
						segments.add(constructedSeg);
					}
				}
			}
		}

		return nonMinimalSegments;
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

			for (Segment segment : givenSegments)
			{
				for (Point point : implicitPoints)
				{
					if (segment.pointLiesBetweenEndpoints(point)) allMinimalSegments.remove(segment);
				}	
			}

			return allMinimalSegments;
		}
	}

	// Compute all implicit segments attributed to implicit points
	public Set<Segment> computeImplicitBaseSegments(Set<Point> implicitPoints) 
	{
		Set<Segment> implicitBaseSegments = new LinkedHashSet<>();

		for (Segment segment : _givenSegments) 
		{
			SortedSet<Point> pointList = new TreeSet<>();
			pointList.add(segment.getPoint1());

			for (Point point : segment.collectOrderedPointsOnSegment(implicitPoints)) 
			{
				pointList.add(point);
			}

			pointList.add(segment.getPoint2());

			Iterator<Point> iter = pointList.iterator();

			//Sets left = to the first point in the pointList
			Point left = iter.next();

			// Create segments between adjacent points on the segment
			while (iter.hasNext()) {
				
				//Sets right = to the next point in the pointList
				Point right = iter.next();
				Segment newSeg = new Segment(left, right);

				// Only add the segment if it is not already a given segment
				if (!_givenSegments.contains(newSeg)) 
				{
					implicitBaseSegments.add(newSeg);
				}

				left = right;
			}
		}

		return implicitBaseSegments;
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
