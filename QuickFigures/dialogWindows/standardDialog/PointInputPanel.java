/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package standardDialog;

import java.awt.geom.Point2D;

public class PointInputPanel extends NumberArrayInputPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public PointInputPanel(String name, Point2D start) {
		super(2,2);
		this.setLabel(name);
		this.setArray(new float[] {(float) start.getX(), (float) start.getY()});
	}
	
	public Point2D getPoint() {
		return new Point2D.Double(getArray()[0], getArray()[1]);
	}
}


