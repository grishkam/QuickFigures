package utilityClassesForObjects;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import gridLayout.MontageSpaces;
import logging.IssueLog;


public class SnappingPosition implements  RectangleEdgePosisions, Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double snapHOffset=0;
	private double snapVOffset=0;
	public double snapRangeH=0;
	public double snapRangeV=0;
	private boolean supressed=false;
	private int useExtendedBounds=0;
	private int gridLayoutSnapType=0;
	
	/**whether this snaps to an internal or an external location*/
	private int snapType=0; public static int INTERNAL=0, EXTERNAL=1;
	
	private static String[] boundsChoices=new String[] {"Normal ", "Extened Bounds"};
	private static String[] gridChoices=new String[] {"To Panel", "To Column", "To Row", "To Montage", "To Block of Panels"};
	private static int[] gridchoices=new int[] {MontageSpaces.PANELS,MontageSpaces.COLUMN_OF_PANELS,MontageSpaces.ROW_OF_PANELS, MontageSpaces.ALL_MONTAGE_SPACE, MontageSpaces.BLOCK_OF_PANELS};
	
	private String[] stapTypeChoices=new String[] {"Internal Snap", "External Snaps"};
	
	
	
	/**the internal and external snapping types. around 20 in total*/
	private int snapLocationTypeInternal=0;
	private int snapLocationTypeExternal=0;
	
	
	public boolean same(SnappingPosition s) {
		if(s.snapLocationTypeInternal!=snapLocationTypeInternal) return false;
		if(s.snapLocationTypeExternal!=snapLocationTypeExternal) return false;
		if(s.snapType!=snapType) return false;
		if(s.gridLayoutSnapType!=gridLayoutSnapType) return false;
		if(s.snapHOffset!=snapHOffset) return false;
		if(s.snapVOffset!=snapVOffset) return false;
		if(s.snapRangeH!=snapRangeH) return false;
		if(s.snapRangeV!=snapRangeV) return false;
		if(s.useExtendedBounds!=useExtendedBounds) return false;
		if(s.gridLayoutSnapType!=gridLayoutSnapType) return false;
		if(s.supressed!=this.supressed)return false;
		return true;
		
	}
	
	static int translateSnapTypeToChoiceIndex(int it) {
		for(int i=0;i< gridchoices.length; i++) {
			
			if ( gridchoices[i]==it) return i; 
		}
		return 0;
	}
	
	public void setGridChoice2(int choice) {
		this.setGridLayoutSnapType(translateSnapTypeToChoiceIndex(choice));
	}
	
	public SnappingPosition() {}
	public static SnappingPosition defaultExternal() {
		SnappingPosition output = new SnappingPosition();
		output.setSnapLocationTypeExternal(ABOVE_AT_MIDDLE);
		output.setSnapType(1);
		return output;
	}
	
	public static SnappingPosition defaultRowLabel() {
		SnappingPosition out = defaultRowSide(); out.setSnapHOffset(2); return out;
	}
	public static SnappingPosition defaultRowSide() {
		SnappingPosition output = new SnappingPosition();
		output.setSnapLocationTypeExternal(LEFT_SIDE_MIDDLE);
		output.setGridChoice2(MontageSpaces.ROW_OF_PANELS);
		output.setSnapType(1);
		return output;
	}
	public static SnappingPosition defaultColLabel() {
		SnappingPosition out = defaultColSide(); out.setSnapVOffset(1); return out;
	}
	public static SnappingPosition defaultColSide() {
		SnappingPosition output = new SnappingPosition();
		output.setSnapLocationTypeExternal(ABOVE_AT_MIDDLE);
		output.setGridChoice2(MontageSpaces.COLUMN_OF_PANELS);
		output.setSnapType(1);
		return output;
	}
	
	public static SnappingPosition defaultPlotTitle() {
		SnappingPosition output = new SnappingPosition();
		output.setSnapLocationTypeExternal(ABOVE_AT_MIDDLE);
		output.setGridChoice2(MontageSpaces.ALL_OF_THE+MontageSpaces.PANELS);
		output.setSnapType(1);
		return output;
	}
	
	public static SnappingPosition defaultPlotBottomSide() {
		SnappingPosition output = new SnappingPosition();
		output.setSnapLocationTypeExternal(CornerToCenter_UpperRight);
		output.setGridChoice2(MontageSpaces.COLUMN_OF_PANELS);
		output.setSnapVOffset(6);
		output.setSnapHOffset(4);
		output.setSnapType(1);
		return output;
	}
	
	public static SnappingPosition defaultPlotRigthSide() {
		SnappingPosition output = new SnappingPosition();
		output.setSnapLocationTypeExternal(LEFT_SIDE_MIDDLE);
		output.setGridChoice2(MontageSpaces.ROW_OF_PANELS);
		output.setSnapVOffset(6);
		output.setSnapHOffset(4);
		output.setSnapType(1);
		return output;
	}
	
	public static SnappingPosition defaultPlotLegand() {
		SnappingPosition output = new SnappingPosition();
		output.setSnapLocationTypeExternal(RIGHT_SIDE_MIDDLE);
		output.setSnapHOffset(4);
		output.setSnapType(1);
		return output;
	}
	
	
	
	
	public static SnappingPosition partnerExternal() {
		SnappingPosition output = new SnappingPosition();
		output.setSnapLocationTypeExternal(RectangleEdgePosisions.RIGHT_SIDE_TOP);
		output.setSnapType(1);
		return output;
	}
	
	public static ArrayList<SnappingPosition> externalRightLeft() {
		ArrayList<SnappingPosition> out = new  ArrayList<SnappingPosition>();
		SnappingPosition o1 = partnerExternal() ;
		out.add(o1);
		
		o1=partnerExternal() ;
		o1.setSnapLocationTypeExternal(RectangleEdgePosisions.RIGHT_SIDE_MIDDLE);
		out.add(o1);
		
		o1=partnerExternal() ;
		o1.setSnapLocationTypeExternal(RectangleEdgePosisions.RIGHT_SIDE_BOTTOM);
		out.add(o1);
		
		o1=partnerExternal() ;
		o1.setSnapLocationTypeExternal(RectangleEdgePosisions.LEFT_SIDE_TOP);
		out.add(o1);
		
		o1=partnerExternal() ;
		o1.setSnapLocationTypeExternal(RectangleEdgePosisions.LEFT_SIDE_MIDDLE);
		out.add(o1);
		
		o1=partnerExternal() ;
		o1.setSnapLocationTypeExternal(RectangleEdgePosisions.LEFT_SIDE_BOTTOM);
		out.add(o1);
		
		return out;
	}
	
	/**a list of snapping bahaviors that are for placing panels inside at the edges*/
	public static ArrayList<SnappingPosition> internalSpread() {
		ArrayList<SnappingPosition> out = new  ArrayList<SnappingPosition>();
		out.add(defaultInternal(SnappingPosition.UPPER_RIGHT));
		out.add(defaultInternal(SnappingPosition.RIGHT));
		out.add(defaultInternal(SnappingPosition.LOWER_RIGHT));
		out.add(defaultInternal(SnappingPosition.BOTTOM));
		out.add(defaultInternal(SnappingPosition.LOWER_LEFT));
		return out;
	}
	

	
	public boolean isInternalSnap() {
		return snapType==0;
	}
	public boolean isExternalSnap() {
		return snapType==1;
	}
	
	
	/**returns true if this one places the mobile object to the right of the stationary, false otherwise*/
	public boolean isExternalRightSnap() {
		if (isInternalSnap()) return false;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.RIGHT_SIDE_BOTTOM) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.RIGHT_SIDE_TOP) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.RIGHT_SIDE_MIDDLE) return true;
		
		
		return false;
	}
	
	/**returns true if this one places the mobile object to the left of the stationary, false otherwise*/
	public boolean isExternalLeftSnap() {
		if (isInternalSnap()) return false;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.LEFT_SIDE_BOTTOM) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.LEFT_SIDE_TOP) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.LEFT_SIDE_MIDDLE) return true;
		
		
		return false;
	}
	
	/**returns true if this is an external snap that moves an object below a stationary reference object*/
	public boolean isExternalBottomEdgeSnap() {
		if (isInternalSnap()) return false;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.BELOW_AT_RIGHT) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.BELOW_AT_LEFT) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.BELOW_AT_MIDDLE) return true;
		
		
		return false;
	}
	
	/**returns true if this is an external snap that moves an object below a stationary reference object*/
	public boolean isExternalTopEdgeSnap() {
		if (isInternalSnap()) return false;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.ABOVE_AT_RIGHT) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.ABOVE_AT_LEFT) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePosisions.ABOVE_AT_MIDDLE) return true;
		
		
		return false;
	}
	

	public SnappingPosition copy() {
		SnappingPosition o = new SnappingPosition();
		givePropertiesTo(o);
		
		return o;
	}
	
	public void givePropertiesTo(SnappingPosition o) {
		if (o==null) return;
		o.setSnapHOffset(this.getSnapHOffset());
		o.setSnapVOffset(this.getSnapVOffset());
		o.copyPositionFrom(this);
		o.setGridLayoutSnapType(this.getGridLayoutSnapType());
	}

	public SnappingPosition copyWOofffsets() {
		SnappingPosition o = copy();
		o.setSnapHOffset(0);
		o.setSnapVOffset(0);
		return o;
	}
	
	/**takes the location but not the offsets from the argument*/
	public void copyPositionFrom(SnappingPosition  s) {
			if(s==null) return;
		 setSnapType(s.getSnapType());
		 setSnapLocationTypeInternal(s.getSnapLocationTypeInternal());
		 setSnapLocationTypeExternal(s.getSnapLocationTypeExternal());
		 setSupressed(s.isSupressed());
	}
	
	public static SnappingPosition defaultInternal() {
		 return defaultInternal(LOWER_RIGHT);
		
	}
	
	public static SnappingPosition defaultInternalPanel() {
		 return defaultInternal(UPPER_LEFT);
		
	}
	
	private static SnappingPosition defaultInternal(int position) {
		SnappingPosition output = new SnappingPosition();
		output.setSnapLocationTypeExternal(position);
		output.setSnapLocationTypeInternal(position);
		output.setSnapType(0);
		return output;
	}
	
	public static SnappingPosition defaultScaleBar() {
		SnappingPosition output = new SnappingPosition();
		//output.setSnapLocationTypeExternal(LOWER_RIGHT);
		output.setSnapLocationTypeInternal(LOWER_RIGHT);
		output.setSnapType(0);
		output.setSnapVOffset(4);
		output.setSnapHOffset(4);
		return output;
	}
	
	public static SnappingPosition defaultPanelLabel() {
		SnappingPosition output = new SnappingPosition();
		output.setSnapLocationTypeExternal(UPPER_LEFT);
		output.setSnapType(0);
		output.setSnapHOffset(1);
		return output;
	}
	
	public void snapLocatedObjects(LocatedObject2D l1, LocatedObject2D l2) {
		Rectangle r2 = l2.getBounds();
		snapObjectToRectangle(l1, r2);

	}
	
	public void snapObjectToRectangle(LocatedObject2D l1, Rectangle2D r2) {
			if (this.isSupressed()) return;
		Rectangle r1 = l1.getBounds().getBounds();
		Rectangle r1ex=l1.getExtendedBounds();
		if (this.getUseExtendedBounds()==1) {
			if (isVerticalOutside() ) r1=new Rectangle(r1.x, r1ex.y, r1.width, r1ex.height) ;
			if (isHorozontalOutside() ) r1=new Rectangle(r1ex.x, r1.y, r1ex.width, r1.height) ;
			}
	
		snapRects(r1,r2);
	
		l1.setLocationUpperLeft(r1.x, r1.y);
		
			if (this.getUseExtendedBounds()==1)  {
				double dx =0,dy=0;
				if (isHorozontalOutside() )dx=l1.getExtendedBounds().getX()-l1.getBounds().getX();
				if (isVerticalOutside() ) dy=l1.getExtendedBounds().getY()-l1.getBounds().getY();
			l1.moveLocation(-(int)dx, -(int)dy);
			
		}
	}
	
	private boolean isHorozontalOutside() {
		if (this.getSnapType()==0) return false;
		 if (this.getSnapLocationTypeExternal()/factor==LEFT) return true;
		 if (this.getSnapLocationTypeExternal()/factor==RIGHT) return true;
		
		return false;
	}
	
	private boolean isVerticalOutside() {
			if (this.getSnapType()==0) return false;
			if (this.getSnapLocationTypeExternal()/factor==TOP) return true;
			if (this.getSnapLocationTypeExternal()/factor==BOTTOM) return true;
			return false;
	}
	
	/**Performs the action of this object. Moves the rectangles into the positions
	  specified by this objects fields. */
	public void snapRects(Rectangle2D r1, Rectangle2D r2) {
		if(r1==null||r2==null) return;
		java.awt.geom.Rectangle2D.Double r1b = new Rectangle2D.Double(); r1b.setRect(r1);
		java.awt.geom.Rectangle2D.Double r2b = new Rectangle2D.Double(); r2b.setRect(r2);
		snapRects(r1b, r2b);
		r1.setRect(r1b);
		r2.setRect(r2b);
	}
	
	private void snapRects(Rectangle2D.Double r1, Rectangle2D.Double r2) {
			if (snapType==INTERNAL) {
				doInternalSnapEdgePointToEdgePoint(snapLocationTypeInternal,r1,r2);
			
			}
			if (snapType==EXTERNAL) {
				doExternalSnap(snapLocationTypeExternal, r1, r2);
			}
	}
	
	
	public String getDescription() {
		if (snapType==0) {
			return RectangleEdges.locationToString(getSnapLocationTypeInternal());
			
			}else {
			return RectangleEdges.locationToString(getSnapLocationTypeExternal());
			}
	}
	
	public String getShortDescription() {
		String st=this.getDescription();
		return st.split(",")[0];
	}
	
	public String getSecondDescription() {
		String st=this.getDescription();
		if (st.contains(","))	return st.split(",")[1];
		return "";
	}
	
	private HashMap<Integer, Rectangle2D> getAllPossibleInternalSnaps(Rectangle2D r1, Rectangle2D  r2) {
		HashMap<Integer, Rectangle2D> output = new HashMap<Integer, Rectangle2D>();
		
		Rectangle2D.Double r3=new Rectangle2D.Double();r3.setRect(r1);
		Rectangle2D.Double r4=new Rectangle2D.Double();r4.setRect(r2);
		
		for(int i:  internalSnapOptions() ) {
			Rectangle2D dub = r3.getBounds();
			doInternalSnapEdgePointToEdgePoint(i,r3,r4);
			
			dub.setRect(r3.getBounds());//bugfix
			output.put(i,dub);
		}
		
		return output;
	}
	

	
	/**when given the position of two rectangles, it sets this object to the
	 snapping position that is nearest to point p*/
	public void setToNearestSnap(Rectangle2D r1, Rectangle2D r2, Point2D p) {
		if(r2==null||r1==null) return;
		if (r2.contains(p)) {
			this.setSnapType(0);;
			setToNearestInternalSnap(r1,r2,p);
			} else 
				{this.setSnapType(1);setToNearestExternalSnap(r1,r2,p);}
		
	}
	public void setToNearestInternalSnap(Rectangle2D r1, Rectangle2D r2, Point2D p) {
		this.setSnapLocationTypeInternal(getNearestInternalSnap(r1,r2,p));
	}
	public void setToNearestExternalSnap(Rectangle2D r1, Rectangle2D r2, Point2D p) {
		this.setSnapLocationTypeExternal(getNearestExternalSnap(r1,r2,p));
	}
	
	protected int getNearestInternalSnap(Rectangle2D r1, Rectangle2D r2, Point2D p) {
		HashMap<Integer, Rectangle2D> ss = getAllPossibleInternalSnaps(r1,r2);
		
		return getNearest(ss,p);
	}
	protected int getNearestExternalSnap(Rectangle2D r1, Rectangle2D r2, Point2D p) {
		HashMap<Integer, Rectangle2D> ss = getAllPossibleExternalSnaps(r1, r2);
		return getNearest(ss,p);
	}
	
	private HashMap<Integer, Rectangle2D> getAllPossibleExternalSnaps(Rectangle2D r1, Rectangle2D r2) {
		HashMap<Integer, Rectangle2D> output =new HashMap<Integer, Rectangle2D>();
		
		Rectangle2D.Double r3=new Rectangle2D.Double();
		r3.setRect(r1);
		Rectangle2D.Double r4=new Rectangle2D.Double(); 
		r4.setRect(r2);
		
		for(int i:  externalSnapOptions() ) {
			Rectangle dub = r3.getBounds();
			doExternalSnap(i,r3,r4);
			dub=r3.getBounds();//?? possible bugfix by changing r1 to r3//7/7/18 bug fix worked
			output.put(i,dub);
		}
		return output;
	}
	
	public static int getNearest(HashMap<Integer, Rectangle2D> rects, Point2D p) {
		double lowestd=Double.MAX_VALUE;
		int one=1;
		for(int i: rects.keySet()) try {
			Rectangle2D r = rects.get(i);
			double dist=p.distance(r.getCenterX(), r.getCenterY());
			if (dist<lowestd) {
				lowestd=dist;
				one=i;
						
			}
		} catch (Throwable t) {IssueLog.log(t);}
		return one;
	}
	
	public static int[] internalSnapOptions() {
		return RectangleEdgePosisions.locationsforh;
		
	}
	public static int[] externalSnapOptions() {
		return RectangleEdgePosisions.externalLocations;
		
	}
	
	public double getSnappingRangeH(double objectWidth) {
		return snapRangeH*objectWidth;
	};
	public double getSnappingRangeV(double objectHeight) {
		return snapRangeV*objectHeight;
	};
	
	public static   Point2D.Double[] RectangleVertices(Rectangle2D rect) {
		 Point2D.Double[] output = new Point2D.Double[4];
		 output[0]=new Point2D.Double(rect.getX(), rect.getY());
		 output[1]=new Point2D.Double(rect.getX()+rect.getWidth(), rect.getY());
		 output[2]=new Point2D.Double(rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight());
		 output[3]=new Point2D.Double(rect.getX(), rect.getY()+rect.getHeight());
		 return output;
	  }

	
	/**given a rectangle you want to snap to rectangle 2 this returns a point with the 
	  snapped distance. This will snap to wherever is in range. r1's location changes*/
	private void snapBoundsSideIfInRange(Rectangle2D.Double r1, Rectangle2D.Double r2) {
		double snaph = getSnappingRangeH(r1.width) ;
		double snapv = getSnappingRangeH(r1.height) ;
		
		
		if (Math.abs(r1.x-r2.x)<snaph) r1.x=r2.x+snapHOffset;// snaps the left side
		if (Math.abs(r1.x+r1.width-r2.x-r2.width)<snaph) r1.x=r2.x+r2.width-r1.width-snapHOffset;// snaps the right side
		if (Math.abs(r1.y-r2.y)< snapv) r1.y=r2.y+snapVOffset;// snaps the top side
		if (Math.abs(r1.y+r1.height-r2.y-r2.height)< snapv) r1.y=r2.y+r2.height-r1.height-snapVOffset;// snaps the bottom side
		
		/**snaps the external side*/
		if (Math.abs(r1.x+r1.width-r2.x)<snaph){ r1.x=r2.x-r1.width-snapHOffset;}//snaps right side to the left of r2
		if (Math.abs(r1.x-r2.x-r2.width)<snaph) {r1.x=r2.x+r2.width+snapHOffset;}//snaps left side to the right of r2
		if (Math.abs(r1.y+r1.height-r2.y)< snapv) {r1.y=r2.y-r1.height-snapVOffset;}//snaps bottom to the top of r2
		if (Math.abs(r1.y-r2.y-r2.height)< snapv) {r1.y=r2.y+r2.height+snapVOffset;}//snaps top to the bottom of r2
		
		if (Math.abs(r1.getCenterX()-r2.getCenterX())<snaph) {
			r1.x=(int)(r2.getCenterX()-r1.getWidth()/2)+snapHOffset;
		}
		
		if (Math.abs(r1.getCenterY()-r2.getCenterY())<snapv) {
			r1.y=(int)(r2.getCenterY()-r1.getHeight()/2)+snapVOffset;
		}
		
		return;
	}
	
	/**Handles internal corner to internal corner snapping. snaps location of r1 to r2*/
	private void doInternalSnapLocationType(int locationType, Rectangle2D.Double r1, Rectangle.Double r2) {
		
		switch (locationType) {
		case LEFT:  r1.x=r2.x+getSnapHOffset(); {}; return;
		case RIGHT: r1.x=r2.x+r2.width-r1.width-getSnapHOffset(); return;
		case TOP:	r1.y=r2.y+getSnapVOffset(); return;
		case BOTTOM:r1.y=r2.y+r2.height-r1.height-getSnapVOffset(); return;
		case UPPER_LEFT: doInternalSnapLocationType(LEFT,r1,r2);doInternalSnapLocationType(TOP,r1,r2); return;
		case LOWER_LEFT:doInternalSnapLocationType(LEFT,r1,r2);doInternalSnapLocationType(BOTTOM,r1,r2); return;
		case UPPER_RIGHT:doInternalSnapLocationType(RIGHT,r1,r2);doInternalSnapLocationType(TOP,r1,r2); return;
		case LOWER_RIGHT:doInternalSnapLocationType(RIGHT,r1,r2);doInternalSnapLocationType(BOTTOM,r1,r2); return;
		case LEFT+CENTER: 	r1.y=(int)(r2.getCenterY()-r1.getHeight()/2)+getSnapVOffset();return;
		case RIGHT+CENTER: r1.y=(int)(r2.getCenterY()-r1.getHeight()/2)+getSnapVOffset();return;
		
		case TOP+CENTER:    r1.x=(int)(r2.getCenterX()-r1.getWidth()/2)+getSnapHOffset(); return;
		case BOTTOM+CENTER:r1.x=(int)(r2.getCenterX()-r1.getWidth()/2)+getSnapHOffset(); return;
		/**The center+center one was added after the normal center got in the way of the +CENTER versions of the external snaps*/
		case MIDDLE: {r1.y=(int)(r2.getCenterY()-r1.getHeight()/2)+getSnapVOffset(); r1.x=(int)(r2.getCenterX()-r1.getWidth()/2)+getSnapHOffset(); }return;
		
		case CornerToCenter_LowerLeft: {r1.x=(int)r2.getCenterX()+getSnapHOffset(); r1.y=(int) (r2.getY()-r1.height)+getSnapVOffset();; return;}
		case CornerToCenter_UpperRight: {r1.x=(int)r2.getCenterX()+getSnapHOffset()-r1.width; r1.y=(int) (r2.getMaxY())+getSnapVOffset();; return;}
		default: return;
		}
		

	}
	
	/**The Horizontal offset and the vertical offset are either in the + or i direction,
	  returns those directions*/
	public int[] getOffSetPolarities() {
		
		if (snapType==INTERNAL) {
			return getOffsetPolarityInternal(snapLocationTypeInternal);
		
		}
		if (snapType==EXTERNAL) {
			return getOffsetPolarityExternal(snapLocationTypeExternal);
		}
		
		return new int[] {1,1};
	}
	
	private int[] getOffsetPolarityInternal(int locationType) {
		return new int[] {getDirectionInternalSnapOffSetX(locationType),getDirectionInternalSnapOffSetY(locationType)};
	}
	
	/**The direction of offset for external snaps, still working on the bugs for this one*/
	private int[] getOffsetPolarityExternal(int locationType) {
		int dx=1;
		int dy=1;
		
		int[] internal= getOffsetPolarityInternal(locationType%factor);
		dx=dx*internal[0];
		dy=dy*internal[1];
		switch (locationType/factor) {
		
		case RIGHT: dx=1;break;
		case BOTTOM:dy=1; break;
		case LEFT: dx=-1;break;
		case TOP:dy=-1; break;
		
		
		}
		
		
		
		return new int[] {dx,dy};
	}
	
	private int getDirectionInternalSnapOffSetX(int locationType) {
		switch (locationType) {
		case LEFT: return 1;
		case RIGHT:  return -1;
		case UPPER_LEFT: return getDirectionInternalSnapOffSetX(LEFT);
		case LOWER_LEFT: return getDirectionInternalSnapOffSetX(LEFT);
		case UPPER_RIGHT:return getDirectionInternalSnapOffSetX(RIGHT);
		case LOWER_RIGHT:return getDirectionInternalSnapOffSetX(RIGHT);

		default: return 1;
		}
	}
	
	private int getDirectionInternalSnapOffSetY(int locationType) {
		switch (locationType) {
	
		case TOP:	return 1;
		case BOTTOM: return -1;
		case UPPER_LEFT: return getDirectionInternalSnapOffSetY(TOP);
		case LOWER_LEFT: return getDirectionInternalSnapOffSetY(BOTTOM);
		case UPPER_RIGHT: return getDirectionInternalSnapOffSetY(TOP);
		case LOWER_RIGHT: return getDirectionInternalSnapOffSetY(BOTTOM);
	
		default: return 1;
		}
	}
	
	/**Handles internal corner to internal corner snapping. snaps location of r1 to r2.
	  locations are from r1's point of view. Possible arguments are right, left, top, bottom from 
	  RectangleEdgePositions*/
	private void doExternalSnapAtSide(int locationType, Rectangle2D.Double r1, Rectangle2D.Double r2) {
		switch (locationType) {
		case LEFT:  r1.x=r2.x+r2.width+getSnapHOffset(); return;//snaps left side of r1 to r2's right
		case RIGHT: r1.x=r2.x-r1.width-getSnapHOffset(); return;//snaps the right side of r1 to r2's left
		case TOP:	r1.y=r2.y+r2.height+getSnapVOffset(); return; //snaps the top of r1 to r2's bottom
		case BOTTOM:r1.y=r2.y-r1.height-getSnapVOffset(); return;//snaps the bottom of r1 to r2's top
		}

	}
	
	/**Snaps Rectangle r1. Performs a snap where the side given is from r2's point of view */
	private void doExternalSnapSideOfPanel(int locationType, Rectangle2D.Double r1, Rectangle2D.Double r2) {
		int l2 = RectangleEdges.oppositeSide(locationType);
		doExternalSnapAtSide(l2,r1, r2);
	}
	
	
	/**Performs an external snap based on possible location types.
	  Possible areguments are the external locations from RectangleEdgePositions
	  The call to the internal snap is problematic as it combines */
	private void doExternalSnap(int locationType, Rectangle2D.Double r1, Rectangle2D.Double r2) {
		doExternalSnapSideOfPanel(locationType/factor,r1,r2);
		doInternalSnapLocationType(locationType%factor,r1,r2);
		if (locationType%factor==CENTER) {
			doInternalSnapLocationType(locationType/factor+locationType%factor,r1,r2);
		}
	}
	
	/**Snaps rectangle 1 inside of rectangle 2. Possible Arguments are the internal positions
		from RectangleEdgePositions.
		Snaps so that two edges will be the same
		*/
	public void doInternalSnapEdgePointToEdgePoint(int locationType, Rectangle2D.Double r1, Rectangle2D.Double r2) {
		switch (locationType) {
			case LEFT:  
			case RIGHT:
			case TOP:	
			case BOTTOM:
			//case CENTER:
				doInternalSnapLocationType(locationType,r1,r2);
				doInternalSnapLocationType(CENTER+locationType,r1,r2);
				return;
			
		}
		switch (locationType) {
			default: doInternalSnapLocationType(locationType,r1,r2);
		}
		
	}
	
	
	public double getSnapHOffset() {
		return snapHOffset;
	}
	public void setSnapHOffset(double snapHOffset) {
		this.snapHOffset = snapHOffset;
	}
	public double getSnapVOffset() {
		return snapVOffset;
	}
	public void setSnapVOffset(double snapVOffset) {
		this.snapVOffset = snapVOffset;
	}

	public int getSnapType() {
		return snapType;
	}
	public void setSnapType(int snapType) {
		this.snapType = snapType;
	}
	public int getSnapLocationTypeInternal() {
		return snapLocationTypeInternal;
	}
	public void setSnapLocationTypeInternal(int snapLocationTypeInternal) {
		this.snapLocationTypeInternal = snapLocationTypeInternal;
	}
	public int getSnapLocationTypeExternal() {
		return snapLocationTypeExternal;
	}
	public void setSnapLocationTypeExternal(int snapLocationTypeExternal) {
		this.snapLocationTypeExternal = snapLocationTypeExternal;
	}
	public String[] getStapTypeChoices() {
		return stapTypeChoices;
	}
	public boolean isSupressed() {
		return supressed;
	}
	public void setSupressed(boolean supressed) {
		this.supressed = supressed;
	}
	
	public String toString() {
		String des = ""+this.getDescription();
		des+=" hoffset "+this.getSnapHOffset();
		des+=" voffset "+this.getSnapVOffset();
		return des;
	}
	public int getUseExtendedBounds() {
		return useExtendedBounds;
	}
	public void setUseExtendedBounds(int useExtendedBounds) {
		this.useExtendedBounds = useExtendedBounds;
	}
	public static String[] getBoundsChoices() {
		return boundsChoices;
	}
	public int getGridLayoutSnapType() {
		return gridLayoutSnapType;
	}
	public void setGridLayoutSnapType(int gridLayoutSnapType) {
		this.gridLayoutSnapType = gridLayoutSnapType;
	}
	public static String[] getGridChoices() {
		return gridChoices;
	}
	
	public int getGridChoiceNumbers() {
		return getGridchoices()[this.getGridLayoutSnapType()];
	}
	public static int[] getGridchoices() {
		return gridchoices;
	}

	
	/**returns the snapping behavior of external snaps after a diagnol flip*/
	private int diagnalFlip() {
		int e = this.getSnapLocationTypeExternal();
		switch(e) {
		case ABOVE_AT_LEFT: return LEFT_SIDE_BOTTOM;
		case  LEFT_SIDE_BOTTOM: return ABOVE_AT_LEFT;
		case ABOVE_AT_MIDDLE: return LEFT_SIDE_MIDDLE;
		case  LEFT_SIDE_MIDDLE: return ABOVE_AT_MIDDLE ;
		case ABOVE_AT_RIGHT: return LEFT_SIDE_TOP;
		case LEFT_SIDE_TOP: return ABOVE_AT_RIGHT;
		}
		
		return e;
	}
	
	/**swaps row label positions with column label ones*/
	public void flipDiag(){
		this.setSnapLocationTypeExternal(diagnalFlip() );
		
		if (this.getGridLayoutSnapType()==1) this.setGridLayoutSnapType(2);
		else if (this.getGridLayoutSnapType()==2) this.setGridLayoutSnapType(1);
	}
	
	
	public static ArrayList<SnappingPosition> findAllSnappings(ArrayList<LocatedObject2D> objects) {
		ArrayList<SnappingPosition> bahs=new ArrayList<SnappingPosition>();
		for(LocatedObject2D o: objects) {
			if (o.getSnappingBehaviour()!=null && !bahs.contains(o.getSnappingBehaviour())) {
				bahs.add(o.getSnappingBehaviour());
			}
		}
		return bahs;
	}
	
	
	private Point2D lastScaleAbout;
	public void scaleAbout(Point2D p, double scale) {
		if(p==lastScaleAbout) return; //in the event that this was already scaled
		lastScaleAbout=p;
		snapHOffset*=scale;
		snapVOffset*=scale;
		
	}

}
