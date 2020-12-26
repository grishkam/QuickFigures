/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package layout.basicFigure;

import layout.PanelLayout;

/**A layout that is a grid. Positions are references with a number index as well as a row-columns number
   The methods below convert between the two*/
public interface GridLayout extends PanelLayout{
	public int getIndexAtPosition( int row,int column);
	public int getRowAtIndex(int i);
	public int getColAtIndex(int i);
	public int nRows();
	public int nColumns();
	
	public GridLayoutEditListenerList getListeners();
}