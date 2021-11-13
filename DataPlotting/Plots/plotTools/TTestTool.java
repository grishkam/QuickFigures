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
 * Date Modified: April 28, 2021
 * Version: 2021.2
 */
package plotTools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import applicationAdapters.ImageWorkSheet;
import columnPlots.ColumnPlot;
import dataSeries.DataSeries;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import icons.IconWrappingToolIcon;
import plotParts.Core.PlotArea;
import plotParts.stats.ConnectorGraphic;
import plotParts.stats.StatTestOrganizer;
import storedValueDialog.ReflectingFieldSettingDialog;
import undo.UndoAddItem;

/**a tool for t tests on plots, */
public class TTestTool extends BasicPlotTool {
	{
		{super.iconSet=IconWrappingToolIcon.createIconSet(new PlotToolIcon());}
		
};


	StatTestOrganizer testProperties=new  StatTestOrganizer();


	@Override
	protected void createMarker(int pressX, int pressY, int dragX, int dragY, ImageWorkSheet imageClicked) {
		preliminaryPath = testProperties.createLinkingLineForShapes(getPressShape(), getDragShape(), pressX, pressY, dragX, dragY);
		
		GraphicGroup group1 = new GraphicGroup(true, getTTextMarkingGraphic(true, preliminaryPath), generateMarkerForSwitch());
		group1.hideHandles(true);
		imageClicked.getOverlaySelectionManagger().setSelection(group1, 0);
	}


	
	
	/**Called after the mouse is released ober a plot, */
	@Override
	public void afterPlotRelease(ImageWorkSheet imageClicked) {
		if(this.getPressShape()==null||this.getDragShape()==null)
			return;
		PlotArea a1 = getPressShape().getPlotArea();
		PlotArea a2 = getDragShape().getPlotArea();
		
		if (a1!=a2) return;// cannot compare from two different plots
		
		ZoomableGraphic toAdd = getTTextMarkingGraphic(testProperties.useLinkingLine(), preliminaryPath);
			if (toAdd instanceof ShapeGraphic) return;
		
			GraphicLayer layer = getPressShape().getParentLayer().getParentLayer();
			
			layer.add(toAdd);
			
			imageClicked.getUndoManager().addEdit(new UndoAddItem(layer, toAdd));
		
	}

	
	private ZoomableGraphic getTTextMarkingGraphic(boolean linkline, ConnectorGraphic preliminaryPath) {
		ZoomableGraphic toAdd=null;
			ComplexTextGraphic text = getTTestResult();
			
					{
				if (text==null) return preliminaryPath;
				
				
					StatTestOrganizer pane = this.testProperties.copy();
					pane.setDataSeries(testProperties.data1, testProperties.data2);
					pane.setAnchorShapes(this.pressShape, this.dragShape);
					pane.setTheText(text);
					
					
					
					
					toAdd=pane;
					if (linkline)
						{
							pane.addItemToLayer(preliminaryPath);
						    pane.setLinker(preliminaryPath);
					    }
					}
			
		return toAdd;
	}

	private ComplexTextGraphic getTTestResult() {
		if (isColumnPlot() &&pressShape==dragShape ) return null;
		
		DataSeries d1;
		
		DataSeries d2; 
		if (isColumnPlot()) {
			d1= getPressShape().getTheData();
			d2= getDragShape().getTheData();
		} else {
			if (dataSeriesPressed==null||dataSeriesDragged==null) return null;
			d1 = dataSeriesPressed;
			d2 = dataSeriesDragged;
		}
		
		if ( dataSeriesPressed==dataSeriesDragged) return null;
		
		testProperties.setDataSeries(d1, d2);
		
		ComplexTextGraphic text = testProperties.createTextForTest(d1, d2, super.preliminaryPath,  this.getDragShape());
		
		return text;
	}

	private boolean isColumnPlot() {
		return pressShape.getPlotArea() instanceof ColumnPlot;
	}

	

	


	
	public String getToolName() {
		return "Perform T-Test (Drag from one column to another)";
	}
	

	
	public class PlotToolIcon extends PlotIcon {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String name="**";
		public PlotToolIcon() {
			super(new Color[] {Color.gray, Color.black, Color.darkGray}, new int[] {10, 15, 20});
		}
		

		@Override
		public void paintLayer2Icon(Component arg0, Graphics g, int arg2, int arg3) {
			TextGraphic.setAntialiasedText(g, true);
			//new PlotIcon().paintIcon(arg0, g, arg2, arg3);
			
			g.setFont(new Font("Arial",Font.BOLD, 18 ));
			g.setColor(Color.black);
			g.drawString(name, arg2+5, arg3+14);
				}}

	
	@Override
	public void showOptionsDialog() {
		new ReflectingFieldSettingDialog(this.testProperties, "markType", "linkType", "tTestType", "numberTails").showDialog();
	}
}
