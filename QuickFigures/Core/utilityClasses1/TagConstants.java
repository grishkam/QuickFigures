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
 * Date Modified: Jan 21, 2022
 * Version: 2022.0
 */
package utilityClasses1;


/**
 stores usful constants so they do not have to be defined over and ober
 */
public interface TagConstants {
	
	public static String INDEX="Index";//the index indicates which panel, row or column an object is glued to. If none is assigned then QuickFigures will automatically update the index that it believes is appropriate based on location relative to layout
	
	public static String GRID_WIDTH="GridWidth", GridHeight="GridHeight", GRID_SIZE="GridSize";
	
}
