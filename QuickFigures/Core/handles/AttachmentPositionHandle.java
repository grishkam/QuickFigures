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
 * Version: 2021.1
 */
package handles;

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
import applicationAdapters.CanvasMouseEvent;
import graphicTools.AttachedItemTool;
import graphicTools.AttachedItemTool2;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.KeyDownTracker;
import imageDisplayApp.OverlayObjectManager;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.ObjectContainer;
import locatedObject.RectangleEdges;
import locatedObject.TakesAttachedItems;
import menuUtil.SmartPopupJMenu;
import objectDialogs.MultiSnappingDialog;
import undo.CombinedEdit;
import undo.UndoReorder;
import undo.UndoAttachmentPositionChange;
import undo.UndoAddOrRemoveAttachedItem;

/**A handle that allows a user to move an item that is attached to another
 * @see AttachmentPosition
 * @see TakesAttachedItems
 * */
public class AttachmentPositionHandle extends SmartHandle {

	private static final int HandleNumberAdjust_100 = 100;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected TakesAttachedItems attachmentSite;
	protected LocatedObject2D object;
	private boolean infineControl=false;
	private Shape lastInnerShape;

	static LocatedObject2D potentialTransplantTarget;
	protected boolean releaseIt=false;
	private boolean transplantIt;
	protected static CombinedEdit currentEdit;
	protected  AttachmentPosition originalSnap;
	protected Rectangle originalBounds;
	boolean willTransplant=true;
	protected boolean suppressMenu;
	private AttachmentPositionHandle demiVerion;

	public AttachmentPositionHandle() {
	
		handlesize=20;
	}
	
	/***/
	public AttachmentPositionHandle(TakesAttachedItems taker, LocatedObject2D object, int num) {
		this();
		this.attachmentSite=taker;
		this.setObject(object);
		this.setHandleColor(new Color(100,100,100, 50));
		this.setHandleNumber(HandleNumberAdjust_100+num);
		this.setCordinateLocation(RectangleEdges.getLocation(RectangleEdges.CENTER, object.getBounds()));
		updateLocation();
		releaseIt=false;
	}
	
	public AttachmentPositionHandle copy() {
		AttachmentPositionHandle output = new AttachmentPositionHandle(attachmentSite, object, this.getHandleNumber()-HandleNumberAdjust_100);
		
		return output;
	}
	public AttachmentPositionHandle copyForSimpleDrag() {
		AttachmentPositionHandle output = copy();
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
	public void handleDrag(CanvasMouseEvent mEvent) {
		Point2D p2=mEvent.getCoordinatePoint();
		UndoAttachmentPositionChange undo = new UndoAttachmentPositionChange(object);
		if (this.fineControlMode()) {
			AttachedItemTool2.adjustPosition((int)p2.getX(), (int)p2.getY(), attachmentSite, object);
		} else
		getObject().getAttachmentPosition().setToNearestSnap(getObject().getBounds().getBounds(), attachmentSite.getContainerForBounds(object), new Point((int)p2.getX(), (int)p2.getY() ));
		
		
		undo.establishFinalState();
		if(!undo.same()) {
			if(currentEdit==null)
			getUndoManager().addEdit(undo);
			else currentEdit.addEditToList(undo);
		}
		
		showMessageForOutOfRange(mEvent);
	
	}

	public void showMessageForOutOfRange(CanvasMouseEvent mEvent) {
		boolean out = outOfRange(mEvent);
		Point2D p2=mEvent.getCoordinatePoint();
		OverlayObjectManager selectionManagger = mEvent.getAsDisplay().getImageAsWrapper().getOverlaySelectionManagger();
		if(out &&willTransplant) {
			releaseIt=true;
			TextGraphic marker = new TextGraphic("Release Item?");marker.setLocation(p2);
			LocatedObject2D marker2 = getObject().copy();marker2.setLocation(p2.getX(), p2.getY()+10);
			selectionManagger.setSelection(marker, 1);
			selectionManagger.setSelection(marker2, 0);
			//getObject().getSnappingBehaviour().copyPositionFrom(s);
			
			LocatedObject2D a = AttachedItemTool.getPotentialLockAcceptorAtPoint(mEvent.getCoordinatePoint(), this.getObject(), mEvent.getAsDisplay().getImageAsWrapper());
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
			getObject().getAttachmentPosition().copyPositionFrom(originalSnap);
			
		} else {
			releaseIt=false;
			transplantIt=false;
			selectionManagger.setSelectionstoNull();
		}
	}

	protected boolean outOfRange(CanvasMouseEvent mEvent) {
		return AttachedItemTool2.outofRange(this.getObject().getBounds(), attachmentSite.getBounds(), mEvent.getCoordinatePoint());
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
				attachmentSite.removeLockedItem(object);
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
	if(!attachmentSite.getLockedItems().contains(object)) return true;//if the item has been removed from the locked item list
	ObjectContainer cont = attachmentSite.getTopLevelContainer();
	if (cont==null) return true;
	if(!cont.getLocatedObjects().contains(object)) return true;
	
	return false;
}

@Override
public void draw(Graphics2D graphics, CordinateConverter cords) {
	
	this.updateLocation();
	
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


public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
	originalSnap=getObject().getAttachmentPosition().copy();
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



public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {
	
	if(releaseIt) {
		UndoAddOrRemoveAttachedItem undo = new UndoAddOrRemoveAttachedItem(attachmentSite, getObject() , true);
		
		attachmentSite.removeLockedItem(getObject());
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
	
	canvasMouseEventWrapper.getAsDisplay().getImageAsWrapper().getOverlaySelectionManagger().setSelectionstoNull();
}

private void performTransplant() {
	
	/**These lines of code move the locked item beween lock takers*/
	TakesAttachedItems potentialTransplant2 = (TakesAttachedItems) potentialTransplantTarget;
	UndoAddOrRemoveAttachedItem undo = new UndoAddOrRemoveAttachedItem(potentialTransplant2, getObject() , false);
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
		
		if (needsLayerTransplant) {
			//not yet implemented
		}
		
		} catch (Throwable t) {}

	
}

/**Creates a simplified version of this handle that will be used for dragging the attached object directly*/
public AttachmentPositionHandle createDemiVersion() {
	if (demiVerion==null) demiVerion = copyForSimpleDrag();
	 demiVerion.suppressMenu=true;
	 demiVerion.handlesize=handlesize/2;
	 demiVerion.handleStrokeColor=new Color(0,0,0,0);
	return  demiVerion;
}


}
