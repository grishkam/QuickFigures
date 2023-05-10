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
 * Version: 2023.2
 */
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import applicationAdapters.CanvasMouseEvent;
import fLexibleUIKit.MenuChoiceAnnotation;
import fLexibleUIKit.MenuItemMethod;
import graphicalObjects_Shapes.ArrowGraphic;
import undo.AbstractUndoableEdit2;
import undo.UndoManagerPlus;
import undo.UndoScalingAndRotation;
import menuUtil.BasicSmartMenuItem;
import menuUtil.PopupMenuSupplier;

/**a popup menu for arrows that includes both the standard menu options for shapes
  and those specific to arrows.
  */
public class ArrowGraphicMenu extends ShapeGraphicMenu implements ActionListener,
PopupMenuSupplier  {

	
	/**
	 * 
	 */
	/**the arrow specific menu options*/
	static final String  EDIT_ARROW_OUTLINE="Outline Shape";
	
	ArrowGraphic targetArrow;
	ShapeGraphicMenu shapeGraphicMenu;

	
	public ArrowGraphicMenu(ArrowGraphic arrow) {
		super(arrow);
		this.targetArrow = arrow;
		
		
		
		/**Adds the arrow specific menu options*/
		add(createMenuItem(EDIT_ARROW_OUTLINE));
		
		JComponent addedMenu=this;
		///DonatesMenu.MenuFinder.addDonatedMenusTo(addedMenu, arrow);
		
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

	/**Swaps the heads of the arrow
	 * @return 
	 */
	@MenuItemMethod( menuText = "Swap Heads", orderRank=6)
	public AbstractUndoableEdit2 swapHeads() {
		UndoScalingAndRotation output = new UndoScalingAndRotation(targetArrow);
		targetArrow.swapDirections();
		targetArrow.updateDisplay();
		output.establishFinalState();
		return output;
	}

	/**
	 * @return 
	 */
	@MenuItemMethod( menuText = "Make Vertical", orderRank=7)
	public AbstractUndoableEdit2 makeVertical() {
		UndoScalingAndRotation output = new UndoScalingAndRotation(targetArrow);
		ArrayList<Point2D> pp = targetArrow.getEndPoints();
		double x = pp.get(0).getX();
		double y = pp.get(1).getY();
		targetArrow.setPoints(pp.get(0), new Point2D.Double(x, y));
		output.establishFinalState();
		return output;
	}

	/**
	 * @return 
	 */
	@MenuItemMethod( menuText = "Make Horizontal", orderRank=8)
	public AbstractUndoableEdit2 makeHorizontal() {
		UndoScalingAndRotation output = new UndoScalingAndRotation(targetArrow);
		ArrayList<Point2D> pp = targetArrow.getEndPoints();
		double x2 = pp.get(1).getX();
		double y = pp.get(0).getY();
		targetArrow.setPoints(pp.get(0), new Point2D.Double(x2, y));
		output.establishFinalState();
		return output;
	}
	
	@MenuItemMethod( menuText = "Use two identical heads:Use two different heads", orderRank=10)
	public AbstractUndoableEdit2 setHeadStatus(
					@MenuChoiceAnnotation(findCurrent="sameHeads")
						Boolean useSameHead
			) {
		AbstractUndoableEdit2 undo = targetArrow.provideUndoForDialog();
		targetArrow.setNumerOfHeads(2);
		targetArrow.setHeadsSame(useSameHead);
		undo.establishFinalState();
		return undo;
	}
	
	/**returns true if the arrow heads are identical*/
	public Boolean sameHeads() {
		if(targetArrow==null &&targetShape instanceof ArrowGraphic)
			targetArrow=(ArrowGraphic) targetShape;
		return targetArrow.headsAreSame();
		}
}
