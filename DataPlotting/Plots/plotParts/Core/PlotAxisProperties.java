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
 * Date Modified: Jan 7, 2021
 * Version: 2022.1
 */
package plotParts.Core;

import java.io.Serializable;

/**stores the propereis of a plot axis*/
public class PlotAxisProperties implements Serializable {
	
	/**
	 * 
	 */
	static final int LOG_SCALE=1, NORMAL_SCALE=0;
	private static final long serialVersionUID = 1L;
	
	private int majorTic=25;
	private int minorTic=5;
	int scaleType=NORMAL_SCALE;
	private Gap gap=new Gap(0,0);
	
	private double minValue=0;
	private double maxValue=100;
	private boolean vertical=true;
	
	PlotAxisProperties copy(){
		PlotAxisProperties pa = new PlotAxisProperties();
		pa.copyFieldsFrom(this);
		return pa;
	}
	
	public void copyFieldsFrom(PlotAxisProperties pa) {
		this.maxValue=pa.maxValue;
		this.minValue=pa.minValue;
		this.vertical=pa.vertical;
		this.scaleType=pa.scaleType;
		this.majorTic=pa.majorTic;
		this.minorTic=pa.minorTic;
		gap.copyFrom(pa.getGap());
	}
	
	public String toString() {
		return "From "+minValue+" to "+maxValue+";  Major tics at "+
					majorTic+ "minor tics at "+minorTic;}
	
	/**returns the distance between the minimum and maximum values of the axis*/
	public double getRange() {
		return getMaxValue()- getMinValue();
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public int getMajorTic() {
		return majorTic;
	}

	public void setMajorTic(int majorTic) {
		this.majorTic = majorTic;
	}

	/**returns the minor tic spacing, cannot be 0 (infinite loop issue), returns 1 if set to 0*/
	public int getMinorTic() {
		if (minorTic==0) return 1;
		
		return minorTic;
	}

	public void setMinorTic(int minorTic) {
		this.minorTic = minorTic;
	}

	public boolean isVertical() {
		return vertical;
	}

	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}

	public Gap getGap() {
		return gap;
	}

	public void setGap(Gap gap) {
		this.gap = gap;
	}
	
	/**returns true if the axis is on a log scale*/
	public boolean usesLogScale() {
		return this.scaleType==LOG_SCALE;
	}


}
