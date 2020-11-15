package utilityClassesForObjects;

/**contains constants that indicate points within a rectangle as well as constants 
  indicating positions of one objects relative to the bounding box of another.
  Heavily used*/
public interface RectangleEdgePosisions {

	static final int factor=100;
	
	/**these represent 8 positions along the sides of a Rectangle*/
	static final int UPPER_LEFT=0, UPPER_RIGHT=1, LOWER_RIGHT=2, LOWER_LEFT=3, 
			LEFT=4, TOP=5, RIGHT=6, BOTTOM=7, CENTER=8, MIDDLE=9;
	
	
	
	/**all critical locations for a rectangle*/
	static final int[] locationsforh=new int[] {UPPER_LEFT, UPPER_RIGHT, LOWER_RIGHT, LOWER_LEFT, LEFT, TOP, RIGHT, BOTTOM, CENTER};
	
	/**all critical locations on the edge of a rectangle*/
	static final int[] locationsonSide=new int[] {UPPER_LEFT, UPPER_RIGHT, LOWER_RIGHT, LOWER_LEFT, LEFT, TOP, RIGHT, BOTTOM, CENTER, MIDDLE};
	
	
	/**the text positions that are possible*/
	static final int POINT_START=10;
	static final int BELOW_POINT_START=11;
	static final int ABOVE_POINT_START=12;
	static final int LEFT_OF_POINT_START=13;
	static final int RIGHT_OF_POINT_START=14;
	
	static final int CornerToCenter=15;
	static final int CornerToCenter_LowerLeft=CornerToCenter+LOWER_LEFT;
	static final int CornerToCenter_UpperRight=CornerToCenter+UPPER_RIGHT;
	
	/**positions within a bounding box*/
	static final int[] locationsfort=new int[] {UPPER_LEFT, UPPER_RIGHT, LOWER_RIGHT, LOWER_LEFT, LEFT, TOP, RIGHT, BOTTOM, CENTER, /**POINT_START, BELOW_POINT_START, ABOVE_POINT_START, LEFT_OF_POINT_START, RIGHT_OF_POINT_START*/};
	
	
	/**Represent the positions outside of a Rectangle that one may place another object
	   they are made so that the left,right, bottom, top components can be easily derived from them*/
	static final int LEFT_SIDE_TOP=     	LEFT*factor+TOP, 
					LEFT_SIDE_MIDDLE=	    LEFT*factor+CENTER,
					LEFT_SIDE_BOTTOM=       LEFT*factor+BOTTOM,
					RIGHT_SIDE_TOP= 		RIGHT*factor+TOP, 
					RIGHT_SIDE_MIDDLE=		RIGHT*factor+CENTER,
					RIGHT_SIDE_BOTTOM=		RIGHT*factor+BOTTOM,
					ABOVE_AT_LEFT=			TOP*factor+LEFT,
					ABOVE_AT_MIDDLE=		TOP*factor+CENTER,
					ABOVE_AT_RIGHT=			TOP*factor+RIGHT,
					BELOW_AT_LEFT=			BOTTOM*factor+LEFT,
					BELOW_AT_RIGHT=			BOTTOM*factor+RIGHT,
					BELOW_AT_MIDDLE=		BOTTOM*factor+CENTER;
					
					;
					
	/**each location on the outside of a rectangle but adjacent to one of the sides*/
	static final int[] externalLocations=new int[] {ABOVE_AT_LEFT, ABOVE_AT_MIDDLE, ABOVE_AT_RIGHT, BELOW_AT_LEFT, BELOW_AT_MIDDLE, BELOW_AT_RIGHT, LEFT_SIDE_TOP, LEFT_SIDE_MIDDLE, LEFT_SIDE_BOTTOM, RIGHT_SIDE_TOP, RIGHT_SIDE_MIDDLE, RIGHT_SIDE_BOTTOM, CornerToCenter_LowerLeft, CornerToCenter_UpperRight};

	

}
