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
package utilityClassesForObjects;

import java.util.ArrayList;

public interface ItemLayer<graphicItem> {
	
	public boolean hasItemWithKey(Object key) ;
	public graphicItem getItemWithKey(Object key) ;
	public void swapItemPositions(graphicItem z1, graphicItem z2);
	
	
	public void swapmoveObjectPositionsInArray(graphicItem z1, graphicItem z2);
	
	
	
	
	/**returns all the graphics. Regardless of whether they are in sublayers of not*/
	public ArrayList<graphicItem> getAllGraphics();
	
	/**returns an array of all the graphics*/
	public ArrayList<graphicItem> getItemArray();
	
	/**returns all the graphics that have a specified tag*/
	public ArrayList<graphicItem> getGraphics(Object... key);
	
	/**add a graphic*/
	public void add(graphicItem z);
	/**removes a graphic*/
	public void remove(graphicItem z);
	
	/**returns true if this item can accept the given graphci*/
	public boolean canAccept(graphicItem z);
	
	/**returns true is the item is either in this container or a sub-container*/
	public boolean hasItem(graphicItem z) ;
	
	

	

}
