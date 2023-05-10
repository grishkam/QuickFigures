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
package addObjectMenus;

import graphicalObjects_LayoutObjects.ObjectDefinedLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;

public class ImagePanelLayoutAdder extends PlasticLayoutAdder {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PanelLayoutGraphic getNewLayout() {
		return  new ObjectDefinedLayoutGraphic();
	}
	
	
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "Add Image Panel Dependent Layout";
	}

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return "Add Image Panel Dependent Layout";
	}
}
