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
import dataSeries.ColumnDataSeries;
import dataSeries.KaplenMeierDataSeries;
import plotCreation.KaplanMeierPlotCreator;

public class ExcelFileToKaplanPlot extends  ExcelDataImport{

	
	
	private KaplanMeierPlotCreator creator;




	public ExcelFileToKaplanPlot(int t) {
		creator=new KaplanMeierPlotCreator(t);
	}
	
	
	@Override
	public String getMenuPath() {
		
		return "Plots<Kaplan Plots<Create From Excel File";
	}

	

	
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		try {
		
			File f=getFileAndaddExtension();
			if (f==null) return;
			
			ArrayList<KaplenMeierDataSeries> items = ReadExcelData.readKaplan(f.getAbsolutePath());
			String name=f.getName();
			ArrayList<KaplenMeierDataSeries> items2 = new ArrayList<KaplenMeierDataSeries>();
			for(KaplenMeierDataSeries i: items)items2.add(i);
			
			createPlot(name, items2, diw);
		
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


	public void createPlot(String name, ArrayList<KaplenMeierDataSeries> items, DisplayedImage diw) {
		creator.createPlot(name, items, diw);
		
	}


	public String getNameText() {
		return creator.getNameText();
	}

}
