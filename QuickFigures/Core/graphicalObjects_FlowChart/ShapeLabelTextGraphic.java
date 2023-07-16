/*******************************************************************************
 * Copyright (c) 2023 Gregory Mazo
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
 * Date Created: May 24, 2023
 * Date Modified: July 9, 2023
 * Version: 2023.2
 */
package graphicalObjects_FlowChart;

import java.awt.geom.Point2D;

import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import handles.ItemGlueSmartHandle;
import locatedObject.RectangleEdges;

/**
 A label that stays attached to a parent shape
 */
public class ShapeLabelTextGraphic extends ComplexTextGraphic {

	
	/**
	 * @param name
	 */
	public ShapeLabelTextGraphic(String name) {
		super(name);
	}
	
	/**makes sure this is located at the center of the parent shape*/
	public static void updateLocation(ShapeGraphic parentShape, ShapeLabelTextGraphic textItem) {
		if(parentShape==null)
			return;
		if(textItem==null)
			return;
		
		textItem.setLocationType(RectangleEdges.CENTER);
		Point2D p = parentShape.getCenterOfRotation();
		textItem.setLocation(p);
	}
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**item will always be attached to one shape*/
	public ItemGlueSmartHandle getGlueHandle() {return null;}
	
}
