package fileread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import applicationAdapters.DisplayedImage;
import dataSeries.ColumnDataSeries;
import dataSeries.XYDataSeries;
import plotCreation.XYPlotCreator;

public class ExcelFileToXYPlot extends ExcelDataImport{

	private XYPlotCreator creator;


	public ExcelFileToXYPlot(int t) {
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
			
			ExcelRowSubset subset = new ExcelRowSubset(ReadExcelData.fileToWorkBook(f.getAbsolutePath()));
			
			
			ArrayList<XYDataSeries> items = subset.createXYDataSeries(0);//ReadExcelData.readXY(f.getAbsolutePath());
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
