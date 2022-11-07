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
 * Version: 2022.2
 */
package graphicalObjects_Shapes;

import java.io.Serializable;

import undo.SimpleTraits;


/**stores information about a parameter value that is defined relative to the center of rotation of a rectangular object
 The value may depend on an angle, a radius, a ratio or some combination
 * , used by the angle handle and certain shapes*/
public class AngleParameter implements Serializable, SimpleTraits<AngleParameter> {
	
	/**
	 * 
	 */
	public static final int ANGLE_TYPE=0, RADIUS_TYPE=1, ANGLE_AND_RADIUS_TYPE=2, ANGLE_RATIO_TYPE=3, ANGLE_RATIO_AND_RAD_TYPE=4;
	private int type=ANGLE_TYPE;
	
	private static final long serialVersionUID = 1L;
	private double angle=0;
	private double ratioToMaxRadius=0.75;
	private double ratioToStandardAngle=0;
	public AngleParameter[] attached;
	

	public AngleParameter(RectangularGraphic ovalGraphic) {
	}
	
	/**creates  a copy*/
	public AngleParameter copy() {
		AngleParameter output = new AngleParameter(null);
		giveTraitsTo(output);
		return output;
	}
	
	
	public void giveTraitsTo(AngleParameter output) {
		output.angle=angle;
		output.ratioToMaxRadius= ratioToMaxRadius;
		output.ratioToStandardAngle=ratioToStandardAngle;
		output.type=type;
	}
	
	/**returns the angle in degrees*/
	double inDegrees() {
		return getAngle()*180/Math.PI;
	}
	/**returns the angle*/
	public double getAngle() {
		return angle;
	}
	/**Sets the angle*/
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	/**returns the radius ratio*/
	public double getRatioToMaxRadius() {
		return ratioToMaxRadius;
	}
	
	/**Sets the radius ratio*/
	public void setRatioToMaxRadius(double ratioToMaxRadius) {
		this.ratioToMaxRadius = ratioToMaxRadius;
	}
	
	/**returns a code indicating the type of value stored in this parameter*/
	public int getType() {
		return type;
	}
	/**sets the type of value stored in this parameter*/
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public AngleParameter self() {
		return this;
	}

	/**returns the stored angle ratio*/
	public double getRatioToStandardAngle() {
		return ratioToStandardAngle;
	}

	/**sets the stored angle ratio*/
	public void setRatioToStandardAngle(double ratioToStandardAngle) {
		this.ratioToStandardAngle = ratioToStandardAngle;
	}

	public void setAttachedParameters(AngleParameter... attached) {
		this.attached=attached;
		
	}
	
	/**Adds a particular value to the radius ratio*/
	public void increaseRadiusRatio(double d) {
		this.ratioToMaxRadius +=d;
	}
	
	/**Adds a particular value to the angle ratio*/
	public void increaseAngleRatio(double d) {
		this.ratioToStandardAngle +=d;
	}
	
	public void increaseAngle(double d) {
		this.angle +=d;
	}
	
}


