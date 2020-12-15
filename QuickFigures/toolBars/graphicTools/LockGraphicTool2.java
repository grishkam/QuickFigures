/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package graphicTools;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.ImageWrapper;
import graphicalObjectHandles.SmartHandle;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import imageDisplayApp.KeyDownTracker;
import undo.CombinedEdit;
import undo.UndoManagerPlus;
import undo.UndoMoveItems;
import undo.UndoSnappingChange;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.AttachmentPosition;
import utilityClassesForObjects.TakesLockedItems;

public class LockGraphicTool2 extends LockGraphicTool {
	protected LocatedObject2D inside;
	protected TakesLockedItems lockTaker;
	private UndoSnappingChange undosnap;
	private UndoMoveItems undoMove;
	boolean alwaysFine=false;
	
	public LockGraphicTool2(boolean fine) {
		alwaysFine=fine;
	}
	

	public void onPress(ImageWrapper gmp, LocatedObject2D roi2) {
		
		
		
		inside=this.getPrimarySelectedObject();
		if (inside==null) return;
		lockTaker=getLockContainterForObject(inside, getPotentialLockAcceptors(gmp));
		
		undoer=createUndoableEdit();
		
		
		
		if (this.getLastClickMouseEvent().isPopupTrigger()) {
			JPopupMenu menu = showpopup();
			
			
			Component c = getLastClickMouseEvent().getComponent();
			 if (menu!=null) menu.show(c, getLastClickMouseEvent().getClickedXScreen(),getLastClickMouseEvent().getClickedYScreen());
			
		}
		
	}


	private CombinedEdit createUndoableEdit() {
		CombinedEdit undoer=new CombinedEdit(undosnap); 
		undosnap = new UndoSnappingChange(inside);
		undoer.addEditToList(undosnap);
		undoMove=new UndoMoveItems(inside);
		undoer.addEditToList(undoMove);
		return undoer;
	}
	
	public void onRelease(ImageWrapper gmp, LocatedObject2D roi2) {
		if (lockTaker==null&&inside!=null) {
			super.onRelease(gmp, roi2);
			lockTaker=getLockContainterForObject(inside, getPotentialLockAcceptors(gmp));
			if (lockTaker!=null){
				inside.getAttachmentPosition().setToNearestSnap(inside.getBounds(), lockTaker.getBounds(), this.getDragPoint() );
			}
		}
		
	}
	
	public void mouseDragged() {
		
		UndoManagerPlus undoMan = this.getImageDisplayWrapperClick().getUndoManager();
		
		if (inside!=null&&lockTaker!=null) {
			Rectangle2D lockbounds = lockTaker.getBounds();
			
			if (lockTaker instanceof PanelLayoutGraphic) {//what to do if the lock taker has many panels
				PanelLayoutGraphic plg = (PanelLayoutGraphic) lockTaker;
				lockbounds = plg.getPanelLayout().getNearestPanel(getDragPoint().getX(), getDragPoint().getY());
				
			}
			
			
		if (getAllSelectedItems(false).size()>1) {
			
		}
			
		/**performs subtle shift in the offset*/
			if (fineControlMode()) {
				int dragx = getDragCordinateX();
				int dragy = getDragCordinateY();
				
				adjustPosition(dragx, dragy, lockTaker, inside);
				
			} else 
				inside.getAttachmentPosition().setToNearestSnap(inside.getBounds(), lockbounds, this.getDragPoint() );
			
			undosnap.establishFinalState();
			
			if (this.shiftDown()) {
				for(LocatedObject2D roi1: otherSelectedItems) {
					UndoSnappingChange undo0 = new UndoSnappingChange(roi1);
					roi1.setAttachmentPosition(inside.getAttachmentPosition().copy());
					undo0.establishFinalState();
					undoer.addEditToList(undo0);
				}
			}
			
			/**removes the item from locking if out of range*/
			if (outofRange( inside.getBounds(), lockbounds, this.getDragPoint()) &&!fineControlMode()) {
				ArrayList<LocatedObject2D> allRoi2 = getPotentialLockAcceptors(getImageClicked());
				
				removeFromAlltakers(inside, allRoi2, undoer);
				inside.setLocation(getDragPoint());
				undoMove.establishFinalLocations();
				lockTaker=null;
			}
		
		} else {
			if (lockTaker==null&&inside!=null&&!fineControlMode()) {
				inside.setLocation(getDragPoint());
			}
		}
		if (!undoMan.hasUndo(undoer)) undoMan.addEdit(undoer);
		
		
	}


	public static void adjustPosition(int dragx, int dragy, TakesLockedItems lockTaker, LocatedObject2D inside) {
		Rectangle lockbounds2 = lockTaker.getBounds();
		adjustPosition(dragx, dragy, lockbounds2, inside);
	}


	public static void adjustPosition(int dragx, int dragy, Rectangle lockbounds2, LocatedObject2D inside) {
		AttachmentPosition s = inside.getAttachmentPosition();
		
		
		int[] poles = s.getOffSetPolarities();
		int dx=(int) (dragx-inside.getBounds().getCenterX());
		
		int dy=(int) (dragy-inside.getBounds().getCenterY());
		
		if (dx!=0) {
			double newdx = dx*poles[0]+s.getHorizontalOffset();
			if (Math.abs(newdx)<lockbounds2.width/4)s.setHorizontalOffset(newdx);
		}
		
		
		
		if (dy!=0){
			double newdy = dy*poles[1]+s.getVerticalOffset();
		if (Math.abs(newdy)<lockbounds2.height/4 )s.setVerticalOffset(newdy);
		
		}
	}
	
	public static void adjustPositionForBar(int dragx, int dragy, Rectangle lockbounds2, LocatedObject2D inside) {
		AttachmentPosition s = inside.getAttachmentPosition();
		
		
		int[] poles = s.getOffSetPolarities();
		int dx=(int) (dragx-inside.getBounds().getCenterX());
		
		int dy=(int) (dragy-inside.getBounds().getCenterY());
		
		if (dx!=0) {
			double newdx = dx*poles[0]+s.getHorizontalOffset();
			if (Math.abs(newdx)<lockbounds2.width)s.setHorizontalOffset(newdx);
		}
		
		
		
		if (dy!=0){
			double newdy = dy*poles[1]+s.getVerticalOffset();
		if (Math.abs(newdy)<lockbounds2.height )s.setVerticalOffset(newdy);
		
		}
	}

	protected boolean fineControlMode() {
		if ( alwaysFine) return true;
		return KeyDownTracker.isKeyDown('f')||KeyDownTracker.isKeyDown('F');
	}
	
	/**returns true if the point is out or range for rectangle 1 to snap to rectangle 2*/
	public static boolean outofRange(Rectangle2D r1, Rectangle2D r2, Point2D p) {
		if (Math.abs(p.getX()-r2.getCenterX())> r2.getWidth()*0.75+r1.getWidth()*0.75) return true;
		if (Math.abs(p.getY()-r2.getCenterY())> r2.getHeight()*0.75+r1.getHeight()*0.75) return true;
		
		return false;
	}
	
	private JPopupMenu showpopup() {
		JPopupMenu  oput = new JPopupMenu("");
		if (inside!=null) {
			JMenuItem rel = new JMenuItem("Release");
			rel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					TakesLockedItems t=(TakesLockedItems) lockTaker;
					t.removeLockedItem(inside);
					
				}});
			
			oput.add(rel);
			
			return oput;
		}
		
		return null;
	}
	
	protected void forPopupTrigger(LocatedObject2D roi2, CanvasMouseEvent e, SmartHandle sh ) {}
	
	
	
	@Override
	public String getToolTip() {
		if (alwaysFine) return "Precisely Position Attached Items";
			return "Move Attached Items (Try holding SHIFT key)";
		}
	
	@Override
	public String getToolName() {
		if (alwaysFine) return "Precise Mover for Attached Items";
			return "Mover For Attached Items";
		}
	
}
