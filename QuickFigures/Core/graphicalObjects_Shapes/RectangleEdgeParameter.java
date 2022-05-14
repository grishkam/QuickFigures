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
 * Date Modified: Jan 5, 2021
 * Version: 2022.1
 */
package graphicalObjects_Shapes;

import java.io.Serializable;

import locatedObject.RectangleEdgePositions;
import undo.SimpleTraits;


/**keeps information about a length or ratio, used by certain handles and certain shapes.
 * */
public class RectangleEdgeParameter implements Serializable, RectangleEdgePositions, SimpleTraits<RectangleEdgeParameter> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double length=0;
	private double ratioToMax=0.75;
	
	/**The location codes for the 0 and max length locations on a rectangle.
	  these determine where the handle is drawn and how far it can be moved*/
	public int zeroLocation=UPPER_LEFT;
	public int maxLengthLocation=TOP;

	public RectangleEdgeParameter(RectangularGraphic ovalGraphic) {
		
	}
	
	public RectangleEdgeParameter(RectangularGraphic ovalGraphic, double length, int zero, int max) {
		this(ovalGraphic);
		this.length=length;
		this.maxLengthLocation=max;
		this.zeroLocation=zero;
	}
	
	public RectangleEdgeParameter copy(RectangularGraphic ovalGraphic) {
		RectangleEdgeParameter out = new RectangleEdgeParameter(ovalGraphic, length, zeroLocation, maxLengthLocation);
		this.giveTraitsTo(out);
		return out;
	}

	public double getRatioToMaxLength() {
		return ratioToMax;
	}
	public void setRatioToMaxLength(double a) {
		ratioToMax= a;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double angle) {
		this.length = angle;
	}

	@Override
	public SimpleTraits<RectangleEdgeParameter> copy() {
		return copy(null);
	}

	@Override
	public void giveTraitsTo(RectangleEdgeParameter t) {
		t.length=length;
		t.ratioToMax=ratioToMax;
		
	}

	@Override
	public RectangleEdgeParameter self() {
		return this;
	}

	/**copies the values from one paramter to the other
	 * @param theParameter
	 */
	public void copyValuesFrom(RectangleEdgeParameter theParameter) {
		this.length=theParameter.getLength();
		this.ratioToMax=theParameter.getRatioToMaxLength();
		
	}
	
	
}


