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
 * Version: 2022.1
 */
package selectedItemMenus;

import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import objectDialogs.CroppingDialog;

/**shows a crop dialog for many image panels*/
public class CroppingSyncer extends BasicMultiSelectionOperator{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Set Cropping Of Image";
	}



	@Override
	public void run() {	
		CroppingDialog cd = new CroppingDialog();
		cd.setArray(array);
		ArrayList<ImagePanelGraphic> pan = cd.getImagepanels();
		if (pan.size()==0) return;
		cd.showDialog(pan.get(0));
		
	}
	public Icon getIcon() {
		return ImagePanelGraphic.createImageIcon();
	}
	
	public String getMenuPath() {
		
		return "Advanced";
	}

}
