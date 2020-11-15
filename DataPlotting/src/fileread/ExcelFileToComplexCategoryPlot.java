package fileread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import applicationAdapters.DisplayedImage;
import dataSeries.ColumnDataSeries;
import dataSeries.GroupedDataSeries;
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
		ExcelRowSubset subset = new ExcelRowSubset(ReadExcelData.fileToWorkBook(f.getAbsolutePath()));
		
		ArrayList<GroupedDataSeries> items = subset.createCategoryDataSeries(0, 1, 2);//ReadExcelData.readXY(f.getAbsolutePath());
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
