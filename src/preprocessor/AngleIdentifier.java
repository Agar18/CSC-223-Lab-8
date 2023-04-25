package preprocessor;

import java.util.List;

import java.util.Map;

import exceptions.FactException;
import geometry_objects.Segment;
import geometry_objects.angle.Angle;
import geometry_objects.angle.AngleEquivalenceClasses;
import geometry_objects.points.Point;
import geometry_objects.angle.comparators.AngleStructureComparator;

public class AngleIdentifier
{
	protected AngleEquivalenceClasses _angles;
	protected Map<Segment, Segment> _segments; // The set of ALL segments for this figure

	public AngleIdentifier(Map<Segment, Segment> segments)
	{
		_segments = segments;
		
	}


	/*
	 * Compute the figure triangles on the fly when requested; memoize results for subsequent calls.
	 */
	public AngleEquivalenceClasses getAngles()
	{
		if (_angles != null) return _angles;

		_angles = new AngleEquivalenceClasses();
		

		computeAngles();

		return _angles;
	}

	/**
	Computes the angles between all pairs of non-overlapping segments in the shape.
	 */
	private void computeAngles() {
		// Initialize a counter variable i to 0.
		//int i = 0;

		// Loop over all pairs of segments in the shape.
		for (Segment s1 : _segments.keySet()) {
			for (Segment s2 : _segments.keySet()) {
				// Skip if the segments are the same.
				if (s1 != s2) {
					// Check if the segments share a vertex.
					Point shared = s1.sharedVertex(s2);
					if (shared != null) {
						try {
							// Compute the angle between the two segments and add it to the angle set if it doesn't already exist.
							Angle angle = new Angle(s1, s2);
							if (!_angles.contains(angle)) {
								_angles.add(angle);
							}
						} catch (FactException e) {
							// Handle the case where the angle cannot be computed.
							System.err.println("Error creating angle: " + e.getMessage());
						}
					}
				}
			}
		}

		// Print the size of the angle set and the number of equivalence classes.
		System.out.println("size : " + _angles.size());
		System.out.println("Number of equivalence classes: " + _angles.numClasses());
	}
	
}
