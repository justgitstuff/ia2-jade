package sma.scout_manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sma.gui.Quadrant;
import sma.ontology.Cell;

public class ScoutManagerUtils {

	/**
	 * Divide the map into x quadrants, where x is the number of scouts (+1 if that number is odd)
	 * @param x number of cells for the x
	 * @param y number of cells for the y
	 * @param numberOfScouts The number of scouts in play
	 * @return
	 */
	public static List<Quadrant> divideCity(int x, int y, int numberOfScouts) {
		int numQuadrants;
		if (numberOfScouts % 2 == 1) { // Si és senar
			numQuadrants = numberOfScouts + 1;
		} else {
			numQuadrants = numberOfScouts;
		}
		
		// Determina el nombre de divisions per les x i les y que tindrà el mapa
		int xDivide = 2;
		int yDivide = numQuadrants / xDivide;
		
		List<Quadrant> listQuadrants = new ArrayList<Quadrant>();
		
		int a = x / xDivide;
		int aIni = -a; // A la primera volta del bucle passarà a 0.
		int bIni = 0;
		for (int iterX = 0; iterX < xDivide; iterX++) {
			aIni += a;
			bIni = 0;
			int b = y / yDivide;
			for (int iterY = 0; iterY < yDivide; iterY++) {
				if (aIni + a * 2 > x) {
					a = x - aIni + 1;
				}
				
				if (bIni + b * 2 > y) {
					b = y - bIni + 1;
				}
				
				listQuadrants.add(new Quadrant(aIni, aIni + a - 1, bIni, bIni + b - 1));
				bIni += b;
			}
		}
		
		return listQuadrants;
	}

	/**
	 * Choose a point in the specified quadrant for a scout to go to discover.
	 * 
	 * @param quadrant Quadrant of the map to chose a point.
	 * @param map The map, with its cells and stuff...
	 * @param scoutPosition The position (x, y) of the scout in the map.
	 * @return
	 */
	public static Point chooseUnchartedPointToSendScout(Quadrant quadrant, Cell[][] map, Point scoutPosition) {
		List<Rectangle> unchartedRectangles = divideQuadrantIntoSmallerRectangles(quadrant, map);
		
		// Choose a good point to go to discover!
		Point betterPoint = new Point();
		double compareValue = Double.MAX_VALUE;
		for (Rectangle rectangle:unchartedRectangles) {
			// Calculate the value of omptimality for each rectangle
			Point center = new Point(rectangle.x1, (rectangle.y2 - rectangle.y1) / 2);
			double distance = Math.hypot((scoutPosition.x - center.x), (scoutPosition.y - center.y));
			double newCompareValue = distance / Math.sqrt(rectangle.y2 - rectangle.y1);
			
			if (newCompareValue < compareValue) {
				betterPoint = center;
				compareValue = newCompareValue;
			}
		}
		
		return betterPoint;
	}

	
	/**
	 * For each row of the quadrant in the map, return the uncharted rectangles
	 * @param quadrant
	 * @param map
	 * @return
	 */
	private static List<Rectangle> divideQuadrantIntoSmallerRectangles(Quadrant quadrant, Cell[][] map) {
		List<Rectangle> unchartedRectangles = new ArrayList<Rectangle>();
		
		int iIni = quadrant.x1;
		int jIni = quadrant.y1;
		boolean unchartedZone = false;
		for (int i = quadrant.x1; i < quadrant.x2; i++) { // For each row
			if (unchartedZone) { // For when there is an end of line.
				unchartedRectangles.add(new Rectangle(iIni, i-1, jIni, quadrant.y2)); 
				unchartedZone = false;
			}
//			if (i == quadrant.x1) {
//				unchartedZone = true;
//			}
			for (int j = quadrant.y1; j < quadrant.y2; j++) { // For each column
				if (map[i][j] != null && unchartedZone) {
					unchartedRectangles.add(new Rectangle(iIni, i, jIni, j));
					unchartedZone = false;
				} else if (map[i][j] == null && !unchartedZone) {
					unchartedZone = true;
					iIni = i;
					jIni = j;
				}
			}
		}
		if (unchartedZone) {
			unchartedRectangles.add(new Rectangle(iIni, quadrant.x2, jIni, quadrant.y2)); // For the last line.
		}
		
		return unchartedRectangles;
	}

	/**
	 * It will choose a point in the biggest uncharted zone of the quadrant.
	 * @param quadrant
	 * @param map
	 * @param lastPoint 
	 * @return The point where the scouts are expected to discover.
	 */
	public static Point chooseUnchartedPointInAQuadrant(Quadrant quadrant, Cell[][] map, Point lastPoint) {
		// TODO fer la suma de les files sense descobrir
		List<Rectangle> unchartedRectangles = divideQuadrantIntoSmallerRectangles(quadrant, map);
		
		Map<Integer, List<Rectangle>> groups = new HashMap<Integer, List<Rectangle>>();
		
		boolean inContact = false;
		for (Rectangle rectangleToCheck : unchartedRectangles) {
			inContact = false;
			for (Integer key : groups.keySet()) {
				if (groups.get(key).size() > 0) {
					// Take the last from the group
					Rectangle rectangle = groups.get(key).get(groups.get(key).size() - 1);
					// Check if it is in contact
					if (rectangleToCheck.y1 <= rectangle.y2 && rectangleToCheck.y2 >= rectangle.y1
							&& rectangleToCheck.x1 - 1 == rectangle.x1) {
						groups.get(key).add(rectangleToCheck);
						inContact = true;
						break;
					}
				}
				
			}
			
			if (!inContact) {
				List<Rectangle> newRectangleList = new ArrayList<Rectangle>();
				newRectangleList.add(rectangleToCheck);
				groups.put(groups.size(), newRectangleList);
			}
		}
		
		// Determine the central points of the uncharted areas and their size
		Point[] points = new Point[groups.size()];
		int[] sizes = new int[groups.size()];
		int i = 0;
		for (Integer key:groups.keySet()) {
			// Determine the central point the area
			int half = (groups.get(key).size() - 1) / 2;
			int x = half + groups.get(key).get(0).x1;
			int y = ((groups.get(key).get(half).y2 - groups.get(key).get(half).y1) / 2) + groups.get(key).get(half).y1;
			points[i] = new Point(x, y);
			
			// Determine the size of the area
			int groupSize = 0;
			for (Rectangle rectangle:groups.get(key)) {
				groupSize += rectangle.y2 - rectangle.y1 + 1;
			}
			sizes[i] = groupSize;
			
			i++;
		}
		
		// Determine which point is the best
		Point targetPoint = null;
		if (lastPoint != null) {
			// Determine which point is the nearest to this
			// FIXME Could be improved using the shortest path algorithm
			double minimmumDistance = Double.MAX_VALUE;
			Point nearestPoint = null;
			for (int p = 0; p < points.length; p++) {
				// Calculate distance to the point
				double distance = Math.hypot(Math.abs(points[p].x - lastPoint.x), Math.abs(points[p].y - lastPoint.y));
				
				if (distance < minimmumDistance) {
					nearestPoint = points[p];
					minimmumDistance = distance;
				}
			}
			targetPoint = nearestPoint;
		} else {
			// Determine the biggest uncharted area
			int biggestGroup = 0;
			int biggestGroupSize = 0;
			for (int s = 0; s < sizes.length; s++) {
				if (sizes[s] > biggestGroupSize) {
					biggestGroup = s;
					biggestGroupSize = sizes[s];
				}
			}
			
			// Point of the biggest zone
			targetPoint = points[biggestGroup];
		}
		
		if (targetPoint == null) {
			targetPoint = lastPoint;
		}
		
		return targetPoint;
	}
}
