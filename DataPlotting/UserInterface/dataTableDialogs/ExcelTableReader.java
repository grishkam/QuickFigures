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
 * Date Modified: May 9, 2023
 * Version: 2023.2
 */
package dataTableDialogs;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.CustomIndexedColorMap;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import fileread.ReadExcelData;
import logging.IssueLog;
import messages.ShowMessage;
import textObjectProperties.TextLine;
import textObjectProperties.TextLineSegment;
import ultilInputOutput.FileChoiceUtil;

/**an implementation of the table reader interface for excel files
 * @see TableReader*/
public class ExcelTableReader implements TableReader {

	private  org.apache.poi.ss.usermodel.Sheet sheet;
	private Workbook workbook;
	private String fileLocation;
	private HashMap<String, XSSFCellStyle> cellStyles;//a list of already used cell styles
	private ArrayList<XSSFCellStyle> used=new ArrayList<XSSFCellStyle>();
	private FormulaEvaluator createFormulaEvaluator;

	public ExcelTableReader() throws IOException {
		workbook= WorkbookFactory.create(true);
		workbook.createSheet();
		createFormulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
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
				this.createFormulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
				this.fileLocation=fileLocation.getAbsolutePath();
				inp.close();
			} catch (Exception e) {
				IssueLog.logT(e);
				
			}
	}
	
	public ExcelTableReader(Workbook wb2, Sheet wb, String fileLocation) {
		sheet=wb;
		this.workbook=wb2;
		createFormulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
		this.fileLocation=fileLocation;
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		Row row2 = sheet.getRow(row);
		if(row2==null)
			return null;
		Cell cell = row2.getCell(col);
		if(cell!=null) {
			
			return ReadExcelData.getObjectInCell(cell, createFormulaEvaluator);
		}
		return cell;
	}
	
	@Override
	public String getStringValueAt(int row, int col) {
		Object out = this.getValueAt(row, col);
		if(out instanceof String)
			return (String) out;
		return out+"";
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
		
		/**Sets a formula. this tends not to work */
		if(value!=null && value.getClass()==String.class&& (""+value).startsWith("=")) {
			
			cell.setCellFormula((value+"").replace("=", ""));
		
			} else {
		
				if(value!=null&&value.getClass()==Integer.class)
					cell.setCellValue((Integer) value);
				else
					cell.setCellValue(value+"");
		
			}
		
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
		Sheet createSheet=null;
		if(workbook.getSheet(name)!=null) {
			//IssueLog.log("Workbook already has sheet: "+name);
			//IssueLog.log("Old sheet will be deleted: "+name);
			
			createSheet = workbook.getSheet(name);
			
			boolean yn =true;// ShowMessage.yesOrNo("workbook already has a sheet named "+name+". Do you wish to create a new one");
			if(yn) {
				
				int index=workbook.getSheetIndex(name);
				workbook.removeSheetAt(index);
				createSheet = workbook.createSheet(name);
			}
		}
		else 
			createSheet = workbook.createSheet(name);
		return new  ExcelTableReader(workbook, createSheet, fileLocation);
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
	
	@Override
	public ArrayList<String> getColumnHeaders() {
		Row row = sheet.getRow(0);
		ArrayList<String> ss = getAllColumnHeaders(row);
		 
		 return ss;
	}

	/**returns the column headers
	 * @param row
	 * @return
	 */
	public  ArrayList<String> getAllColumnHeaders(Row row) {
		short count1 = row.getLastCellNum();
		 ArrayList<String> ss=new ArrayList<String>();
		 for(int i=0; i<count1; i++) {
			 Object valueAt = null;//this.getValueAt(0, i);
			 Cell cell = row.getCell(i);
				if(cell!=null)valueAt =ReadExcelData.getObjectInCell(cell, workbook.getCreationHelper().createFormulaEvaluator());
			 if(valueAt==null)
				 ss.add(i, "null");
			 else  ss.add(i, valueAt+"");
		 }
		return ss;
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
		//boolean setup =false;
		if(cellStyles!=null) {
			XSSFCellStyle xssfCellStyle = cellStyles.get(color.toString());
			
			cellAt.setCellStyle(xssfCellStyle);
		}
	}
	
	@Override
	public void setCellBorder(int border, int i, int j) {
		Cell cellAt = this.findCellAt(i, j);
		
		//boolean setup =false;
		if(cellStyles!=null) {
			XSSFCellStyle xssfCellStyle = cellStyles.get("border"+border);
			
			cellAt.setCellStyle(xssfCellStyle);
		}
	}
	


	/**Craeates cell styles for each color listed*/
	public void setupColorMap(ArrayList<Color> c) {
		 cellStyles = new HashMap<String, XSSFCellStyle>();
		// c.clear();
		// c.add(new Color(128,255,159));
		 for(Color color1: c) {
			  XSSFCellStyle cellStyle = createCenterAlignedStyle();
			  XSSFColor color;
			  //create the RGB byte array
			  color = convertColor(color1);
			 
			  //set fill color to cell style
			  cellStyle.setFillForegroundColor(color);
			  cellStyle.setShrinkToFit(true);
			  
			  cellStyles.put(color1.toString(),cellStyle);
		  }
		 
		  XSSFCellStyle cellStyle = createBorderCellStyle();
		 cellStyles.put("border0", cellStyle);
		
		 cellStyle = createBorderCellStyle();
		 cellStyle.setAlignment(HorizontalAlignment.CENTER);
		 cellStyles.put("border1", cellStyle);
		 
		 cellStyles.put("centered", createCenterAlignedStyle());
		 
		
	}

	/**creates a cell style with a thin border
	 * @return
	 */
	public XSSFCellStyle createBorderCellStyle() {
		XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
		 
		  BorderStyle border=BorderStyle.THIN;
		 
		cellStyle.setBorderTop(border);;
		cellStyle.setBorderBottom(border);;
		cellStyle.setBorderLeft(border);;
		cellStyle.setBorderRight(border);;
		return cellStyle;
	}

	/**returns a cell style
	 * @return
	 */
	public XSSFCellStyle createCenterAlignedStyle() {
		XSSFCellStyle cellStyle;
		  

		  //Your custom color #800080
		  //create cell style on workbook level
		  cellStyle = (XSSFCellStyle) workbook.createCellStyle();
		  cellStyle.setWrapText(true);
		  cellStyle.setAlignment(HorizontalAlignment.CENTER);
		  VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
		cellStyle.setVerticalAlignment(verticalAlignment);
		  //set pattern fill settings
		  cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return cellStyle;
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

	@Override
	public Object getSheetName(int i) {
		return workbook.getSheetAt(0).getSheetName();
		
	}
	
	/**
	 * @return
	 */
	public static ExcelTableReader openExcelFile(File targetFile) {
		if(targetFile==null)
			return null;
		if(!targetFile.exists())
			return null;
		if(targetFile.getAbsolutePath().endsWith(".xlsx"))
			return new ExcelTableReader(targetFile);
		IssueLog.log("The file is not an excel file", targetFile.getAbsolutePath(), "");
		return null;
	}
	
	
	@Override
	public void setColWidth(int col_index, int width) {
		//int oldWidth = sheet.getColumnWidth(col_index);
		
		sheet.setColumnWidth(col_index, width);
	}
	/**
	 * @param col2
	 * @return
	 */
	public int getColumnWidth(int col2) {
		return sheet.getColumnWidth(col2);
	}
	
	
	
	@Override
	public void setRowHeight(int i, int h) {
		Row row = sheet.getRow(i);
		//short hold = row.getHeight();
		if(row==null)
			return;
		row.setHeight((short) h);
		
	}
	

	@Override
	public void mergeIdenticalCells(boolean b, int[] is) {
		int s=1;
		Row rowS = sheet.getRow(s);
		ArrayList<Cell> chosenRange=new ArrayList<Cell>();
		
		int row=1;
		int rowMax=24;
		int colMax=24;
		
		for(;row<=rowMax; row++) {
			int colStart=1;
			int colEnd=1;
			int i=colStart;
			
			
				for(; i<colMax; i++) {
				Object v2 ;
				Object v1 ;
				if(b) {
					v2 = this.getValueAt(row,colStart);
					v1 = this.getValueAt(row,colEnd+1);
				} else {
					v2 = this.getValueAt(colStart, row);
					v1 = this.getValueAt(colEnd+1, row);
				}
				
				if(v1!=null&&v2!=null&&v1.equals(v2)) {
					colEnd++;
				} else {
					if(colStart!=colEnd) try {
						if(b)
							sheet.addMergedRegion(new CellRangeAddress(row, row, colStart, colEnd));
						else
							sheet.addMergedRegion(new CellRangeAddress(colStart, colEnd, row, row));
					} catch (Throwable t) {
						if(t instanceof java.lang.IllegalStateException) {
							IssueLog.log("Wells cannot be merged");
						}
						else 
							IssueLog.log(t);
					}
					
					
					colStart=colEnd+1;
					colEnd=colStart;
				}
				
				}
				
		}
		
	}

	@Override
	public int getRowHeight(int rowR) {
		Row row = sheet.getRow(rowR);
		if(row==null)
			return 10;
		return row.getHeight();
	}

	
	/**Sets the rich text value of the cell*/
	@Override
	public void setRichText(TextLine line, int ii, int jj) { 
		XSSFRichTextString t = new XSSFRichTextString();
		
		for(int i=0; i< line.size(); i++) {
			TextLineSegment tseg = line.get(i);
				XSSFFont f = new XSSFFont();
				f.setFontName(tseg.getFont().getFamily());
				f.setFontHeightInPoints((short) 12);
				f.setBold(tseg.getFont().isBold());
				f.setColor(convertColor(tseg.getTextColor()));
				t.append(tseg.getText(), f);
		}
		
		Cell cellAt = this.findCellAt(ii, jj);
		cellAt.setCellValue(t);
		
	}

	/**Sets the font to something more eleborate*/
public void setRichText(Color[] colors, String[] text, int ii, int jj) {
	//Font baseFont = new Font("Arial",Font.BOLD, 18 );
	
	XSSFRichTextString t = new XSSFRichTextString();
	for(int i=0; i< colors.length && i<text.length; i++) {
			XSSFFont f = new XSSFFont();
			f.setFontName("Arial");
			f.setFontHeightInPoints((short) 12);
			f.setBold(true);
			f.setColor(convertColor(colors[i]));
			t.append(text[i], f);
	}
	
	Cell cellAt = this.findCellAt(ii, jj);
	cellAt.setCellValue(t);
			//cell.getRichStringCellValue();
}


	
	
}
