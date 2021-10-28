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
 * Version: 2021.2
 */
package fileread;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import dataTableDialogs.DataTable;
import dataTableDialogs.SmartDataInputDialog;
import genericPlot.BasicPlot;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.ZoomableGraphic;
import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;
import plotCreation.PlotCreator;

/**A menu item that adds an example plot to the worksheet
 * with fictional data*/
public class PlotExampleShower extends BasicMenuItemForObj {

	/**
	 * 
	 */
	
	
	
	PlotType type=PlotType.COLUMN_PLOT_TYPE;
	boolean plotToo=false;
	
	
	public PlotExampleShower(PlotType t, boolean plot) {
		this.type=t;
		plotToo=plot;
	}
	

	@Override
	public String getNameText() {
		if(plotToo) return "Show Example Plot";
		return "Show Example Data";
	}

	@Override
	public String getMenuPath() {
		if (type==PlotType.XY_PLOT_TYPE) return "Plots<XY Plots";
		if (type==PlotType.GROUP_PLOT_TYPE) return "Plots<Grouped Plots";
		if (type==PlotType.COLUMN_PLOT_TYPE) return "Plots<Column Plots";
		if (type==PlotType.DEFAULT_PLOT_TYPE_COLS) return "Plots<Column Plots";
		if (type==PlotType.KAPLAN_MEIER_PLOT_TYPE) return "Plots<Kaplan Plots";
		return "Plots";
	}
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		showExample();
	}


	/**creates an example
	 * @return 
	 * 
	 */
	public DisplayedImage showExample() {
		
		  try {
			SmartDataInputDialog dialog = showDataTable();
					if (dialog.getPlotMakerMenu()!=null &&plotToo) {
						
						Component menuitem1 = dialog.getPlotMakerMenu().getMenuComponent(0);
						if (menuitem1 instanceof ActionListener) {
							((ActionListener) menuitem1).actionPerformed(null);
							dialog.setVisible(false);
						}
					}
			return CurrentFigureSet.getCurrentActiveDisplayGroup();
		} catch ( Exception e) {
			IssueLog.logT(e);
		}
		  return null;
	}


	/**shows a data input dialog
	 * @return
	 * @throws IOException
	 */
	public SmartDataInputDialog showDataTable() throws IOException {
		DataTable table = getExampleData();
		
		
		SmartDataInputDialog dialog = new SmartDataInputDialog(table, type);
		
				dialog.showDialog();;
		return dialog;
	}


	/**returns a data table with the example data for this example shower
	 * @return
	 * @throws IOException
	 */
	public DataTable getExampleData() throws IOException {
		String s = type.getExampleFileName();
		InputStream inp = this.getClass().getClassLoader().getResourceAsStream(s);
		
			Workbook wb = WorkbookFactory.create(inp);
		DataTable table = ExcelRowToJTable.DataTableFromWorkBookSheet(wb.getSheetAt(0));
		return table;
	}
	
	
	public ImageWindowAndDisplaySet showAllPlots() {
		try {
			SmartDataInputDialog table = showDataTable();
			ImageWindowAndDisplaySet output = ImageWindowAndDisplaySet.createAndShowNew("New Workshet", 800,600);
			int count=0;
			
			
			int spacing = 225;
			int limit = 3;
			if(type!=PlotType.COLUMN_PLOT_TYPE&&type!=PlotType.DEFAULT_PLOT_TYPE_COLS) {
				spacing = 350;
				limit=2;
			}
			
			for(PlotCreator<?> pc:table.getListOfPlotCreators()) {
				
				ZoomableGraphic plot = pc.createPlot(pc.getNameText(), table, output).getAddedItem();
				if (plot instanceof BasicPlot) {
					BasicPlot p=(BasicPlot) plot;
					
					p.moveEntirePlot(count*spacing, 0);
					if(count>=limit) {
						
						p.moveEntirePlot(-limit*spacing, 250);
						
					}
					p.getTitleLabel().getParagraph().get(0).get(0).setText(pc.getNameText());
					p.fullPlotUpdate();
					
				}
				count++;
			}
			output.updateDisplay();
			return output;
		} catch (IOException e) {
			IssueLog.log(e);
			
		}
		return null;
	}

	
	public static void main(String[] args) {
		new PlotExampleShower(PlotType.DEFAULT_PLOT_TYPE_COLS , false).showAllPlots();
	}
}
