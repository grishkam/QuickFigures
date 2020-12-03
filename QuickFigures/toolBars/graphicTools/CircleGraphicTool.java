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
import graphicalObjects_BasicShapes.CircularGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;

/**draws a circular graphic. Either an oval or a*/
public class CircleGraphicTool extends RectGraphicTool {


	{model=new CircularGraphic(new Rectangle(0,0,15,15));}
	{getModel().setStrokeColor(Color.black);{super.set=TreeIconWrappingToolIcon.createIconSet(getModel());}}
	int isArc=CircularGraphic.NO_ARC;
	
	
	public CircleGraphicTool() {this(0);}
	public CircleGraphicTool(int  arc) {
		
		isArc=arc;
		CircularGraphic mCircle = new CircularGraphic(new Rectangle(0,0,15,15), arc);;
	
		model=mCircle;
		getModel().setStrokeColor(Color.black);
		super.set=TreeIconWrappingToolIcon.createIconSet(getModel());
		
	}
	public RectangularGraphic createShape(Rectangle r) {
		CircularGraphic ovalGraphic = new CircularGraphic(r, isArc);

		return ovalGraphic;
	}
	
	
	/**returns the name of the model shape (default is rectangle)*/
	public String getShapeName() {
		if (isArc>0) return "Arc";
		return "Oval";
	}
}
