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
import graphicalObjects_BasicShapes.SimpleRing;

public class RingGraphicTool extends CircleGraphicTool {
	public RingGraphicTool(int arc) {
		super(arc);
		SimpleRing simpleRing = new SimpleRing(new Rectangle(0,0,15,15));
		model=simpleRing;
		simpleRing.arc=arc;
		
		getModel().setStrokeColor(Color.black);
		super.set=TreeIconWrappingToolIcon.createIconSet(getModel());
		
	}

	{model=new SimpleRing(new Rectangle(0,0,15,15));}{getModel().setStrokeColor(Color.black);{super.set=TreeIconWrappingToolIcon.createIconSet(getModel());}}
	
	public RectangularGraphic createShape(Rectangle r) {
		return new  SimpleRing(r, isArc);
	}
	
	/**returns the name of the model shape */
	public String getShapeName() {
		return "Ring";
	}
	
	
}
