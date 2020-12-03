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
package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;

import externalToolBar.TreeIconWrappingToolIcon;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.RoundedRectangleGraphic;

public class RoundRectGraphicTool extends RectGraphicTool {
	{model=new RoundedRectangleGraphic(new Rectangle(0,0,0,0));}{getModel().setStrokeColor(Color.black);{super.set=TreeIconWrappingToolIcon.createIconSet(getModel());}}
	
	public RectangularGraphic createShape(Rectangle r) {
		RoundedRectangleGraphic ouput = new RoundedRectangleGraphic(r);
		ouput.setArch(((RoundedRectangleGraphic)getModel()).getArch());
		ouput.setArcw(((RoundedRectangleGraphic)getModel()).getArcw());
		return ouput;
	}
	
	@Override
	public String getToolTip() {
			
			return "Draw an Rounded Rectangle";
		}
	
	@Override
	public String getToolName() {
		return "Draw Round Rect";
	}
	
		
}
