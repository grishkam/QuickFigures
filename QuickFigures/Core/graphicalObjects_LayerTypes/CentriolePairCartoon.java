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
 * Date Modified: Jan 4, 2021
 * Version: 2022.1
 */
package graphicalObjects_LayerTypes;

import java.awt.Color;

import graphicalObjects_Shapes.RectangularGraphic;

/**A group of two rectangles to depict a centriole pair*/
public class CentriolePairCartoon extends GraphicGroup {

	/**
	 * 
	 */
	
	public CentriolePairCartoon() {
		generateInnitialCentrioles();
	}
	
	private static final long serialVersionUID = 1L;
	
	RectangularGraphic centriole1;
	RectangularGraphic centriole2;
	
	/**generates the rectangular objects*/
	public void generateInnitialCentrioles() {
		centriole1=new RectangularGraphic(0,0,10,25);
		centriole2=new RectangularGraphic(20,0,25,10);
		getTheInternalLayer().add(centriole1);
		getTheInternalLayer().add(centriole2);
		setPropertiesInitialOfCentriole(centriole1);
		setPropertiesInitialOfCentriole(centriole2);
		centriole2.setAngle(Math.PI/8);
		
	}
	
	/**Sets the properties of the rectangle*/
	private void setPropertiesInitialOfCentriole(RectangularGraphic centriole1) {
		centriole1.setStrokeWidth(1);
		centriole1.makeNearlyDashLess();
		centriole1.setFillColor(Color.black);
		centriole1.setFilled(true);
		centriole1.setStrokeColor(Color.DARK_GRAY);
		centriole1.setAntialize(true);
	}


}
