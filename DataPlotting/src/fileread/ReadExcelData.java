package fileread;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import dataSeries.BasicDataPoint;
import dataSeries.ColumnDataSeries;
import dataSeries.KaplenMeierDataSeries;
import dataSeries.XYDataSeries;
import dataTableDialogs.ExcelTableReader;
import dataTableDialogs.SmartDataInputDialog;
import figureTemplates.DirectoryHandler;
import logging.IssueLog;

public class ReadExcelData {


	
	public static void main(String[] args) {
		String path=DirectoryHandler.getDefaultHandler().getFigureFolderPath()+"/example.xlsx";
		try {
			 InputStream inp = new FileInputStream(path);
			    Workbook wb = WorkbookFactory.create(inp);
			    ArrayList<Integer> validColumns=new  ArrayList<Integer>();
				ArrayList<String>  validNames=new  ArrayList<String>();
				Sheet sheet=wb.getSheetAt(0);
				 findValidColumns(sheet, validColumns, validNames);
				 
				 for(int i=0; i<validColumns.size(); i++) {
					 Integer index = validColumns.get(i);
					 System.out.println("Checking Column "+index+" named "+validNames.get(i));
					 System.out.println("Col is Number: "+ReadExcelData.isTypeColumn(sheet, index, 0));
					
					 boolean sCol=ReadExcelData.isTypeColumn(sheet, index, 1);
					 System.out.println("Col is Strings: "+sCol);
					 if (sCol) 
						 {
						 ArrayList<String> allStrings = ReadExcelData.getStringsForColumn(sheet, index, null, 1);
						 System.out.println(allStrings);
						ArrayList<String> unique = UtilForDataReading.getUniqueStrings(allStrings);
						 System.out.println("Unique categories are "+unique);
						 for(String u: unique) {
							ArrayList<Row> rows = ReadExcelData.getRowsWith(sheet, index, u);
							ArrayList<String> strings = ReadExcelData.getStringsForColumn(rows, index, null, 1);
							 System.out.println("Rows with category :  "+strings);
								
						 }
						 
						 }
				 }
				 
				 
			    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**When given a sheet and a col index, returns only the rows with the right string value*/
	public static ArrayList<Row> getRowsWith(Iterable<Row> sheet, int colIndex, String value) {
		ArrayList<Row> rowlist=new ArrayList<Row>();
		for(Row row: sheet) {
			 for(Cell cell: row) {
				 if (cell.getCellType()==1&&cell.getColumnIndex()==colIndex &&cell.getStringCellValue().equals(value))
					 rowlist.add(row);else 
				 if (cell.getCellType()==0&&cell.getColumnIndex()==colIndex &&(cell.getNumericCellValue()+"").equals(value))
					 rowlist.add(row);
			 }
		 }
		
		return rowlist;
	}

	
	/**reads an xlsx file and returns the data*/
public static ColumnDataSeries[] read(String st) throws InvalidFormatException, IOException {
		
	    InputStream inp = new FileInputStream(st);
	    //InputStream inp = new FileInputStream("workbook.xlsx");

	    Workbook wb = WorkbookFactory.create(inp);
	    
	   return extractBasicDataSeriesF(wb);
	}


protected static ColumnDataSeries[] extractBasicDataSeriesF(Workbook wb) {
	Sheet sheet = wb.getSheetAt(0);
	   
	   ArrayList<Integer> validColumns=new  ArrayList<Integer>();
	   ArrayList<String>  validNames=new  ArrayList<String>();
	  
	   
	 findValidColumns(sheet, validColumns, validNames);
		
	 ColumnDataSeries[] data = new  ColumnDataSeries[validColumns.size()];
		for(int i=0; i<validColumns.size(); i++) {
			ArrayList<Double> n = getNumbersForColumn(sheet, validColumns.get(i), null);
			
			data[i]=new ColumnDataSeries(validNames.get(i), n.toArray(new Double[n.size()] ));
		}
		return data;
}

public static Workbook fileToWorkBook(String st) throws InvalidFormatException, IOException {
	 InputStream inp = new FileInputStream(st);
    Workbook wb = WorkbookFactory.create(inp);
    return wb;
}

/**reads an xlsx file and returns the data*/
public static ArrayList<XYDataSeries> readXY(String st) throws InvalidFormatException, IOException {

   return extractXYDataSeriesF(fileToWorkBook(st));
}

/**reads an xlsx file and returns the data*/
public static ArrayList<KaplenMeierDataSeries> readKaplan(String st) throws InvalidFormatException, IOException {

   return extractKaplanDataSeries(fileToWorkBook(st));
}

private static ArrayList<KaplenMeierDataSeries> extractKaplanDataSeries(Workbook wb) {
	Sheet sheet = wb.getSheetAt(0);
	   
	 return  SmartDataInputDialog.getKaplanDataSeriesUsingDefaultClassification(new ExcelTableReader(sheet));
	
}

/**Simply looks */
public static ArrayList<XYDataSeries> extractXYDataSeriesF(Workbook wb) {
	Sheet sheet = wb.getSheetAt(0);
	   
	   ArrayList<Integer> validColumns= new  ArrayList<Integer>();
	   ArrayList<String>  validNames  = new  ArrayList<String>();
	  
	   /**sets up the valid column and row names*/
	 findValidColumns(sheet, validColumns, validNames);
		
	 ArrayList<XYDataSeries> data = new   ArrayList<XYDataSeries>();;
		for(int i=0; i<validColumns.size()-1; i++) {
			Integer col1 = validColumns.get(i);
			Integer col2 = validColumns.get(i+1);
			
			/**if they occur one after the other, they are treates as an xy*/
			if (col1+1==col2) {
				ArrayList<BasicDataPoint> nums = getNumbersForTwoColumn(sheet, col1, col2);
				if (nums.size()==0) {
					IssueLog.log("found no numbers in those two valid columns"+ col1+" "+col2);
					continue;
				}
				XYDataSeries datai = new XYDataSeries(nums);
				datai.setxName(validNames.get(i));
				datai.setyName(validNames.get(i+1));
				datai.setName(sheet.getSheetName());
				data.add(datai);
				i++;
			}
		}
		
		
		return data;
}


protected static void findValidColumns(Sheet sheet, ArrayList<Integer> validColumns, ArrayList<String> validNames) {
	Row firstrow = sheet.getRow(0);
		for(Cell cell: firstrow) {
			if (cell.getCellType()==1) {
				validColumns.add(cell.getColumnIndex());
				validNames.add(cell.getStringCellValue());
				
			}
		}
}


/**Returns true if the values in the row are numbers*/
protected static boolean isTypeColumn(Sheet sheet, int colIndex, int type) {
	
	  for(Row row: sheet) {
		  if (sheet.getFirstRowNum()==row.getRowNum()) continue;//does not count the first row
		   for(Cell cell: row) try {
			  
			  if (cell.getCellType()!=type&&cell.getColumnIndex()==colIndex)
				return false;
			 
		   } catch (Throwable t) {t.printStackTrace();}
	  }
	  
	  return true;
}

/**Returns true if the values in the row are numbers*/
protected static boolean isTypeColumn(Iterable<Row> sheet, int colIndex, int type) {
	
	  for(Row row: sheet) {
		   for(Cell cell: row) try {
			  
			  if (cell.getCellType()!=type&&cell.getColumnIndex()==colIndex)
				return false;
			 
		   } catch (Throwable t) {t.printStackTrace();}
	  }
	  
	  return true;
}

public static ArrayList<Double> getNumbersForColumn(Iterable<Row> sheet, int col, ArrayList<Integer> wantedRows) {
	ArrayList<Double>  output=new ArrayList<Double> ();
	  for(Row row: sheet) {
		 
		 // if (wantedRows!=null&&wantedRows.contains(row.getRowNum())) {continue;}
		   for(Cell cell: row) try {
			  if (cell.getCellType()==0&&cell.getColumnIndex()==col)
				  output.add(cell.getNumericCellValue());
			 
		   } catch (Throwable t) {t.printStackTrace();}
		  
	   }
	  
	  return output;
}

public static ArrayList<String> getStringsForColumn(Iterable<Row> sheet, int col, ArrayList<Integer> wantedRows, int startingRow) {
	ArrayList<String>  output=new ArrayList<String> ();
	  for(Row row: sheet) {
		 // IssueLog.log("checking row r "+row.getRowNum());
		  if (row.getRowNum()<startingRow) continue;
		 // if (wantedRows!=null&&wantedRows.contains(row.getRowNum())) {continue;}
		   for(Cell cell: row) try {
			  if (cell.getCellType()==1&&cell.getColumnIndex()==col)
				  output.add(cell.getStringCellValue());
			 
		   } catch (Throwable t) {t.printStackTrace();}
		  
	   }
	  
	  return output;
}

public static ArrayList<String> getTextForColumn(Iterable<Row> sheet, int col, ArrayList<Integer> wantedRows, int startingRow) {
	ArrayList<String>  output=new ArrayList<String> ();
	  for(Row row: sheet) {
		 // IssueLog.log("checking row r "+row.getRowNum());
		  if (row.getRowNum()<startingRow) continue;
		 // if (wantedRows!=null&&wantedRows.contains(row.getRowNum())) {continue;}
		   for(Cell cell: row) try {
			  if (cell.getCellType()==1&&cell.getColumnIndex()==col)
				  output.add(cell.getStringCellValue());
			  if (cell.getCellType()==0&&cell.getColumnIndex()==col)
				  output.add(cell.getNumericCellValue()+"");
		   } catch (Throwable t) {t.printStackTrace();}
		  
	   }
	  
	  return output;
}

public static ArrayList<BasicDataPoint> getNumbersForTwoColumn(Iterable<Row>  sheet, int col, int col2) {
	ArrayList<BasicDataPoint>  output=new ArrayList<BasicDataPoint> ();
//	IssueLog.log("Checking columns "+col+" "+col2); 
	for(Row row: sheet)try {
		 Double x=null;
		   Double y=null;
		   for(Cell cell: row) try {
			  if (cell.getCellType()==0&&cell.getColumnIndex()==col)
				 x=cell.getNumericCellValue()  ;
			  if (cell.getCellType()==0&&cell.getColumnIndex()==col2)
					 y=cell.getNumericCellValue()  ;
		   } catch (Throwable t) {}
		   
		   output.add(new BasicDataPoint(x, y));
		  
	   } catch (Throwable t) {}
	  
	  return output;
}
	
/**gets the cell at the given row and col*/
	public static void getCellNumber(Sheet sheet, int row, int col) {
		Cell cell = sheet.getRow(row).getCell(col);
		
		getCellInfo(cell);
	}


	protected static void getCellInfo(Cell cell) {
		if (cell.getCellType()==1) {
			IssueLog.log(" "+cell.getStringCellValue());
		} else 
		if (cell.getCellType()==0) {
			IssueLog.log(" "+cell.getNumericCellValue());
		} else {
			IssueLog.log("	");
		}
		//IssueLog.log("Cell type is " +cell.getCellType());
		//""+" of number "+cell.getNumericCellValue());
		//
	}
	
	
	public static Object getObjectInCell(Cell cell) {
		if (cell.getCellType()==1) {
			return cell.getStringCellValue();
		} else 
		if (cell.getCellType()==0) {
			return cell.getNumericCellValue();
		} else {

		}
		
		
		return null;
	}
	
}


