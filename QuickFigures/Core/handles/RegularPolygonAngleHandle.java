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
 * Date Modified: Mar 5, 2023
 * Version: 2023.2
 */
package handles;

import java.awt.Color;

import graphicalObjects_Shapes.AngleParameter;
import graphicalObjects_Shapes.RegularPolygonGraphic;

/**S special angle handle for polygons and starts*/
public class RegularPolygonAngleHandle extends  AngleHandle {
	public RegularPolygonGraphic polygon;
	
	private int pointNumber=1;
	
	public RegularPolygonAngleHandle(RegularPolygonGraphic r, AngleParameter angle, Color c, double startAngle,
			int handleNumber, int point) {
		super(r, angle, c, startAngle, handleNumber);
		polygon=r;
		 pointNumber=point;
		 this.setType(angle.getType());
		
	}
	

	public double getHandleDrawAngle() {
		return getPointNumber()*polygon.getIntervalAngle();
	}
	
	public double getStandardAngle() {
		return polygon.getIntervalAngle();
	}

	public int getPointNumber() {
		return pointNumber;
	}



	public void setPointNumber(int pointNumber) {
		this.pointNumber = pointNumber;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	}