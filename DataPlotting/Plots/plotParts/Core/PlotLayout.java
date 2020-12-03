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
package plotParts.Core;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import genericMontageKit.BasicObjectListHandler.LocatedObjectFilter;
import genericPlot.BasicPlot;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import gridLayout.LayoutSpaces;
import menuUtil.SmartPopupJMenu;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import popupMenusForComplexObjects.LockedItemMenu;
import utilityClassesForObjects.LocatedObject2D;

public class PlotLayout extends MontageLayoutGraphic implements LayoutSpaces{

	private BasicPlot plotArea;

	public PlotLayout(BasicMontageLayout basicMontageLayout) {
		super(basicMontageLayout);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	{this.setName("Plot Layout");}
	
	public void reDivideToCols(int nCol) {
		Rectangle plotArea = getPanelLayout().getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds();
	
		int h = plotArea.height;
		int forEachSection= (int)( plotArea.getWidth()/nCol);
		int w =forEachSection-1;
		
		
		getPanelLayout().setPanelSizes(w, h);
		getPanelLayout().setNRows(1);
		getPanelLayout().setNColumns(nCol);
		getPanelLayout().setHorizontalBorder(1);
		getPanelLayout().setVerticalBorder(0);
		
	}
	
	
	public GenericMontageEditor getEditor() {
		if (editor==null){
			editor= new GenericMontageEditor ();
			editor.setQualificationsForPanelObject(new careFullPanelItentifier());
		}
		
		return editor;
	}
	
	class careFullPanelItentifier implements LocatedObjectFilter {

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
	
	public PopupMenuSupplier getMenuSupplier(){
		JPopupMenu output = new  LockedItemMenu(this, this.getLockedItems()).getJPopup();
		
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
				// TODO Auto-generated method stub
				return output;
			}

			};
	}
	
	protected void addAdditionalHandles(SmartHandleList box) {}

}
