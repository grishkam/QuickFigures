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
 * Date Modified: Jan 31, 2022
 * Version: 2022.0
 */
package popupMenusForComplexObjects;


import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import fLexibleUIKit.ObjectAction;
import figureOrganizer.PanelManager;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayerTypes.PanelMirror;
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
import utilityClasses1.TagConstants;
import menuUtil.PopupMenuSupplier;

/**A menu for shapes*/
public class ShapeGraphicMenu extends SmartPopupJMenu implements 
PopupMenuSupplier  {

	/**
	 * 
	 */
	
	static final String SHOW_OPTIONS_DIALOG="Options";
	
	ShapeGraphic targetShape;
	public ShapeGraphicMenu(ShapeGraphic theShape) {
		super();
		setupForShape(theShape);
		
	}

	/**
	 * @param theShape
	 */
	protected void setupForShape(ShapeGraphic theShape) {
		this.targetShape = theShape;
		this.addAllMenuItems(createMenuItems());
		new MenuItemExecuter(this).addToJPopupMenu(this);
		new MenuItemExecuter(theShape).addToJPopupMenu(this);
	}

	/**Creates the menu items for this menu*/
	ArrayList<JMenuItem> createMenuItems() {
		ArrayList<JMenuItem> j=new ArrayList<JMenuItem>();
		
		
		
		j.add( new ObjectAction<ShapeGraphic>(targetShape) {
			public AbstractUndoableEdit2  performAction() {
				item.showOptionsDialog();
				return null;
			}}.createJMenuItem("Options"));
			
	
	
		
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
	@MenuItemMethod(menuActionCommand = "duplicate", menuText = "Normal Duplicate", subMenuName="Duplicate", orderRank=2)
	public AbstractUndoableEdit2 performNormalDuplication() {
		LocatedObject2D copy = targetShape.copy();
		copy.moveLocation(5, 25);
		return Edit.addItem(targetShape.getParentLayer(), (ZoomableGraphic) copy);
	}

	/**makes a path copy of the object and deletes the original
	 * @return
	 */
	@MenuItemMethod(menuActionCommand = "point replace", menuText = "Replace With Points",  orderRank=8)
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
	@MenuItemMethod(menuActionCommand = "point duplicate", menuText = "Duplicate Points", subMenuName="Duplicate", orderRank=2)
	public AbstractUndoableEdit2 performPointDuplicate() {
		LocatedObject2D copy = targetShape.createPathCopy();
		copy.moveLocation(5, 25);
		return Edit.addItem(targetShape.getParentLayer(),(ZoomableGraphic) copy);
	}

	/**creates series of duplicates for either panels, rows, or columns 
	 * @return
	 */
	@MenuItemMethod(menuActionCommand = "l duplicate",menuText = "to ENUMs", subMenuName="Duplicate", orderRank=4)
	public AbstractUndoableEdit2 performLayoutDuplicate( LayoutSpaces.SpaceType type) {
		return performLayoutDuplicate( type, false);
	}
	
	/**creates series of duplicates for either panels, rows, or columns and keeps the duplicates in symetry with the original
	 * @return
	 */
	@MenuItemMethod(menuActionCommand = "l duplicate2",menuText = "to ENUMs and mirror", subMenuName="Duplicate", orderRank=5)
	public AbstractUndoableEdit2 performLayoutDuplicateAndMirror( LayoutSpaces.SpaceType type) {
		return performLayoutDuplicate( type, true);
	}
	
	
	/**Creates duplicates of this item in every panel
	 * @param mirrow set to true if a panel mirrow should be created
	 * */
	private AbstractUndoableEdit2 performLayoutDuplicate( LayoutSpaces.SpaceType type, boolean mirror) {
		CombinedEdit output = new CombinedEdit();
		GraphicLayerPane layer = (GraphicLayerPane) targetShape.getParentLayer();
		DefaultLayoutGraphic layout = PanelManager.getGridLayout(layer);
		if(layout==null) {
			ShowMessage.showOptionalMessage("Layout required", true, "A layout is required to use this option. Parent layer does not have a layout.", "Either draw a shape inside a layout or move it to a layer with a layout");
			return null;
		}
		
		if(layout.getPanelLayout().nPanels()<2) {
			ShowMessage.showOptionalMessage("Layout required", true, "A layout with multiple panels is required to use this option. Layout has only one panel.");
			return null;
		}
		
		if(LayoutSpaces.SpaceType.COLUMN==type&&layout.getPanelLayout().nColumns()<2) {
			ShowMessage.showOptionalMessage("Layout required", true, "A layout with multiple columns is required to use this option. Layout has only one.");
			return null;
		}
		
		if(LayoutSpaces.SpaceType.ROW==type&&layout.getPanelLayout().nRows()<2) {
			ShowMessage.showOptionalMessage("Layout required", true, "A layout with multiple rows is required to use this option. Layout has only one.");
			return null;
		}
		
		Point2D location = targetShape.getLocation();
		BasicLayout panelLayout = layout.getPanelLayout();
		panelLayout=panelLayout.makeAltered(type.getFullSpaceCode());
		int panelIndex = panelLayout.getPanelIndex(location.getX(), location.getY());
		Rectangle2D panel = panelLayout.getPanel(panelIndex);
		
		
		
		if(panel==null) {
			ShowMessage.showOptionalMessage("The object must overlap a layout panel for this option to work");
			return null;
		}
		
		int nPanelPerObject=0;//how many panels worth of space each object occupies
		for(Rectangle2D p: panelLayout.getPanels()) {
			if(targetShape.doesIntersect(p))
				nPanelPerObject++;
		}
		
		if(nPanelPerObject==0) {
			nPanelPerObject=1;//if no panels intersect the shape, assumes that each 
		}
		
		targetShape.getTagHashMap().put(TagConstants.GRID_SIZE,nPanelPerObject);
		
		double displaceX = location.getX()-panel.getX();
		double displaceY = location.getY()-panel.getY();
		
		
		GraphicLayer targetLayer = layout.getParentLayer();
		PanelMirror mirror1=null;
		if(mirror) {
			mirror1=new PanelMirror(targetShape, new PanelMirror.LayoutAddress(layout, panelIndex, type));
			output.addEdit(
					Edit.addItem(targetLayer, mirror1));
			targetLayer=mirror1;
		}
		
		
		SimpleGraphicalObject copy=null;
		for(int i=panelIndex; i<=panelLayout.nPanels(); i+=nPanelPerObject) {
			if(i==panelIndex)
				continue;
			copy=createCopy(i, output, displaceX, displaceY, panelLayout, targetLayer);
			if(mirror1!=null&&copy!=null) {
				mirror1.addReflection(copy, new PanelMirror.LayoutAddress(layout, i, type));
			}
		}
		for(int i=panelIndex; i>=1; i-=nPanelPerObject) {
			if(i==panelIndex)
				continue;
			copy=createCopy(i, output, displaceX, displaceY, panelLayout, targetLayer);
			if(mirror1!=null&&copy!=null) {
				mirror1.addReflection(copy, new PanelMirror.LayoutAddress(layout, i, type));
			}
		}
		
		if(copy!=null) {
			copy.getTagHashMap().put(TagConstants.GRID_SIZE,nPanelPerObject);
		}
		
		if(mirror1!=null)
			mirror1.updateAllReflectionLocations();
		
		return output;
		
	}

	/**Creates the copy for layout index i
	 * @param i
	 * @param output
	 * @param panelLayout 
	 * @param displaceY 
	 * @param displaceX 
	 * @return 
	 */
	private SimpleGraphicalObject createCopy(int i, CombinedEdit output, double displaceX, double displaceY, BasicLayout panelLayout, GraphicLayer layer) {
		Rectangle2D currentPanel = panelLayout.getPanel(i);
		SimpleGraphicalObject newItem = targetShape.copy();
		newItem.setLocationType(targetShape.getLocationType());
		newItem.setLocation(currentPanel.getX()+displaceX, currentPanel.getY()+displaceY);
		output.addEditToList(
				Edit.addItem(layer, newItem)
				);
		return newItem;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof ShapeGraphicMenu) {
			if(((ShapeGraphicMenu) o).targetShape==this.targetShape)
				return true;
		}
		
		return false;
	}
	
	
}
