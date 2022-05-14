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
 * Version: 2022.1
 */
package plates;

import logging.IssueLog;

/**
 
 * 
 */
public class PlateCell {

	private int listAddress;
	private String plateAddress;
	private int spreadSheetRow=1;
	private Object shortName;

	public PlateCell(int i, String plateAddress) {
		this.listAddress=i;
		this.plateAddress=plateAddress;
	}
	
	
	public int[] plateAddressToCell() {
		int[] output=new int[2];
		if(plateAddress==null)	{ 
			IssueLog.log("Plate Address missing");
			return null;
			}
		
		int row=(int)plateAddress.charAt(0)-Plate.A_Index;
		int col=Integer.parseInt(plateAddress.substring(1));
		
		output[0]=row;
		output[1]=col;
		
		return output;
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


	public int getSpreadSheetRow() {
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
	
	
}
