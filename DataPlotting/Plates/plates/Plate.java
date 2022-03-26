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
 * Date Created: Mar 26, 2022
 * Date Modified: Mar 26, 2022
 * Version: 2022.0
 */
package plates;

import java.util.ArrayList;

/**
 
 * 
 */
public class Plate {
	
	/**
	 * 
	 */
	public static final int A_Index = (int)'A';
	String formatName="Generic Plate";
	int nCol=12;
	int nRow=8;
	
	ArrayList<PlateCell> cellList=new ArrayList<PlateCell>();
	
	public Plate() {
		createPlaceCells();
	}
	public Plate(int row, int col) {
		this.nCol=col;
		this.nRow=row;
		createPlaceCells();
	}
	
	/**returns the row/col address of the */
	public String getIndexAddress(int index) {
		int colIndex = index%nCol;
		int rowIndex = index/nCol;
		
		char letter=(char)(A_Index+rowIndex);
		return ""+letter+(colIndex+1);
	}
	
	public static char getCharForIndex(int rowIndex) {
		return (char)(A_Index+rowIndex);
	}
	
	public void createPlaceCells() {
		ArrayList<String> names=new ArrayList<String>();
		for(int i=0; i<nRow*nCol; i++) {
			String a1 = this.getIndexAddress(i);
			PlateCell cell = new PlateCell(i, a1);
			cellList.add(cell);
			names.add(a1);
		
		}
	}
	
	/**returns a list of plate cells*/
	public ArrayList<PlateCell> getPlateCells() {
		return this.cellList;
	}
	/**
	 * @return the number of rows
	 */
	public int getNRows() {
		return nRow;
	}
	
	/**
	 * @return the number of columns
	 */
	public int getNCol() {
		return nCol;
	}
	
	


}
