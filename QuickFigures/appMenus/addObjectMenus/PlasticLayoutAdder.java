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
package addObjectMenus;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_LayoutObjects.PlasticPanelLayoutGraphic;

public class PlasticLayoutAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		GraphicLayerPane l22 = new GraphicLayerPane("Plastic Layout Layer");
		gc.add(l22);
		PanelLayoutGraphic p = getNewLayout() ;
		l22.add(p);
		p.select();
		return l22;
	}
	
	public PanelLayoutGraphic getNewLayout() {
		return  new PlasticPanelLayoutGraphic();
	}

	@Override
	public String getCommand() {
		return "Add Flexible Layout";
	}

	@Override
	public String getMenuCommand() {
		return "Add Flexible Layout";
	}
	
	@Override
	public String getMenuPath() {
		return "Alternate Layouts";
	}

}
