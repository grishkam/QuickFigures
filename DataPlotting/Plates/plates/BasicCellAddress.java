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
 * Date Created: May 16, 2022
 * Date Modified: May 26, 2022
 * Version: 2022.1
 */
package plates;

/**
 An object that keeps track of where a well is located (Example: A1, D8, G12)
 */
public class BasicCellAddress {
	public static final int A_Index = (int)'A';
	
	
	 int row;
	 int col;

	public BasicCellAddress(int row, int col) {
		this.row=row;
		this.col=col;
	}
	/**returns the address in A1 format*/
	public String getAddress() {
		char letter=(char)(A_Index+getRow());
		return ""+letter+(getCol()+1);
	}
	public int getRow() {
		return row;
	}
	public int getCol() {
		return col;
	}
	
	  public String toString() {
	        return getClass().getName() + "["+"row="+this.getRow()+", "+"col="+this.getCol()+", code="+this.getAddress()+"]";
	    }
	
	/**returns true if the cell addresses match. 
	 * @param b
	 * @return
	 */
	public boolean matches(BasicCellAddress b) {
		if(b.row==row&&b.col==col)
			return true;
		return false;
	}
	
	/**returns the names of the rows, A, B, C... as an array.*/
	public static String[] namesOfAxisRows(int n) {
		String[] output = new String[n];
		for(int i=0; i<n; i++) {
			output[i]=""+getCharForIndex(i);
		}
		return output;
	}
	
	/**returns the names of the cols, 1, 2, 3 ... as an array.*/
	public static String[] namesOfAxisCols(int n) {
		String[] output = new String[n];
		for(int i=0; i<n; i++) {
			output[i]=""+(i+1);
		}
		return output;
	}
	
	/**returns the letter A, B, C, D and so on dpending on the input. A=0, B=1, C=2...*/
	public static char getCharForIndex(int rowIndex) {
		return (char)(A_Index+rowIndex);
	}
	
	
}
