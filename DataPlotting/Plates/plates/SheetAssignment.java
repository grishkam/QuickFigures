/**
 * Author: Greg Mazo
 * Date Modified: Dec 18, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
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
}
