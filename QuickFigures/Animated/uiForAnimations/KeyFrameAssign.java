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
package uiForAnimations;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Area;

import javax.swing.Icon;
import animations.KeyFrameCompatible;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import standardDialog.graphics.GraphicDisplayComponent;

/**A menu item for creating key frames within the time line*/
public class KeyFrameAssign extends BasicTimeLineOperator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean update=false;
		
	public KeyFrameAssign(boolean update) {this.update=update;}

	@Override
	public void run() {
		for(ZoomableGraphic item: array) {
			if (item==null) continue;
			actioinOnSelected(item);
			
		}
		
	}
	
	@Override
	public String getMenuCommand() {
		if (update)return "Update Key Frame";
		return "Make Key Frame";
	}
	
	/**adds a key frame or updates an existing key frame of a given object*/
	public void actioinOnSelected(ZoomableGraphic selectedItem) {
		
		
		if (selectedItem instanceof KeyFrameCompatible ) {
			KeyFrameCompatible  m=(KeyFrameCompatible ) selectedItem;
			int frame = new CurrentFigureSet().getCurrentlyActiveDisplay().getCurrentFrame();
			
			if (update)m.getOrCreateAnimation().updateKeyFrame(frame); else
			m.getOrCreateAnimation().recordKeyFrame(frame);
		}
		
		
		
		
	}
	
	/**Creates a cartoon that will be used for an icon */
	static ShapeGraphic createCartoonForIcon(boolean selected) {
		Point p1=new Point(2,-3);
		Point p2=new Point(17,15);
		
			ArrowGraphic ag1 =ArrowGraphic.createDefaltOutlineArrow(Color.black, Color.black);
			ag1.setStrokeWidth(4);
			if (selected) ag1.getBackGroundShape().setFillColor(Color.red);
			ag1.setPoints(p1, p2);
			ag1.setNumerOfHeads(0);
			
			ArrowGraphic ag2 = ag1.copy();
			p1=new Point(17,2);
			p2=new Point(10,10);
			ag2.setPoints(p1, p2);
			
			ArrowGraphic ag3 = ag2.copy();
			ag3.moveLocation(2, 8); ag3.moveLocation(3, -3);
			
			Area s= ag2.getDrawOutline();
			s.add(ag1.getDrawOutline()); s.add(ag3.getDrawOutline());
			BasicShapeGraphic output = new BasicShapeGraphic(s);
			output.copyAttributesFrom(ag2);
			output.setStrokeColor(Color.black);
			output.setFillColor(Color.yellow);
			output.setStrokeWidth(2);
			output.setFilled(true);
			output.setAntialize(true);
			return output;
	}
	
	public GraphicDisplayComponent getTheIcon(boolean selected) {
		 GraphicDisplayComponent output = new GraphicDisplayComponent(createCartoonForIcon( selected));	 
		 output.setRelocatedForIcon(false);
		 return output;
	}
	
	
	public Icon getIcon() {
		return  getTheIcon(true);
	}

}
