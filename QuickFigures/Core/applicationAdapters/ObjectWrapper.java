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
package applicationAdapters;

import java.awt.Point;

import utilityClassesForObjects.DrawnGraphic;
import utilityClassesForObjects.LocatedObject2D;

public interface ObjectWrapper<Roi> extends LocatedObject2D, DrawnGraphic{

	
	/**returns the wrapped object*/
	//public Roi getObject();
	
	/**sets the wrapped object*/
	//public void setWrappedObject(Roi roi);
	
	/**returns the points along a line or polygon outline*/
	//public Point getPoint(int ind);

}
