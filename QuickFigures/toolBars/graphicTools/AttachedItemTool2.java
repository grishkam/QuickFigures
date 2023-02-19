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
 * Date Modified: Oct 24, 2021
 * Version: 2023.1
 */
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
import applicationAdapters.ImageWorkSheet;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import handles.SmartHandle;
import imageDisplayApp.KeyDownTracker;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.TakesAttachedItems;
import undo.CombinedEdit;
import undo.UndoManagerPlus;
import undo.UndoMoveItems;
import undo.UndoAttachmentPositionChange;

/**A tool for moving attached items. no longer included in the toolbars but the methods
 * in this class are accessed via handles that can be clicked on without the use of this tool*/
public class AttachedItemTool2 extends AttachedItemTool {
	/**
	 * 
	 */
	private static final double STANDARD_LIMIT = 0.3;
	protected LocatedObject2D inside;
	protected TakesAttachedItems lockTaker;
	private UndoAttachmentPositionChange undosnap;
	private UndoMoveItems undoMove;
	boolean alwaysFine=false;
	
	public AttachedItemTool2(boolean fine) {
		alwaysFine=fine;
	}
	

	public void onPress(ImageWorkSheet gmp, LocatedObject2D roi2) {
		
		
		
		inside=this.getPrimarySelectedObject();
		if (inside==null) return;
		lockTaker=getLockContainterForObject(inside, getPotentialLockAcceptors(gmp));
		
		undoer=createUndoableEdit();
		
		
		
		if (this.getLastMouseEvent().isPopupTrigger()) {
			JPopupMenu menu = showpopup();
			
			
			Component c = getLastMouseEvent().getComponent();
			 if (menu!=null) menu.show(c, getLastMouseEvent().getClickedXScreen(),getLastMouseEvent().getClickedYScreen());
			
		}
		
	}


	private CombinedEdit createUndoableEdit() {
		CombinedEdit undoer=new CombinedEdit(undosnap); 
		undosnap = new UndoAttachmentPositionChange(inside);
		undoer.addEditToList(undosnap);
		undoMove=new UndoMoveItems(inside);
		undoer.addEditToList(undoMove);
		return undoer;
	}
	
	public void onRelease(ImageWorkSheet gmp, LocatedObject2D roi2) {
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
					UndoAttachmentPositionChange undo0 = new UndoAttachmentPositionChange(roi1);
					roi1.setAttachmentPosition(inside.getAttachmentPosition().copy());
					undo0.establishFinalState();
					undoer.addEditToList(undo0);
				}
			}
			
			/**removes the item from locking if out of range*/
			if (outofRange( inside.getBounds(), lockbounds, this.getDragPoint()) &&!fineControlMode()) {
				ArrayList<?> allRoi2 = getPotentialLockAcceptors(getImageClicked());
				
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


	public static void adjustPosition(int dragx, int dragy, TakesAttachedItems lockTaker, LocatedObject2D inside) {
		Rectangle lockbounds2 = lockTaker.getBounds();
		adjustPosition(dragx, dragy, lockbounds2, inside, STANDARD_LIMIT);
	}


	public static void adjustPosition(int dragx, int dragy, Rectangle lockbounds2, LocatedObject2D inside, double limitRatio) {
		AttachmentPosition s = inside.getAttachmentPosition();
		
		
		int[] poles = s.getOffSetPolarities();
		int dx=(int) (dragx-inside.getBounds().getCenterX());
		
		int dy=(int) (dragy-inside.getBounds().getCenterY());
		
		if (dx!=0) {
			double newdx = dx*poles[0]+s.getHorizontalOffset();
			if (Math.abs(newdx)<lockbounds2.width*limitRatio)s.setHorizontalOffset(newdx);
		}
		
		
		
		if (dy!=0){
			double newdy = dy*poles[1]+s.getVerticalOffset();
		if (Math.abs(newdy)<lockbounds2.height*limitRatio )s.setVerticalOffset(newdy);
		
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
					TakesAttachedItems t=(TakesAttachedItems) lockTaker;
					t.removeLockedItem(inside);
					
				}});
			
			oput.add(rel);
			
			return oput;
		}
		
		return null;
	}
	
	protected boolean forPopupTrigger(LocatedObject2D roi2, CanvasMouseEvent e, SmartHandle sh ) {return false;}
	
	
	
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
