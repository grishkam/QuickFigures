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
package addObjectMenus;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.SimpleGraphicalObject;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;

class LayoutAdder extends BasicGraphicAdder {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		GraphicLayerPane l22 = new GraphicLayerPane("Layout Layer");
		gc.add(l22);
		DefaultLayoutGraphic p = createStandard() ;
		p.showOptionsDialog();
		p.getPanelLayout().resetPtsPanels();
		p.moveLocation(10, 10);
		l22.add(p);
		
		return l22;
	}
	
	public DefaultLayoutGraphic createStandard() {
DefaultLayoutGraphic p = new DefaultLayoutGraphic();
		
		p.getPanelLayout().setStandardPanelWidth(100);
		p.getPanelLayout().setStandardPanelHeight(100);
		p.getPanelLayout().setNColumns(3);
		p.select();
		return p;
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "Add Grid Layout";
	}

	@Override
	public String getMenuCommand() {
		return "Add Normal Layout (a grid)";
	}

	
	public SimpleGraphicalObject getCurrentDisplayObject() {
		// TODO Auto-generated method stub
		return createStandard();
	}

	
	public void setCurrentDisplayObject(
			SimpleGraphicalObject currentDisplayObject) {
		// TODO Auto-generated method stub
		
	}}
