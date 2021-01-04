/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 4, 2021
 * Version: 2021.1
 */
package locatedObject;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import layout.basicFigure.LayoutSpaces;
import logging.IssueLog;

/**The positions of certain objects (like text and scale bars). 
  are set relative to their parent panels. This class stores the preferred 
  position of one object and contains methods related to placing the object in position
  and calculating that location (in x,y). 
  @see RectangleEdgePositions interface for information about
  there location codes. Locations may be internal (within the parent objects bounds) 
  or external (outside of it). In either case, each position is offset from the edge of the 
  parent panel. the magnitude of the offset is a changeable value. the direction of the offset
  depends on where the attached object is relative to the parent panel.
  For row and column labels attached to a layout, another field indicating the type of layout positions
  is also important @see LayoutSpaces for more information about the layout locations*/
public class AttachmentPosition implements  RectangleEdgePositions, Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double horizontalOffset=0;
	private double verticalOffset=0;
	

	private boolean supressed=false;
	
	/**some objects have both a bounds and an extended bounds,
	 *  the extended bounds option determines how the position is determined
	 *  see manual
	 *  */
	private static final String[] boundsChoices=new String[] {"Normal ", "Extened Bounds"};
	public static final int NORMAL_BOUNDS=0, EXTENDED_BOUNDS=1;
	private int useExtendedBounds=NORMAL_BOUNDS;
	
	
	
	/**values determine whether the position in question is inside or outside of its parent object*/
	private final String[] locationCategoryChoices=new String[] {"Internal Snap", "External Snaps"};
	public static final int INTERNAL=0, EXTERNAL=1;
	/**whether this snaps to an internal or an external location*/
	private int snapType=INTERNAL; 
	
	/**items attached to a layout may be attached to either a row, coloumn, panel or comthing else
	 * the descriptions of the options for the parent panel are listed here
	 * */
	private static String[] gridChoices=new String[] {"To Panel", "To Column", "To Row", "To Montage", "To Block of Panels"};
	
	/**the layout space codes for every layout location*/
	private static int[] gridSpaceCodes=new int[] {LayoutSpaces.PANELS,LayoutSpaces.COLUMN_OF_PANELS,LayoutSpaces.ROW_OF_PANELS, LayoutSpaces.ALL_MONTAGE_SPACE, LayoutSpaces.BLOCK_OF_PANELS};
	private int gridLayoutSnapType=Arrays.binarySearch(gridSpaceCodes, LayoutSpaces.PANELS);
	
	
	/**the internal and external snapping types. around 20 in total*/
	private int snapLocationTypeInternal=RectangleEdgePositions.UPPER_LEFT;
	private int snapLocationTypeExternal= RectangleEdgePositions.ALL_EXTERNAL_LOCATIONS[0];
	
	
	public AttachmentPosition() {}
	
	/**when given a parent object, and an attached item 
	  changes the location of the attached object based on the bounds of the parent
	   this position*/
	public void snapLocatedObjects(LocatedObject2D attachedObject, LocatedObject2D parentObject) {
		Rectangle r2 = parentObject.getBounds();
		snapObjectToRectangle(attachedObject, r2);

	}
	
	
	/**returns an array with every internal position code*/
	public static int[] internalSnapOptions() {
		return RectangleEdgePositions.internalAttachmentLocations;
		
	}
	
	/**returns an array with every external position code*/
	public static int[] externalSnapOptions() {
		return RectangleEdgePositions.ALL_EXTERNAL_LOCATIONS;
		
	}
	
	/**returns true if the given position has all the same settings as this one*/
	public boolean same(AttachmentPosition position) {
		if(position.snapLocationTypeInternal!=snapLocationTypeInternal) return false;
		if(position.snapLocationTypeExternal!=snapLocationTypeExternal) return false;
		if(position.snapType!=snapType) return false;
		if(position.gridLayoutSnapType!=gridLayoutSnapType) return false;
		if(position.horizontalOffset!=horizontalOffset) return false;
		if(position.verticalOffset!=verticalOffset) return false;
		if(position.snapRangeH!=snapRangeH) return false;
		if(position.snapRangeV!=snapRangeV) return false;
		if(position.useExtendedBounds!=useExtendedBounds) return false;
		if(position.gridLayoutSnapType!=gridLayoutSnapType) return false;
		if(position.supressed!=this.supressed)return false;
		return true;
		
	}
	
	/**returns the choice index for the given grid location type*/
	static int translateSnapTypeToChoiceIndex(int it) {
		for(int i=0;i< gridSpaceCodes.length; i++) {
			if ( gridSpaceCodes[i]==it) return i; 
		}
		return 0;
	}
	/**when given a grid location type, sets the grid location snap type */
	public void setGridChoice2(int choice) {
		this.setGridLayoutSnapType(translateSnapTypeToChoiceIndex(choice));
	}
	
	/**returns the location for an external label that is above the object*/
	public static AttachmentPosition defaultExternal() {
		AttachmentPosition output = new AttachmentPosition();
		output.setLocationTypeExternal(ABOVE_AT_MIDDLE);
		output.setLocationCategory(EXTERNAL);
		return output;
	}
	
	/**returns the default location for an external label that is a row label*/
	public static AttachmentPosition defaultRowLabel() {
		AttachmentPosition out = defaultRowSide(); 
		out.setHorizontalOffset(2); 
		return out;
	}
	
	/**returns the default location for an object that is beside a row of a layout*/
	public static AttachmentPosition defaultRowSide() {
		AttachmentPosition output = new AttachmentPosition();
		output.setLocationTypeExternal(LEFT_SIDE_MIDDLE);
		output.setGridChoice2(LayoutSpaces.ROW_OF_PANELS);
		output.setLocationCategory(EXTERNAL);
		return output;
	}
	
	/**returns the location for a label that is a column label and located above the columns, centered*/
	public static AttachmentPosition defaultColLabel() {
		AttachmentPosition out = defaultColSide(); out.setVerticalOffset(1); return out;
	}
	/**returns the default location for an object that is beside a column of a layout*/
	public static AttachmentPosition defaultColSide() {
		AttachmentPosition output = new AttachmentPosition();
		output.setLocationTypeExternal(ABOVE_AT_MIDDLE);
		output.setGridChoice2(LayoutSpaces.COLUMN_OF_PANELS);
		output.setLocationCategory(EXTERNAL);
		return output;
	}
	
	/**returns the default location for an external label that is positioned to be a title for a plot*/
	public static AttachmentPosition defaultPlotTitle() {
		AttachmentPosition output = new AttachmentPosition();
		output.setLocationTypeExternal(ABOVE_AT_MIDDLE);
		output.setGridChoice2(LayoutSpaces.ALL_OF_THE+LayoutSpaces.PANELS);
		output.setLocationCategory(EXTERNAL);
		return output;
	}
	
	/**returns the default location for an external label that is positioned to be a label at
	  the bottom of a plot referring to a data series*/
	public static AttachmentPosition defaultPlotBottomSide() {
		AttachmentPosition output = new AttachmentPosition();
		output.setLocationTypeExternal(CornerToCenter_UpperRight);
		output.setGridChoice2(LayoutSpaces.COLUMN_OF_PANELS);
		output.setVerticalOffset(6);
		output.setHorizontalOffset(4);
		output.setLocationCategory(EXTERNAL);
		return output;
	}
	
	/**returns the default location for an external label that is positioned to be a label at
	  at the left side of a plot*/
	public static AttachmentPosition defaultPlotSide() {
		AttachmentPosition output = new AttachmentPosition();
		output.setLocationTypeExternal(LEFT_SIDE_MIDDLE);
		output.setGridChoice2(LayoutSpaces.ROW_OF_PANELS);
		output.setVerticalOffset(6);
		output.setHorizontalOffset(4);
		output.setLocationCategory(EXTERNAL);
		return output;
	}
	
	/**returns the default location for an external label that is positioned to be a label within
	 * the legend of a plot*/
	public static AttachmentPosition defaultPlotLegand() {
		AttachmentPosition output = new AttachmentPosition();
		output.setLocationTypeExternal(RIGHT_SIDE_MIDDLE);
		output.setHorizontalOffset(4);
		output.setLocationCategory(1);
		return output;
	}
	
	
	
	/**the location for an object that is to be placed to the right of its parent
	    */
	public static AttachmentPosition partnerExternal() {
		AttachmentPosition output = new AttachmentPosition();
		output.setLocationTypeExternal(RectangleEdgePositions.RIGHT_SIDE_TOP);
		output.setLocationCategory(EXTERNAL);
		return output;
	}
	
	/**Returns a list of positions that are on the right and left sides of the parent object*/
	public static ArrayList<AttachmentPosition> externalRightLeft() {
		ArrayList<AttachmentPosition> out = new  ArrayList<AttachmentPosition>();
		AttachmentPosition o1 = partnerExternal() ;
		o1.setLocationTypeExternal(RectangleEdgePositions.RIGHT_SIDE_TOP);
		out.add(o1);
		
		o1=partnerExternal() ;
		o1.setLocationTypeExternal(RectangleEdgePositions.RIGHT_SIDE_MIDDLE);
		out.add(o1);
		
		o1=partnerExternal() ;
		o1.setLocationTypeExternal(RectangleEdgePositions.RIGHT_SIDE_BOTTOM);
		out.add(o1);
		
		o1=partnerExternal() ;
		o1.setLocationTypeExternal(RectangleEdgePositions.LEFT_SIDE_TOP);
		out.add(o1);
		
		o1=partnerExternal() ;
		o1.setLocationTypeExternal(RectangleEdgePositions.LEFT_SIDE_MIDDLE);
		out.add(o1);
		
		o1=partnerExternal() ;
		o1.setLocationTypeExternal(RectangleEdgePositions.LEFT_SIDE_BOTTOM);
		out.add(o1);
		
		return out;
	}
	
	/**a list of snapping positions that are for placing panels inside the parent panel 
	 * along the edges*/
	public static ArrayList<AttachmentPosition> internalSpread() {
		ArrayList<AttachmentPosition> out = new  ArrayList<AttachmentPosition>();
		out.add(defaultInternal(AttachmentPosition.UPPER_RIGHT));
		out.add(defaultInternal(AttachmentPosition.RIGHT));
		out.add(defaultInternal(AttachmentPosition.LOWER_RIGHT));
		out.add(defaultInternal(AttachmentPosition.BOTTOM));
		out.add(defaultInternal(AttachmentPosition.LOWER_LEFT));
		return out;
	}
	

	/**returns true if the attachment location is inside of the parent panel*/
	public boolean isInternalSnap() {
		return snapType==INTERNAL;
	}
	/**returns true if the attachment location is outside of the parent panel*/
	public boolean isExternalSnap() {
		return snapType==EXTERNAL;
	}
	
	
	/**returns true if this one places the mobile object to the right of the parent, false otherwise*/
	public boolean isExternalRightSnap() {
		if (isInternalSnap()) return false;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.RIGHT_SIDE_BOTTOM) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.RIGHT_SIDE_TOP) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.RIGHT_SIDE_MIDDLE) return true;
		
		
		return false;
	}
	
	/**returns true if this one places the mobile object to the left of the parent, false otherwise*/
	public boolean isExternalLeftSnap() {
		if (isInternalSnap()) return false;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.LEFT_SIDE_BOTTOM) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.LEFT_SIDE_TOP) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.LEFT_SIDE_MIDDLE) return true;
		
		
		return false;
	}
	
	/**returns true if this is an external snap that moves an object below a parent object*/
	public boolean isExternalBottomEdgeSnap() {
		if (isInternalSnap()) return false;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.BELOW_AT_RIGHT) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.BELOW_AT_LEFT) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.BELOW_AT_MIDDLE) return true;
		
		
		return false;
	}
	
	/**returns true if this is an external snap that moves an object below a parent object*/
	public boolean isExternalTopEdgeSnap() {
		if (isInternalSnap()) return false;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.ABOVE_AT_RIGHT) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.ABOVE_AT_LEFT) return true;
		if (this.getSnapLocationTypeExternal()==RectangleEdgePositions.ABOVE_AT_MIDDLE) return true;
		
		
		return false;
	}
	

	/**creates an identical duplicate*/
	public AttachmentPosition copy() {
		AttachmentPosition o = new AttachmentPosition();
		givePropertiesTo(o);
		
		return o;
	}
	
	/**Sets all the trait of the position given to be identical to this one*/
	public void givePropertiesTo(AttachmentPosition position) {
		if (position==null) return;
		position.setHorizontalOffset(this.getHorizontalOffset());
		position.setVerticalOffset(this.getVerticalOffset());
		position.copyPositionFrom(this);
		position.setGridLayoutSnapType(this.getGridLayoutSnapType());
	}

	/**creates a non-identical copy. one that is similar except that there is no offset x or y*/
	public AttachmentPosition copyWOofffsets() {
		AttachmentPosition o = copy();
		o.setHorizontalOffset(0);
		o.setVerticalOffset(0);
		return o;
	}
	
	/**takes the location but not the offsets from the argument*/
	public void copyPositionFrom(AttachmentPosition  s) {
			if(s==null) return;
		 setLocationCategory(s.getLocationCategory());
		 setLocationTypeInternal(s.getSnapLocationTypeInternal());
		 setLocationTypeExternal(s.getSnapLocationTypeExternal());
		 setSupressed(s.isSupressed());
	}
	
	/**Returns the default internal position for an object that is attached to a panel
	  if an object is attached with not position, then this method will create one*/
	public static AttachmentPosition defaultInternal() {
		 return defaultInternal(LOWER_RIGHT);
	}
	
	/**returns the default position for an image panel that is attached to a parent layout*/
	public static AttachmentPosition defaultInternalPanel() {
		 return defaultInternal(UPPER_LEFT);
		
	}
	
	/**returns an internal position that is defined by the argument*/
	private static AttachmentPosition defaultInternal(int position) {
		AttachmentPosition output = new AttachmentPosition();
		output.setLocationTypeExternal(position);
		output.setLocationTypeInternal(position);
		output.setLocationCategory(INTERNAL);
		return output;
	}
	
	/**returns the default position for scale bars*/
	public static AttachmentPosition defaultScaleBar() {
		AttachmentPosition output = new AttachmentPosition();
		output.setLocationTypeInternal(LOWER_RIGHT);
		output.setLocationCategory(INTERNAL);
		output.setVerticalOffset(4);
		output.setHorizontalOffset(4);
		return output;
	}
	
	
	/**returns the default position for panel labels*/
	public static AttachmentPosition defaultPanelLabel() {
		AttachmentPosition output = new AttachmentPosition();
		output.setLocationTypeExternal(UPPER_LEFT);
		output.setLocationCategory(INTERNAL);
		output.setHorizontalOffset(1);
		return output;
	}
	
	
	/**Sets the location of the target object to the bounds given*/
	public void snapObjectToRectangle(LocatedObject2D attachedObject, Rectangle2D parentBounds) {
			if (this.isSupressed()||attachedObject==null||parentBounds==null) return;
		Rectangle r1 = attachedObject.getBounds().getBounds();//a new rectangle for the current location
		Rectangle r1ex=attachedObject.getExtendedBounds();
		
		/**if the extended bounds of an object are to be used, there is some increase in the distance 
		   used for external positions*/
		if (this.getUseExtendedBounds()==EXTENDED_BOUNDS) {
			if (isVerticalOutside() ) r1=new Rectangle(r1.x, r1ex.y, r1.width, r1ex.height) ;
			if (isHorozontalOutside() ) r1=new Rectangle(r1ex.x, r1.y, r1ex.width, r1.height) ;
			}
	
		snapRects(r1,parentBounds);
	
		attachedObject.setLocationUpperLeft(r1.x, r1.y);
		
		/**if the extended bounds of an object are to be used, may need to subtract the difference between those and the regular bounds\
		   fron the location*/
			if (this.getUseExtendedBounds()==EXTENDED_BOUNDS)  {
				double dx =0,dy=0;
				if (isHorozontalOutside() )dx=attachedObject.getExtendedBounds().getX()-attachedObject.getBounds().getX();
				if (isVerticalOutside() ) dy=attachedObject.getExtendedBounds().getY()-attachedObject.getBounds().getY();
			attachedObject.moveLocation(-(int)dx, -(int)dy);
			
		}
	}
	
	/**returns true if the position is outside the parent object and on the left/right sites*/
	private boolean isHorozontalOutside() {
		if (this.getLocationCategory()==INTERNAL) return false;
		 if (this.getSnapLocationTypeExternal()/factor==LEFT) return true;
		 if (this.getSnapLocationTypeExternal()/factor==RIGHT) return true;
		
		return false;
	}
	
	/**returns true if the position is outside the parent object and on the top/bottom sites*/
	private boolean isVerticalOutside() {
			if (this.getLocationCategory()==INTERNAL) return false;
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
	
	/**Changes the location for rectangle r1, setting its location relative to the parent panel  r2*/
	private void snapRects(Rectangle2D.Double r1, Rectangle2D.Double r2) {
			if (snapType==INTERNAL) {
				doInternalSnapEdgePointToEdgePoint(snapLocationTypeInternal,r1,r2);
			
			}
			if (snapType==EXTERNAL) {
				doExternalSnap(snapLocationTypeExternal, r1, r2);
			}
	}
	
	/**returns a string describing the current position*/
	public String getDescription() {
		if (snapType==INTERNAL) {
				return RectangleEdges.locationToString(getSnapLocationTypeInternal());
			}else {
				return RectangleEdges.locationToString(getSnapLocationTypeExternal());
			}
	}
	
	/**returns a string with a short description of the current position*/
	public String getShortDescription() {
		String st=this.getDescription();
		return st.split(",")[0];
	}
	
	/**Assuming the get description string returns something with an internal comma
	 returns a string with the second half of a descriptor*/
	public String getSecondDescription() {
		String st=this.getDescription();
		if (st.contains(","))	return st.split(",")[1];
		return "";
	}
	
	/**returns a set of possible locations for internal positions*/
	private HashMap<Integer, Rectangle2D> getAllPossibleInternalPositions(Rectangle2D attachedObjectBounds, Rectangle2D  parentBounds) {
		HashMap<Integer, Rectangle2D> output = new HashMap<Integer, Rectangle2D>();
		
		/**creates copies*/
		Rectangle2D.Double rAttached=new Rectangle2D.Double();rAttached.setRect(attachedObjectBounds);
		Rectangle2D.Double rPanel=new Rectangle2D.Double();rPanel.setRect(parentBounds);
		
		for(int i:  internalSnapOptions() ) {
			Rectangle2D dub = rAttached.getBounds();//creates a new rectangle
			doInternalSnapEdgePointToEdgePoint(i,rAttached,rPanel);
			
			dub.setRect(rAttached.getBounds());//sets the rectangle
			output.put(i,dub);
		}
		
		return output;
	}
	

	
	/**when given the position of two rectangles, it sets this object to the
	 snapping position that is nearest to point p*/
	public void setToNearestSnap(Rectangle2D r1, Rectangle2D r2, Point2D p) {
		if(r2==null||r1==null) return;
		if (r2.contains(p)) {
			this.setLocationCategory(INTERNAL);;
			setToNearestInternalSnap(r1,r2,p);
			} else 
				{this.setLocationCategory(EXTERNAL);setToNearestExternalSnap(r1,r2,p);}
		
	}
	
	/**sets the internal position field such that the resulting pacement for the attached item is 
	  near the click point*/
	public void setToNearestInternalSnap(Rectangle2D rAttached, Rectangle2D rParent, Point2D clickPoint) {
		this.setLocationTypeInternal(getNearestInternalSnap(rAttached,rParent,clickPoint));
	}
	public void setToNearestExternalSnap(Rectangle2D r1, Rectangle2D r2, Point2D p) {
		this.setLocationTypeExternal(getNearestExternalSnap(r1,r2,p));
	}
	
	/**when given the bounding rectangles for the parent panel and the attached object
	  returns the internal position that is nearest to the click point*/
	protected int getNearestInternalSnap(Rectangle2D rAttached, Rectangle2D rParent, Point2D clickPoint) {
		HashMap<Integer, Rectangle2D> ss = getAllPossibleInternalPositions(rAttached,rParent);
		
		return getNearest(ss,clickPoint);
	}
	/**when given the bounding rectangles for the parent panel and the attached object
	  return the external position that is nearest to the click point*/
	protected int getNearestExternalSnap(Rectangle2D attachedItemBounds, Rectangle2D parentPanelBounds, Point2D clickPoint) {
		HashMap<Integer, Rectangle2D> ss = getAllPossibleExternalSnaps(attachedItemBounds, parentPanelBounds);
		return getNearest(ss,clickPoint);
	}
	
	/**returns a hashmap with rectangles representing every external location that the attached item may be placed*/
	private HashMap<Integer, Rectangle2D> getAllPossibleExternalSnaps(Rectangle2D attachedItemBounds, Rectangle2D parentPanelBounds) {
		HashMap<Integer, Rectangle2D> output =new HashMap<Integer, Rectangle2D>();
		
		Rectangle2D.Double rAttached=new Rectangle2D.Double();
		rAttached.setRect(attachedItemBounds);
		Rectangle2D.Double rParent=new Rectangle2D.Double(); 
		rParent.setRect(parentPanelBounds);
		
		for(int i:  externalSnapOptions() ) {
			Rectangle dub = rAttached.getBounds();
			doExternalSnap(i,rAttached,rParent);
			dub=rAttached.getBounds();
			output.put(i,dub);
		}
		return output;
	}
	
	/**when given a point and a hashmap with integer keys referencing rectangles of different locations
	  returns the location of the rectangle that is nearest the point  */
	private static int getNearest(HashMap<Integer, Rectangle2D> rects, Point2D p) {
		double lowestd=Double.MAX_VALUE;
		int one=1;
		for(int i: rects.keySet()) try {
			Rectangle2D r = rects.get(i);
			double dist=p.distance(r.getCenterX(), r.getCenterY());
			if (dist<lowestd) {
				lowestd=dist;
				one=i;
						
			}
		} catch (Throwable t) {IssueLog.logT(t);}
		return one;
	}
	

	
	/**returns an array containing each of the 4 vertex points of the rectangle*/
	public static   Point2D.Double[] RectangleVertices(Rectangle2D rect) {
		 Point2D.Double[] output = new Point2D.Double[4];
		 output[0]=new Point2D.Double(rect.getX(), rect.getY());
		 output[1]=new Point2D.Double(rect.getX()+rect.getWidth(), rect.getY());
		 output[2]=new Point2D.Double(rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight());
		 output[3]=new Point2D.Double(rect.getX(), rect.getY()+rect.getHeight());
		 return output;
	  }

	
	
	
	/**Handles internal corner to internal corner snapping. snaps location of r1 to r2*/
	private void doInternalSnapLocationType(int locationType, Rectangle2D.Double r1, Rectangle.Double referenceRectangle) {
		
		switch (locationType) {
		case LEFT:  r1.x=referenceRectangle.x+getHorizontalOffset(); {}; return;
		case RIGHT: r1.x=referenceRectangle.x+referenceRectangle.width-r1.width-getHorizontalOffset(); return;
		case TOP:	r1.y=referenceRectangle.y+getVerticalOffset(); return;
		case BOTTOM:r1.y=referenceRectangle.y+referenceRectangle.height-r1.height-getVerticalOffset(); return;
		case UPPER_LEFT: doInternalSnapLocationType(LEFT,r1,referenceRectangle);doInternalSnapLocationType(TOP,r1,referenceRectangle); return;
		case LOWER_LEFT:doInternalSnapLocationType(LEFT,r1,referenceRectangle);doInternalSnapLocationType(BOTTOM,r1,referenceRectangle); return;
		case UPPER_RIGHT:doInternalSnapLocationType(RIGHT,r1,referenceRectangle);doInternalSnapLocationType(TOP,r1,referenceRectangle); return;
		case LOWER_RIGHT:doInternalSnapLocationType(RIGHT,r1,referenceRectangle);doInternalSnapLocationType(BOTTOM,r1,referenceRectangle); return;
		case LEFT+CENTER: 	r1.y=(int)(referenceRectangle.getCenterY()-r1.getHeight()/2)+getVerticalOffset();return;
		case RIGHT+CENTER: r1.y=(int)(referenceRectangle.getCenterY()-r1.getHeight()/2)+getVerticalOffset();return;
		
		case TOP+CENTER:    r1.x=(int)(referenceRectangle.getCenterX()-r1.getWidth()/2)+getHorizontalOffset(); return;
		case BOTTOM+CENTER:r1.x=(int)(referenceRectangle.getCenterX()-r1.getWidth()/2)+getHorizontalOffset(); return;
		/**Including a center alone in this method got in the way of the +CENTER versions of the external snaps. so center is not included*/
		case MIDDLE: {r1.y=(int)(referenceRectangle.getCenterY()-r1.getHeight()/2)+getVerticalOffset(); r1.x=(int)(referenceRectangle.getCenterX()-r1.getWidth()/2)+getHorizontalOffset(); }return;
		
		case CornerToCenter_LowerLeft: {r1.x=(int)referenceRectangle.getCenterX()+getHorizontalOffset(); r1.y=(int) (referenceRectangle.getY()-r1.height)+getVerticalOffset();; return;}
		case CornerToCenter_UpperRight: {r1.x=(int)referenceRectangle.getCenterX()+getHorizontalOffset()-r1.width; r1.y=(int) (referenceRectangle.getMaxY())+getVerticalOffset();; return;}
		default: return;
		}
		

	}
	
	/**The Horizontal offset and the Vertical offset are either in the + or - direction,
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
	
	/**returns the directions for the offset for internal positions.*/
	private int[] getOffsetPolarityInternal(int locationType) {
		return new int[] {getDirectionInternalSnapOffSetX(locationType),getDirectionInternalSnapOffSetY(locationType)};
	}
	
	/**returns the directions for the offset for external positions.*/
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
	
	/**returns the direction (positive or negative +/-) of the vertical offset*/
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
	
	/**returns the direction (positive or negative +/-) of the horizontal offset*/
	private int getDirectionInternalSnapOffSetY(int locationType) {
		
		switch (locationType) {
		case CENTER: return 1;
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
		case LEFT:  r1.x=r2.x+r2.width+getHorizontalOffset(); return;//snaps left side of r1 to r2's right
		case RIGHT: r1.x=r2.x-r1.width-getHorizontalOffset(); return;//snaps the right side of r1 to r2's left
		case TOP:	r1.y=r2.y+r2.height+getVerticalOffset(); return; //snaps the top of r1 to r2's bottom
		case BOTTOM:r1.y=r2.y-r1.height-getVerticalOffset(); return;//snaps the bottom of r1 to r2's top
		}

	}
	
	/**Snaps Rectangle r1. Performs a snap where the side given is from r2's point of view */
	private void doExternalSnapSideOfPanel(int locationType, Rectangle2D.Double r1, Rectangle2D.Double r2) {
		int l2 = RectangleEdges.oppositeSide(locationType);
		doExternalSnapAtSide(l2,r1, r2);
	}
	
	
	/**Performs an external snap based on possible location types.
	  Possible arguments are the external locations from RectangleEdgePositions
	   */
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
	
	
	public double getHorizontalOffset() {
		return horizontalOffset;
	}
	public void setHorizontalOffset(double snapHOffset) {
		this.horizontalOffset = snapHOffset;
	}
	public double getVerticalOffset() {
		return verticalOffset;
	}
	public void setVerticalOffset(double snapVOffset) {
		this.verticalOffset = snapVOffset;
	}

	/**returns the location type as internal or external*/
	public int getLocationCategory() {
		return snapType;
	}
	/**sets the location type as internal or external*/
	public void setLocationCategory(int snapType) {
		this.snapType = snapType;
	}
	
	/**returns what type of internal location this object uses
	 * @see RectangleEdgePositions
	 * @see RectangleEdges
	 * */
	public int getSnapLocationTypeInternal() {
		return snapLocationTypeInternal;
	}
	/**sets what type of internal location this object uses
	 * @see RectangleEdgePositions
	 * @see RectangleEdges
	 * */
	public void setLocationTypeInternal(int snapLocationTypeInternal) {
		this.snapLocationTypeInternal = snapLocationTypeInternal;
	}
	
	/**returns what type of external location this object uses
	 * @see RectangleEdgePositions
	 * @see RectangleEdges
	 * */
	public int getSnapLocationTypeExternal() {
		return snapLocationTypeExternal;
	}
	
	/**returns what type of internal location this object uses
	 * @see RectangleEdgePositions
	 * @see RectangleEdges
	 * */
	public void setLocationTypeExternal(int snapLocationTypeExternal) {
		this.snapLocationTypeExternal = snapLocationTypeExternal;
	}
	
	/**returns the names of the two location categories*/
	public String[] getLocationCategoryChoices() {
		return locationCategoryChoices;
	}
	
	public boolean isSupressed() {
		return supressed;
	}
	public void setSupressed(boolean supressed) {
		this.supressed = supressed;
	}
	
	public String toString() {
		String des = ""+this.getDescription();
		des+=" hoffset "+this.getHorizontalOffset();
		des+=" voffset "+this.getVerticalOffset();
		return des;
	}
	
	/**returns whether extended bounds are used as a basis for locations*/
	public int getUseExtendedBounds() {
		return useExtendedBounds;
	}
	/**set whether extended bounds are used as a basis for locations*/
	public void setUseExtendedBounds(int useExtendedBounds) {
		this.useExtendedBounds = useExtendedBounds;
	}
	/**the names of the options regarding how to use the bounds of an object*/
	public static String[] getBoundsChoices() {
		return boundsChoices;
	}
	
	/**returns the names of the grid spaces that may be used*/
	public static String[] getGridSpaceCodeNames() {
		return gridChoices;
	}
	
	/**returns the layout space code for the type of layout location chosen
	 * @see LayoutSpaces*/
	public int getGridSpaceCode() {
		return getGridchoices()[this.getGridLayoutSnapType()];
	}
	
	/**returns the choices for grid layout locations for attachment*/
	public static int[] getGridchoices() {
		return gridSpaceCodes;
	}
	
	/**returns the index for the type of grid location that this attached to*/
	public int getGridLayoutSnapType() {
		return gridLayoutSnapType;
	}
	/**set the index for the type of grid location that this attached to*/
	public void setGridLayoutSnapType(int gridLayoutSnapType) {
		this.gridLayoutSnapType = gridLayoutSnapType;
	}

	
	/**returns the external position that would result from a diagonal flip
	 * of a layout*/
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
	
	/**Performs a diagonal flip of for the layout locations
	 * swaps row label positions with column label ones. */
	public void flipDiag(){
		this.setLocationTypeExternal(diagnalFlip() );
		
		if (this.getGridLayoutSnapType()==LayoutSpaces.COLS) this.setGridLayoutSnapType(LayoutSpaces.ROWS);
		else if (this.getGridLayoutSnapType()==LayoutSpaces.ROWS) this.setGridLayoutSnapType(LayoutSpaces.COLS);
	}
	
	/**when given a list of located objects, returns a list containing every position from each object
	  if two objects share the same position, that position only appears once on the list */
	public static ArrayList<AttachmentPosition> findAllPositions(ArrayList<LocatedObject2D> objects) {
		ArrayList<AttachmentPosition> alreadyDone=new ArrayList<AttachmentPosition>();
		for(LocatedObject2D o: objects) {
			if (o.getAttachmentPosition()!=null && !alreadyDone.contains(o.getAttachmentPosition())) {
				alreadyDone.add(o.getAttachmentPosition());
			}
		}
		return alreadyDone;
	}
	
	/**stores the last point object used for scaling to ensure that this object will not be scale itself more than one time
	*/
	private Point2D lastScaleAbout;
	
	/**scales this position*/
	public void scaleAbout(Point2D p, double scale) {
		if(p==lastScaleAbout) return; //in the event that this was already scaled
		lastScaleAbout=p;
		horizontalOffset*=scale;
		verticalOffset*=scale;
	}
	
	
	
	/**given a rectangle you want to snap to rectangle 2 this returns a point with the 
	  snapped distance. This will snap to wherever is in range. r1's location changes.
	  not used*/
	@Deprecated
	public void snapBoundsSideIfInRange(Rectangle2D.Double r1, Rectangle2D.Double r2) {
		double snaph = getSnappingRangeH(r1.width) ;
		double snapv = getSnappingRangeV(r1.height) ;
		
		
		if (Math.abs(r1.x-r2.x)<snaph) r1.x=r2.x+horizontalOffset;// snaps the left side
		if (Math.abs(r1.x+r1.width-r2.x-r2.width)<snaph) r1.x=r2.x+r2.width-r1.width-horizontalOffset;// snaps the right side
		if (Math.abs(r1.y-r2.y)< snapv) r1.y=r2.y+verticalOffset;// snaps the top side
		if (Math.abs(r1.y+r1.height-r2.y-r2.height)< snapv) r1.y=r2.y+r2.height-r1.height-verticalOffset;// snaps the bottom side
		
		/**snaps the external side*/
		if (Math.abs(r1.x+r1.width-r2.x)<snaph){ r1.x=r2.x-r1.width-horizontalOffset;}//snaps right side to the left of r2
		if (Math.abs(r1.x-r2.x-r2.width)<snaph) {r1.x=r2.x+r2.width+horizontalOffset;}//snaps left side to the right of r2
		if (Math.abs(r1.y+r1.height-r2.y)< snapv) {r1.y=r2.y-r1.height-verticalOffset;}//snaps bottom to the top of r2
		if (Math.abs(r1.y-r2.y-r2.height)< snapv) {r1.y=r2.y+r2.height+verticalOffset;}//snaps top to the bottom of r2
		
		if (Math.abs(r1.getCenterX()-r2.getCenterX())<snaph) {
			r1.x=(int)(r2.getCenterX()-r1.getWidth()/2)+horizontalOffset;
		}
		
		if (Math.abs(r1.getCenterY()-r2.getCenterY())<snapv) {
			r1.y=(int)(r2.getCenterY()-r1.getHeight()/2)+verticalOffset;
		}
		
		return;
	}
	@Deprecated
	private double snapRangeH=0;
	@Deprecated
	private double snapRangeV=0;
	/**returns the horizontal range used used to determine if automated snapping of objects to nearby panels
	  will occur. This is only relevant in a handful of contexts*/
	@Deprecated
	private double getSnappingRangeH(double objectWidth) {
		return snapRangeH*objectWidth;
	};
	@Deprecated
	/**returns the vertical range used used to determine if automated snapping of objects to nearby panels
	  will occur. This is only relevant in a handful of contexts*/
	private double getSnappingRangeV(double objectHeight) {
		return snapRangeV*objectHeight;
	};

}
