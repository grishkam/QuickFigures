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
 * Version: 2021.2
 */
package dataSeries;

import java.io.Serializable;
import java.util.HashMap;

/**objects that contain the data for plots implement
 * this interface. */
public interface DataSeries extends Serializable {

	public Basic1DDataSeries getIncludedValues();
	
	/**If multiple data points are associated with a given position, 
	  returns a basic data series*/
	public DataSeries getValuesForPosition(double position);
	
	/**how many points are included in the data series*/
	public int length();
	
	/**returns the position or value for point i.
	  Note: the points will be in no particular order*/
	public double getValue(int i);
	public double getPosition(int i);
	public DataPoint getDataPoint(int i);
	
	/**returns an array with each position appearing only once*/
	public double[] getAllPositions() ;
	public double[] getAllPositionsInOrder() ;
	public String getName();
	public void setName(String name);
	
	
	/**Slight shifts in position are needed in a few kinds of plots*/
	public double getPositionOffset();
	public void setPositionOffset(double o);
	
	public HashMap<Double, Double> getValueOffsetMap();

	 
}
