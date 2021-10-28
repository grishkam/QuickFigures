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
 * Version: 2021.2
 */
package plotParts.Core;

import java.awt.Rectangle;
import java.io.Serializable;

/**A defines the key features of a plot area*/
public interface PlotArea extends Serializable {
	public Rectangle getPlotArea() ;
	
	PlotCordinateHandler getCordinateHandler();
	PlotCordinateHandler getCordinateHandler(int i);
	
	/**returns the independent variable axis*/
	public PlotAxisProperties getXaxis();
	
	/**returns the dependant variable axis*/
	public PlotAxisProperties getYaxis();
	
	/**returns an alternative dependant variable axis*/
	public PlotAxisProperties getSecondaryYaxis();
	
	/**called to reset the range of the plot axis*/
	public void autoCalculateAxisRanges();
	
	/**methods to update the plot in response to changes in the data and format*/
	public void onAxisUpdate();
	public void fullPlotUpdate();

	/**sets the size of the plot area*/
	//public void setAreaDims(double number, double number2);
	
	/**returns the plot orientation as either vertical or horizontal*/
	public PlotOrientation getOrientation();
	
	/**moves the plot a certain distance*/
	public boolean moveEntirePlot(double dx, double dy);

	/**
	 * @param area
	 */
	public void setPlotArea(Rectangle area);
}
