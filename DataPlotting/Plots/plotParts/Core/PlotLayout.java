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
 * Date Modified: Jan 7, 2021
 * Version: 2021.1
 */
package plotParts.Core;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import genericPlot.BasicPlot;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import handles.SmartHandleList;
import layout.BasicObjectListHandler.LocatedObjectFilter;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.GenericMontageEditor;
import layout.basicFigure.LayoutSpaces;
import locatedObject.LocatedObject2D;
import menuUtil.SmartPopupJMenu;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import popupMenusForComplexObjects.AttachedItemMenu;

/**A special layout object for plot areas*/
public class PlotLayout extends DefaultLayoutGraphic implements LayoutSpaces{

	private BasicPlot plotArea;

	public PlotLayout(BasicLayout basicMontageLayout) {
		super(basicMontageLayout);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	{this.setName("Plot Layout");}
	
	/**
	private void reDivideToCols(int nCol) {
		Rectangle plotArea = getPanelLayout().getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds();
	
		int h = plotArea.height;
		int forEachSection= (int)( plotArea.getWidth()/nCol);
		int w =forEachSection-1;
		
		
		getPanelLayout().setPanelSizes(w, h);
		getPanelLayout().setNRows(1);
		getPanelLayout().setNColumns(nCol);
		getPanelLayout().setHorizontalBorder(1);
		getPanelLayout().setVerticalBorder(0);
		
	}*/
	
	
	public GenericMontageEditor getEditor() {
		if (editor==null){
			editor= new GenericMontageEditor ();
			editor.setQualificationsForPanelObject(new PlotLayoutPanelItentifier());
		}
		
		return editor;
	}
	
	/**class modifies the criteria used to itentify something as a panel object*/
	class PlotLayoutPanelItentifier implements LocatedObjectFilter {

		@Override
		public boolean isObjectDesireableForPanel(Rectangle2D gra, LocatedObject2D objects) {
		
			//Rectangle bPanel = gra.getBounds();
			//Rectangle bOb = objects.getBounds();
			if (!gra.contains(objects.getBounds().getCenterX(), objects.getBounds().getCenterY())) return false;
			//if (bOb.getCenterX()+  bOb.getWidth()*.25 >bPanel.getMaxX()) return false;
			//if (bOb.getCenterY()+ bOb.getHeight()*0.25>bPanel.getMaxY()) return false;
			
			return true;
		}}

	public void setPlotArea(BasicPlot plotLayers) {
		plotArea=plotLayers;
		
	}
	
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		super.handleMove(handlenum, p1, p2);
		if (handlenum<1000) plotArea.onPlotUpdate();
	}
	
	@Override
	public void scaleAbout(Point2D p, double mag) {
		super.scaleAbout(p, mag);
		plotArea.onPlotUpdate();
	}
	
	/**Generates a menu containing options related to plots*/
	public PopupMenuSupplier getMenuSupplier(){
		JPopupMenu output = new  AttachedItemMenu(this, this.getLockedItems()).getJPopup();
		
		GraphicLayer par = this.getParentLayer();
		if (par instanceof HasUniquePopupMenu) {
			JPopupMenu jp = ((HasUniquePopupMenu) par).getMenuSupplier().getJPopup();
			if (jp instanceof SmartPopupJMenu) {
				JMenu menuadded = ((SmartPopupJMenu) jp).extractToMenu("Plot");
				output.add(menuadded, 0);
				
			}
		}
		
		return new PopupMenuSupplier() {

			@Override
			public JPopupMenu getJPopup() {
				
				return output;
			}

			};
	}
	
	/**overrides the superclass method so that any layout handles that are
	  not needed for plots will not be added */
	@Override
	protected void addAdditionalHandles(SmartHandleList box) {}

}
