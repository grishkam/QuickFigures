package graphicalObjectHandles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import applicationAdapters.CanvasMouseEventWrapper;
import genericMontageKit.SelectionManager;
import graphicalObjects.CordinateConverter;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_FigureSpecific.FigureScaler;
import graphicalObjects_LayerTypes.GraphicGroup;
import logging.IssueLog;
import undo.CompoundEdit2;
import undo.UndoMoveItems;
import undo.UndoScaling;
import utilityClassesForObjects.ArrayObjectContainer;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.RectangleEdgePosisions;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.RotatesFully;
import utilityClassesForObjects.Scales;
import utilityClassesForObjects.ScalesFully;

/**a handle list for resizing and rotating groups of objects*/
public class ReshapeHandleList extends SmartHandleList implements RectangleEdgePosisions{
	
	
	private static final int rotationOnlyType = 1;
	protected ArrayList<LocatedObject2D> objects;
	private RectangularGraphic rect;
	public  int handleNumberCorrection=8000000;
	private int rotationType=10;
	private double handleSize=2;
	ReshapeSmartHandle lastDrag;
	boolean singleScale=false;
	private int type;
	private boolean showLineConnectionForRotationHandle;
	protected boolean hideCenterHandle=true;
	protected  Color reshapeHandleColor = Color.pink;
	protected Color fixedpointHandleColor = Color.red;
	
	public boolean isSimilarList(ReshapeHandleList l) {
		if(l==null) return false;
		if(l.objects.size()!=this.objects.size()) return false;
		for(int i=0; i<objects.size(); i++) {
			if(l.objects.get(i)!=objects.get(i)) return false;
		}
		
		return true;
	}
	
	public ReshapeHandleList(int type, LocatedObject2D... o) {
		this.type=type;
		objects=new ArrayList<LocatedObject2D>();
		for(LocatedObject2D o1:o) {objects.add(o1);}
		refreshList(objects);
	}
	
	public ReshapeHandleList(int type, int hNumber, LocatedObject2D... o) {
		this.type=type;
		this.handleNumberCorrection=hNumber;
		objects=new ArrayList<LocatedObject2D>();
		for(LocatedObject2D o1:o) {objects.add(o1);}
		refreshList(objects);
	}

	public ReshapeHandleList(ArrayList<LocatedObject2D> objects, double handleSize, int handleNumberCorrection, boolean twoWay, int type, boolean hidecenter) {
		this.handleNumberCorrection=handleNumberCorrection;
		this.type=type;
		singleScale=!twoWay;
		this.objects=objects;
		this.handleSize=handleSize;
		hideCenterHandle=hidecenter;
		refreshList(objects);
		
			
	}

	protected void refreshList(ArrayList<LocatedObject2D> objects) {
		updateRectangle(objects);
		if (type!=rotationOnlyType) for(int i:RectangleEdges.locationsforh) {
			SmartHandle createSmartHandle = createSmartHandle(i);
			if (i==CENTER)
				createSmartHandle.handlesize=(int) (2*handleSize);
			if (isHiddenCenterHandle()&&i==CENTER)	createSmartHandle.setHidden(true);;
			
			add(createSmartHandle);
		}
		
		SmartHandle rotationHandle = createSmartHandle(rotationType);
		if (showLineConnectionForRotationHandle)rotationHandle.setLineConnectionHandle(createSmartHandle(CENTER));
		add(rotationHandle);
	}

	protected boolean isHiddenCenterHandle() {
		return hideCenterHandle;
	}

	public void updateRectangle() {
		updateRectangle(objects);
		
	}
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

	protected SmartHandle createSmartHandle(int type) {
		ReshapeSmartHandle out = new ReshapeSmartHandle(type, rect);
		out.handlesize=(int) handleSize;
		out.setHandleNumber(handleNumberCorrection+type);
		out.updateLocation(type);
				return out;
	}
	
	class ReshapeSmartHandle extends SmartHandle {
		

		private RectangularGraphic rect;
		
		private Point pressPoint;
		private Rectangle startingReshape;

		private ArrayList<LocatedObject2D> o2;

		private double xScale;

		private double yScale;

		private Point2D centerOfScaling;

		private double angle;

		private transient SelectionManager selectionManagger;

		public boolean editOngoing;

		private double xyScale;

		private double disPlaceX;

		private double disPlaceY;

		public  ReshapeSmartHandle(int type, RectangularGraphic r) {
			super(0, 0);
			this.setHandleNumber(type);
			this.rect=r;
			
			
		}
		public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
			
			this.updateLocation(getHandleNumber());
			if(this.getLineConnectionHandle() instanceof ReshapeSmartHandle)  {
				 ReshapeSmartHandle h2=(ReshapeSmartHandle) getLineConnectionHandle();
				 h2.updateLocation(h2.getHandleNumber());
			}
			if (getHandleType()==rect.getLocationType()) {
				
				this.setHandleColor(fixedpointHandleColor);
				
			} else this.setHandleColor(reshapeHandleColor);
			
			if (isRotationHandle()) {
				this.setHandleColor(Color.orange);
				//new GraphicUtil(). drawSizeHandlesAtPoint(graphics, cords,  this.getCordinateLocation(),rect.getCenterOfRotation());
			}
			
			
			
			if (this.isRotationHandle()&&specialShape==null) {
				int x2 = (int) (-handlesize*1.5);
				int w = (int) (handlesize*3);
				this.specialShape=new Ellipse2D.Double(x2, x2, w, w);
			}
			
			super.draw(graphics, cords);
		}
		
		/**@Override
		protected Area getOverdecorationShape() {
			if (this.getHandleType()==CENTER&&overDecorationShape==null) {
				
				this.decorationColor=Color.black;
				overDecorationShape=getAllDirectionArrows(2,2, false);
			}
			return overDecorationShape;
		}*/

		public boolean isRotationHandle() {
			return getHandleType()==rotationType;
		}

		public boolean handlesOwnUndo() {
			return true;
		}
		
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
		
		@Override
		public void handlePress(CanvasMouseEventWrapper w) {
			onHandlePress();
			this.pressPoint=w.getCordinatePoint();
			startingReshape=rect.getRectangle().getBounds();
		}
		
		@Override
		public void handleDrag(CanvasMouseEventWrapper w) {
			if(pressPoint==null||startingReshape==null) 
			{
				handlePress(w);
			}
			
		
			
			lastDrag=this;
			editOngoing=true;
			int handletype = getHandleType();
			
		
			
			rect.flipDuringHandleDrag=false;
			rect.handleSmartMove(handletype, pressPoint, w.getCordinatePoint());
			
			o2=copyObjects();
			xScale = rect.getRectangle().getWidth()/startingReshape.getWidth();
			yScale = rect.getRectangle().getHeight()/startingReshape.getHeight();
			
			 double dist1=RectangleEdges.distanceOppositeSide(handletype, startingReshape);
			double dist2= RectangleEdges.getLocation(rect.getLocationType(), rect.getRectangle()).distance(w.getCordinatePoint());
			xyScale=dist2/dist1;
			
			
			centerOfScaling = rect.getLocation();
			angle=rect.getAngle();
			
				if(handletype==CENTER) {
				 moveList(o2, pressPoint, w.getCordinatePoint());
			}  else if (this.isRotationHandle()) {
				if (angle!=0) {
				performRotate(o2, -angle);
				}
			} else {
				
			performScale(o2);
			
			}
				
			GraphicGroup g = new GraphicGroup(o2);
		
			selectionManagger = w.getAsDisplay().getImageAsWrapper().getSelectionManagger();
			
			selectionManagger.setSelectionGraphicWithoutSelecting(g);
	//if(!this.isRotationHandle()||o2.size()<2)//freezes under certain circumstances. failed to identify the issue but it only occurs when it tries to draw the copy
		
		
		
			
			
		}
		protected int getHandleType() {
			return this.getHandleNumber()-handleNumberCorrection;
		}
		
		private void moveList(ArrayList<LocatedObject2D> o22, Point pressPoint2, Point point) {
			disPlaceX = point.getX()-pressPoint2.getX();
			disPlaceY = point.getY()-pressPoint2.getY();
			
			for(LocatedObject2D ob:o22) {
				ob.moveLocation(disPlaceX, disPlaceY);
			}
		
		}
		
		private UndoMoveItems performMove(ArrayList<LocatedObject2D> o22) {
			UndoMoveItems undo = new UndoMoveItems(o22);
			for(LocatedObject2D ob:o22) {
				ob.moveLocation(disPlaceX, disPlaceY);
			}
			return undo;
		}
		
		
		private CompoundEdit2 performRotate(ArrayList<LocatedObject2D> o2, double angle) {
			return rotateList(o2, angle, rect.getCenterOfRotation());
			
		}
		private CompoundEdit2 rotateList(ArrayList<LocatedObject2D> o2, double angle, Point2D centerOfRotation) {
			CompoundEdit2 edit = new CompoundEdit2();
			for(LocatedObject2D ob:o2) {
				if (ob instanceof RotatesFully) try {
					UndoScaling undo = new UndoScaling(ob);
					((RotatesFully) ob).rotateAbout(centerOfRotation, angle);
					undo.establishFinalState();
					edit.addEditToList(undo);
				} catch (Throwable t) {t.printStackTrace();}
			}
			return edit;
		}
		private CompoundEdit2 performScale(ArrayList<LocatedObject2D> o2) {
			if(singleScale) {
				return scaleList(o2, xyScale, centerOfScaling);
			} else 
			return scaleList(o2, xScale, yScale, centerOfScaling);
		}
		private CompoundEdit2 scaleList(ArrayList<LocatedObject2D> o2, double xScale, double yScale, Point2D s) {
			CompoundEdit2 edit = new CompoundEdit2();
			for(LocatedObject2D ob:o2) {
				if (ob instanceof ScalesFully &&s!=null) {
					UndoScaling undo = new UndoScaling(ob);
					((ScalesFully) ob).scaleAbout(s, xScale, yScale);
					edit.addEditToList(undo);
				}
			}
			return edit;
		}
		
		private CompoundEdit2 scaleList(ArrayList<LocatedObject2D> o2, double xyScale, Point2D s) {
			CompoundEdit2 edit = new CompoundEdit2();
			for(LocatedObject2D ob:o2) {
				if (ob instanceof Scales &&s!=null) {
					UndoScaling undo = new UndoScaling(ob);
					((Scales) ob).scaleAbout(s, xyScale);
					undo.establishFinalState();
					edit.addEditToList(undo);
				}
			}
			return edit;
		}
		
		private ArrayList<LocatedObject2D>  copyObjects() {
			ArrayList<LocatedObject2D> output=new ArrayList<LocatedObject2D> ();
			for(LocatedObject2D o: objects) {
				output.add(o.copy());
			}
			
			return output;
		}
		@Override
		public void handleRelease(CanvasMouseEventWrapper w) {
			editComplete(w);
		}
		protected void editComplete(CanvasMouseEventWrapper w) {
			if(!editOngoing) return;
			if (selectionManagger!=null)selectionManagger.setSelectionGraphic2(null);
			if(isMoveHandle()) {
				UndoMoveItems edit =performMove(objects);
				addUndo(w, edit);
			} else
				if (!isRotationHandle()) 
					{
					CompoundEdit2 edit = performScale(objects);
					if(w!=null) {
						addUndo(w, edit);
						
					}
					FigureScaler.showScaleWarnings(objects);
					}
				else {
					CompoundEdit2 edit =performRotate(objects, -angle);
					
					if(w!=null) {
						addUndo(w, edit);
						
					}
				}
				rect.setAngle(0);
				updateRectangle();
				editOngoing=false;
				editover(w);
				
		}
		protected void addUndo(CanvasMouseEventWrapper w, AbstractUndoableEdit edit) {
			w.addUndo(edit);
		}
		


		
		
	
		
		private boolean isMoveHandle() {
			return getHandleType()==CENTER;
		}
		/**What to do when a handle is moved from point p1 to p2*/
		public void handleMove(Point2D p1, Point2D p2) {
			
			rect.handleSmartMove(getHandleNumber()-handleNumberCorrection, (Point) p1,  (Point) p2) ;
			
			
		}
		
	}

	public void finishEdit() {
		if(lastDrag!=null &&lastDrag.editOngoing) lastDrag.editComplete(null);
	}

	public void onHandlePress() {
		// TODO Auto-generated method stub
		
	}

	public void editover(CanvasMouseEventWrapper w) {
		// TODO Auto-generated method stub
		
	}
	


}
