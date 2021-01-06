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
 * Version: 2021.1
 */
package addObjectMenus;

import java.awt.Color;
import java.awt.Rectangle;

import graphicTools.RectGraphicTool;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.RectangularGraphic;

/**An adding menu item that adds a rectangular object 
  (or at least one that extends the rectangular graphic superclass)*/
public class RectangleAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	static int count=0; {count++;}
	private static final long serialVersionUID = 1L;
	private RectGraphicTool tool;
	String menuPath = "Shapes";
	
	/**creates an object adder from the rectangular graphic tool*/
	public RectangleAdder(RectGraphicTool t, String subMenuName) {
		this.tool=t;
		if (subMenuName!=null) menuPath +="<"+subMenuName;
		t.getModel().setStrokeColor(Color.blue);
		t.getModel().setRectangle(new Rectangle(0,0,50,50));// the icon will be large due to this
	}
	
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		RectangularGraphic rg = getModelForIcon().copy();
		
		gc.add(rg);
		rg.showOptionsDialog();
		return rg;
	}
	
	

	@Override
	public String getCommand() {
		return "Add rect"+unique+tool.getShapeName()+count;
	}

	@Override
	public String getMenuCommand() {
		return "Add "+tool.getShapeName();
	}

	public RectangularGraphic getModelForIcon() {
		return tool.getModel();
	}
	
	@Override
	public String getMenuPath() {
		return menuPath;
	}


}
