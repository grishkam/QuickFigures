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
 * Date Created: Nov 28, 2021
 * Date Modified: Nov 28, 2021
 * Version: 2023.1
 */
package graphicalObjects_Shapes;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import illustratorScripts.ArtLayerRef;
import locatedObject.PathPointList;

/**
 An arbitrary shape that can be edited
 */
public class FreeformShape extends BasicShapeGraphic {




		/**
	 * @param shape2
	 */
	public FreeformShape(Shape shape2) {
		super(shape2);
		
	}

		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**moves the shape*/
		public void moveLocation(double dx, double dy) {//added so user will be able to move this shape
			setShape(AffineTransform.getTranslateInstance(dx, dy).createTransformedShape(getShape()));
		}
	
		/**overrides the superclass because some shapes wont export*/
		@Override
		public Object toIllustrator(ArtLayerRef aref) {
			if(hasQuads(getShape()));
				setShape(PathPointList.convertToCubicShape(getShape()));
			return super.toIllustrator(aref);
		}
		
		/**returns true if the shape has quad curves*/
		public static boolean hasQuads(Shape p2) {
			PathIterator iterator = p2.getPathIterator(AffineTransform.getTranslateInstance(0, 0));
			while(!iterator.isDone()) {
				double[] points = new double[6];
				int type = iterator.currentSegment(points);
				if(type==PathIterator.SEG_QUADTO)
					return true;
				iterator.next();
			}
			
			return false;
		}

}
