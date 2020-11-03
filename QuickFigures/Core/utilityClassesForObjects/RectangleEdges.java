package utilityClassesForObjects;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

public class RectangleEdges implements  RectangleEdgePosisions {

	
	/**gets the edge location along a rectangle*/
	public static Point2D getLocation(int locationType, Rectangle2D r) {
		switch (locationType) {
		case UPPER_LEFT: {return new Point2D.Double(r.getX(),r.getY());}
		case UPPER_RIGHT: {return new Point2D.Double(r.getX()+r.getWidth(), r.getY());		}
		case LOWER_RIGHT: {return new Point2D.Double(r.getX()+r.getWidth(), r.getY()+r.getHeight()); }
		case LOWER_LEFT:  {return new Point2D.Double(r.getX(), r.getY()+r.getHeight());		}
		case LEFT:  {return new Point2D.Double(r.getX(), r.getY()+r.getHeight()/2);		}
		case RIGHT: {return new Point2D.Double(r.getX()+r.getWidth(), r.getY()+r.getHeight()/2);		}
		case TOP: {return new Point2D.Double(r.getX()+r.getWidth()/2, r.getY());		}
		case BOTTOM: {return new Point2D.Double(r.getX()+r.getWidth()/2, r.getY()+r.getHeight()); }
		case CENTER: {return new Point2D.Double(r.getX()+r.getWidth()/2, r.getY()+r.getHeight()/2); }
		}
		return new Point2D.Double(r.getX(),r.getY());
	}
	
	/**gets the edge location along a rectangle*/
	public static Point2D getMidPointLocation(int locationType, int locationType2,  Rectangle2D r) {
		Point2D p1 = getLocation(locationType,r);
		Point2D p2 = getLocation(locationType2,r);
		double x = p1.getX()+p2.getX();
		double y = p1.getY()+p2.getY();
		return new Point2D.Double(x/2,y/2);
	}
	
	/**when given a point and alist of rectangle edge positions, this returns the edge position closest
	  to the point*/
	public static int getNearestEdgeFromList(Rectangle2D r, int[] edges, Point2D position) {
		double distance=Double.MAX_VALUE;
		int output=edges[0];
		for(int i=0; i<edges.length; i++) {
			Point2D loc = getLocation(edges[i], r);
			double dist = position.distance(loc);
			if(dist<distance) {
				distance=dist;
				output=edges[i];
			}
		}
		return output;
		
		
	}
	
	
	/**Turns a location int into a String*/
	public static String locationToString(int locationType) {
		switch (locationType) {
		case UPPER_LEFT: {return "Upper Left";}
		case UPPER_RIGHT: {return "Upper Right";		}
		case LOWER_RIGHT: {return "Lower Right"; }
		case LOWER_LEFT:  {return "Lower Left";		}
		case LEFT:  {return "Left";		}
		case RIGHT: {return "Right";		}
		case TOP: {return "Top";		}
		case BOTTOM: {return "Bottom"; }
		case CENTER: {return "Center"; }
		case MIDDLE: {return "Center"; }
		
		case POINT_START: return "At Baseline";
		case BELOW_POINT_START: return "Below Baseline";
		case ABOVE_POINT_START: return "Above Baseline";
		case LEFT_OF_POINT_START: return "Left Of Baseline";
		case RIGHT_OF_POINT_START: return "Right Of Baseline";
		
		
		
		case LEFT_SIDE_TOP: return "Left of item, toward Top";
		case LEFT_SIDE_BOTTOM: return "Left of item, toward Bottom";
		case LEFT_SIDE_MIDDLE: return "Left of item, Centered";
		case RIGHT_SIDE_TOP:  return "Right of item, toward Top";
		case RIGHT_SIDE_BOTTOM:  return "Right of item, toward Bottom";
		case RIGHT_SIDE_MIDDLE:  return  "Right of item, Centered";
		case ABOVE_AT_LEFT:    return "Above item, toward Left";
		case ABOVE_AT_RIGHT:    return "Above item, toward Right";
		case ABOVE_AT_MIDDLE:    return "Above item, Centered";
		case BELOW_AT_LEFT: return "Below item, toward Left";
		case BELOW_AT_RIGHT: return "Below item, toward Right";
		case BELOW_AT_MIDDLE: return "Below item, Centered";
		
		case CornerToCenter_LowerLeft: return "With Lower Corner To Center";
		case CornerToCenter_UpperRight: return "With Upper Corner To Center";
		
		}
		return "Upper Left";
	}
	
	public static HashMap<Integer, Integer> generateTranslationMap(int[] choices) {
		HashMap<Integer, Integer> output=new HashMap<Integer, Integer>();
		for(int i=0; i<choices.length; i++) {
			output.put(i, choices[i]);
		}
		return output;
	}
	
	/**when given a location on the outside of one rectangle, returns what the 
	 fixed edge of the snapped object would have to be to keep its position as it is resized.
	 Also returns appropriate a values for internal snap types.*/
	public static int getAppropriateFixedEdgeForSnapType(int locationType) {
		switch (locationType) {
		case UPPER_LEFT: 
		case UPPER_RIGHT:
		case LOWER_RIGHT:
		case LOWER_LEFT: 
		case LEFT:  
		case RIGHT: 
		case TOP:
		case BOTTOM: 
		case CENTER: {return locationType;}
		
		
		case LEFT_SIDE_TOP: 	 return UPPER_RIGHT;
		case LEFT_SIDE_BOTTOM: 	 return LOWER_RIGHT;
		case LEFT_SIDE_MIDDLE: 	 return RIGHT;
		case RIGHT_SIDE_TOP:  	 return UPPER_LEFT;
		case RIGHT_SIDE_BOTTOM:  return LOWER_LEFT;
		case RIGHT_SIDE_MIDDLE:  return  LEFT;
		case ABOVE_AT_LEFT:   	 return LOWER_LEFT;
		case ABOVE_AT_RIGHT:  	 return LOWER_RIGHT;
		case ABOVE_AT_MIDDLE:    return BOTTOM;
		case BELOW_AT_LEFT: 	 return UPPER_LEFT;
		case BELOW_AT_RIGHT:	 return UPPER_RIGHT;
		case BELOW_AT_MIDDLE: 	 return TOP;
		
		
		case CornerToCenter_LowerLeft: return LOWER_LEFT;
		case CornerToCenter_UpperRight: return UPPER_RIGHT;
		}
		return UPPER_LEFT;
	}
	
	/**Returns 1 or -1 depending on whether the offset from the base location 
	 of the snap type is positive or negative*/
	public int getXDirectionOfOffSetForSnapType(int locationType){
		switch (locationType) {
		case UPPER_LEFT:  return 1;
		case UPPER_RIGHT:return -1;
		case LOWER_RIGHT:return -1;
		case LOWER_LEFT:  return 1;
		case LEFT:   return 1;
		case RIGHT:  return -1;
		case TOP:return 1;
		case BOTTOM: return 1;
		case CENTER: return 1;
		
		
		case LEFT_SIDE_TOP: 	 return -1;
		case LEFT_SIDE_BOTTOM: 	 return -1;
		case LEFT_SIDE_MIDDLE: 	 return -1;
		case RIGHT_SIDE_TOP:  	 return 1;
		case RIGHT_SIDE_BOTTOM:  return 1;
		case RIGHT_SIDE_MIDDLE:  return 1;
		case ABOVE_AT_LEFT:   	 return 1;
		case ABOVE_AT_RIGHT:  	 return 1;
		case ABOVE_AT_MIDDLE:    return 1;
		case BELOW_AT_LEFT: 	 return 1;
		case BELOW_AT_RIGHT:	 return 1;
		case BELOW_AT_MIDDLE: 	 return 1;
		
		case CornerToCenter_LowerLeft: return 1;
		case CornerToCenter_UpperRight: return 1;
		}
		
		
		return 1;
	} 
	
	/**see above*/
public int getYDirectionOfOffSetForSnapType(int locationType){
	switch (locationType) {
	case UPPER_LEFT:  return 1;
	case UPPER_RIGHT:return 1;
	case LOWER_RIGHT:return -1;
	case LOWER_LEFT:  return -1;
	case LEFT:   return 1;
	case RIGHT:  return 1;
	case TOP:return 1;
	case BOTTOM: return -1;
	case CENTER: return 1;
	
	
	case LEFT_SIDE_TOP: 	 return -1;
	case LEFT_SIDE_BOTTOM: 	 return 1;
	case LEFT_SIDE_MIDDLE: 	 return 1;
	case RIGHT_SIDE_TOP:  	 return -1;
	case RIGHT_SIDE_BOTTOM:  return 1;
	case RIGHT_SIDE_MIDDLE:  return 1;
	case ABOVE_AT_LEFT:   	 return -1;
	case ABOVE_AT_RIGHT:  	 return -1;
	case ABOVE_AT_MIDDLE:    return -1;
	case BELOW_AT_LEFT: 	 return 1;
	case BELOW_AT_RIGHT:	 return 1;
	case BELOW_AT_MIDDLE: 	 return 1;
	
	
	case CornerToCenter_LowerLeft: return -1;
	case CornerToCenter_UpperRight: return 1;
	}
	
	
	return 1;
	} 
	
/**turns a list of positions into a set of Strings*/
	public static String[] translate(int[] positions) {
		String[] output=new String[positions.length];
		for(int i=0; i<positions.length; i++) {
			output[i]=locationToString(positions[i]);
			
		}
		return output;
	}
	
	/**
	public static int[] translate(String[] positions) {
		
	}*/
	
	public static String[] getLocationsRect() {
		return translate(locationsforh);
	}
	public static HashMap<Integer, Integer> translatorForLocationsRect() {
		return generateTranslationMap(locationsforh);
	}
	
	public static String[] getLocationsText() {
		return translate(locationsfort);
	}
	public static HashMap<Integer, Integer> translatorForLocationsText() {
		return generateTranslationMap(locationsfort);
	}
	
	public static String[] getLocationsForOutside() {
		return translate(externalLocations);
	}
	public static HashMap<Integer, Integer> translatorForOutside() {
		return generateTranslationMap(externalLocations);
	}
	
	
	
	
	
	/**when given a point, returns a location at the side of a rectangle sharing one cordinate with it
	  Location type determines which one*/
	public static Point2D getRelativeLocation(java.awt.geom.Point2D double1, int locationType, Rectangle2D r) {
		switch (locationType) {
		case BELOW_POINT_START: return new Point2D.Double(double1.getX(), r.getY()+r.getHeight());
		case ABOVE_POINT_START: return new Point2D.Double(double1.getX(), r.getY());
		case LEFT_OF_POINT_START: return new Point2D.Double(r.getX(), double1.getY());
		case RIGHT_OF_POINT_START: return new Point2D.Double(r.getX()+r.getWidth(), double1.getY());
		}
		
		return double1;
	}
	
	
	/**sets the location of a rectangle based on the given location type*/
	public static void setLocation(Rectangle r, int locationType, double x, double y) {
		switch (locationType) {
		case UPPER_RIGHT: {
				r.y=(int) y;
				r.x=(int) (x-r.width);
				return;
				}
		case LOWER_RIGHT: {
				r.y=(int) (y-r.height);
				r.x=(int) (x-r.width);
				return;
			}
		case LOWER_LEFT: {
			r.y=(int) (y-r.height);
			r.x=(int) x;
			return;
		}
		
		case UPPER_LEFT: {
			r.y=(int) y;
			r.x=(int) x;
			return;
		}
		
		case LEFT: {
			r.y=(int) (y-r.height/2);
			r.x=(int) x;
			return;
		}
		case RIGHT: {
			r.y=(int) (y-r.height/2);
			r.x=(int) (x-r.width);
			return;
		}
		case TOP: {
			r.y=(int) y;
			r.x=(int) (x-r.width/2);
			return;
		}
		case BOTTOM: {
			r.y=(int) (y-r.height);
			r.x=(int) (x-r.width/2);
			return;
		}
		case CENTER: {
			r.y=(int) (y-r.height/2);
			r.x=(int) (x-r.width/2);
			return;
		}
		
	
	}
	}
	
	/**sets the location of a rectangle based on the given location type*/
	public static void setLocation(Rectangle2D.Double r, int locationType, double x, double y) {
		switch (locationType) {
		case UPPER_RIGHT: {
				r.y=y;
				r.x=x-r.width;
				return;
				}
		case LOWER_RIGHT: {
				r.y=y-r.height;
				r.x=x-r.width;
				return;
			}
		case LOWER_LEFT: {
			r.y=y-r.height;
			r.x=x;
			return;
		}
		
		case UPPER_LEFT: {
			r.y=y;
			r.x=x;
			return;
		}
		
		case LEFT: {
			r.y=y-r.height/2;
			r.x=x;
			return;
		}
		case RIGHT: {
			r.y=y-r.height/2;
			r.x=x-r.width;
			return;
		}
		case TOP: {
			r.y=y;
			r.x=x-r.width/2;
			return;
		}
		case BOTTOM: {
			r.y=y-r.height;
			r.x=x-r.width/2;
			return;
		}
		case CENTER: {
			r.y=y-r.height/2;
			r.x=x-r.width/2;
			return;
		}
		
	
	}
	}
	
	/**sets the location of a rectangle based on the given location type*/
	public static void setLocation(Ellipse2D.Double r, int locationType, double x, double y) {
		switch (locationType) {
		case UPPER_RIGHT: {
				r.y=y;
				r.x=x-r.width;
				return;
				}
		case LOWER_RIGHT: {
				r.y=y-r.height;
				r.x=x-r.width;
				return;
			}
		case LOWER_LEFT: {
			r.y=y-r.height;
			r.x=x;
			return;
		}
		
		case UPPER_LEFT: {
			r.y=y;
			r.x=x;
			return;
		}
		
		case LEFT: {
			r.y=y-r.height/2;
			r.x=x;
			return;
		}
		case RIGHT: {
			r.y=y-r.height/2;
			r.x=x-r.width;
			return;
		}
		case TOP: {
			r.y=y;
			r.x=x-r.width/2;
			return;
		}
		case BOTTOM: {
			r.y=y-r.height;
			r.x=x-r.width/2;
			return;
		}
		case CENTER: {
			r.y=y-r.height/2;
			r.x=x-r.width/2;
			return;
		}
		
	
	}
	}

	/**when given a location on the edge of a rectangle, this returns the opposide edge
	  possible arguments are the internal locations in */
	public static int oppositeSide(int side) {
		switch (side) {
		case UPPER_LEFT: return LOWER_RIGHT;
		case LOWER_RIGHT: return UPPER_LEFT;
		case LOWER_LEFT: return UPPER_RIGHT;
		case UPPER_RIGHT: return LOWER_LEFT;
		case TOP: return BOTTOM;
		case BOTTOM: return TOP;
		case LEFT: return RIGHT;
		case RIGHT: return LEFT;
		case CENTER: return CENTER;
		}
		
		
		return UPPER_LEFT;
	}
	
	/**when given a location on the edge of a rectangle, this returns the opposide edge
	  possible arguments are the internal locations in */
	public static int[] adjacentSide(int side) {
		switch (side) {
		case UPPER_LEFT: return new int[] {UPPER_RIGHT, LOWER_LEFT};
		case LOWER_RIGHT: return new int[] {UPPER_RIGHT, LOWER_LEFT};
		case LOWER_LEFT: return new int[] {LOWER_RIGHT, UPPER_LEFT};
		case UPPER_RIGHT:  return new int[] {LOWER_RIGHT, UPPER_LEFT};
		case TOP: return new int[] {RIGHT, LEFT};
		case BOTTOM: return new int[] {RIGHT, LEFT};
		case LEFT: return new int[] {TOP, BOTTOM};
		case RIGHT: return new int[] {TOP, BOTTOM};
		case CENTER: return new int[] {CENTER};
		}
		 return new int[] {CENTER};
	}
	
	/**when given a location on the edge of a rectangle, this returns the opposide edge
	  possible arguments are the internal locations in */
	public static int[] adjacentPositions(int side) {
		switch (side) {
		case UPPER_LEFT: return new int[] {TOP, LEFT};
		case LOWER_RIGHT: return new int[] {BOTTOM, RIGHT};
		case LOWER_LEFT: return new int[] {BOTTOM, LEFT};
		case UPPER_RIGHT:  return new int[] {TOP, RIGHT};
		case TOP: return new int[] {UPPER_LEFT, UPPER_RIGHT};
		case BOTTOM: return new int[] {LOWER_RIGHT, LOWER_LEFT};
		case LEFT: return new int[] {UPPER_LEFT,LOWER_LEFT};
		case RIGHT: return new int[] {UPPER_RIGHT, LOWER_RIGHT};
		case CENTER: return new int[] {CENTER};
		}
		 return new int[] {CENTER};
	}
	
	public static double distanceOppositeSide(int side, Rectangle2D r) {
		int side2 = oppositeSide(side);
		Point2D p1 = getLocation(side, r);
		Point2D p2 = getLocation(side2, r);
		
		return p1.distance(p2);
	}
	

	

	
	public static ArrayList<Point2D> getLocationsForHandles(Rectangle2D r) {
			ArrayList<Point2D> output = new ArrayList<Point2D>();
			for(int i: locationsforh) {
				output.add(RectangleEdges.getLocation(i,r));
			}
			return output;
		}
	
	/**returns which edge of a rectangle is nearest a point*/
	public static int nearestEdge(Point2D p, Rectangle rect) {
		double dist=Double.MAX_VALUE;
		int edge=UPPER_LEFT;
		for(int i: locationsonSide) {
			double thisdist=p.distance(getLocation(i,rect));
			if (thisdist<dist) {
				dist=thisdist;
				edge=i;
			}
		}
			return edge;
		}
	
	/**when given a rectangle and a set of dimensions, returns a resized rectangle that fits in the 
	 given dimensions*/
	public static Rectangle2D  fit(Rectangle2D r, double width, double height) {
		if (width==r.getWidth() || height==r.getHeight()) return r;
		double as1 = ((double) height)/((double) width);
		double as2 = ((double) r.getHeight())/((double) r.getWidth());
		if (as2>as1) { return new Rectangle2D.Double(0,0, (((double)height)/as2), height);} else {
			return new Rectangle2D.Double(0,0,width, (width*as2));
		}	
	}
		
	public static AffineTransform getRotationAboutCenter(Shape s, double angle) {
		return AffineTransform.getRotateInstance(angle, s.getBounds().getCenterX(), s.getBounds().getCenterY());
	}
	
}
