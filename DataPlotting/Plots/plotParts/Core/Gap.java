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
 * Version: 2023.1
 */
package plotParts.Core;

import java.io.Serializable;

/**The axes of some plots skip over a particular range
 * this class defines the properties of a 'gap' in the plot.
 * */
public class Gap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**where does the gap start*/
	double start;
	
	/**the distance from the start of the gap to the end*/
	double size;
	
	/**the size of the marker for the gap*/
	double markerTicWidth=14;
	double gapMarkerHeight=4;
	
	public Gap copy() {
		Gap g = new Gap(start, size);
		g.copyFrom(this);
		return g;
	}
	
	/**copies the properties of this gap*/
	public void copyFrom(Gap gap) {
		start=gap.start;
		size=gap.size;
		markerTicWidth=gap.markerTicWidth;
		gapMarkerHeight=gap.gapMarkerHeight;
	}

	public Gap(double start, double size) {
		this.start=start;
		this.size=size;
	}
	
	public boolean isInside(double value) {
		if (value>start&&value<start+size) return true;
		return false;
	} 
	
	public boolean isAfter(double value) {
		if (value>=start+size) return true;
		return false;
	}

	/**returns the start position for the gap*/
	public double location() {
		return start;
	} 
	
	/**sets the start position for the gap*/
	public void setLocation(double s) {
		start=s;
	}

	/**return the distance that is skipped over*/
	public double getSize() {
		return size;
	}

	/**sets what distance is skipped over*/
	public void setSize(double size) {
		this.size = size;
	}
	
	
	
}
