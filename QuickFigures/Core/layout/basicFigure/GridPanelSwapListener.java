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
 * Version: 2023.2
 */
package layout.basicFigure;

/**Listens for a panel swap event*/
public interface GridPanelSwapListener {
	public void onSwapCol(GridLayout ml, int row1, int row2);
	public void onSwapRow(GridLayout ml, int row1, int row2);
	
	public void onColInsertion(GridLayout ml, int col, int ncols);
	public void onColRemoval(GridLayout ml, int col, int ncols);
	public void onRowInsertion( GridLayout ml, int row, int nrows);
	public void onRowRemoval(GridLayout ml, int row1, int row2);
	
	public void onBorderChange(GridLayout ml, int row1, int row2) ;
	
	void onSwapPanel(GridLayout ml, int row1, int row2);
	
}
