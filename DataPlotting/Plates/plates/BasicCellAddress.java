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
 * Version: 2022.2
 */
package plates;

/**
 An object that keeps track of where a well is located (Example: A1, D8, G12)
 */
public class BasicCellAddress {
	public static final int A_Index = (int)'A';
	
	
	 int row;
	 int col;


	private AddressModification addressMod;

	public BasicCellAddress(int row, int col, AddressModification addressModification) {
		this.row=row;
		this.col=col;
		this.addressMod=addressModification;
	}
	/**returns the address in A1 format
	 * @param addressModification */
	public String getAddress(AddressModification addressModification) {
		
		int r = A_Index+getRow();
		int c = getCol()+1;
		if(addressModification!=null) {
			r+=addressModification.getRowShift();
			c+=addressModification.getColShift();
		}
		
		char letter=(char)r;
		return ""+letter+c;
	}
	public int getRow() {
		return row;
	}
	public int getCol() {
		return col;
	}
	
	  public String toString() {
	        return getClass().getName() + "["+"row="+this.getRow()+", "+"col="+this.getCol()+", code="+this.getAddress(addressMod)+"]";
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
	public static String[] namesOfAxisRows(int n, AddressModification m) {
		String[] output = new String[n];
		for(int i=0; i<n; i++) {
			output[i]=""+getCharForIndex(i+m.getRowShift());
		}
		return output;
	}
	
	/**returns the names of the cols, 1, 2, 3 ... as an array.*/
	public static String[] namesOfAxisCols(int n, AddressModification m) {
		String[] output = new String[n];
		for(int i=0; i<n; i++) {
			output[i]=""+(i+1+m.getColShift());
		}
		return output;
	}
	
	/**returns the letter A, B, C, D and so on dpending on the input. A=0, B=1, C=2...*/
	public static char getCharForIndex(int rowIndex) {
		return (char)(A_Index+rowIndex);
	}
	
	
}
