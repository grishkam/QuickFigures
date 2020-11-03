package genericMontageKit;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import applicationAdapters.ImageWrapper;
import gridLayout.BasicMontageLayout;
import logging.IssueLog;
import utilityClasses1.ArraySorter;
import utilityClasses1.ItemPicker;
import utilityClassesForObjects.ArrayObjectContainer;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.ObjectContainer;

/**Handles the operations for moving, cutting and pasting objects within a montage. */
public class BasicOverlayHandler {
	/**A variable for storing copy pasted groups of objects
 	for each panel of a montage. */
	public ArrayList<ArrayList<LocatedObject2D>> lastset=null;
	
	


	/**This returns an array of arrays. the inner arrays contain each panel's Objects.*/
	public ArrayList<ArrayList<LocatedObject2D>> getOverlaysInPanels(BasicMontageLayout ml, ObjectContainer imp) {
		ArrayList<ArrayList<LocatedObject2D>> output=new ArrayList<ArrayList<LocatedObject2D>>();
		ml.setPanelRectangles();
		for (int i=1; i<=ml.getPanels().length; i++) {
			output.add(getOverlapOverlaypingrois(ml.getPanel(i),  imp)); ;
		}
		return output;
	}
	
	
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
	
	/**returns all the Objects inside a given rectangular panel of the image. the index here is 0 based*/
	public ArrayList<LocatedObject2D> getOverlaysInPanelX(BasicMontageLayout ml,ObjectContainer imp,  int index) {
		ml.setPanelRectangles();
		return getOverlapOverlaypingrois(ml.getPanel(index+1),  imp); 
	}
	
	
	/**Takes the xy positions of an object and subtracts the base cordinate at index panelIndex in the montage layout from it. */
	public void moveRoiToBasicCord(LocatedObject2D roi,  BasicMontageLayout ml, ObjectContainer imp,int panelIndex, boolean takeFromImage) {
		roi.moveLocation( -ml.getPoint(panelIndex).getX(), -ml.getPoint(panelIndex).getY());
		if (takeFromImage) imp.takeRoiFromImage(roi);//takeRoiFromImage(imp, roi);
	}
	
	//public abstract void takeRoiFromImage(ImageType imp, locatedObject roi);
	
	/**Takes the xy positions of an object and adds the base cordinate at index panelIndex in the montage layout to it. */
	public void moveRoiToPanelCord(LocatedObject2D roi,  BasicMontageLayout ml, ObjectContainer imp, int panelIndex, boolean addToImage){
		roi.moveLocation( ml.getPoint(panelIndex).getX(), ml.getPoint(panelIndex).getY());
		if (addToImage) {imp.addRoiToImage(roi);
	//	addRoiToimage( imp, roi);
		}
	}

	/**Takes the xy positions of several sets of object and subtracts the base cordinates of the corresponding panel in the montage layout from them.*/
	public  void moveAllRoisToBasicCordinates(ArrayList<ArrayList<LocatedObject2D>> ro, BasicMontageLayout ml, ObjectContainer imp, boolean takeFromImage) {
		for (int i=0; i<ml.getPanels().length; i++) {
			if (i> ro.size()) break;
			for (LocatedObject2D roi: ro.get(i)) {
				moveRoiToBasicCord( roi,   ml, imp,  i+1, takeFromImage);
			}
		}
	}
	

	/**Takes the xy positions of several sets of object and adds the base cordinates of the corresponding panel in the montage layout to them.*/
	public void moveAllRoisToPanelCordinates(ArrayList<ArrayList<LocatedObject2D>> ro, BasicMontageLayout ml, ObjectContainer imp, boolean addToImage) {
		if (ro==null ||ml==null) return;
		for (int i=0; i<ml.getPanels().length; i++) {
			for (LocatedObject2D roi: ro.get(i)) {
				moveRoiToPanelCord( roi,   ml, imp,  i+1, addToImage);
			}
		}
	}	

	
	/**moves all the given objects by a given displacement*/
	public void shiftAllRois(ArrayList<ArrayList<LocatedObject2D>> ro, double xs, double ys) {
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
	
	/**moves all the given objects in an image by a given displacement*/
	public void shiftAllRois(ObjectContainer ro, double xs, double ys) {
		ArrayList<LocatedObject2D> oo = ro.getLocatedObjects();
		for(LocatedObject2D roi:oo) {
			roi.moveLocation( xs, ys);
			}
	}
	
	
	/**Removes the objects from each panel of the image and stores them as an 
	  array list of array lists. Can be placed back by the setOverlay Rois method.*/
	public void liftOverLayRois(BasicMontageLayout ml, ObjectContainer imp) {
		lastset= new ArrayList<ArrayList<LocatedObject2D>>();
		ml.resetPtsPanels(ml.xshift, ml.yshift);
		for (Rectangle2D r: ml.getPanels()) {lastset.add(liftOverlaysInPanelX(imp, r));}
	}
	
	/**Places the stored objects into the montage panels.*/
	public void setOverLayRois(BasicMontageLayout ml, ObjectContainer imp) {
		moveAllRoisToPanelCordinates(lastset, ml, imp,  true);
	}
	
	/**Removes the objects from the image and changes the cordinates such that each x and y is 
	  set relative to the upper left hand corner of the panel and not the upper left hand corner of the
	  image*/
	public ArrayList<LocatedObject2D> liftOverlaysInPanelX(ObjectContainer imp,  Rectangle2D r, LocatedObjectFilter... moreCriteria) {
		
		ArrayList<LocatedObject2D> output=getOverlapOverlaypingrois(r,imp, moreCriteria);
		
		for (LocatedObject2D roi: output) {
			if (this.getNeverRemove().contains(roi)) continue;
			roi.moveLocation( -r.getX(), -r.getY());
			if (imp!=null) imp.takeRoiFromImage(roi);
			//takeRoiFromImage(imp, roi);
			
		}
		return output;
	}
	
	/**Places the objects into the image at cordinates relative to the upper left hand corner of the panel rectangle*/
	public  void setOverlaysInPanelX(ArrayList<LocatedObject2D> input, ObjectContainer imp,  Rectangle2D r) {
		for (LocatedObject2D roi: input) {
			if (this.getNeverRemove().contains(roi)) continue;
			roi.moveLocation(r.getX(), r.getY());
			imp.addRoiToImage(roi);
		}
	}
	
	
	/**Adds a list of objects to the image*/
	 public void addRoisToImage(ArrayList<LocatedObject2D> roi, ObjectContainer imp) {
		for (LocatedObject2D roi1: roi) imp.addRoiToImage(roi1);
	 }
	
	
	/**Moves a regions of interest from a single panel to the equivalent point in
	  any other panel. also sets the point of the montage layout to the location of 
	  the roi*/
	 public void moveRoiToSelectedPanel(BasicMontageLayout ml, LocatedObject2D roi, int index) {
		Point2D r=ml.getPoint(getRoiPanelIndex( ml,  roi));
		roi.moveLocation( -r.getX(), -r.getY());
		r=ml.getPoint(index);
		roi.moveLocation( r.getX(), r.getY());
	}

	 /**Returns the index of the montage panel containing the given object*/
	public int getRoiPanelIndex(BasicMontageLayout ml, LocatedObject2D roi) {
		Point2D roip = roi.getLocation();
		return ml.getPanelIndex(roip.getX(), roip.getY());
	}
	
	/**Sets the point array withinn the motnage layout to points corresponind to the roi's location.*/
	public void setPointsBasedOnRoiLication(BasicMontageLayout ml, LocatedObject2D roi) {
		Point2D roip = roi.getLocation();
		double roix = roip.getX();
		double roiy = roip.getX();
		int originalPanel = ml.getPanelIndex(roip.getX(), roip.getY());
		ml.setPoints((roix-ml.getPoint(originalPanel).getX()),  (roiy-ml.getPoint(originalPanel).getY()));		
	}
	
	/**Repetitively clears the same roi into multiple locations of the same image.
	   if the int[] include is not null this will only draw into the positions that 
	   are listed.*/
	public void clearRois(ObjectContainer imp, LocatedObject2D roi, Point[] pts, ArrayList<Integer> j, int redirectToPhotoShop, boolean fill) {
		//if (redirectToPhotoShop==PHOTOSHOP2 ) lastJScript="";
		for (int i=0; i<pts.length; i++) {
			if (j!=null) {
			if (!hasInt(j, i+1)) continue;
			}
			roi.setLocation(pts[i].getX(), pts[i].getY());
		try{	clearRoi( imp,  roi.getBounds()) ;} catch (Exception e) {IssueLog.log(e);}
		}
	}
	
	
	
	/**Returns true if the array list has the given integer, false otherwise*/
	public static boolean hasInt(ArrayList<Integer> v, int i) {
		for(Integer in:v) {if (in==i) return true;}
		return false;
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
	public ArrayList<LocatedObject2D> getOverlapOverlaypingrois(Rectangle2D rect, ObjectContainer imp, LocatedObjectFilter... moreCriteria) {
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
		ArrayList<LocatedObject2D> intersect= this.getOverlapOverlaypingrois(r1, imp);
		ArrayList<LocatedObject2D>  output=imp.getLocatedObjects();
		ArrayList<LocatedObject2D> output2= new ArrayList<LocatedObject2D>();
		output2.addAll(output);
		output2.removeAll(included);
		output2.removeAll(intersect);
		return output2;
	}


	
	/**when given a Rectangular area within in a montage, returns all objects that are in the equivalent position of each panel*/
	public ArrayList<LocatedObject2D> getMontagePositionObjects(BasicMontageLayout  ml, ObjectContainer imp, Rectangle r) {
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
			liftOverlaysInPanelX(imp, roi);
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
	
	
	/**returns the roi at the clickpoint only if it is a member of one of the given class types
	 * order is inverted*/
	public ArrayList<LocatedObject2D> getAllClickedRoi(ObjectContainer imp, double d, double e, Class<?> onlySelectThoseOfClass) {
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
			}
	}
		return ouput;
	}
	
	
	
	public ArrayList<LocatedObject2D> neverEditLocationOf=new ArrayList<LocatedObject2D>();
	public ArrayList<LocatedObject2D> getNeverRemove() {
		if (neverEditLocationOf==null) neverEditLocationOf=new ArrayList<LocatedObject2D>();
		return neverEditLocationOf;
	}

	public void setNeverRemove(ArrayList<LocatedObject2D> neverRemove) {
		
		this.neverEditLocationOf = neverRemove;
	}
	
	
	
	
	/**clears the area of Rois of any rois that are either of class type or
	   wrap an object of class type*/
	public void clearThoseOfClass(ObjectContainer imp, Rectangle r, Class<?> type) {
		ArrayList<LocatedObject2D> rois = getOverlapOverlaypingrois(r, imp);
		ArrayList<LocatedObject2D> rois2 = new ArraySorter<LocatedObject2D>().getThoseOfClass(rois, type);
		ArrayList<LocatedObject2D> rois3 = new ArraySorter<LocatedObject2D>().getThoseThatWrapObjectClass(rois, type);
		
		for(LocatedObject2D roi: rois2) {
			imp.takeRoiFromImage(roi);
			}
		for(LocatedObject2D roi: rois3) {
			imp.takeRoiFromImage(roi);
			}
	}
	

	/**moves the roi through the points and deletes what is in each points*/
	public void clearRoisOfSpecifiedClasses(ObjectContainer imp, LocatedObject2D roi,
			Point[] pts, ArrayList<Integer> j, boolean b, Class<?> exclusiveType) {
		
		if (imp==null||roi==null) return;
		
		for (int i=0; i<pts.length; i++) {
			if (j!=null) {
			if (!BasicOverlayHandler .hasInt(j, i+1)) continue;
			}
			roi.setLocation(pts[i].getX(), pts[i].getY());
		try{	clearThoseOfClass( imp,  roi.getBounds(), exclusiveType) ;} catch (Exception e) {IssueLog.log(e);}
		}
	}
	
	
	
	/**resizes the canvas if the given offsets while off setting objects as well*/
	public void CanvasResizeObjectsIncluded(ImageWrapper iw, int width, int height, int xOff, int yOff) {
		iw.CanvasResizePixelsOnly( width, height, xOff, yOff);
		shiftAllRois(iw, xOff, yOff);
	}
	
	/**resizes the canvas to fit all objects*/
	public void resizeCanvasToFitAllObjects(ImageWrapper iw) {
		ArrayList<LocatedObject2D> arr = iw.getLocatedObjects();
		for(LocatedObject2D l:arr) {
			resizeCanvasToFitObject(iw,l);
		}
	}
	
	/**resizes the canvas to fit the object*/
	public void resizeCanvasToFitObject(ImageWrapper iw, LocatedObject2D l) {
		int xOff=0;
		int yOff=0;
		int xAdded=0;
		int yAdded=0;
		
		Dimension dims = iw.getCanvasDims();
		Rectangle r1 = new Rectangle(0,0, dims.width, dims.height);
		Rectangle r2 = l.getBounds();
	   if (r1.contains(r2)) return;
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
	   
	}
	
	
	
	
	public void clearAreaOfDrawnGraphicType(ObjectContainer container, Rectangle2D r, ItemPicker pick ) {
		ArrayList<LocatedObject2D> list = this.getOverlapOverlaypingrois(r, container);
		ArrayList<LocatedObject2D> willRemove = new ArraySorter<LocatedObject2D>().getThosePicked(list,pick);
		
		for(LocatedObject2D o: willRemove) {
			container.takeRoiFromImage(o);
		}
		
	}
	
	
	/**returns the objects that overlap most with the rectangle.
	  returns null if the panel overlaps too little below threshold*/
	public static ArrayList<LocatedObject2D> identifyHightlyOverlappingPanels(Rectangle gra, ArrayList<LocatedObject2D> objects , double threshold) {
		ArrayList<LocatedObject2D> items = new BasicOverlayHandler().getOverlapOverlaypingOrContainedItems(gra, new ArrayObjectContainer(objects));
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
	  returns null if the panel overlaps too little*/
	public static LocatedObject2D identifyPanel(Rectangle2D gra, ArrayList<LocatedObject2D> objects ) {
		ArrayList<LocatedObject2D> items = new BasicOverlayHandler().getOverlapOverlaypingOrContainedItems(gra, new ArrayObjectContainer(objects));
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
	
	
	public interface LocatedObjectFilter {
		boolean isObjectDesireableForPanel(Rectangle2D gra, LocatedObject2D objects );
	}
	
	public static class excluder implements LocatedObjectFilter {
		Class<?> excludeClass;
		
		public excluder(Class<?> s) {
			excludeClass=s;
		}

		@Override
		public boolean isObjectDesireableForPanel(Rectangle2D gra, LocatedObject2D objects) {
			if(objects.getClass()==excludeClass) return false;
			return true;
		}
	}
	
	public static class includer implements LocatedObjectFilter {
		Class<?> excludeClass;
		
		public includer(Class<?> s) {
			excludeClass=s;
		}

		@Override
		public boolean isObjectDesireableForPanel(Rectangle2D gra, LocatedObject2D objects) {
			if(objects.getClass()==excludeClass) return true;
			return false;
		}
	}
	
	/**Returns the located objects in any collection*/
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
