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
package selectedItemMenus;

import objectDialogs.LocationSettingDialog;
import utilityClassesForObjects.LocatedObject2D;

public class LocationSetter extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Set Item Location";
	}

	@Override
	public void run() {
		if (this.array.size()<1) return ;
		if (array.get(0) instanceof LocatedObject2D)
		new LocationSettingDialog((LocatedObject2D) array.get(0)).showDialog();;

	}
	
public String getMenuPath() {
		
		return "Item";
	}

}
