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
 * Date Modified: July 17, 2023
 * Version: 2023.2
 */
package plates;

/**
 
 * 
 */
public class SheetAssignment {

	private Integer sheetRow;
	private Integer replicateNumber;
	private String sourceSheet;
	private int sampleNameCol;

	/**
	 * @param currentrow
	 * @param currentReplicate
	 */
	public SheetAssignment(int currentrow, int currentReplicate,  String sheetName, int sampleNameCol) {
		sheetRow=currentrow;
		replicateNumber=currentReplicate;
		this.setSampleNameCol(sampleNameCol);
		setSourceSheetName(sheetName);
	}

	public Integer getSheetRow() {
		return sheetRow;
	}
	
	
	
	/**
	 * @param sheetName
	 */
	public void setSourceSheetName(String sheetName) {
		sourceSheet=sheetName;
		
	}
	
	public boolean matches(SheetAssignment other) {
		if(other.replicateNumber!=this.replicateNumber)
			return false;
		if(other.sheetRow!=this.sheetRow)
			return false;
		if(!other.sourceSheet.contentEquals(this.sourceSheet))return false;
		
		return true;
	}
	
	/**if there is a sheet assignment that matches this one in the list*/
	public SheetAssignment findMatching(Iterable<SheetAssignment> all) {
		for(SheetAssignment a:all) {
			if(this.matches(a))
				return a;
		}
		
		return null;
	}

	public String toString() {
		return "row "+sheetRow+ " from "+this.replicateNumber+" of "+this.sourceSheet;
	}
	
	/**gets the foruma code for the col*/
	public String getFormula() {
		return getFormulaForCell(sheetRow+1, sampleNameCol);// the sheetRow 0 is the first row below the column tititles

		
	}
	
	/**returns the formula for a specifc cell*/
	public static String getFormulaForCell(int row, int col) {
		int baseCol=(int) 'A';
		baseCol+=col;
		char colLetter=(char)baseCol;
		return ""+colLetter+""+row;
	}

	/**
	 * @return
	 */
	public String getSheetName() {
		return sourceSheet;
	}

	public int getSampleNameCol() {
		return sampleNameCol;
	}

	public void setSampleNameCol(int sampleNameCol) {
		this.sampleNameCol = sampleNameCol;
	}
	
	/**returns true if this sheet row assignment matches the other*/
	public boolean isSameSample(SheetAssignment other) {
		if(other.sheetRow!=sheetRow)
			return false;
		if(other.replicateNumber!=replicateNumber)
			return false;
		
		return true;
	}
}
