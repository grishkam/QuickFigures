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
 * Date Modified: Mar 27, 2021
 * DateCreated: Mar 27, 2021
 * Version: 2021.1
 */
package fileread;

/**
A list of the major types of plot
 */
public enum PlotType {
	
	DEFAULT_PLOT_TYPE_COLS("exampleCols.xlsx"), 
	XY_PLOT_TYPE("ExampleXY.xlsx"), 
	GROUP_PLOT_TYPE("ExampleGrouped.xlsx"),  
	COLUMN_PLOT_TYPE("exampleCols.xlsx") , 
	KAPLAN_MEIER_PLOT_TYPE("Kaplan Example.xlsx");
	
	
	/**The file that can be used as an example*/
	private String example;

	PlotType(String exampleData) {
		this.example=exampleData;
	}
	
	public String getExampleFileName() {return example;}
}
