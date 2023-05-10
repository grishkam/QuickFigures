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
 * Date Modified: Jan 13, 2021
 * Version: 2023.2
 */
package handles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JPopupMenu;
import javax.swing.undo.AbstractUndoableEdit;

import applicationAdapters.CanvasMouseEvent;
import figureOrganizer.FigureScaler;
import graphicalObjects.CordinateConverter;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.GraphicList;
import imageDisplayApp.OverlayObjectManager;
import locatedObject.ArrayObjectContainer;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdgePositions;
import locatedObject.RectangleEdges;
import locatedObject.RotatesFully;
import locatedObject.Scales;
import locatedObject.ScalesFully;
import logging.IssueLog;
import undo.CombinedEdit;
import undo.UndoMoveItems;
import undo.UndoScalingAndRotation;

/**a handle list for resizing, moving and rotating objects.
  The use can make many modifications simply by dragging handles
 The user can drag handles to scale rotate and move objects or groups of objects
 */
public class ReshapeHandleList extends SmartHandleList implements RectangleEdgePositions{
	

	public static final int defaultHandleNumber = 8000000;
	private static final int DEFAULT_TYPE=0, ROTATION_ONLY_TYPE = 1;
	boolean specialShapeUser=false;
	protected ArrayList<LocatedObject2D> objects;
	private RectangularGraphic rect;
	public  int handleNumberCorrection=defaultHandleNumber;//determines the id numbers for the handles
	private static final int rotationType=RectangularShapeSmartHandle.ROTATION_HANDLE;
	private double handleSize=2;
	ReshapeSmartHandle lastDrag;//the most recently draged handle
	boolean singleScale=false;//set to true if both x and y scale factors should be the same
	private int type=DEFAULT_TYPE;
	private boolean showLineConnectionForRotationHandle;
	protected boolean hideCenterHandle=true;
	
	/**The color of the handles*/
	protected  Color reshapeHandleColor = Color.pink;
	protected Color fixedpointHandleColor = Color.red;
	
	private boolean drawsRectOver;//if set to true, draws the rectangle
	public int RectandleDrawThickness = 1;
	
	public JPopupMenu thePopup;//a popup menu for the handles
	
	/**returns true if the argument contains the same objects as this list*/
	public boolean isSimilarList(ReshapeHandleList l) {
		if(l==null) return false;
		if(l.getTargetedObjects().size()!=this.getTargetedObjects().size()) return false;
		for(int i=0; i<getTargetedObjects().size(); i++) {
			if(l.getTargetedObjects().get(i)!=getTargetedObjects().get(i)) return false;
		}
		
		return true;
	}
	
	/**Constructs a reshape handle list with the objects in o.
	 * The type argument determines which handles are included
	  */
	public ReshapeHandleList(int type, LocatedObject2D... o) {
		this.type=type;
		objects=new ArrayList<LocatedObject2D>();
		for(LocatedObject2D o1:o) {getTargetedObjects().add(o1);}
		refreshList(getTargetedObjects());
	}
	
	/**Constructs a reshape handle list with the objects in o.
	 * The type argument determines which handles are included.
	  the hNumber affects the handle id numbers assigned to each handle
	  */
	public ReshapeHandleList(int type, int hNumber, LocatedObject2D... o) {
		this.type=type;
		this.handleNumberCorrection=hNumber;
		objects=new ArrayList<LocatedObject2D>();
		for(LocatedObject2D o1:o) {getTargetedObjects().add(o1);}
		refreshList(getTargetedObjects());
	}

	/**Constructs a reshape handle list with the objects in o.
	 * The type argument determines which handles are included.
	  the hNumber affects the handle id numbers assigned to each handle
	  Arguments two way determine which method call is used for resizing. 
	  a hide-center argument indicates to hide the center handle (at least temporarily).
	  */
	public ReshapeHandleList(ArrayList<LocatedObject2D> objects, double handleSize, int handleNumberCorrection, boolean twoWay, int type, boolean hidecenter) {
		this.handleNumberCorrection=handleNumberCorrection;
		this.type=type;
		singleScale=!twoWay;
		this.objects=objects;
		this.handleSize=handleSize;
		hideCenterHandle=hidecenter;
		refreshList(objects);
		specialShapeUser=true;
	}
	
	public ReshapeHandleList(ArrayList<LocatedObject2D> objects, boolean twoway) {
		this(objects, 5, 100000, twoway, DEFAULT_TYPE, false);
	}

	/**updates the handle list according to the listed objects*/
	public void refreshList(ArrayList<LocatedObject2D> objects) {
		updateRectangle(objects);
		if(this.size()<1) {
			if (type!=ROTATION_ONLY_TYPE) for(int i:RectangleEdges.internalLocations) {
				crateHandleFor(i);
			}
			
			createRotationHandle();
		}
	}

	/**
	Method call adds a handle to the list for rotating objects
	 */
	public void createRotationHandle() {
		SmartHandle rotationHandle = createSmartHandle(rotationType);
		if (showLineConnectionForRotationHandle)
			rotationHandle.setLineConnectionHandle(createSmartHandle(CENTER));
		add(rotationHandle);
	}

	/**
	Creates a handle for the given rectangle position i.
	 */
	public void crateHandleFor(int i) {
		SmartHandle createSmartHandle = createSmartHandle(i);
		if (i==CENTER)
			createSmartHandle.handlesize=(int) (2*handleSize);
		if (isHiddenCenterHandle()&&i==CENTER)	createSmartHandle.setHidden(true);;
		
		add(createSmartHandle);
	}

	/**returns true if the list is hiding the center handle*/
	protected boolean isHiddenCenterHandle() {
		return hideCenterHandle;
	}

	/**sets the rectangle for this list based on the bounding box of all the objects in the list*/
	public void updateRectangle() {
		updateRectangle(getTargetedObjects());
	}
	/**sets the rectangle for this list based on the bounding box of all the objects in the list*/
	private void updateRectangle(ArrayList<LocatedObject2D> objects) {
		Shape a = ArrayObjectContainer.combineOutLines(objects);
		Rectangle r = new Rectangle(a.getBounds());
		if(rect==null) {
			rect=new RectangularGraphic(r);
		} else rect.setRectangle(r);
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**creates a handle for the given location on the bounding box. */
	public SmartHandle createSmartHandle(int location) {
		ReshapeSmartHandle out = new ReshapeSmartHandle(location, rect);
		out.handlesize=(int) handleSize;
		out.setHandleNumber(handleNumberCorrection+location);
		out.updateLocation(location);
				return out;
	}
	
	/**returns a particular handle depending on the given location
	 * @see RectangleEdgePositions for internal locations
	 * */
	public ReshapeSmartHandle getHandleOfType(int type) {
		for(SmartHandle sh: this) {
			if (sh instanceof  ReshapeSmartHandle) {
				if (((ReshapeSmartHandle) sh).getHandleType()==type)
					return (ReshapeSmartHandle) sh;
			}
		}
		
		return null;
	}
	
	/**A handle for moving or resizing selected objects*/
	public class ReshapeSmartHandle extends SmartHandle {

		private RectangularGraphic rect;
		
		private Point pressPoint;
		private Rectangle startingReshape;

		private ArrayList<LocatedObject2D> o2;

		private double xScale;

		private double yScale;

		private Point2D centerOfScaling;

		private double angle;

		private transient OverlayObjectManager selectionManagger;

		public boolean editOngoing;

		private double xyScale;

		private double disPlaceX;

		private double disPlaceY;

		

		public  ReshapeSmartHandle(int type, RectangularGraphic r) {
			
			this.setHandleNumber(type);
			this.rect=r;
			
			
		}
		
		/**returns true if the handle list
		 * is set to use a special shape
		 * Also sets up a special shape in which an outward 
		 * displaced fragment is present near the original handle */
		protected boolean hasSpecialShape() {
			if(!specialShapeUser)
				return false;
			if(specialShape==null) {
				int size = 6;
				int sizeHalf = size/2;
				int t = this.getHandleType();
				if(t!=RectangleEdgePositions.CENTER)
					specialShape=createDirectionArrow(size, sizeHalf, t);
				/**
				Rectangle rmain = new Rectangle(-size,-size, size*2, size*2);
				Rectangle rInward = new Rectangle(-sizeHalf,-sizeHalf, size, size);
			
				
				if(t!=RectangleEdgePositions.CENTER) {
					Point2D p = RectangleEdges.getLocation(t, rmain);
					
					Rectangle rOutward = new Rectangle(-sizeHalf,-sizeHalf, size, size);
					RectangleEdges.setLocation(rOutward, CENTER, p.getX(), p.getY());;
					Area a = new Area(new Ellipse2D.Double(rOutward.getX(), rOutward.getY(), rOutward.getWidth(), rOutward.getHeight()));
					a.add(new Area(rInward));
					specialShape=a;
				}*/
			}
			return specialShape!=null;
		}
		
		/**draws the handle*/
		public void draw(Graphics2D graphics, CordinateConverter cords) {
			
			this.updateLocation(getHandleNumber());
			if(this.getLineConnectionHandle() instanceof ReshapeSmartHandle)  {
				 ReshapeSmartHandle h2=(ReshapeSmartHandle) getLineConnectionHandle();
				 h2.updateLocation(h2.getHandleNumber());
			}
			
			if (getHandleType()==rect.getLocationType()) {
				this.setHandleColor(fixedpointHandleColor);
			} else this.setHandleColor(reshapeHandleColor);
			
			/**the rotation handle will look somewhat different*/
			if (isRotationHandle()) {
				this.setHandleColor(Color.orange);
				if (specialShape==null) {
					int x2 = (int) (-handlesize*1.5);
					int w = (int) (handlesize*3);
					this.specialShape=new Ellipse2D.Double(x2, x2, w, w);
				}
			
			}

			
			super.draw(graphics, cords);
		}
		
		public double getWorkingScaleFactor() {return xyScale;}
		
		/**returns the popup menu for the handle*/
		public JPopupMenu getJPopup() {
			return thePopup;
		}

		public boolean isRotationHandle() {
			return getHandleType()==rotationType;
		}

		public boolean handlesOwnUndo() {
			return true;
		}
		
		/**updates the handle location*/
		public void updateLocation(int type) {
			type-=handleNumberCorrection;
			if (!isRotationHandle()) {
				Point2D p = RectangleEdges.getLocation(type,rect.getBounds());
				rect.undoRotationCorrection(p);
				setCordinateLocation(p);
				
				}  else {
				setCordinateLocation(rect.getRotationHandleLocation());
				}
			
		
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**handle press*/
		@Override
		public void handlePress(CanvasMouseEvent w) {
			onHandlePress();
			this.pressPoint=w.getCoordinatePoint();
			startingReshape=rect.getRectangle().getBounds();
		}
		
		/***/
		@Override
		public void handleDrag(CanvasMouseEvent w) {
			if(pressPoint==null||startingReshape==null) 
					{handlePress(w);}
			
		
			
			lastDrag=this;
			editOngoing=true;
			int handletype = getHandleType();
			
		
			/**Alters the rectangle as if its own handle was being dragged*/
			rect.flipDuringHandleDrag=false;
			RectangularShapeSmartHandle.handleSmartMove(rect,handletype, w.getCoordinatePoint());
			
			
				o2=copyObjects();//creates a copy of every object to be modified
			
			
			/**Determines the scale factors for the rectangle drag*/
			xScale = rect.getRectangle().getWidth()/startingReshape.getWidth();
			yScale = rect.getRectangle().getHeight()/startingReshape.getHeight();
			double dist1=RectangleEdges.distanceOppositeSide(handletype, startingReshape);
			double dist2= RectangleEdges.getLocation(rect.getLocationType(), rect.getRectangle()).distance(w.getCoordinatePoint());
			xyScale=dist2/dist1;
			centerOfScaling = rect.getLocation();
			
			
			angle=rect.getAngle();
			
			/**Transforms the copy of the objects based on the handle drag being done*/
			if(handletype==CENTER) {
				 moveList(o2, pressPoint, w.getCoordinatePoint());
			}  else if (this.isRotationHandle()) {
				if (angle!=0) {
				performRotate(o2, -angle);
				}
			} else {
				performScale(o2);
			}
			
			
			
			
			
			selectionManagger = w.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger();
			
			/**displays copy over the original image so the user can see the new locations or transformations being implemented*/
			GraphicList g = new GraphicList(o2);
			selectionManagger.setSelectionGraphicWithoutSelecting(g);
			
		}
		
		
		/**returns the handle type of this handle. @see RectangleEdgePositions*/
		protected int getHandleType() {
			return this.getHandleNumber()-handleNumberCorrection;
		}
		
		/**moves all objects in the list based on a movement from one point to another.
		  stores the displacemets done*/
		private void moveList(ArrayList<LocatedObject2D> o22, Point startingPoint, Point finishingPoint) {
			disPlaceX = finishingPoint.getX()-startingPoint.getX();
			disPlaceY = finishingPoint.getY()-startingPoint.getY();
			for(LocatedObject2D ob:o22) {
				if(ob!=null)
				ob.moveLocation(disPlaceX, disPlaceY);
			}
		
		}
		
		/**moves all the objects based on the stored x and y displacements. returns an undoable edit */
		private UndoMoveItems performMove(ArrayList<LocatedObject2D> o22) {
			UndoMoveItems undo = new UndoMoveItems(o22);
			for(LocatedObject2D ob:o22) {
				ob.moveLocation(disPlaceX, disPlaceY);
			}
			return undo;
		}
		
		/**rotates all the objects by an angle. Returns an undoable edit */
		private CombinedEdit performRotate(ArrayList<LocatedObject2D> o2, double angle) {
			return rotateList(o2, angle, rect.getCenterOfRotation());
			
		}
		/**rotates all the objects by an angle around a given point. Returns an undoable edit */
		private CombinedEdit rotateList(ArrayList<LocatedObject2D> o2, double angle, Point2D centerOfRotation) {
			CombinedEdit edit = new CombinedEdit();
			for(LocatedObject2D ob:o2) {
				if (ob instanceof RotatesFully) try {
					UndoScalingAndRotation undo = new UndoScalingAndRotation(ob);
					((RotatesFully) ob).rotateAbout(centerOfRotation, angle);
					undo.establishFinalState();
					edit.addEditToList(undo);
				} catch (Throwable t) {IssueLog.logT(t);}
			}
			return edit;
		}
		
		/**Scales each object based on the stored scale information. Returns an undoable edit */
		private CombinedEdit performScale(ArrayList<LocatedObject2D> o2) {
			if(singleScale) {
				return scaleList(o2, xyScale, centerOfScaling);
			} else 
			return scaleList(o2, xScale, yScale, centerOfScaling);
		}
		/**Scales each object based on the arguments. Returns an undoable edit */
		private CombinedEdit scaleList(ArrayList<LocatedObject2D> o2, double xScale, double yScale, Point2D s) {
			CombinedEdit edit = new CombinedEdit();
			for(LocatedObject2D ob:o2) {
				if (ob instanceof ScalesFully &&s!=null) {
					UndoScalingAndRotation undo = new UndoScalingAndRotation(ob);
					((ScalesFully) ob).scaleAbout(s, xScale, yScale);
					edit.addEditToList(undo);
				}
			}
			return edit;
		}
		/**Scales each object based on the arguments. Returns an undoable edit */
		private CombinedEdit scaleList(ArrayList<LocatedObject2D> o2, double xyScale, Point2D s) {
			CombinedEdit edit = new CombinedEdit();
			for(LocatedObject2D ob:o2) {
				if (ob instanceof Scales &&s!=null) {
					UndoScalingAndRotation undo = new UndoScalingAndRotation(ob);
					((Scales) ob).scaleAbout(s, xyScale);
					undo.establishFinalState();
					edit.addEditToList(undo);
				}
			}
			return edit;
		}
		
		/**creates a copy of the selected objects.
		  */
		private ArrayList<LocatedObject2D>  copyObjects() {
			ArrayList<LocatedObject2D> output=new ArrayList<LocatedObject2D> ();
			for(LocatedObject2D o: getTargetedObjects()) {
				output.add(o.copy());
			}
			
			return output;
		}
		
		@Override
		public void handleRelease(CanvasMouseEvent w) {
			editComplete(w);
		}
		
		/**called in order to finish the edit.
		  transforms the shapes and not just the preview*/
		protected void editComplete(CanvasMouseEvent w) {
			if(!editOngoing) return;//if no edit was started by the user this just returns. dragging a handle starts an edit
			if (selectionManagger!=null)selectionManagger.setSelectionGraphic2(null);//removes the preview of the transformation
			
			/**Performs the edit on the selected objects*/
			if(isMoveHandle()) {
				UndoMoveItems edit =performMove(getTargetedObjects());
				addUndo(w, edit);
			} else
				if (!isRotationHandle()) 
					{
						CombinedEdit edit = performScale(getTargetedObjects());
						if(w!=null) {
							addUndo(w, edit);}
						FigureScaler.showScaleMessages(getTargetedObjects());
					}
				else {
					CombinedEdit edit =performRotate(getTargetedObjects(), -angle);
					
					if(w!=null) {
						addUndo(w, edit);
					}
				}
			
				/**resets the bounding rectangle so match the new bounds of the transformed objects*/
				rect.setAngle(0);
				updateRectangle();
				
				editOngoing=false;
				editover(w);
		}
		
		protected void addUndo(CanvasMouseEvent w, AbstractUndoableEdit edit) {
			if (w!=null)w.addUndo(edit);
		}
		
		private boolean isMoveHandle() {
			return getHandleType()==CENTER;
		}
		
		/**What to do when a handle is moved from point p1 to p2. 
		  The bounding rectangle is resized, rotated or moved depending on which handle the user is dragging*/
		public void handleMove(Point2D p1, Point2D p2) {
			RectangularShapeSmartHandle.handleSmartMove(rect,getHandleType(),  p2) ;
		}
		
	}

	/**Called after edit is completed*/
	public void finishEdit() {
		if(lastDrag!=null &&lastDrag.editOngoing) lastDrag.editComplete(null);
	}

	/**Called after a handle is pressed. this may be overwritten by subclasses*/
	public void onHandlePress() {
		// TODO Auto-generated method stub
		
	}
	/**Called after the edit is completed. this may be overwritten by subclasses*/
	public void editover(CanvasMouseEvent w) {
		
	}
	
	/**draw method for this handle list*/
	public void draw(Graphics2D g, CordinateConverter cords) {
		if(this.drawsRectOver) {
			RectangularGraphic blankRect = RectangularGraphic.blankRect(rect.getBounds(), Color.LIGHT_GRAY);
			blankRect.setStrokeWidth(RectandleDrawThickness);
			blankRect.draw(g, cords);
		}
		super.draw(g, cords);
	}

	public ArrayList<LocatedObject2D> getTargetedObjects() {
		return objects;
	}
	


}
