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
 * Date Modified: Jan 6, 2021
 * Version: 2022.1
 */
package dataSeries;

/**class stores information about a particular 1D data point*/
public class BasicDataPoint implements DataPoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double position;
	private double value;
	private boolean excluded=false;

	public BasicDataPoint(double position, double value) {
		this.position=position;
		this.value=value;
	}
	
	@Override
	public double getPosition() {
		return position;
	}

	@Override
	public double getValue() {
		return value;
	}
	
	@Override
	public String getValueString() {
		return value+(this.isExcluded()? "*": "");
	}

	@Override
	public boolean isExcluded() {
		return this.excluded;
	}

	public void setExcluded(boolean excluded) {
		this.excluded = excluded;
	}
	
	public String toString() {
		return "("+position+", "+value+")";
	}

}
