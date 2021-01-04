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
package plotParts.DataShowingParts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import dataSeries.DataSeries;
import graphicalObjects.CordinateConverter;

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
	public Shape getOutline2() {
		 return	new BasicStroke(5).createStrokedShape(this.getShape());

		}

	public void drawHandesSelection(Graphics2D g2d, CordinateConverter cords) {
		super.drawHandesSelection(g2d, cords);
		if (this.isSelected()) {
			g2d.setColor(new Color(0, 0, 0, 50));
			g2d.fill(cords.getAffineTransform().createTransformedShape(getOutline2()));
		}
	}

}
