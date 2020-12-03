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
