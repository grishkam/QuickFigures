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
 * Date Modified: Dec 10, 2022
 * Version: 2023.2
 */
package plates;

import java.awt.Color;

import logging.IssueLog;

/**
 contains information about a cell in a plate
 */
public class PlateCell {

	/**Address starts out invalid*/
	private String plateAddress="";
	private SheetAssignment spreadSheetRow=null;
	BasicCellAddress address= new BasicCellAddress(0,0, null);
	private Plate plate;
	
	
	
	private Object shortName;
	private Color color=Color.lightGray;
	private String sourceSheet="Sheet0";
	

	public PlateCell(BasicCellAddress address, AddressModification m) {
		this.address=address;
		plateAddress=address.getAddress(m);
		//this.addressMod=m;
	}
	
	
	@Override
	public String toString() {
		return plateAddress;
	}
	
	/**returns the short name of the cell that will be displayed on the summary sheet*/
	public String getShortLabel() {
		if(getCellText()!=null)
			return ""+getCellText();
		return plateAddress;
		}

	/**If a specific text has been asigned to this cell
	 * @return
	 */
	public Object getCellText() {
		return shortName;
	}


	public Integer getSpreadSheetRow() {
		if(getSheetAddress()!=null)
			return getSheetAddress().getSheetRow();
		return null;
	}


	


	/**
	 * @param valueAt
	 */
	public void setShortName(Object valueAt) {
		this.shortName=valueAt;
		
	}
	
	/**sets the formula for the short name
	 * @param string
	 */
	public String getFormulaForShortName() {
		String string =""+spreadSheetRow.getSheetName()+"!"+"A"+(spreadSheetRow.getSheetRow()+1);
		return string;
		
	}

	public BasicCellAddress getAddress() {
		return address;
	}

	/**
	 * 
	 */
	public void setColor(Color c) {
		this.color=c;
		
	}

	public Color getColor() {
		return color;
	}

	/**
	 * @return
	 */
	public String getSourceSheetName() {
		// TODO Auto-generated method stub
		return sourceSheet;
	}

	

	

	/**
	 * @param sa
	 */
	public void setSheetAssignment(SheetAssignment sa) {
		this.spreadSheetRow=sa;
		
	}

	public SheetAssignment getSheetAddress() {
		return spreadSheetRow;
	}


	public Plate getPlate() {
		return plate;
	}


	public void setPlate(Plate plate) {
		this.plate = plate;
	}

	
	
	
}
