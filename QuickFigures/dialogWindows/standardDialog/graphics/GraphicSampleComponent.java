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
package standardDialog.graphics;

import java.awt.Dimension;

import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects.ZoomableGraphic;
import locatedObject.LocatedObject2D;

/**A component that shows a single graphic
 * @see ZoomableGraphic*/
public class GraphicSampleComponent extends GraphicComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ZoomableGraphic z;
	int x=0;
	int y=0;
	
	public GraphicSampleComponent(ZoomableGraphic z) {
		super.getGraphicLayers().add(z);
		this.z=z;
	}
	
	public LocatedObject2D zLoc() {
		if (z instanceof LocatedObject2D) {
			return (LocatedObject2D) z;
		}
		return null;
	}
	
	public BasicCoordinateConverter getCord() {
	
		BasicCoordinateConverter bcc = new BasicCoordinateConverter();
		bcc.setMagnification(1);
		if (zLoc()!=null) {
			bcc.setX(zLoc().getLocationUpperLeft().getX());
			bcc.setY(zLoc().getLocationUpperLeft().getY());
		}
		
		cords=bcc;
		
		return cords;
	}
	
	@Override
	public Dimension getPreferredSize() {
		if (zLoc()!=null)  {
			return new Dimension(zLoc().getBounds().width*2, zLoc().getBounds().height*2);
		}
        return new Dimension(100,100);
    }
	
	
	
}
