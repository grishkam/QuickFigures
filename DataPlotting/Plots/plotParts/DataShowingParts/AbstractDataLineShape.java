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
 * Date Modified: Mar 28, 2021
 * Version: 2022.0
 */
package plotParts.DataShowingParts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import dataSeries.DataSeries;
import graphicalObjects.CordinateConverter;
import graphicalObjects_Shapes.PathGraphic;

/**A shape of a line that is to be drawn on plots, sublcasses include regression lines, kaplan meier plot lines
 * and ordinary connecting lines*/
public abstract class AbstractDataLineShape extends DataShowingShape implements DataLineShape {

	public AbstractDataLineShape(DataSeries data) {
		super(data);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**returns the area that this item takes up for 
	  receiving user clicks*/
	@Override
	public Shape getOutline() {
	 return	new BasicStroke(3).createStrokedShape(this.getShape());

	}
	private Shape getSelectedOutline() {
		 return	new BasicStroke(5).createStrokedShape(this.getShape());

		}

	/**dras a special line appearance when selected*/
	@Override
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter cords) {
		super.drawHandesSelection(g2d, cords);
		if (this.isSelected()) {
			g2d.setColor(new Color(0, 0, 0, 50));
			g2d.fill(cords.getAffineTransform().createTransformedShape(getSelectedOutline()));
		}
	}
	
	
	/**returns a pathGraphic that looks just like this shape
	 * @see PathGraphic*/
	public PathGraphic createPathCopy() {
		PathGraphic oo = super.createPathCopy();
		oo.setClosedShape(false);
		return oo;
	}
	

}
