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
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_Shapes.ArrowGraphic;
import menuUtil.SmartPopupJMenu;
import undo.UndoScalingAndRotation;
import menuUtil.PopupMenuSupplier;

/**a popup menu for arrows that includes both the standard menu options for shapes
  and those specific to arrows*/
public class ArrowGraphicMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier  {

	private static final String USE_DIFFERENT_HEADS = "Use different heads";

	/**
	 * 
	 */
	/**the arrow specific menu options*/
	static final String  editOutline="Outline Shape", flipHead="Swap Ends", makeVertical= "Make Vertical",makeHorizontal= "Make Horizontal";
	
	ArrowGraphic targetArrow;
	ShapeGraphicMenu shapeGraphicMenu;

	
	public ArrowGraphicMenu(ArrowGraphic arrow) {
		super();
		this.targetArrow = arrow;
		
		/**adds the shape manu options*/
		shapeGraphicMenu = new ShapeGraphicMenu(arrow);
		this.addAllMenuItems(shapeGraphicMenu.createMenuItems());
		
		/**Adds the arrow specific menu options*/
		add(createMenuItem(editOutline));
		
		add(createMenuItem(flipHead));
		add(createMenuItem(makeHorizontal));
		add(createMenuItem(makeVertical));
		if (arrow.getNHeads()>1 &&arrow.headsAreSame()) {
			add(createMenuItem(USE_DIFFERENT_HEADS));
		}
	}
	
	public void setLastMouseEvent(CanvasMouseEvent e) {
		super.setLastMouseEvent(e);
		if (shapeGraphicMenu!=null)shapeGraphicMenu.setLastMouseEvent(e);
	}
	
	public JMenuItem createMenuItem(String st) {
		JMenuItem o=new JMenuItem(st);
		o.addActionListener(this);
		o.setActionCommand(st);
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
		if (com.equals(editOutline)) {
			showOutlineDialog();
		}
	
		if (com.equals(flipHead)) {
			swapHeads();
		}
		
		if (com.equals(makeVertical)) {
			makeVertical();
			
			
		}
		if (com.equals(makeHorizontal)) {
			makeHorizontal();
		}
		
		if(com.equals(USE_DIFFERENT_HEADS)) {
			targetArrow.setHeadsSame(false);
		}
		targetArrow.updateDisplay();
		
	}

	/**
	 * 
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
	 * 
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
