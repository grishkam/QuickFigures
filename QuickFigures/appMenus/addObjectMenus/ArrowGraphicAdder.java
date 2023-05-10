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

import java.awt.Color;

import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.ArrowGraphic;

/**An implementation of graphic adder that adds an arrow*/
public class ArrowGraphicAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrowGraphic model = new ArrowGraphic(); {model.setStrokeColor(Color.darkGray);}
	
	@Override
	public ArrowGraphic add(GraphicLayer gc) {
		ArrowGraphic ag =  getModelForIcon().copy();
		gc.add(ag);;
		return  ag;
	}

	@Override
	public String getCommand() {
		return "Arrow "+unique+" "+model.getNHeads();
	}

	@Override
	public String getMenuCommand() {
		return "Add Arrow";
	}

	public ArrowGraphic getModelForIcon() {
		return model;
	}
	
	@Override
	public String getMenuPath() {
		return "Shapes";
	}


	
}
