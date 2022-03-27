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
 * Date Modified: Mar 26, 2021
 * Version: 2022.0
 */
package dataTableDialogs;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import fileread.ReadExcelData;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

/**an implementation of the table reader interface for excel files
 * @see TableReader*/
public class ExcelTableReader implements TableReader {

	private  org.apache.poi.ss.usermodel.Sheet sheet;
	private Workbook workbook;
	private String fileLocation;

	public ExcelTableReader() throws IOException {
		workbook= WorkbookFactory.create(true);
		workbook.createSheet();
		sheet=workbook.getSheetAt(0);
	}
	
	public ExcelTableReader(File fileLocation) {
		try {
				 InputStream inp = new FileInputStream(fileLocation.getAbsolutePath());
				    Workbook wb2;
					
						wb2 = WorkbookFactory.create(inp);
				
				sheet=wb2.getSheetAt(0);
				this.workbook=wb2;
				this.fileLocation=fileLocation.getAbsolutePath();
				inp.close();
			} catch (Exception e) {
				IssueLog.logT(e);
				
			}
	}
	
	public ExcelTableReader(Workbook wb2, Sheet wb, String fileLocation) {
		sheet=wb;
		this.workbook=wb2;
		this.fileLocation=fileLocation;
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		Cell cell = sheet.getRow(row).getCell(col);
		if(cell!=null) return ReadExcelData.getObjectInCell(cell);
		return cell;
	}

	@Override
	public int getRowCount() {
		return sheet.getLastRowNum();
	}

	@Override
	public void setValueAt(Object value, int rowNumber, int colNumber) {
		Row row = sheet.getRow(rowNumber);
		
		if(row==null) {
			sheet.createRow(rowNumber);
			 row = sheet.getRow(rowNumber);
		}
		
		Cell cell = row.getCell(colNumber);
		if(cell==null) {
			row.createCell(colNumber);
			cell = row.getCell(colNumber);
		}
		
		if(value!=null&&value.getClass()==Integer.class)
			cell.setCellValue((Integer) value);
		cell.setCellValue(value+"");
	}

	@Override
	public void saveTable(boolean b, String outputFileName) {
		File fileAddress = null;
		
			
			
		if(outputFileName==null)
			 fileAddress = FileChoiceUtil.getSaveFile();
		else  {
			if(!outputFileName.endsWith("xlsx")) {
				outputFileName+=".xlsx";
			}
			fileAddress = new File(outputFileName);
		}
		
		
		

		try {
		    OutputStream fileOut = new FileOutputStream(fileAddress.getAbsolutePath());
		    workbook.write(fileOut);
		    if(b) {
		    	Desktop.getDesktop().open(fileAddress);
		    }
		    fileOut.close();
		}

		catch(Exception e) {
		    IssueLog.log(e);
		}
	}

	@Override
	public TableReader createNewSheet(String name) {
		return new  ExcelTableReader(workbook, workbook.createSheet(name), fileLocation);
	}

	@Override
	public String getOriginalSaveAddress() {
		return fileLocation;
	}

	@Override
	public int getColumnCount() {
		int count=1;
		for(int i=0; i<this.getRowCount(); i++) {
			Row row = sheet.getRow(i);
			if(row==null) {break;}
			if(row.getLastCellNum()>count)
				count=row.getLastCellNum();
		}
		IssueLog.log("Column count "+count+ "   for    "+this.getOriginalSaveAddress());
		return count;
	}

}
