/**
 * Author: Greg Mazo
 * Date Modified: May 16, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package plates;

/**
 
 */
public class BasicCellAddress {
	public static final int A_Index = (int)'A';
	public static char getCharForIndex(int rowIndex) {
		return (char)(A_Index+rowIndex);
	}
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
	
	/**returns true if the cell addresses matchs
	 * @param b
	 * @return
	 */
	public boolean matches(BasicCellAddress b) {
		if(b.row==row&&b.col==col)
			return true;
		return false;
	}
	
	public static String[] namesOfAxis(int n) {
		String[] output = new String[n];
		for(int i=0; i<n; i++) {
			output[i]=""+getCharForIndex(i);
		}
		return output;
	}
	public static String[] namesOfAxisCols(int n) {
		String[] output = new String[n];
		for(int i=0; i<n; i++) {
			output[i]=""+(i+1);
		}
		return output;
	}
}
