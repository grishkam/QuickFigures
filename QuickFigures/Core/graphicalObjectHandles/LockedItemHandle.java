package graphicalObjectHandles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import actionToolbarItems.AlignItem;
import applicationAdapters.CanvasMouseEventWrapper;
import genericMontageKit.SelectionManager;
import graphicTools.LockGraphicTool;
import graphicTools.LockGraphicTool2;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import imageDisplayApp.KeyDownTracker;
import menuUtil.SmartPopupJMenu;
import objectDialogs.MultiSnappingDialog;
import undo.CombinedEdit;
import undo.UndoReorder;
import undo.UndoSnappingChange;
import undo.UndoTakeLockedItem;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.ObjectContainer;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.SnappingPosition;
import utilityClassesForObjects.TakesLockedItems;

public class LockedItemHandle extends SmartHandle {

	private static final int HandleNumberAdjust_100 = 100;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected TakesLockedItems taker;
	protected LocatedObject2D object;
	private CordinateConverter<?> cords;
	private boolean infineControl=false;
	private Shape lastInnerShape;

	static LocatedObject2D potentialTransplantTarget;
	protected boolean releaseIt=false;
	private boolean transplantIt;
	protected static CombinedEdit currentEdit;
	protected  SnappingPosition originalSnap;
	protected Rectangle originalBounds;
	boolean willTransplant=true;
	protected boolean suppressMenu;
	private LockedItemHandle demiVerion;

	public LockedItemHandle(int x, int y) {
		super(x, y);
		
		handlesize=20;
	}
	
	public LockedItemHandle(TakesLockedItems taker, LocatedObject2D object, int num) {
		this(0,0);
		this.taker=taker;
		this.setObject(object);
		this.setHandleColor(new Color(100,100,100, 50));
		this.setHandleNumber(HandleNumberAdjust_100+num);
		this.setCordinateLocation(RectangleEdges.getLocation(RectangleEdges.CENTER, object.getBounds()));
		updateLocation();
		releaseIt=false;
	}
	
	public LockedItemHandle copy() {
		LockedItemHandle output = new LockedItemHandle(taker, object, this.getHandleNumber()-100);
		
		return output;
	}
	public LockedItemHandle copyForSimpleDrag() {
		LockedItemHandle output = copy();
		output.willTransplant=this.willTransplant;
		output.originalBounds=originalBounds;
		output.setInfineControl(infineControl);
		return output;
	}
	
	protected boolean fineControlMode() {
		if (isInfineControl()) return true;
		return KeyDownTracker.isKeyDown('f')||KeyDownTracker.isKeyDown('F');
	}

	public void updateLocation() {
		this.setCordinateLocation(RectangleEdges.getLocation(RectangleEdges.CENTER, getObject().getBounds()));
		
		
	}
	
	/**What to do when a handle is moved from point p1 to p2*/
	@Override
	public void handleDrag(CanvasMouseEventWrapper mEvent) {
		Point2D p2=mEvent.getCoordinatePoint();
		UndoSnappingChange undo = new UndoSnappingChange(object);
		if (this.fineControlMode()) {
			LockGraphicTool2.adjustPosition((int)p2.getX(), (int)p2.getY(), taker, object);
		} else
		getObject().getSnapPosition().setToNearestSnap(getObject().getBounds().getBounds(), taker.getContainerForBounds(object), new Point((int)p2.getX(), (int)p2.getY() ));
		
		
		undo.establishFinalState();
		if(!undo.same()) {
			if(currentEdit==null)
			getUndoManager().addEdit(undo);
			else currentEdit.addEditToList(undo);
		}
		
		showMessageForOutOfRange(mEvent);
	
	}

	public void showMessageForOutOfRange(CanvasMouseEventWrapper mEvent) {
		boolean out = outOfRange(mEvent);
		Point2D p2=mEvent.getCoordinatePoint();
		SelectionManager selectionManagger = mEvent.getAsDisplay().getImageAsWrapper().getSelectionManagger();
		if(out &&willTransplant) {
			releaseIt=true;
			TextGraphic marker = new TextGraphic("Release Item?");marker.setLocation(p2);
			LocatedObject2D marker2 = getObject().copy();marker2.setLocation(p2.getX(), p2.getY()+10);
			selectionManagger.setSelection(marker, 1);
			selectionManagger.setSelection(marker2, 0);
			//getObject().getSnappingBehaviour().copyPositionFrom(s);
			
			LocatedObject2D a = LockGraphicTool.getPotentialLockAcceptorAtPoint(mEvent.getCoordinatePoint(), this.getObject(), mEvent.getAsDisplay().getImageAsWrapper());
			if (a!=null)
				{
				RectangularGraphic marker3 = RectangularGraphic.blankRect(a.getBounds(), Color.green);
				selectionManagger.setSelection(marker3, 0);
				marker.setText("Transplant Item?");
				marker.setTextColor(Color.red);
				potentialTransplantTarget=a;
				transplantIt=true;
				} else transplantIt=false;
			if(originalSnap!=null)
			getObject().getSnapPosition().copyPositionFrom(originalSnap);
			
		} else {
			releaseIt=false;
			transplantIt=false;
			selectionManagger.setSelectionstoNull();
		}
	}

	protected boolean outOfRange(CanvasMouseEventWrapper mEvent) {
		return LockGraphicTool2.outofRange(this.getObject().getBounds(), taker.getBounds(), mEvent.getCoordinatePoint());
	}

	

	public LocatedObject2D getObject() {
		return object;
	}

	public void setObject(LocatedObject2D object) {
		this.object = object;
	}
	
public JPopupMenu getJPopup() {
	if (suppressMenu) return null;
		SmartPopupJMenu men = new SmartPopupJMenu();
		JMenuItem jm = createAdjustPositionMenuItem();

		men.add(jm);
		
		JMenuItem jm2 = new JMenuItem("Release Locked item");
		men.add(jm2);
		
		jm2.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				taker.removeLockedItem(object);
			}});
		
		
		
		return men;
	}

public JMenuItem createAdjustPositionMenuItem() {
	JMenuItem jm = new JMenuItem("Adjust Position");
	jm.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			MultiSnappingDialog d = new MultiSnappingDialog(false);
			ArrayList<Object> array=new ArrayList<Object>(); 
			array.add(object);
			d.setGraphics(array);
			d.showDialog();
		}});
	return jm;
}

public boolean absent() {
	if(!taker.getLockedItems().contains(object)) return true;//if the item has been removed from the locked item list
	ObjectContainer cont = taker.getTopLevelContainer();
	if (cont==null) return true;
	if(!cont.getLocatedObjects().contains(object)) return true;
	
	return false;
}

@Override
public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
	
	this.updateLocation();
	this.cords=cords;
		super.draw(graphics, cords);
		
		Point2D pt = cords.transformP(getCordinateLocation());
		
		drawOnInnerShape(graphics, createDrawnCirc(pt));
}

/**Draws and inner shape to let the user know that clicking on the exact middle of the handle does sonething different*/
protected void drawOnInnerShape(Graphics2D graphics, Shape s) {
	graphics.setColor(Color.red);
	graphics.fill(s);
	
	graphics.setStroke(getHandleStroke());
	graphics.setColor(Color.black);
	graphics.draw(s);
	lastInnerShape=s;
}

private Shape createDrawnCirc(Point2D pt) {
	double size=3.5;
	double xr = pt.getX()-size;
	double yr = pt.getY()-size;
	double widthr = size*2;
	double heightr = size*2;
	return new Ellipse2D.Double(xr,yr, widthr, heightr);
}


public void handlePress(CanvasMouseEventWrapper canvasMouseEventWrapper) {
	originalSnap=getObject().getSnapPosition().copy();
	originalBounds=getObject().getBounds();
	if(lastInnerShape!=null&&lastInnerShape.contains(canvasMouseEventWrapper.getClickedXScreen(), canvasMouseEventWrapper.getClickedYScreen()))
		setInfineControl(true); else setInfineControl(false);
	
	releaseIt=false;
	transplantIt=false;
	currentEdit=new CombinedEdit();
	
	//	double distance = this.getCordinateLocation().distance(canvasMouseEventWrapper.getClickedXImage(), canvasMouseEventWrapper.getClickedYImage());
	
	//if (distance<2.5) setInfineControl(true); else setInfineControl(false);
	
}

public boolean isInfineControl() {
	return infineControl;
}

private void setInfineControl(boolean infineControl) {
	this.infineControl = infineControl;
}

/**If the locked item is either hidden or not in the image anymore, will hide the handle*/
@Override
public boolean isHidden() {
	if (absent()) return true;
	if(object.isHidden()) return true;
	if (object instanceof TextGraphic &&((TextGraphic) object).isEditMode()) {return true;}
	return super.isHidden();
}



public void handleRelease(CanvasMouseEventWrapper canvasMouseEventWrapper) {
	
	if(releaseIt) {
		UndoTakeLockedItem undo = new UndoTakeLockedItem(taker, getObject() , true);
		
		taker.removeLockedItem(getObject());
		if(currentEdit!=null) currentEdit.addEditToList(undo);
		getObject().setLocation(canvasMouseEventWrapper.getCoordinatePoint());
		releaseIt=false;
	}
	if (transplantIt) {
		performTransplant();
		transplantIt=false;
	}
	
	
	if(currentEdit!=null)
	 canvasMouseEventWrapper.getAsDisplay().getUndoManager().addEdit(currentEdit);
	
	canvasMouseEventWrapper.getAsDisplay().getImageAsWrapper().getSelectionManagger().setSelectionstoNull();
}

private void performTransplant() {
	
	/**These lines of code move the locked item beween lock takers*/
	TakesLockedItems potentialTransplant2 = (TakesLockedItems) potentialTransplantTarget;
	UndoTakeLockedItem undo = new UndoTakeLockedItem(potentialTransplant2, getObject() , false);
	potentialTransplant2.addLockedItem(getObject());
	if(currentEdit!=null) currentEdit.addEditToList(undo);
	
	boolean needsLayerTransplant = false;
	boolean needsLayerReorder = false;
	
	try {
	ZoomableGraphic z= (ZoomableGraphic) potentialTransplantTarget;
	ZoomableGraphic z2= (ZoomableGraphic) getObject();
	
		if(!z.getParentLayer().getParentLayer().hasItem(z))
			needsLayerTransplant = true;
		
		
		
		ArrayList<ZoomableGraphic> all = z.getParentLayer().getTopLevelParentLayer().getAllGraphics();
		if(all.indexOf(z)>all.indexOf(z2))
			needsLayerReorder = true;
		
		if(needsLayerReorder) {
			UndoReorder undoRe = AlignItem.moveItemInLayer(z2, AlignItem.MOVE_TO_FRONT, z2.getParentLayer());			
			currentEdit.addEditToList(undoRe);
		}
		
		} catch (Throwable t) {}

	
}

public LockedItemHandle createDemiVersion() {
	
	if (demiVerion==null) demiVerion = copyForSimpleDrag();
	 demiVerion.suppressMenu=true;
	 demiVerion.handlesize=handlesize/2;
	demiVerion.handleStrokeColor=new Color(0,0,0,0);
	return  demiVerion;
}


}
