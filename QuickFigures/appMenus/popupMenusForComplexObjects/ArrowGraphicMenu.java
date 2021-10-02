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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package popupMenusForComplexObjects;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import applicationAdapters.CanvasMouseEvent;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects_Shapes.ArrowGraphic;
import locatedObject.LocationChangeListener;
import menuUtil.SmartPopupJMenu;
import undo.AbstractUndoableEdit2;
import undo.UndoManagerPlus;
import undo.UndoScalingAndRotation;
import menuUtil.BasicSmartMenuItem;
import menuUtil.PopupMenuSupplier;

/**a popup menu for arrows that includes both the standard menu options for shapes
  and those specific to arrows.
  */
public class ArrowGraphicMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier  {

	private static final String USE_DIFFERENT_HEADS = "Use two different heads", USE_IDENTICAL_HEADS = "Use two identical heads";

	/**
	 * 
	 */
	/**the arrow specific menu options*/
	static final String  EDIT_ARROW_OUTLINE="Outline Shape", SWAP_HEADS="Swap Ends", MAKE_VERTICAL= "Make Vertical",MAKE_HORIZONTAL= "Make Horizontal";
	
	ArrowGraphic targetArrow;
	ShapeGraphicMenu shapeGraphicMenu;

	
	public ArrowGraphicMenu(ArrowGraphic arrow) {
		super();
		this.targetArrow = arrow;
		
		/**adds the shape manu options*/
		shapeGraphicMenu = new ShapeGraphicMenu(arrow);
		this.addAllMenuItems(shapeGraphicMenu.createMenuItems());
		
		/**Adds the arrow specific menu options*/
		add(createMenuItem(EDIT_ARROW_OUTLINE));
		
		add(createMenuItem(SWAP_HEADS));
		add(createMenuItem(MAKE_HORIZONTAL));
		add(createMenuItem(MAKE_VERTICAL));
		
		if (arrow.headsAreSame()) {
			add(createMenuItem(USE_DIFFERENT_HEADS));
		}
		if (!arrow.headsAreSame()) {
			add(createMenuItem(USE_IDENTICAL_HEADS));
		}
		JComponent addedMenu=this;
		DonatesMenu.MenuFinder.addDonatedMenusTo(addedMenu, arrow);
		
	}

	
	
	public void setLastMouseEvent(CanvasMouseEvent e) {
		super.setLastMouseEvent(e);
		if (shapeGraphicMenu!=null)shapeGraphicMenu.setLastMouseEvent(e);
	}
	
	public JMenuItem createMenuItem(String st) {
		return createMenuItem(st, false);
	}
	public JMenuItem createMenuItem(String st, boolean grey) {
		BasicSmartMenuItem o=new BasicSmartMenuItem(st);
		o.addActionListener(this);
		o.setActionCommand(st);
		o.setGreyOut(grey);
		return o;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public JPopupMenu getJPopup() {
		return this;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String com=arg0.getActionCommand();
		if (com.equals(EDIT_ARROW_OUTLINE)) {
			showOutlineDialog();
			return;
		}
		
		AbstractUndoableEdit2 undo = targetArrow.provideUndoForDialog();
	
		if (com.equals(SWAP_HEADS)) {
			swapHeads();
		}
		
		if (com.equals(MAKE_VERTICAL)) {
			makeVertical();
			
			
		}
		if (com.equals(MAKE_HORIZONTAL)) {
			makeHorizontal();
		}
		
		if(com.equals(USE_DIFFERENT_HEADS)) {
			targetArrow.setNumerOfHeads(2);
			targetArrow.setHeadsSame(false);
		}
		
		if(com.equals(USE_IDENTICAL_HEADS)) {
			targetArrow.setNumerOfHeads(2);
			targetArrow.setHeadsSame(true);
		}
		
		undo.establishFinalState();
		UndoManagerPlus um = getUndoManager();
		if (um!=null)um.addEdits(undo);
		
		targetArrow.updateDisplay();
		
	}

	/**
	shows a dialog for the outline shape
	 */
	public void showOutlineDialog() {
		/**sets the arrow to outline mode if it is not already*/
				if (targetArrow.drawnAsOutline()!=ArrowGraphic.OUTLINE_SHAPE)
					{
					UndoScalingAndRotation undo = new  UndoScalingAndRotation(targetArrow);
					targetArrow.setDrawAsOutline(ArrowGraphic.OUTLINE_SHAPE);
					undo.establishFinalState();
					if (this.getUndoManager()!=null)this.getUndoManager().addEdit(undo);
					}
		targetArrow.getBackGroundShape().showOptionsDialog();
	}

	/**
	
	 */
	public void swapHeads() {
		targetArrow.swapDirections();
		targetArrow.updateDisplay();
	}

	/**
	 * 
	 */
	public void makeVertical() {
		ArrayList<Point2D> pp = targetArrow.getEndPoints();
		double x = pp.get(0).getX();
		double y = pp.get(1).getY();
		targetArrow.setPoints(pp.get(0), new Point2D.Double(x, y));
	}

	/**
	 * 
	 */
	public void makeHorizontal() {
		ArrayList<Point2D> pp = targetArrow.getEndPoints();
		double x2 = pp.get(1).getX();
		double y = pp.get(0).getY();
		targetArrow.setPoints(pp.get(0), new Point2D.Double(x2, y));
	}
	
	
	
}
