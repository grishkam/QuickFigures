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
 * Date Modified: Jan 7, 2021
 * Version: 2021.2
 */
package dataTableDialogs;

/**implementations of RowFilter(see below) select certain rows of a
 * table as fitting a particular category and others as not*/
public interface RowFilter {
	
	public boolean isChosenRow(TableReader table, int rowNumber);
	

	class CombinedFilter implements RowFilter {

		private RowFilter[] filter;

		public CombinedFilter(RowFilter... filters ) {
			this.filter=filters;
		}
		
		@Override
		public boolean isChosenRow(TableReader table, int rowNumber) {
			for(RowFilter f:filter) {
				if (!f.isChosenRow(table, rowNumber)) return false;
			}
			
			return true;
		}
		
	}
	
	class HeaderExcludingFilter implements RowFilter {

		@Override
		public boolean isChosenRow(TableReader i, int rowNumber) {
			return rowNumber!=0;
		}}
	
	class ColEqualFilter implements RowFilter {

		private String checkForValue;
		private int checkCol;

		public ColEqualFilter(String name, int classColumn) {
			this.checkForValue=name;
			this.checkCol=classColumn;
		}

		@Override
		public boolean isChosenRow(TableReader table, int rowNumber) {
			Object value = table.getValueAt(rowNumber, checkCol);
			if (value==checkForValue) return true;
			if (value==null) return false;
			return (value+"").equals(checkForValue);
		}}
	
}