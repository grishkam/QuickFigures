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
import org.apache.poi.ss.usermodel.Workbook;

import applicationAdapters.DisplayedImage;
import dataSeries.ColumnDataSeries;
import dataSeries.XYDataSeries;
import dataTableActions.MakePlotFromTable;
import dataTableDialogs.ExcelTableReader;
import plotCreation.XYPlotCreator;

public class ExcelFileToXYPlot extends ExcelDataImport{

	private XYPlotCreator creator;

	public ExcelFileToXYPlot() {
		this(XYPlotCreator.xyPlotForm.DefaultForm);
	}

	public ExcelFileToXYPlot(XYPlotCreator.xyPlotForm t) {
		creator=new XYPlotCreator(t);
	}
	@Override
	public String getMenuPath() {
		return "Plots<XY Plots<Create From Excel File";
	}



	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		try {
			
			
			File f=getFileAndaddExtension();
			if (f==null) return;
			
			Workbook fileToWorkBook = ReadExcelData.fileToWorkBook(f.getAbsolutePath());
			ArrayList<String> headers = ExcelTableReader.getAllColumnHeaders(fileToWorkBook.getSheetAt(0).getRow(0));
			ExcelRowSubset subset = new ExcelRowSubset(fileToWorkBook);
			
			
			ArrayList<XYDataSeries> items=null;
			if(headers.size()==3)
				items = subset.createXYDataSeries(0);//ReadExcelData.readXY(f.getAbsolutePath());
			else {
				MakePlotFromTable pm = new MakePlotFromTable(f, new String[] {"name","x", "y"});
				pm.showSelectionDialog();
				
				items = subset.createXYDataSeries(pm.columnsChosen.getIndex("x"), pm.columnsChosen.getIndex("y"), pm.columnsChosen.getIndex("name"));
			
			
			}
			
			String name=f.getName().split("\\.")[0];
			
			createPlot(name, items, diw);
		
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public ArrayList<XYDataSeries> readExcelDataXY() {
		
		File f=getFileAndaddExtension();
		if (f==null) return null;
		ExcelRowSubset subset;
		try {
			subset = new ExcelRowSubset(ReadExcelData.fileToWorkBook(f.getAbsolutePath()));
			return 	subset.createXYDataSeries(0);//ReadExcelData.readXY(f.getAbsolutePath());
			
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
		
		
	}
	
	public void createPlot(String name, ArrayList<XYDataSeries> items, DisplayedImage diw) {
		creator.createPlot(name, items, diw);
		
	}


	public String getNameText() {
		return creator.getNameText();
	}

}
