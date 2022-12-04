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
package fileread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import applicationAdapters.DisplayedImage;
import channelMerging.ChannelEntry;
import dataSeries.ColumnDataSeries;
import dataSeries.GroupedDataSeries;
import dataTableActions.MakePlotFromTable;
import logging.IssueLog;
import plotCreation.GroupedPlotCreator;

public class ExcelFileToComplexCategoryPlot extends  ExcelDataImport {


	private GroupedPlotCreator creator;
	
	public ExcelFileToComplexCategoryPlot(int t) {
		creator=new GroupedPlotCreator(t);
	}
	
	
	@Override
	public String getMenuPath() {
		return "Plots<Grouped Plots<Create From Excel File";
	}


	
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		try {
			
			
			File f=getFileAndaddExtension();
			if (f==null) return;
			
			createPlotFromFileExcelFile(diw, f);
		
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void createPlotFromFileExcelFile(DisplayedImage diw, File f)
			throws InvalidFormatException, IOException { 
		MakePlotFromTable pm = new MakePlotFromTable(f, new String[] {"Subset Column","Category Column", "Values"});
		pm.showSelectionDialog();
		
		createPlotFromFileExcelFile(diw, f, pm.columnsChosen.getIndex("Subset Column"),  pm.columnsChosen.getIndex("Category Column"), pm.columnsChosen.getIndex("Values"));
	}

	public void createPlotFromFileExcelFile(DisplayedImage diw, File f, int subsetcol, int groupCol, int numberCol)
			throws InvalidFormatException, IOException {
		ExcelRowSubset subset = new ExcelRowSubset(ReadExcelData.fileToWorkBook(f.getAbsolutePath()));
		
		ArrayList<GroupedDataSeries> items = subset.createCategoryDataSeries(subsetcol, groupCol, numberCol);//ReadExcelData.readXY(f.getAbsolutePath());
		IssueLog.log("creating file from "+items.size());
		String name=f.getName().split("\\.")[0];
		
		
		createPlot(name, items, diw);
	}


	
	public ColumnDataSeries[]  getDataFromFile() {
		File f=getFileAndaddExtension();
		ColumnDataSeries[] items=null;
		try {
			items = ReadExcelData.read(f.getAbsolutePath());
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return items;
	}

	public void createPlot(String name, ArrayList<GroupedDataSeries> items, DisplayedImage diw) {
		creator.createPlot(name, items, diw);
	}

	public String getNameText() {
		return creator.getNameText();
	}

}
