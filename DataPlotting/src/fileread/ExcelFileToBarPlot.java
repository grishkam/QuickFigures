package fileread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import applicationAdapters.DisplayedImageWrapper;
import dataSeries.ColumnDataSeries;
import plotCreation.columnPlotCreator;

public class ExcelFileToBarPlot extends  ExcelDataImport{

	
	
	private columnPlotCreator creator;




	public ExcelFileToBarPlot(int t) {
		creator=new columnPlotCreator(t);
	}
	
	
	@Override
	public String getMenuPath() {
		
		return "Plots<Column Plots<Create From Excel File";
	}

	

	
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
		try {
		
			File f=getFileAndaddExtension();
			if (f==null) return;
			
			ColumnDataSeries[] items = ReadExcelData.read(f.getAbsolutePath());
			String name=f.getName();
			ArrayList<ColumnDataSeries> items2 = new ArrayList<ColumnDataSeries>();
			for(ColumnDataSeries i: items)items2.add(i);
			
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


	public void createPlot(String name, ArrayList<ColumnDataSeries> items, DisplayedImageWrapper diw) {
		creator.createPlot(name, items, diw);
		
	}


	public String getNameText() {
		return creator.getNameText();
	}

}
