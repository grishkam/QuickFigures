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
package genericMontageLayoutToolKit;
import layout.basicFigure.LayoutSpaces;



/**A layout tool for changing the border between panels*/
public class BorderAdjusterTool extends GeneralLayoutEditorTool implements LayoutSpaces {

	{
		this.setIconSet(new BorderAdjustToolIcon(0).generateIconSet());
	}
	
	public void performDragEdit(boolean shift) {
			
			if (isMovableRow()) {
				 getLayoutEditor().expandBorderY2(getCurrentLayout(), getMouseDisplacementY());
					}
		
			if (isMoveableColumn()) {
				getLayoutEditor().expandBorderX2(getCurrentLayout(), getMouseDisplacementX());
			}

	}


	/**
	 * @return true if the location of a user click is valid for horizontal border adjustment. 
	 * The user must click a column that is the second one or higher for this
	 * Or click a row that is the second one or beyond
	 */
	boolean isMoveableColumn() {
		return getColIndexClick() > 1 && getColIndexClick() <= getCurrentLayout().nColumns() && getMouseDisplacementX() != 0;
	}


	/**
	 * @return true if the location of a user click is valid for vertical border adjustment. 
	 * The user must click a row that is the second one or higher for this
	 */
	boolean isMovableRow() {
		return getRowIndexClick() > 1 && getMouseDisplacementY() != 0 && getRowIndexClick() <= getCurrentLayout().nRows();
	}
	

	@Override
	public String getToolTip() {
			return "Adjust Border Between Panels";
		}
	
	@Override
	public String getToolName() {
			return "Adjust Border Between Panels";
		}
	
	
	
}
