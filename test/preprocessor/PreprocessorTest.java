package preprocessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import input.InputFacade;
import input.components.ComponentNode;
import input.components.FigureNode;
import preprocessor.delegates.ImplicitPointPreprocessor;

class PreprocessorTest
{
	@Test
	void test_implicit_crossings()
	{
		                                 // TODO: Update this file path for your particular project
		
		//
		//
		//		               D(3, 7)
		//
		//																
		//   E(-2,4)
		//		                       		C(6, 3)
		//
		//		       A(2,0)        B(4, 0)									F(26, 0)


		ComponentNode node = InputFacade.extractFigure("fully_connected_irregular_polygon.json");
		assertTrue(node instanceof FigureNode);
		
		FigureNode fig = (FigureNode)node;
		AbstractMap.Entry<PointDatabase,Set<Segment>> pair = InputFacade.toGeometryRepresentation(fig);
		
		assertEquals(6,pair.getKey().size());
		
		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 5 new implied points inside the pentagon

	}
}