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

import java.awt.Color;
import java.awt.Insets;
import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import standardDialog.GraphicDisplayComponent;
import utilityClassesForObjects.ScaleInfo;

/**A menu option that adds a scale bar to selected panels*/
public class BarGraphicAdder extends BasicGraphicAdder {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BarGraphic modelbar = new BarGraphic(); {modelbar.moveLocation(-4, 0);}
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		
		BarGraphic ag = getModelBar().copy();
		 addLockedItemToSelectedImage(ag);
		gc.add(ag);
		
		
		return ag;
	}
	
	



	@Override
	public String getCommand() {
	
		return "bar";
	}

	@Override
	public String getMenuCommand() {
	
		return "Scale Bar";
	}
	
	public BarGraphic getModelBar() {
		return modelbar;
	}
	
	public BarGraphic getModelForIcon() {
		BarGraphic out = getModelBar().copy();
		out.setStrokeColor(Color.black);
		out.setFillColor(Color.black);
		out.getBarText().setTextColor(Color.black);
		out.setScaleInfo(new ScaleInfo("units",.10,.10));
		return out;
	}
	public Icon getIcon() {
		 BarGraphic m = getModelForIcon();
		if (m==null)return null;
		GraphicDisplayComponent out = new GraphicDisplayComponent(m, .5);
		m.setLocation(0,16);
		out.setCurrentItemInsets(new Insets(5,5,1,5));
		 return out;
	}
	
	
	@Override
	public String getMenuPath() {
		return "to selected panels";
	}
}