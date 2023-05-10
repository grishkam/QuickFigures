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
package objectDialogs;

import locatedObject.LocatedObject2D;
import standardDialog.StandardDialog;
import standardDialog.numbers.NumberInputPanel;

/**A dialog that prompts a user to input an X,Y position*/
public class LocationSettingDialog extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LocatedObject2D ob;
	
	public  LocationSettingDialog(LocatedObject2D ob) {
		this.ob=ob;
		this.setModal(true);
		addOptionsToDialog();
	}
	
	void addOptionsToDialog() {
		this.add("x", new NumberInputPanel("x", ob.getLocation().getX()));
		this.add("y", new NumberInputPanel("y", ob.getLocation().getY()));
		
	}
	
	@Override
	public void onOK() {
		setItemToDialog();
	}
	
	void setItemToDialog() {
		ob.setLocation(this.getNumberInt("x"), getNumberInt("y"));
		 GraphicItemOptionsDialog.updateCurrentDisplay();
	}

	
	

}
