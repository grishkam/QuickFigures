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

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import applicationAdapters.DisplayedImage;
import dataTableDialogs.DataTable;
import dataTableDialogs.SmartDataInputDialog;
import exportMenus.QuickImport;
import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;
import undo.CombinedEdit;

/**creates a menu item to open an excel file in a window with options to create a plot*/
public class ExcelRowToJTable extends QuickImport implements genericTools.NormalToolDragHandler.FileDropListener {

	

	int type=-1;
	
	@Override
	public String getMenuPath() {
		return "Plots";
	}

	@Override
	protected String getExtension() {
		return "xlsx";
	}

	@Override
	protected String getExtensionName() {
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
			
			showFile(diw, f);
		
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**shows a data table for teh given excel file
	 * @param diw
	 * @param f
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public void showFile(DisplayedImage diw, File f) throws InvalidFormatException, IOException {
		Workbook wb = ReadExcelData.fileToWorkBook(f.getAbsolutePath());
		Sheet sheet = wb.getSheetAt(0);
		DataTable table = DataTableFromWorkBookSheet(sheet);
		
		SmartDataInputDialog ss = new SmartDataInputDialog(table, null);
		ss.getDataTable().shiftToTopLeft();
		ss.showDialog();;
		diw.updateDisplay();diw.updateDisplay();
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
				CellType typeCell = cell.getCellType();
				Object value=null;
				if (typeCell==CellType.NUMERIC) value=cell.getNumericCellValue();
				else
					if (typeCell==CellType.FORMULA) {
						String st="does not handle formulas";
						IssueLog.log(st);
					}
					else 
				if (typeCell==CellType.STRING/**1*/) value=cell.getStringCellValue();
				else 
				if (typeCell==CellType.BOOLEAN/**4*/) value=cell.getBooleanCellValue();
					else{
					IssueLog.log("Table unprepared for value tyepe"+typeCell);
				}
				if (cell.getColumnIndex()>nCol) continue;
				table.setValueAt(value, rNum, cell.getColumnIndex());
			} catch (Throwable t) {t.printStackTrace();}
		}
		return table;
	}

	@Override
	public boolean canTarget(ArrayList<File> file) {
		for(File f: file) {
			if(f.getAbsolutePath().toLowerCase().endsWith(".xls"))
				return true;
			if(f.getAbsolutePath().toLowerCase().endsWith(".xlsx"))
				return true;
		}
		return false;
	}

	@Override
	public CombinedEdit handleFileListDrop(ImageWindowAndDisplaySet imageAndDisplaySet, Point location,
			ArrayList<File> file) {
		for(File f: file) try {
			showFile(imageAndDisplaySet, f);
		} catch (Throwable t) {
			IssueLog.log(t);
		}
		return null;
	}
	

}