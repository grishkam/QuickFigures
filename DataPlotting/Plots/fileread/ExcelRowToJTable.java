/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import applicationAdapters.DisplayedImage;
import dataTableDialogs.DataTable;
import dataTableDialogs.SmartDataInputDialog;
import exportMenus.QuickImport;
import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;

public class ExcelRowToJTable extends QuickImport  {

	

	int type=-1;
	
	@Override
	public String getMenuPath() {
		return "Plots";
	}

	@Override
	protected String getExtension() {
		// TODO Auto-generated method stub
		return "xlsx";
	}

	@Override
	protected String getExtensionName() {
		// TODO Auto-generated method stub
		return "excel files";
	}

	@Override
	public String getNameText() {
		return "View Excel File Data";
	}
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		try {
			if (diw==null|| (diw.getWindow().isVisible()==false)) {
				diw=ImageWindowAndDisplaySet.createAndShowNew("Figure", 300,300);
			}
			
			File f=getFileAndaddExtension();
			if (f==null) return;
			
			Workbook wb = ReadExcelData.fileToWorkBook(f.getAbsolutePath());
			Sheet sheet = wb.getSheetAt(0);
			DataTable table = DataTableFromWorkBookSheet(sheet);
			
			SmartDataInputDialog ss = new SmartDataInputDialog(table, 0);
			ss.getDataTable().shiftToTopLeft();
			ss.showDialog();;
			diw.updateDisplay();diw.updateDisplay();
		
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static DataTable DataTableFromWorkBookSheet(Sheet sheet) {
		int nCol=6;
		int nRow=100;
		if (sheet.getPhysicalNumberOfRows()>nRow) nRow=sheet.getPhysicalNumberOfRows();
		for(Row row: sheet) {
			for(Cell cell: row) {
				if (cell.getColumnIndex()>nCol) nCol=cell.getColumnIndex();
			}}
		
		
		DataTable table = new DataTable(nRow, nCol);
		
		
		
		
		for(Row row: sheet) {
			int rNum = row.getRowNum();
			for(Cell cell: row) try {
				int typeCell = cell.getCellType();
				Object value=null;
				if (typeCell==0) value=cell.getNumericCellValue();
				else
					if (typeCell==2) {
						String st="does not handle formulas";
						IssueLog.log(st);
					}
					else 
				if (typeCell==1) value=cell.getStringCellValue();
				else 
				if (typeCell==4) value=cell.getBooleanCellValue();
					else{
					IssueLog.log("Table unprepared for value tyepe"+typeCell);
				}
				if (cell.getColumnIndex()>nCol) continue;
				table.setValueAt(value, rNum, cell.getColumnIndex());
			} catch (Throwable t) {t.printStackTrace();}
		}
		return table;
	}
	

}