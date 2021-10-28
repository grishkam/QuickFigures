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
 * Version: 2021.2
 */
package locatedObject;

import java.util.ArrayList;

/**A super interface for layers*/
public interface ItemLayer<GraphicItemType> {
	
	public boolean hasItemWithKey(Object key) ;
	public GraphicItemType getItemWithKey(Object key) ;
	public void swapItemPositions(GraphicItemType z1, GraphicItemType z2);
	
	
	public void swapmoveObjectPositionsInArray(GraphicItemType z1, GraphicItemType z2);
	
	
	
	
	/**returns all the graphics. Regardless of whether they are in sublayers of not*/
	public ArrayList<GraphicItemType> getAllGraphics();
	
	/**returns an array of all the graphics*/
	public ArrayList<GraphicItemType> getItemArray();
	
	/**returns all the graphics that have a specified tag*/
	public ArrayList<GraphicItemType> getGraphics(Object... key);
	
	/**add a graphic*/
	public void add(GraphicItemType z);
	/**removes a graphic*/
	public void remove(GraphicItemType z);
	
	/**returns true if this item can accept the given graphci*/
	public boolean canAccept(GraphicItemType z);
	
	/**returns true is the item is either in this container or a sub-container*/
	public boolean hasItem(GraphicItemType z) ;
	
	

	

}
