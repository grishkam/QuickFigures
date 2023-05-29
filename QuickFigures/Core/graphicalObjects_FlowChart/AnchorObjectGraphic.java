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
 * Date Modified: May 26, 2023
 * Version: 2023.2
 */
package graphicalObjects_FlowChart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import graphicalObjects.CordinateConverter;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import handles.SmartHandleList;
import locatedObject.PathPoint;

/**
 WORK IN PROGRESS, a line that is anchored at either end by a shape
 */
public class AnchorObjectGraphic extends PathGraphic {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	
	public AnchorObjectGraphic() {
		super(new Point(2,2), new Point(3,3));
		this.setStrokeColor(Color.black);
	}
	
	public AnchorObjectGraphic(ChartNexus s, ChartNexus s2, Point2D point) {
		this();
		
		
		AnchorAttachment aa = new AnchorAttachment(0, this, s, s.getNearestAttachmentPointIndex(point));
		s.addAttachment(aa);
		
		
		AnchorAttachment aa2 = new AnchorAttachment(1, this, s2, s2.getNearestAttachmentPointIndex(point));
		s2.addAttachment(aa2);
	}
	
	
	
	
	
	/**returns the handles for the points in the path */
	public synchronized SmartHandleList getPointHandles() {
		if (smartHandleBoxes==null) {
			setSmartHandleBoxes(AnchoredHandle.getPathSmartHandles(this));
		}
		return smartHandleBoxes;
	}
	
	
	

	
	

}
