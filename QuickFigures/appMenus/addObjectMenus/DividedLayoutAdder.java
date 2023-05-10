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

import java.awt.geom.Rectangle2D;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import layout.dividerLayout.DividedPanelLayout;
import layout.dividerLayout.DividedPanelLayoutGraphic;
import layout.dividerLayout.DividedPanelLayout.LayoutDividerArea;

public class DividedLayoutAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		GraphicLayerPane l22 = new GraphicLayerPane("Divided Layout Layer");
		gc.add(l22);
		PanelLayoutGraphic p = getNewLayout() ;
		l22.add(p);
		p.select();
		return l22;
	}
	
	public PanelLayoutGraphic getNewLayout() {
		
		DividedPanelLayout layout=new DividedPanelLayout(new Rectangle2D.Double(30, 30, 504,648));
		layout.mainArea.divide(216);
		LayoutDividerArea sub = layout.mainArea.getSubareas().get(1);
		sub.setHorizontal(false);
		sub.divide(200);
		 sub = layout.mainArea.getSubareas().get(0);
		 sub.setHorizontal(false);
				sub.divide(180);
				sub.divide(360);
		 
		layout.mainArea.divide(432);
		
		DividedPanelLayoutGraphic p=new DividedPanelLayoutGraphic(layout);
		
		return p;
	}

	@Override
	public String getCommand() {
		return "Add Divided Layout";
	}

	@Override
	public String getMenuCommand() {
		return "Add Divided Layout";
	}
	
	@Override
	public String getMenuPath() {
		return "Alternate Layouts";
	}

}
