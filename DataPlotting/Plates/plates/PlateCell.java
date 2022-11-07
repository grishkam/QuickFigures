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
 * Version: 2022.2
 */
package plates;

import java.awt.Color;

import logging.IssueLog;

/**
 
 * 
 */
public class PlateCell {

	/**Address starts out invalid*/
	private String plateAddress="";
	BasicCellAddress address= new BasicCellAddress(0,0, null);
	
	private int listAddress;
	
	private Integer spreadSheetRow=null;
	private Object shortName;
	private Color color=Color.lightGray;
	private String sourceSheet="Sheet0";
	private AddressModification addressMod;

	public PlateCell(BasicCellAddress address, AddressModification m) {
		this.address=address;
		plateAddress=address.getAddress(m);
		this.addressMod=m;
	}
	
	private PlateCell(int i, String plateAddress) {
		this.listAddress=i;
		this.plateAddress=plateAddress;
		address= plateAddressToCell() ;
		
	}
	
	/**turns the plate address to a row/col*/
	private BasicCellAddress plateAddressToCell() {
		int[] output=new int[2];
		if(plateAddress==null)	{ 
			IssueLog.log("Plate Address missing");
			return null;
			}
		
		int row=(int)plateAddress.charAt(0)-BasicCellAddress.A_Index;
		int col=Integer.parseInt(plateAddress.substring(1));
		
		output[0]=row;
		output[1]=col;
		return new BasicCellAddress(row,col, addressMod);
		
	}
	
	@Override
	public String toString() {
		return plateAddress;
	}
	
	public String getShortLabel() {
		if(shortName!=null)
			return ""+shortName;
		return plateAddress;
		}


	public Integer getSpreadSheetRow() {
		return spreadSheetRow;
	}


	public void setSpreadSheetRow(int spreadSheetRow) {
		this.spreadSheetRow = spreadSheetRow;
	}


	/**
	 * @param valueAt
	 */
	public void setShortName(Object valueAt) {
		this.shortName=valueAt;
		
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
	 * @param sheetName
	 */
	public void setSourceSheetName(String sheetName) {
		sourceSheet=sheetName;
		
	}
	
	
}
