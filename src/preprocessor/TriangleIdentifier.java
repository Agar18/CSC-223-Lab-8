package preprocessor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exceptions.FactException;
import geometry_objects.Segment;
import geometry_objects.Triangle;

public class TriangleIdentifier
{
	protected Set<Triangle>         _triangles;
	protected Map<Segment, Segment> _segments; // The set of ALL segments for this figure.

	public TriangleIdentifier(Map<Segment, Segment> segments)
	{
		_segments = segments;
	}

	/*
	 * Compute the figure triangles on the fly when requested;
	 * memoize results for subsequent calls.
	 */
	public Set<Triangle> getTriangles()
	{
		if (_triangles != null) return _triangles;

		_triangles = new HashSet<Triangle>();

		computeTriangles();

		return _triangles;
	}
	

	private void computeTriangles()
	{
	    List<Segment> segments = Arrays.asList(_segments.keySet().toArray(new Segment[0]));

	    // Find all unique triangles
	    for (int i = 0; i < segments.size(); i++) {
	        for (int j = i + 1; j < segments.size(); j++) {
	            for (int k = j + 1; k < segments.size(); k++) {
	            	
	                Segment a = segments.get(i), b = segments.get(j), c = segments.get(k);
	                
	                boolean areCollinear = a.isCollinearWith(b) && a.isCollinearWith(c);
	                boolean intersect = a.segmentIntersection(b) != null || a.segmentIntersection(c) != null || b.segmentIntersection(c) != null;
	                boolean equalLengths = a.length() == b.length() && b.length() == c.length() && a != b && b != c;
	                

	                if (!areCollinear && !intersect && !equalLengths) {
		                
	                    List<Segment> triangleSegments = Arrays.asList(a, b, c);
	                    Triangle triangle;
	                    try {
	                        triangle = new Triangle(triangleSegments);
	                        _triangles.add(triangle);
	                        System.out.println("Triangle added: " + triangle);
	                    } catch (FactException e) {
	                        System.out.println("Error: Invalid segments to create a triangle.");
	                    }
	                } else {
	                    System.out.println("Skipping triangle with segments " + a + ", " + b + ", " + c);
	                    System.out.println("Collinear: " + areCollinear + ", Intersect: " + intersect + ", Equal Lengths: " + equalLengths);
	                }
	            }
	        }
	    }
	}

}
