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
package graphicalObjects_BasicShapes;

import java.io.Serializable;

import undo.SimpleTraits;


/**keeps information about an angle, used by the angle handle and certain shapes*/
public class AngleParameter implements Serializable, SimpleTraits<AngleParameter> {
	
	/**
	 * 
	 */
	public static final int ANGLE_TYPE=0, RADIUS_TYPE=1, ANGLE_AND_RADIUS_TYPE=2, ANGLE_RATIO_TYPE=3, ANGLE_RATIO_AND_RAD_TYPE=4;
	private int type=0;
	
	private static final long serialVersionUID = 1L;
	private double angle=0;
	private double ratioToMaxRadius=0.75;
	private double ratioToStandardAngle=0;
	public AngleParameter[] attached;
	

	public AngleParameter(RectangularGraphic ovalGraphic) {
		// TODO Auto-generated constructor stub
	}
	
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
	
	
	double inDegrees() {
		return getAngle()*180/Math.PI;
	}
	public double getRatioToMaxRadius() {
		return ratioToMaxRadius;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public void setRatioToMaxRadius(double ratioToMaxRadius) {
		this.ratioToMaxRadius = ratioToMaxRadius;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public AngleParameter self() {
		return this;
	}

	public double getRatioToStandardAngle() {
		return ratioToStandardAngle;
	}

	public void setRatioToStandardAngle(double ratioToStandardAngle) {
		this.ratioToStandardAngle = ratioToStandardAngle;
	}

	public void setAttachedParameters(AngleParameter... attached) {
		this.attached=attached;
		
	}
	
	
	public void increaseRadiusRatio(double d) {
		this.ratioToMaxRadius +=d;
	}
	
	public void increaseAngleRatio(double d) {
		this.ratioToStandardAngle +=d;
	}
	
	public void increaseAngle(double d) {
		this.angle +=d;
	}
	
}


