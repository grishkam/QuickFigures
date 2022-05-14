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
 * Version: 2022.1
 */
package fileread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import applicationAdapters.DisplayedImage;
import dataSeries.ColumnDataSeries;
import logging.IssueLog;
import plotCreation.ColumnPlotCreator;

/**implements a menu item to build a barplot from an excel file*/
public class ExcelFileToBarPlot extends  ExcelDataImport{

	
	
	private ColumnPlotCreator creator;


	public ExcelFileToBarPlot(ColumnPlotCreator.ColumnPlotStyle plotType) {
		creator=new ColumnPlotCreator(plotType);
	}
	
	
	@Override
	public String getMenuPath() {
		
		return "Plots<Column Plots<Create From Excel File";
	}

	

	
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		try {
		
			File f=getFileAndaddExtension();
			if (f==null) return;
			
			ColumnDataSeries[] items = ReadExcelData.read(f.getAbsolutePath());
			String name=f.getName();
			ArrayList<ColumnDataSeries> items2 = new ArrayList<ColumnDataSeries>();
			for(ColumnDataSeries i: items)items2.add(i);
			
			createPlot(name, items2, diw);
		
		} catch (InvalidFormatException | IOException e) {
			IssueLog.logT(e);
		}
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
