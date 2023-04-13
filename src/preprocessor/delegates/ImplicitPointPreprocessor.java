package preprocessor.delegates;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;

public class ImplicitPointPreprocessor
{
    /**
     * It is possible that some of the defined segments intersect
     * and points that are not named; we need to capture those
     * points and name them.
     *
     * Algorithm:
     * 1. Iterate through each segment in the givenSegments list.
     * 2. For each segment, iterate through the other segments in the list and check for intersections.
     * 3. If an intersection is found, add the intersecting point to the implicitPoints set.
     * 4. Return the implicitPoints set containing all the implicit points.
     */
    public static Set<Point> compute(PointDatabase givenPoints, List<Segment> givenSegments)
    {
        Set<Point> implicitPoints = new LinkedHashSet<Point>();

        // Iterate through each segment in the givenSegments list
        for (Segment segment : givenSegments) {
            // Iterate through the other segments in the list
            for (Segment otherSegment : givenSegments) {
             // Check for intersection
                Point intersectingPoint = segment.segmentIntersection(otherSegment);
                if (intersectingPoint != null) {
                    // If an intersection is found, add the intersecting point to the implicitPoints set
                    implicitPoints.add(intersectingPoint);
                }
            }
        }

        return implicitPoints;
    }
}
