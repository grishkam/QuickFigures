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
package utilityClassesForObjects;

import utilityClasses1.ItemPicker;

public class DrawnGraphicPicker implements ItemPicker {

	private int type;

	public DrawnGraphicPicker(int type) {
		this.type=type;
	}
	
	@Override
	public boolean isDesirableItem(Object o) {
		if (o instanceof DrawnGraphic) {
			DrawnGraphic d=(DrawnGraphic) o;
			if (d.getTypeOfGraphic()==type) return true;
			else return false;
			
		}
		
		if (type==DrawnGraphic.Text_Item && o instanceof TextItem) return true;
		
		
		// TODO Auto-generated method stub
		return false;
	}

}
