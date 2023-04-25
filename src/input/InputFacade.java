package input;



import java.util.AbstractMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;

import input.builder.GeometryBuilder;
import input.components.FigureNode;
import input.components.point.PointNode;
import input.components.segment.SegmentNode;
import input.parser.JSONParser;

public class InputFacade
{
	/**
	 * A utility method to acquire a figure from the given JSON file:
	 *     Constructs a parser
	 *     Acquries an input file string.
	 *     Parses the file.
     *
	 * @param filepath -- the path/name defining the input file
	 * @return a FigureNode object corresponding to the input file.
	 */
	public static FigureNode extractFigure(String filepath)
	{
        JSONParser parser = new JSONParser(new GeometryBuilder());
        
        return (FigureNode) parser.parse(filepath);
	}
	
	/**
	 * 1) Convert the PointNode and SegmentNode objects to a Point and Segment objects 
	 *    (those classes have more meaningful, geometric functionality).
	 * 2) Return the points and segments as a pair.
     *
	 * @param fig -- a populated FigureNode object corresponding to a geometry figure
	 * @return a point database and a set of segments
	 */
	public static Map.Entry<PointDatabase, Set<Segment>> toGeometryRepresentation(FigureNode fig)
	{
		
		PointDatabase pointDatabase = new PointDatabase();
		
		// For each PointNode, add the corresponding Point to the database.
		for(PointNode pointNode : fig.getPointsDatabase().getPoints())
		{
			pointDatabase.put(pointNode.getName(), pointNode.getX(), pointNode.getY());
		}
		
		
		LinkedHashSet<Segment> segments = new LinkedHashSet<Segment>();
		
		// For each SegmentNode, convert it to a Segment and add it to the set
		for(SegmentNode segmentNode : fig.getSegments().asUniqueSegmentList())
		{
			segments.add(convertToGeometricSegment(segmentNode));
		}
		
		
		return new AbstractMap.SimpleEntry<PointDatabase, Set<Segment>>(pointDatabase, segments);
	}
	
    
	private static Segment convertToGeometricSegment(SegmentNode segmentNode)
	{
		Point p1 = convertToGeometricPoint(segmentNode.getPoint1());
		Point p2 = convertToGeometricPoint(segmentNode.getPoint2());
		
		return new Segment(p1, p2);
	}
	
		
	private static Point convertToGeometricPoint(PointNode pointNode)
	{
		return new Point(pointNode.getName(), pointNode.getX(), pointNode.getY());
	}
}
