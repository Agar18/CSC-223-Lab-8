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

		_angles = new AngleEquivalenceClasses(new AngleStructureComparator());
		

		computeAngles();

		return _angles;
	}

	private void computeAngles() {
		int i = 0;
		for (Segment s1 : _segments.keySet()) {
			for (Segment s2 : _segments.keySet()) {
				if (s1 != s2) {
					Point shared = s1.sharedVertex(s2);
					if (shared != null) {
						try {
							Angle angle = new Angle(s1, s2);
							if (!_angles.contains(angle)) {
								_angles.add(angle);
							}
						} catch (FactException e) {
							System.err.println("Error creating angle: " + e.getMessage());
						}
					}
				}
			}
		}
		System.out.println("size : " + _angles.size());
		System.out.println("Number of equivalence classes: " + _angles.numClasses());
	}
	
}
