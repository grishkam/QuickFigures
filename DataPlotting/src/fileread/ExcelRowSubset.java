package fileread;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import dataSeries.Basic1DDataSeries;
import dataSeries.BasicDataPoint;
import dataSeries.GroupedDataSeries;
import dataSeries.XYDataSeries;

public class ExcelRowSubset extends ArrayList<Row> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Iterable<Row> rows;
	private Row columnNames;
	private int subsetIndex=0;
	
	public ExcelRowSubset(String st, Iterable<Row> rows, Row columnNames) {
		this.name=st;
		this.rows=rows;
		this.columnNames=columnNames;
	}
	
	public ExcelRowSubset(Workbook wb) {
		this.name=wb.getSheetName(0);
		 Sheet sheet = wb.getSheetAt(0);
		 ArrayList<Row> row=new  ArrayList<Row>();
		 Row colTitiles = sheet.getRow(0);
		for(Row r: sheet) row.add(r);
		row.remove(colTitiles);
		this.rows=row;
		this.columnNames=colTitiles ;
	}
	
	
	/**When given a sheet and the index of the column with category names, returns an array of sebsets
	  one per category*/
	public ArrayList<ExcelRowSubset>  createSubsetsBasedOnColumn(int index) {
		 ArrayList<String> allStrings = ReadExcelData.getTextForColumn(rows, index, null, 1);
		
		ArrayList<String> uni = UtilForDataReading.getUniqueStrings(allStrings);
	
		ArrayList<ExcelRowSubset> out=new ArrayList<ExcelRowSubset>();
		
		for(String u: uni) {
			/**returns  subset of the rows*/
			ArrayList<Row> rows1 = ReadExcelData.getRowsWith(rows, index, u);
			ExcelRowSubset sub = new ExcelRowSubset(u, rows1, columnNames);
			out.add(sub);
		}
		 
		return out;
		
	}
	
	public Basic1DDataSeries createDataSeries(String name, int colIndex) {
		ArrayList<Double> nums = ReadExcelData.getNumbersForColumn(rows, colIndex, null);
		return new Basic1DDataSeries(name, nums);
	}
	
	/**Looks in columns, creates an xy data series from 
	  the first numeric columns it finds */
	public XYDataSeries createXYDataSeries() {
		String name=this.name;
		int col1=findFirstNumericColIndex();
		int col2=col1+1;
		return createXYDataSeries(name, col1, col2);
	}
	
	/**Looks in columns, creates an xy data series from 
	  the first numeric columns it finds */
	public ArrayList<XYDataSeries> createXYDataSeries(int categoryCol) { 
		ArrayList<XYDataSeries> xx=new ArrayList<XYDataSeries>();
		int col1=findFirstNumericColIndex();
		int col2=col1+1;
		ArrayList<ExcelRowSubset> sets = this.createSubsetsBasedOnColumn(categoryCol);
		for(ExcelRowSubset set: sets) {
			xx.add(set.createXYDataSeries(set.name, col1, col2));
		}
		return xx;
	
	}
	
	/**Creates category data series, Divides this into 
	 * separate data series using the column in the second argument,
	 * the categories for each series are found  in the categoryCol1 argument*/
	public ArrayList<GroupedDataSeries> 
		createCategoryDataSeries(int categoryCol1, int categoryCol2, int numericol1) {
		//int numericol1=findFirstNumericColIndex();//the column with numbers
		
		ArrayList<String> allStrings = ReadExcelData.getTextForColumn(rows, categoryCol2, null, 1);
		//IssueLog.log(allStrings);
		ArrayList<String> uni = UtilForDataReading.getUniqueStrings(allStrings);
		//IssueLog.log(uni);
		//IssueLog.log("Future categries include "+uni);
		
		ArrayList<String> allStrings2 = ReadExcelData.getTextForColumn(rows, categoryCol1, null, 1);
		//IssueLog.log(allStrings2);
		ArrayList<String> uni2 = UtilForDataReading.getUniqueStrings(allStrings2);
		//IssueLog.log("Future data series include "+uni2);
		//IssueLog.log("Future numbers include "+ReadExcelData.getNumbersForColumn(rows, numericol1, null));
		
		HashMap<Double, String> map=new HashMap<Double, String>();
		for(double d=1; d<uni.size()+1; d++) {
			map.put(d, uni.get((int)d-1));//need consistent map for locations
		}
		
		ArrayList<ExcelRowSubset> subset = this.createSubsetsBasedOnColumn(categoryCol1);
		ArrayList<GroupedDataSeries> output=new ArrayList<GroupedDataSeries>();
	
		for(ExcelRowSubset s: subset) {
			output.add(s.createCategorySeries(categoryCol2, numericol1, map));
		}
		
		return output;
	}
	
	/**Looks in columns, creates an xy data series from 
	  the first numeric columns it finds */
	public GroupedDataSeries createCategoryDataSeries(int categoryCol) { 
		
		int numericol1=findFirstNumericColIndex();
		
		ArrayList<String> allStrings = ReadExcelData.getStringsForColumn(rows, categoryCol, null, 1);
		ArrayList<String> uni = UtilForDataReading.getUniqueStrings(allStrings);
		HashMap<Double, String> map=new HashMap<Double, String>();
		for(double d=1; d<uni.size(); d++) {
			map.put(d, uni.get((int)d-1));//need consistent map for locations
		}
		
		
		return createCategorySeries(categoryCol, numericol1, map);

	
	}

	private GroupedDataSeries createCategorySeries(int categoryCol, int numericol1, HashMap<Double, String> map) {
		ArrayList<ExcelRowSubset> sets = this.createSubsetsBasedOnColumn(categoryCol);
		ArrayList<Basic1DDataSeries> xx=new ArrayList<Basic1DDataSeries>();
		
		for(ExcelRowSubset set: sets) {
			xx.add(set.createDataSeries(set.name, numericol1));
		}
		return new GroupedDataSeries(this.name, map, xx.toArray(new Basic1DDataSeries[xx.size()]));
	}
	
	
	public int findFirstNumericColIndex() {
		ArrayList<Cell> valid = findValidColumnNames();
				
			for(int i=0; i<valid.size()-1; i++) {
				int col1 = valid.get(i).getColumnIndex();
				if (ReadExcelData.isTypeColumn(rows, col1, 0)) return col1;
					}
			
			return 1;
	}
	
	public XYDataSeries createXYDataSeries(String name, int colIndex, int colIndex2) {
		ArrayList<BasicDataPoint> nums = ReadExcelData.getNumbersForTwoColumn(rows, colIndex, colIndex2);
		XYDataSeries data = new XYDataSeries(name, nums);
		if (columnNames!=null) {
			data.setxName(columnNames.getCell(colIndex).getStringCellValue());
			data.setyName(columnNames.getCell(colIndex2).getStringCellValue());
		}
		return data;
	}
	
	/**returns the columns that represent valid column names*/
	private ArrayList<Cell> findValidColumnNames() {
		ArrayList<Cell> validCols=new ArrayList<Cell>();
		
			for(Cell cell: columnNames) {
				if (cell.getCellType()==1) {
					validCols.add(cell);
					
				}
			}
			return validCols;
	}
	
	
	


	public int getSubsetIndex() {
		return subsetIndex;
	}


	public void setSubsetIndex(int subsetIndex) {
		this.subsetIndex = subsetIndex;
	}

}
