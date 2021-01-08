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
package fileread;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import dataTableDialogs.DataTable;
import dataTableDialogs.SmartDataInputDialog;
import logging.IssueLog;

/**A menu item that adds an example plot to the worksheet*/
public class ExampleShower extends BasicMenuItemForObj {

	/**
	 * 
	 */
	private static final int COLUMN_PLOT_TYPE = 3, DEFAULT_PLOT_TYPE_COLS = 0;
	/**
	 * 
	 */
	private static final int KAPLAN_MEIER_PLOT_TYPE = 4;
	/**
	 * 
	 */
	private static final int GROUP_PLOT_TYPE = 2;
	/**
	 * 
	 */
	private static final int XY_PLOT_TYPE = 1;
	int type;
	boolean plotToo=false;
	static final String[] exampleNames=new String[] {"exampleCols.xlsx", "ExampleXY.xlsx", "ExampleGrouped.xlsx",
			"exampleCols.xlsx", "Kaplan Example.xlsx"};
	
	public ExampleShower(int t, boolean plot) {
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
		if (type==XY_PLOT_TYPE) return "Plots<XY Plots";
		if (type==GROUP_PLOT_TYPE) return "Plots<Grouped Plots";
		if (type==COLUMN_PLOT_TYPE) return "Plots<Column Plots";
		if (type==DEFAULT_PLOT_TYPE_COLS) return "Plots<Column Plots";
		if (type==KAPLAN_MEIER_PLOT_TYPE) return "Plots<Kaplan Plots";
		return "Plots";
	}
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		String s = exampleNames[type];
		InputStream inp = this.getClass().getClassLoader().getResourceAsStream(s);
		  try {
			Workbook wb = WorkbookFactory.create(inp);
			DataTable table = ExcelRowToJTable.DataTableFromWorkBookSheet(wb.getSheetAt(0));
			int form = type+1;
			
			SmartDataInputDialog dialog = new SmartDataInputDialog(table, form);
			
					dialog.showDialog();;
					if (dialog.getPlotMakerMenu()!=null &&plotToo) {
						Component menuitem1 = dialog.getPlotMakerMenu().getMenuComponent(0);
						if (menuitem1 instanceof ActionListener) {
							((ActionListener) menuitem1).actionPerformed(null);
							dialog.setVisible(false);
						}
					}
			
		} catch (InvalidFormatException | IOException e) {
			IssueLog.logT(e);
		}
	}

	
	public static void main(String[] args) {
		new ExampleShower(COLUMN_PLOT_TYPE , false).performActionDisplayedImageWrapper(null);;
	}
}
