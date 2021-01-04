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
 * Version: 2021.1
 */
package locatedObject;

import java.util.ArrayList;

/**a container of located objects*/
public interface ObjectContainer {
	
	/**removes an objects from the image*/
	public void takeFromImage(LocatedObject2D roi) ;
	/**adds an objects to the image*/
	public void addItemToImage(LocatedObject2D roi) ;
	
	/**adds an object to the collection but behind the other objects rather than the front*/
	public void addRoiToImageBack(LocatedObject2D roi) ;
	
	/**returns all the objects in the image*/
	public ArrayList<LocatedObject2D> getLocatedObjects();
	
	/**if a single specific object is the primary selection*/
	public LocatedObject2D getSelectionObject();
	

}
