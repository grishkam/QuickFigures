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
 * Version: 2022.1
 */
package dataTableDialogs;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.CustomIndexedColorMap;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import fileread.ReadExcelData;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

/**an implementation of the table reader interface for excel files
 * @see TableReader*/
public class ExcelTableReader implements TableReader {

	private  org.apache.poi.ss.usermodel.Sheet sheet;
	private Workbook workbook;
	private String fileLocation;
	private HashMap<Color, XSSFCellStyle> cellStyles;
	private ArrayList<XSSFCellStyle> used=new ArrayList<XSSFCellStyle>();

	public ExcelTableReader() throws IOException {
		workbook= WorkbookFactory.create(true);
		workbook.createSheet();
		sheet=workbook.getSheetAt(0);
	}
	
	public ExcelTableReader(File fileLocation) {
		try {
			if(fileLocation==null)
				IssueLog.log("no file provided");
				 InputStream inp = new FileInputStream(fileLocation.getAbsolutePath());
				 
				    Workbook wb2;
					
						wb2 = WorkbookFactory.create(inp);
				
				sheet=wb2.getSheetAt(0);
				if(sheet==null)
					IssueLog.log("no spreadsheet found");
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
		Row row2 = sheet.getRow(row);
		if(row2==null)
			return null;
		Cell cell = row2.getCell(col);
		if(cell!=null) return ReadExcelData.getObjectInCell(cell);
		return cell;
	}

	@Override
	public int getRowCount() {
		if(sheet==null)
		{
			IssueLog.log("null sheet issue");
			workbook.getSheetAt(0);
			}
		return sheet.getLastRowNum();
	}

	@Override
	public void setValueAt(Object value, int rowNumber, int colNumber) {
		Cell cell = findCellAt(rowNumber, colNumber);
		
		if(value!=null&&value.getClass()==Integer.class)
			cell.setCellValue((Integer) value);
		cell.setCellValue(value+"");
	}

	/**
	 * @param rowNumber
	 * @param colNumber
	 * @return
	 */
	public Cell findCellAt(int rowNumber, int colNumber) {
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
		return cell;
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
		
		return count;
	}

	/***/
	@Override
	public void setWrapTextAt(int i, int j) {
		Cell cellAt = this.findCellAt(i, j);
		CellStyle createCellStyle = findCellStyle(cellAt);
		
		createCellStyle.setWrapText(true);
		
		cellAt.setCellStyle(createCellStyle);
		
	}

	/**
	 * @param cellAt
	 * @return
	 */
	public CellStyle findCellStyle(Cell cellAt) {
		CellStyle createCellStyle=null;
		if(cellAt.getCellStyle()!=null)
			createCellStyle =cellAt.getCellStyle();
		else
			createCellStyle = workbook.createCellStyle();
		return createCellStyle;
	}

	@Override
	public void setCellColor(Color color, int i, int j) {
		Cell cellAt = this.findCellAt(i, j);
		boolean setup =false;
		if(cellStyles!=null) {
			XSSFCellStyle xssfCellStyle = cellStyles.get(color);
			
			cellAt.setCellStyle(xssfCellStyle);
		}/**
		for(XSSFCellStyle style: cellStyles) {
			byte[] values = style.getFillForegroundColorColor().getRGB();
			byte[]  rgb = new byte[3];
			  rgb[0] = (byte) color.getRed(); // red
			  rgb[1] = (byte) color.getBlue(); // green
			  rgb[2] = (byte) color.getGreen(); // blue
			  
			if (color.getRed()-256==values[0]&& color.getBlue()-256==values[2]&& color.getGreen()-256==values[1]) {
				cellAt.setCellStyle(style);
				setup = true;
				used.add(style);
			}
			else
				if (color.getRed()+256==values[0]&& color.getBlue()+256==values[2]&& color.getGreen()+256==values[1]) {
					cellAt.setCellStyle(style);
					setup = true;
					used.add(style);
				}
			XSSFColor c2 = this.convertColor(color);
			if(c2.getRGB().equals(values))
				cellAt.setCellStyle(style);
			if(values.equals(rgb))
				cellAt.setCellStyle(style);
			
		}
		if(!setup) {
			for(XSSFCellStyle style: cellStyles) {
				byte[] rgb = style.getFillForegroundColorColor().getRGB();
				if(!used.contains(style))
				IssueLog.log("Checking  "+color+" in "+ style+"versus "+rgb[0]+", "+rgb[1]+","+rgb[2]);
			}
		}
		*/
	}

	public void setupColorMap(ArrayList<Color> c) {
		 cellStyles = new HashMap<Color, XSSFCellStyle>();
		// c.clear();
		// c.add(new Color(128,255,159));
		 for(Color color1: c) {
			  XSSFCellStyle cellStyle;
			  XSSFColor color;
	
			  //Your custom color #800080
			  //create cell style on workbook level
			  cellStyle = (XSSFCellStyle) workbook.createCellStyle();
			  cellStyle.setWrapText(true);
			  //set pattern fill settings
			  cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			  
			  //create the RGB byte array
			  color = convertColor(color1);
			 
			  //set fill color to cell style
			  cellStyle.setFillForegroundColor(color);
	
			  cellStyles.put(color1,cellStyle);
		  }
		
	}

	/**
	 * @param color1
	 * @return
	 */
	public XSSFColor convertColor(Color color1) {
		byte[] rgb;
		XSSFColor color;
		rgb = new byte[3];
		  rgb[0] = (byte) color1.getRed(); // red
		  rgb[2] = (byte) color1.getBlue(); // blue
		  rgb[1] = (byte) color1.getGreen(); // green
		  //create XSSFColor
		  color = new XSSFColor(rgb, new DefaultIndexedColorMap());
		return color;
	}
	
}
