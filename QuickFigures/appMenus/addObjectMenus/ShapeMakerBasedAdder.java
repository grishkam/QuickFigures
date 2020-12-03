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

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.PathGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_LayerTypes.GraphicLayer;
import utilityClassesForObjects.BasicShapeMaker;
import utilityClassesForObjects.CiliaryPocketPathCreator;

public class ShapeMakerBasedAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BasicShapeMaker shape=new CiliaryPocketPathCreator();
	private String indiName="cilia pocket";
	Color startingColor=Color.green;
	
	public ShapeMakerBasedAdder() {}
	public ShapeMakerBasedAdder(String name, BasicShapeMaker s, Color c) {
		 indiName=name;
		 shape=s;
		 startingColor=c;
	}
	
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		// TODO Auto-generated method stub
		PathGraphic cartoon = BasicShapeMaker.createDefaultCartoon(shape);
		cartoon.setFillColor(startingColor);
		gc.add(cartoon);
		BasicShapeMaker.createUpdatingDialog(cartoon, shape);
		return cartoon;
	}

	@Override
	public String getCommand() {
		return "Add Cartoon "+shape.getClass().getName()+indiName;
	}

	@Override
	public String getMenuCommand() {
		return "";
	}

	
	protected BasicGraphicalObject getModelForIcon() {
		PathGraphic shape2 = BasicShapeMaker.createDefaultCartoon(shape);
		shape2.setFillColor(startingColor);
		return  shape2;
	}
	
	
}
