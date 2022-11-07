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
 * Version: 2022.2
 */
package plotParts.Core;

import java.awt.geom.Point2D;

import genericPlot.BasicPlot;
import plotParts.DataShowingParts.PlotLabel;

/**A plot label for an axis*/
public class AxisLabel extends PlotLabel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AxisLabel(String name) {
		super(name);
		
	}

	public AxisLabel(String string, BasicPlot basicPlot) {
		super(string, basicPlot);
	}

	@Override
	public void scaleAbout(Point2D p, double mag) {
		double h=this.getAttachmentPosition().getHorizontalOffset();
		double v=this.getAttachmentPosition().getVerticalOffset();
		super.scaleAbout(p, mag);
		
		getAttachmentPosition().setHorizontalOffset( Math.round(h*mag));
		getAttachmentPosition().setVerticalOffset( Math.round(v*mag));
		this.putIntoAnchorPosition();
		
	}
}
