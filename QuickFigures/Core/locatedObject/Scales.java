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
 * Date Modified: Jan 4, 2021
 * Version: 2022.0
 */
package locatedObject;

import java.awt.geom.Point2D;

/**Interface for objects that can be scaled abount a point*/
public interface Scales {

	/**scales the shape about point p. the fold scaling is determined by mag*/
	public void scaleAbout(Point2D p, double mag);
	
	/**returns a group of messages regareding how scale operates for the given object*/
	public Object getScaleWarning();
	
}
