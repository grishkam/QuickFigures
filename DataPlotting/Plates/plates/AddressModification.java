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
 * Date Created: June 10, 2022
 * Date Modified: June 10, 2022
 * Version: 2023.2
 */
package plates;

/**
 
 * 
 */
public class AddressModification {

	private double rowShift;
	private double colShift;

	
	public AddressModification() {
		this(0,0);
	}
	
	/**
	 * @param rowShift
	 * @param colShift
	 */
	public AddressModification(double rowShift, double colShift) {
		this.setRowShift(rowShift);
		this.setColShift(colShift);
	}

	public int getColShift() {
		return (int) colShift;
	}

	public void setColShift(double colShift) {
		this.colShift = colShift;
	}

	public int getRowShift() {
		return (int) rowShift;
	}

	public void setRowShift(double rowShift) {
		this.rowShift = rowShift;
	}

}
