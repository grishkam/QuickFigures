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
 * Date Modified: Jan 6, 2021
 * Version: 2023.2
 */
package selectedItemMenus;

import graphicalObjects.ZoomableGraphic;
import locatedObject.Fillable;

/**shows a dialog for changing the fill paint of an object*/
public class FillPaintSetter extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Set Special Fill Paint";
	}

	@Override
	public void run() {
		if (array.size()==0) return;
		ZoomableGraphic item = this.array.get(0);
	if (item instanceof Fillable) {
		Fillable f=(Fillable) item;
		f.getFillPaintProvider().showOptionsDialog();
	}

	}
	
public String getMenuPath() {
		
		return "Item";
	}

}
