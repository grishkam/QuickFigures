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
package graphicActionToolbar;

import graphicalObjects.FigureDisplayContainer;
import layersGUI.GraphicTreeUI;

public class TreeActionTool extends DisplayActionTool {

	public TreeActionTool() {
		super("show tree", "TreeIconBeta.jpg");
		// TODO Auto-generated constructor stub
	}
	
	protected void perform(FigureDisplayContainer graphic) {
		if (graphic!=null) {
			new GraphicTreeUI(graphic).showTreeForLayerSet(graphic) ;
		}
	}
	
	@Override
	public String getToolTip() {
			return "Show Layers";
		}
	

}
