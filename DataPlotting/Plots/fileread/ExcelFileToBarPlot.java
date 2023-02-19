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
 * Version: 2023.1
 */
package fileread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import applicationAdapters.DisplayedImage;
import dataSeries.ColumnDataSeries;
import dataTableActions.MakePlotFromTable;
import logging.IssueLog;
import plotCreation.ColumnPlotCreator;

/**implements a menu item to build a barplot from an excel file*/
public class ExcelFileToBarPlot extends  ExcelDataImport{

	
	
	private ColumnPlotCreator creator;
	
	/**determines if every column is used or for a separate bar or whether one column is used to split the table*/
	private boolean selectCategory=true;


	public ExcelFileToBarPlot(ColumnPlotCreator.ColumnPlotStyle plotType, boolean category) {
		creator=new ColumnPlotCreator(plotType);
		selectCategory=category;
	}
	
	
	@Override
	public String getMenuPath() {
		if(selectCategory)
			return "Plots<Column Plots<Create From Excel File (data table)";
		return "Plots<Column Plots<Create From Excel File";
	}

	

	
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		try {
		
			File f=getFileAndaddExtension();
			if (f==null) return;
			
			String name=f.getName();
			
			ArrayList<ColumnDataSeries> items2=new ArrayList<ColumnDataSeries>();
			if(selectCategory) {
				MakePlotFromTable pm = new MakePlotFromTable(f, new String[] {"Category Column", "Values"});
				pm.showSelectionDialog();
				ExcelRowSubset subset = new ExcelRowSubset(ReadExcelData.fileToWorkBook(f.getAbsolutePath()));
				ArrayList<ExcelRowSubset> cc = subset.createSubsetsBasedOnColumn(pm.columnsChosen.getIndex("Category Column"));
				int valueCol = pm.columnsChosen.getIndex("Values");
				for(ExcelRowSubset c: cc) {
					items2.add(c.createColumnDataSeries(valueCol));
				}
			
			} else
				items2 = extractEachColumn(f);
			
			createPlot(name, items2, diw);
		
		} catch (InvalidFormatException | IOException e) {
			IssueLog.logT(e);
		}
	}


	/**
	 * @param f
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public ArrayList<ColumnDataSeries> extractEachColumn(File f) throws InvalidFormatException, IOException {
		ColumnDataSeries[] items = ReadExcelData.read(f.getAbsolutePath());
		
		ArrayList<ColumnDataSeries> items2 = new ArrayList<ColumnDataSeries>();
		for(ColumnDataSeries i: items)items2.add(i);
		return items2;
	}

	/**prompts the user to select a file and returns the data series from that file*/
	public ColumnDataSeries[]  getDataFromFile() {
		File f=getFileAndaddExtension();
		if(f==null)
			return null;
		ColumnDataSeries[] items=null;
		try {
			items = ReadExcelData.read(f.getAbsolutePath());
		} catch (InvalidFormatException | IOException e) {
			IssueLog.logT(e);
		}
		return items;
	}


	public void createPlot(String name, ArrayList<ColumnDataSeries> items, DisplayedImage diw) {
		creator.createPlot(name, items, diw);
		
	}


	public String getNameText() {
		return creator.getNameText();
	}

}
