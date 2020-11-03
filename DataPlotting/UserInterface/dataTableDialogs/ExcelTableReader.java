package dataTableDialogs;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import fileread.ReadExcelData;

public class ExcelTableReader implements TableReader {

	private  org.apache.poi.ss.usermodel.Sheet sheet;

	public ExcelTableReader(Sheet wb) {
		sheet=wb;
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

}
