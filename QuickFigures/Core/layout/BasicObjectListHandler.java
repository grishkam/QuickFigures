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
 * Date Modified: Jan 5, 2021
 * Version: 2022.0
 */
package layout;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import applicationAdapters.ImageWorkSheet;
import layout.basicFigure.BasicLayout;
import locatedObject.ArrayObjectContainer;
import locatedObject.LocatedObject2D;
import locatedObject.ObjectContainer;
import utilityClasses1.ArraySorter;

/**Contains many methods for moving objects from one part of a figure to another
 Most importantly, the methods below determine which objects qualify as inside
 of one panel versus another. These methods are critical for the a lot of the figure editing features.
  contains a lot of utility methods that are used in multiple places*/
public class BasicObjectListHandler {
	/**A variable for storing a copy of groups of objects
 	temporarily */
	public ArrayList<ArrayList<LocatedObject2D>> lastset=null;


	/**When given a layout and an object container, this returns an array of arrays with an array for each 
	 * layout panel.  the inner arrays contain the objects found inside each panel.*/
	public ArrayList<ArrayList<LocatedObject2D>> getObjectsInPanels(BasicLayout ml, ObjectContainer imp) {
		ArrayList<ArrayList<LocatedObject2D>> output=new ArrayList<ArrayList<LocatedObject2D>>();
		ml.setPanelRectangles();
		for (int i=1; i<=ml.getPanels().length; i++) {
			output.add(getOverlapOverlaypingItems(ml.getPanel(i),  imp)); ;
		}
		return output;
	}
	
	/**when given a point and a list of objects, returns the nearest object*/
	public LocatedObject2D nearest(Iterable<LocatedObject2D> list, Point2D p) {
		LocatedObject2D nearest=null;
		double distance=Double.MAX_VALUE;
		for(LocatedObject2D ob1: list) {
			if (ob1==null) continue;
			double dis = p.distance(new Point2D.Double(ob1.getBounds().getCenterX(), ob1.getBounds().getCenterY()));
			if (dis<distance) {
				distance=dis;
				nearest=ob1;
			}
		}
		return nearest;
	}
	
	/**returns all the Objects inside a given rectangular panel of the image. the index here is 0 based.
	  Objects that overlaps with the layout panel at the given index will be in the output list*/
	public ArrayList<LocatedObject2D> getObjectsWithinPanelX(BasicLayout ml,ObjectContainer imp,  int index) {
		ml.setPanelRectangles();
		return getOverlapOverlaypingItems(ml.getPanel(index+1),  imp); 
	}
	
	
	/**Takes the xy positions of an object and subtracts the location of the panel at panel index from it.
	 * if the boolean takeFromImage is true, this will also remove the object lo2D from the ObjectContainer */
	public void moveObjectToBasicCord(LocatedObject2D lo2D,  BasicLayout ml, ObjectContainer imp,int panelIndex, boolean takeFromImage) {
		lo2D.moveLocation( -ml.getPoint(panelIndex).getX(), -ml.getPoint(panelIndex).getY());
		if (takeFromImage) imp.takeFromImage(lo2D);
	}
	
	/**Takes the xy positions of an object and adds the location of the panel at panelIndex to it. 
	if the boolean takeFromImage is true, this will also add the object lo2D to the ObjectContainer */
	public void moveObjectToPanelCord(LocatedObject2D roi,  BasicLayout ml, ObjectContainer imp, int panelIndex, boolean addToImage){
		roi.moveLocation( ml.getPoint(panelIndex).getX(), ml.getPoint(panelIndex).getY());
		if (addToImage) {imp.addItemToImage(roi);
		}
	}

	/**Takes the xy positions of several sets of object and subtracts the location of the corresponding panel in the montage layout from each of them.*/
	public  void moveAllObjectToBasicCordinates(ArrayList<ArrayList<LocatedObject2D>> ro, BasicLayout ml, ObjectContainer imp, boolean takeFromImage) {
		for (int i=0; i<ml.getPanels().length; i++) {
			if (i> ro.size()) break;
			for (LocatedObject2D lObject: ro.get(i)) {
				moveObjectToBasicCord( lObject,   ml, imp,  i+1, takeFromImage);
			}
		}
	}
	

	/**Takes the xy positions of several sets of object and adds the location of the corresponding panel in the montage layout to them.*/
	public void moveAllRoisToPanelCordinates(ArrayList<ArrayList<LocatedObject2D>> ro, BasicLayout ml, ObjectContainer imp, boolean addToImage) {
		if (ro==null ||ml==null) return;
		for (int i=0; i<ml.getPanels().length; i++) {
			for (LocatedObject2D roi: ro.get(i)) {
				moveObjectToPanelCord( roi,   ml, imp,  i+1, addToImage);
			}
		}
	}	

	
	/**moves all the given objects by a given displacement*/
	public void shiftAllObjects(ArrayList<ArrayList<LocatedObject2D>> ro, double xs, double ys) {
		if (ro==null) return;
		for (int i=0; i<ro.size(); i++) {
			if (i> ro.size()) break;
			for (LocatedObject2D roi: ro.get(i)) {
				roi.moveLocation( xs, ys);
				} 
		}
	}
	
	/**moves all the given objects by a given displacement*/
	public void shiftRois(ArrayList<LocatedObject2D> ro, double xs, double ys) {
		for (LocatedObject2D roi: ro) {roi.moveLocation( xs, ys);} 
	}
	
	/**moves all the given objects in a container by a given displacement*/
	public void shiftAll(ObjectContainer ro, double xs, double ys) {
		ArrayList<LocatedObject2D> oo = ro.getLocatedObjects();
		for(LocatedObject2D roi:oo) {
			roi.moveLocation( xs, ys);
			}
	}
	
	
	/**Removes the objects from each panel of the container and stores them as an 
	  array list of array lists. Also subtracts the locations of the panels from the location 
	  of each object. stores the objects.
	  .*/
	public void liftPanelObjects(BasicLayout ml, ObjectContainer cimp) {
		lastset= new ArrayList<ArrayList<LocatedObject2D>>();
		ml.resetPtsPanels(ml.xshift, ml.yshift);
		for (Rectangle2D r: ml.getPanels()) {lastset.add(liftObjectsFromPanelX(cimp, r));}
	}
	
	/**Adds the stored objects to the object container. Adds the location of a layout panel 
	 each object
	 Precondition: objects must have already been stored by the liftPanelObjects method (see above).
	 	The locations of panels may move in between the calls for the two methods*/
	public void setDownPanelObjects(BasicLayout ml, ObjectContainer cimp) {
		moveAllRoisToPanelCordinates(lastset, ml, cimp,  true);
	}
	
	/**Removes the objects from the container and subtracts the x and y of 
	  the upper left hand corner of the rectangle from each objects location*/
	public ArrayList<LocatedObject2D> liftObjectsFromPanelX(ObjectContainer cimp,  Rectangle2D r, LocatedObjectFilter... moreCriteria) {
		
		ArrayList<LocatedObject2D> output=getOverlapOverlaypingItems(r,cimp, moreCriteria);
		
		for (LocatedObject2D roi: output) {
			if (this.getNeverRemove().contains(roi)) continue;
			roi.moveLocation( -r.getX(), -r.getY());
			if (cimp!=null) cimp.takeFromImage(roi);
		}
		return output;
	}
	
	/**Adds the objects to the container.
	 * Also adds the x,y from the upper left hand corner of the panel rectangle
	 * to the objects location*/
	public  void setObjectsIntoPanelX(ArrayList<LocatedObject2D> input, ObjectContainer imp,  Rectangle2D r) {
		for (LocatedObject2D roi: input) {
			if (this.getNeverRemove().contains(roi)) continue;
			roi.moveLocation(r.getX(), r.getY());
			imp.addItemToImage(roi);
		}
	}
	
	
	/**Adds a list of objects to the container*/
	 public void addRoisToImage(ArrayList<LocatedObject2D> roi, ObjectContainer imp) {
		for (LocatedObject2D roi1: roi) imp.addItemToImage(roi1);
	 }
	
	
	/**Moves a regions of interest from a single panel to the equivalent point in
	  any other panel. also sets the point of the montage layout to the location of 
	  the roi*/
	 public void moveRoiToSelectedPanel(BasicLayout ml, LocatedObject2D loc2d, int index) {
		Point2D r=ml.getPoint(getRoiPanelIndex( ml,  loc2d));
		loc2d.moveLocation( -r.getX(), -r.getY());
		r=ml.getPoint(index);
		loc2d.moveLocation( r.getX(), r.getY());
	}

	 /**Returns the index of the montage panel containing the given object*/
	public int getRoiPanelIndex(BasicLayout ml, LocatedObject2D loc2D) {
		Point2D roip = loc2D.getLocation();
		return ml.getPanelIndex(roip.getX(), roip.getY());
	}
	
	/**Sets the point array within the motnage layout to points corresponding to the roi's location.*/
	public void setPointsBasedOnRoiLocation(BasicLayout ml, LocatedObject2D roi) {
		Point2D roip = roi.getLocation();
		double roix = roip.getX();
		double roiy = roip.getX();
		int originalPanel = ml.getPanelIndex(roip.getX(), roip.getY());
		ml.setPoints((roix-ml.getPoint(originalPanel).getX()),  (roiy-ml.getPoint(originalPanel).getY()));		
	}
	

	/**returns a copy of the input list of objects*/
	public ArrayList<LocatedObject2D> copyRois(ArrayList<? extends LocatedObject2D> input) {
		ArrayList<LocatedObject2D> output = new  ArrayList<LocatedObject2D>();
		for(LocatedObject2D in: input) {output.add(in.copy());}
		return output;
	}


	
	/**returns the objects that overlay the rectangluar selection*/
	public ArrayList<LocatedObject2D> getOverlapOverlaypingOrContainedItems(Rectangle2D rect, ObjectContainer imp, LocatedObjectFilter... moreCriteria) {
		ArrayList<LocatedObject2D> output= new ArrayList<LocatedObject2D>();
		if (imp==null) return output;
		ArrayList<LocatedObject2D> rois=imp.getLocatedObjects();
		for (LocatedObject2D roi: rois) {
			if (roi==null) continue;
			
					if (moreCriteria!=null &&moreCriteria.length>0)  {
						for(LocatedObjectFilter c: moreCriteria) {
							if (c!=null&&!c.isObjectDesireableForPanel(rect, roi)) continue;
						}
						}
			if (roi.doesIntersect(rect)||roi.isInside(rect)) {output.add(roi);}
					}
		return output;
	}
	
	
	/**returns the objects that overlay the rectangluar selection*/
	public ArrayList<LocatedObject2D> getOverlapOverlaypingItems(Rectangle2D rect, ObjectContainer imp, LocatedObjectFilter... moreCriteria) {
		ArrayList<LocatedObject2D> output= new ArrayList<LocatedObject2D>();
		if (imp==null) return output;
		ArrayList<LocatedObject2D> rois=imp.getLocatedObjects();
		for (LocatedObject2D roi: rois) {
			if (roi==null) continue;
						if (moreCriteria!=null &&moreCriteria.length>0)  {
									for(LocatedObjectFilter c: moreCriteria) {
										if (c!=null&&!c.isObjectDesireableForPanel(rect, roi)) continue;
									}
					}
			if (roi.doesIntersect(rect)) {output.add(roi);}
					}
		return output;
	}

	
	/**Returns the objects inside of the rectangle*/
	public ArrayList<LocatedObject2D> getContainedObjects(Rectangle2D r1, ObjectContainer imp, LocatedObjectFilter... moreCriteria) {
		ArrayList<LocatedObject2D> output= new ArrayList<LocatedObject2D>();
		ArrayList<LocatedObject2D> rois=imp.getLocatedObjects();
		for (LocatedObject2D roi: rois) {
			
					if (moreCriteria!=null &&moreCriteria.length>0)  {
						for(LocatedObjectFilter c: moreCriteria) {
							if (c!=null&&!c.isObjectDesireableForPanel(r1, roi)) continue;
						}
						}
			
			if (roi.isInside(r1) ) {output.add(roi);}
					}
		return output;
	}
	
	/**Returns the objects outside of the rectangle. Had a BUG. Addition of the interect term was meant to solve
	 * the bug but untested*/
	public ArrayList<LocatedObject2D> getExcludedRois(Rectangle r1, ObjectContainer imp) {
		ArrayList<LocatedObject2D> included= getContainedObjects(r1, imp);
		ArrayList<LocatedObject2D> intersect= this.getOverlapOverlaypingItems(r1, imp);
		ArrayList<LocatedObject2D>  output=imp.getLocatedObjects();
		ArrayList<LocatedObject2D> output2= new ArrayList<LocatedObject2D>();
		output2.addAll(output);
		output2.removeAll(included);
		output2.removeAll(intersect);
		return output2;
	}


	
	/**when given a Rectangular area within in a montage, returns all objects that are in the equivalent position of each panel*/
	public ArrayList<LocatedObject2D> getMontagePositionObjects(BasicLayout  ml, ObjectContainer imp, Rectangle r) {
		ArrayList<LocatedObject2D> rois=new ArrayList<LocatedObject2D>();
		double x= r.getX();
		double y= r.getY();
		int index=ml.getPanelIndex(x, y);
		
		Rectangle2D[] panels=ml.getMontagePositionRectangles(x-ml.getPoint(index).getX(), y-ml.getPoint(index).getY(), r.getWidth(), r.getHeight());
		for (Rectangle2D p: panels) {
			rois.addAll(getContainedObjects(p, imp));
		}
		return rois;
	}
	


	/**removes all rois within the bounds of a given rectangle*/
	public void clearRoi(ObjectContainer imp, Rectangle roi) {
		if (roi==null||imp==null) return;
			liftObjectsFromPanelX(imp, roi);
	}
	
	
	/**returns the object that was present at the point (x,y)*/
	public LocatedObject2D getClickedRoi(ObjectContainer imp, int x, int y) {
		return getClickedRoi(imp, x,y, Object.class);
		
	}
	
	/**returns the object at the clickpoint only if it is a member 
	 of one of the given class types. If multiple object are present, this returns
	 the one in front */
	public LocatedObject2D getClickedRoi(ObjectContainer imp, int x, int y, Class<?>... type) {
		LocatedObject2D roi1;
		ArrayList<LocatedObject2D> rois=imp.getLocatedObjects();
		
		for (int i=rois.size()-1; i>=0;  i-- ) {
			
			LocatedObject2D roi= rois.get(i);
			
			if (roi==null) continue;
			java.awt.Shape p = roi.getOutline();
		
			if (p.contains(x, y) &&ArraySorter.isOfClass(roi, type)) {
				roi1=roi;
				return roi1;
			}
	}
		return null;
	}
	
	
	/**returns the object at the clickpoint only if it is a member of one of the given class types
	 * order is inverted*/
	public ArrayList<LocatedObject2D> getAllClickedRoi(ObjectContainer imp, double d, double e, Class<?> onlySelectThoseOfClass) {
		return getAllClickedRoi(imp,d,e, onlySelectThoseOfClass, false);
	}
	
	
	/**returns the object at the clickpoint only if it is a member of one of the given class types
	 * order is inverted*/
	public ArrayList<LocatedObject2D> getAllClickedRoi(ObjectContainer imp, double d, double e, Class<?> onlySelectThoseOfClass, boolean checkHandles) {
		LocatedObject2D roi1;
		ArrayList<LocatedObject2D> rois=imp.getLocatedObjects();
		
		ArrayList<LocatedObject2D> ouput=new ArrayList<LocatedObject2D>();
		
		for (int i=rois.size()-1; i>=0;  i-- ) {
			LocatedObject2D roi= rois.get(i);
			if (roi==null) continue;
		Shape p=roi.getOutline();
		
			if (p.contains(d, e)&&ArraySorter.isOfClass(roi, onlySelectThoseOfClass)) {
				roi1=roi;
				ouput.add(roi1);
			} else if (checkHandles) {
				
				 //not implemented to check handles
			}
	}
		return ouput;
	}
	
	
	/**certain items are exempt form the actions of this handleer*/
	public ArrayList<LocatedObject2D> neverEditLocationOf=new ArrayList<LocatedObject2D>();
	
	/**getter for the list of items that are protected from editing*/
	public ArrayList<LocatedObject2D> getNeverRemove() {
		if (neverEditLocationOf==null) neverEditLocationOf=new ArrayList<LocatedObject2D>();
		return neverEditLocationOf;
	}

	/**set the list of items that are protected from editing*/
	public void setNeverRemove(ArrayList<LocatedObject2D> neverRemove) {
		this.neverEditLocationOf = neverRemove;
	}
	
	
	
	
	/**clears the area of Rois of any rois that are either of class type or
	   wrap an object of class type*/
	public void clearThoseOfClass(ObjectContainer imp, Rectangle r, Class<?> type) {
		ArrayList<LocatedObject2D> rois = getOverlapOverlaypingItems(r, imp);
		ArrayList<LocatedObject2D> rois2 = new ArraySorter<LocatedObject2D>().getThoseOfClass(rois, type);
		
		for(LocatedObject2D roi: rois2) {
			imp.takeFromImage(roi);
			}
		
	}
	

	/**resizes the canvas if the given offsets while off setting objects as well*/
	public void CanvasResizeObjectsIncluded(ImageWorkSheet iw, int width, int height, int xOff, int yOff) {
		iw.worksheetResize( width, height, xOff, yOff);
		shiftAll(iw, xOff, yOff);
	}
	
	/**resizes the canvas to fit all objects*/
	public boolean resizeCanvasToFitAllObjects(ImageWorkSheet iw) {
		boolean output=false;
		ArrayList<LocatedObject2D> arr = iw.getLocatedObjects();
		for(LocatedObject2D l:arr) {
			if(
					resizeCanvasToFitObject(iw,l)) {output=true;};
		}
		
		return output;
	}
	
	/**resizes the canvas to fit the object*/
	public boolean resizeCanvasToFitObject(ImageWorkSheet iw, LocatedObject2D l) {
		int xOff=0;
		int yOff=0;
		int xAdded=0;
		int yAdded=0;
		
		Dimension dims = iw.getCanvasDims();
		Rectangle r1 = new Rectangle(0,0, dims.width, dims.height);
		Rectangle r2 = l.getBounds();
	   if (r1.contains(r2)) return false;
	  if (r2.x<0) {
		  xOff=-r2.x;
	  }
	  if (r2.y<0) {
		  yOff=-r2.y;
	  }
	  if (r2.x+r2.width>r1.width) {
		  xAdded=r2.x+r2.width-r1.width;
	  }
	  if (r2.y+r2.height>r1.height) {
		  yAdded=r2.y+r2.height-r1.height;
	  }
	  
	  int newwidth=dims.width+xOff+xAdded;
	  int newheight=dims.height+yOff+yAdded;
	  CanvasResizeObjectsIncluded(iw, newwidth, newheight, xOff, yOff);
	   return true;
	}
	
	
	
	/**returns the objects that overlap most with the rectangle.
	  returns null if the panel overlaps too little below threshold*/
	public static ArrayList<LocatedObject2D> identifyHightlyOverlappingPanels(Rectangle gra, ArrayList<LocatedObject2D> objects , double threshold) {
		ArrayList<LocatedObject2D> items = new BasicObjectListHandler().getOverlapOverlaypingOrContainedItems(gra, new ArrayObjectContainer(objects));
		ArrayList<LocatedObject2D> output = new ArrayList<LocatedObject2D>();
		
		Area gArea = new Area(gra.getBounds());
		if (items.size()==0) return output;
		
		for(LocatedObject2D item: items) {
			 gArea = new Area(gra.getBounds());
			gArea.intersect(new Area(item.getBounds()));
			double overLapArea=gArea.getBounds2D().getWidth()*gArea.getBounds2D().getHeight();
			if (overLapArea> threshold* gra.getHeight()*gra.getWidth()) {
				output.add(item);
			}
		}
		
		
		return output;
	
	}
	
	
	/**returns the object that overlaps most with the rectangle.
	  returns null if the panel overlaps too little with any object specifically 
	  if overlap is less than 75% of the rectangle area, returns null*/
	public static LocatedObject2D identifyPanel(Rectangle2D gra, ArrayList<LocatedObject2D> objects ) {
		ArrayList<LocatedObject2D> items = new BasicObjectListHandler().getOverlapOverlaypingOrContainedItems(gra, new ArrayObjectContainer(objects));
		Area gArea = new Area(gra.getBounds());
		if (items.size()==0) return null;
		LocatedObject2D best = items.get(0);
		double maxOverlapSize=0;
		for(LocatedObject2D item: items) {
			 gArea = new Area(gra.getBounds());
			gArea.intersect(new Area(item.getBounds()));
			double overLapArea=gArea.getBounds2D().getWidth()*gArea.getBounds2D().getHeight();
			if (overLapArea>maxOverlapSize) {
				maxOverlapSize=overLapArea;
				best=item;
			}
		}
		
		if (maxOverlapSize< 0.75* best.getBounds().getHeight()*best.getBounds().getWidth())
			return null;
		
		return best;
	
	}
	
	/**when given a rectangle, returns true/false to indicate whether to include
	  or exclude an object*/
	public interface LocatedObjectFilter {
		boolean isObjectDesireableForPanel(Rectangle2D gra, LocatedObject2D objects );
	}
	/**an implementation of LocatedObjectFiler that returns false for objects of a given class*/
	public static class ExcluderFilter implements LocatedObjectFilter {
		Class<?> excludeClass;
		
		public ExcluderFilter(Class<?> s) {
			excludeClass=s;
		}

		@Override
		public boolean isObjectDesireableForPanel(Rectangle2D gra, LocatedObject2D objects) {
			if(objects.getClass()==excludeClass) return false;
			return true;
		}
	}
	/**an implementation of LocatedObjectFiler that returns true for objects of a given class*/
	public static class IncluderFilter implements LocatedObjectFilter {
		Class<?> includeClass;
		
		public IncluderFilter(Class<?> s) {
			includeClass=s;
		}

		@Override
		public boolean isObjectDesireableForPanel(Rectangle2D gra, LocatedObject2D objects) {
			if(objects.getClass()==includeClass) return true;
			return false;
		}
	}
	
	/**Returns the located objects in any collection as an array list of located objects*/
	public static ArrayList<LocatedObject2D> getAs2DObjects(Collection<?> c) {
		ArrayList<LocatedObject2D> output=new ArrayList<LocatedObject2D>();
		for(Object item:c) {
			if(item instanceof LocatedObject2D) {
				output.add((LocatedObject2D) item);
			}
		}
		
		return output;
	}
}
