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
 * Date Modified: Dec 17, 2021
 * Version: 2021.2
 */
package popupMenusForComplexObjects;


import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fLexibleUIKit.ObjectAction;
import figureOrganizer.PanelManager;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_Shapes.SimpleGraphicalObject;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import messages.ShowMessage;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.Edit;
import menuUtil.PopupMenuSupplier;
import menuUtil.SmartJMenu;

/**A menu for shapes*/
public class ShapeGraphicMenu extends SmartPopupJMenu implements 
PopupMenuSupplier  {

	/**
	 * 
	 */
	
	static final String SHOW_OPTIONS_DIALOG="Options";
	
	ShapeGraphic targetShape;
	public ShapeGraphicMenu(ShapeGraphic textG) {
		super();
		this.targetShape = textG;
		this.addAllMenuItems(createMenuItems());
	}

	/**Creates the menu items for this menu*/
	ArrayList<JMenuItem> createMenuItems() {
		ArrayList<JMenuItem> j=new ArrayList<JMenuItem>();
		
		SmartJMenu duplicateMenu = new SmartJMenu("Duplicate");
		
		j.add( new ObjectAction<ShapeGraphic>(targetShape) {
			public AbstractUndoableEdit2  performAction() {
				item.showOptionsDialog();
				return null;
			}}.createJMenuItem("Options"));
			
		duplicateMenu.add( new ObjectAction<ShapeGraphic>(targetShape) {
			public AbstractUndoableEdit2  performAction() {
				return performDuplication();
			}}.createJMenuItem("Normal Duplicate"));
		
		duplicateMenu.add( new ObjectAction<ShapeGraphic>(targetShape) {
			public AbstractUndoableEdit2  performAction() {
				return performPointDuplicate();
						
				
			}}.createJMenuItem("Duplicate Points"));
		
		duplicateMenu.add( new ObjectAction<ShapeGraphic>(targetShape) {
			public AbstractUndoableEdit2  performAction() {
				return performLayoutDuplicate(LayoutSpaces.PANELS);
			}

			}.createJMenuItem("To panels"));
		
		duplicateMenu.add( new ObjectAction<ShapeGraphic>(targetShape) {
			public AbstractUndoableEdit2  performAction() {
				return performLayoutDuplicate(LayoutSpaces.COLS);
			}

			}.createJMenuItem("To columns"));
		
		duplicateMenu.add( new ObjectAction<ShapeGraphic>(targetShape) {
			public AbstractUndoableEdit2  performAction() {
				return performLayoutDuplicate(LayoutSpaces.ROWS);
			}

			}.createJMenuItem("To rows"));
		
		j.add(duplicateMenu);
	
		
		j.add( new ObjectAction<ShapeGraphic>(targetShape) {
			public CombinedEdit  performAction() {
				return performReplaceWithPoints();
			}}.createJMenuItem("Replace With Points"));
		
		try {
			j.add(DonatesMenu.MenuFinder.addDonatedMenusTo(null, targetShape));
		} catch (Exception e1) {
			IssueLog.log(e1);
		}
		
		return j;
	}
	


	private static final long serialVersionUID = 1L;

	@Override
	public JPopupMenu getJPopup() {
		return this;
	}

	/**Adds a duplicate to the layer
	 * @return
	 */
	public AbstractUndoableEdit2 performDuplication() {
		LocatedObject2D copy = targetShape.copy();
		copy.moveLocation(5, 25);
		return Edit.addItem(targetShape.getParentLayer(), (ZoomableGraphic) copy);
	}

	/**makes a path copy of the object and deletes the original
	 * @return
	 */
	public CombinedEdit performReplaceWithPoints() {
		ZoomableGraphic copy = (ZoomableGraphic)targetShape.createPathCopy();
		GraphicLayer layer = targetShape.getParentLayer();
		
		return new CombinedEdit(
				Edit.addItem(layer, copy),
				Edit.swapItemOrder(layer, targetShape, copy),
				Edit.removeItem(layer,targetShape)
				);
	}

	/**creates a single duplicate
	 * @return
	 */
	public AbstractUndoableEdit2 performPointDuplicate() {
		LocatedObject2D copy = targetShape.createPathCopy();
		copy.moveLocation(5, 25);
		return Edit.addItem(targetShape.getParentLayer(),(ZoomableGraphic) copy);
	}

	/**Creates duplicates of this item in every panel*/
	private AbstractUndoableEdit2 performLayoutDuplicate( int type) {
		CombinedEdit output = new CombinedEdit();
		GraphicLayerPane layer = (GraphicLayerPane) targetShape.getParentLayer();
		DefaultLayoutGraphic layout = PanelManager.getGridLayout(layer);
		if(layout==null) {
			ShowMessage.showOptionalMessage("A layout is required to use this option. Paernt layer does not have a layout");
			return null;
		}
		
		Point2D location = targetShape.getLocation();
		BasicLayout panelLayout = layout.getPanelLayout();
		panelLayout=panelLayout.makeAltered(type);
		int panelIndex = panelLayout.getPanelIndex(location.getX(), location.getY());
		Rectangle2D panel = panelLayout.getPanel(panelIndex);
		
		if(panel==null) {
			ShowMessage.showOptionalMessage("The object must overlap a layout panel for this option to work");
			return null;
		}
		
		int nPanels=0;
		for(Rectangle2D p: panelLayout.getPanels()) {
			if(targetShape.doesIntersect(p))
				nPanels++;
		}
		
		if(nPanels==0) {
			IssueLog.log("failed to find panel that overlaps object");
			nPanels=1;
		}
		
		double displaceX = location.getX()-panel.getX();
		double displaceY = location.getY()-panel.getY();
		
		for(int i=1; i<=panelLayout.nPanels(); i+=nPanels) {
			if(i==panelIndex)
				continue;
			Rectangle2D currentPanel = panelLayout.getPanel(i);
			SimpleGraphicalObject newItem = targetShape.copy();
			newItem.setLocationType(targetShape.getLocationType());
			newItem.setLocation(currentPanel.getX()+displaceX, currentPanel.getY()+displaceY);
			output.addEditToList(
					Edit.addItem(layout.getParentLayer(), newItem)
					);
		}
		
		
		return output;
		
	}
	
	
	
	
}
