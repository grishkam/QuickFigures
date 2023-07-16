/*******************************************************************************
 * Copyright (c) 2023 Gregory Mazo
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
 * Date Modified: July 11, 2023
 * Version: 2023.2
 */
package dataTableDialogs;

import logging.IssueLog;

/**
 
 * 
 */
public class ColumnReader {
	
	private Integer colIndex=null;
	private String columnName=null;
	private TableReader table;

	public ColumnReader(TableReader table, Integer newcolIndex, String colName) {
		if(newcolIndex==null) {
			newcolIndex=0;
		}
		Object oldColTitle = table.getValueAt(0, newcolIndex);
		if(isColumnTitleless(oldColTitle)) {
			table.setValueAt(colName, 0, newcolIndex);
		}
		this.colIndex=newcolIndex;
		this.columnName=colName;
		this.table=table;
	}

	/**
	 * @param oldColTitle
	 * @return
	 */
	public boolean isColumnTitleless(Object oldColTitle) {
		return oldColTitle==null ||"".equals(oldColTitle);
	}
	
	public void setValue(Object value, int index) {
		table.setValueAt(value, index, colIndex);
		
	}
	
	Object getValue(int row) {
		return table.getValueAt(row, colIndex);
	}
	
	/**Adds the value to a list*/
	public void appendValueAt(Object value, int index) {
		
		Object val = getValue( index);
		if(val==null)
			val="";
		String wellAddressListText=val+"";
		if(wellAddressListText.length()>0)
			wellAddressListText+=", ";
		
		setValue(wellAddressListText+value,  index);
	}
	
	public String getFormulaCode(int index) {
		return ""+(char)(((int)'A')+colIndex)+""+index;
	}

}
