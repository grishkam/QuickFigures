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
 * Date Created: Feb 1, 2022
 * Date Modified: Feb 1, 2022
 * Version: 2023.1
 */
package lineprofile;

/**
 An enum listing the different ways in which the numbers for the values in a line profile may be calcualted
 */
public enum ProfileValueType {
	RAW_VALUE("Intensity"), PERCENT_OF_MAX_IN_PROFILE("Percent"), PERCENT_OF_MAX_IN_IMAGE("Percent"), PERCENT_OF_DISPLAY_RANGE("Percent");

	private String axisLabel;

	/**
	 * @param string
	 */
	ProfileValueType(String string) {
		this.axisLabel=string;
	}
	
	public String getAxisLabel() {return axisLabel;}
}
