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
 * Date Modified: Jan 5, 2021
 * Version: 2022.2
 */
package layout.basicFigure;

/**this interface contains constants that indicate specific types of layout edits*/
public interface GridEditEventTypes {
	public static int BORDER_EDIT_H=0, BORDER_EDIT_V=1, PANEL_SWAP=2, ROW_SWAP=3, COL_SWAP=4, PANEL_INSERTION=5, COL_INSERTION=6, ROW_INSERTION=7,  PANEL_REMOVAL=8, COL_REMOVAL=9, ROW_REMOVAL=10, PANEL_RESIZE_H=11, PANEL_RESIZE_V=12, COL_RESIZE=13, ROW_RESIZE=14;

	public static final int COL_ADDITION = 16, ROW_ADDITION=15, INVERSION=17;
	public static final int REPACKAGE = 19;

	public static final int LOCATION_EDIT = 18;
	public static int LABEL_SPACE_EDIT=200;
	public static int ADITIONAL_SPACE_OR_LOCATION_EDIT=400;

}
