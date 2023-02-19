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
 * Version: 2023.1
 */
package addObjectMenus;

import javax.swing.Icon;

import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.CentriolePairCartoon;
import graphicalObjects_LayerTypes.GraphicLayer;
import standardDialog.graphics.GraphicDisplayComponent;

/**Adds a group of two rectangles to the image */
public class CentriolePairCartoonAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	CentriolePairCartoon makePair() {
		return new CentriolePairCartoon();
	}
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
	CentriolePairCartoon pp = makePair();
		gc.add(pp);
		return pp;
	}

	@Override
	public String getCommand() {
		return "Centrioles";
	}

	@Override
	public String getMenuCommand() {
		return null;//"Add Centrioles";
	}
	
	protected BasicGraphicalObject getModelForIcon() {
		return  makePair();
	}
	
	public Icon getIcon() {
		BasicGraphicalObject m = getModelForIcon();
		if (m==null)return null;
		return new GraphicDisplayComponent(getModelForIcon(), .7);
	}

}
