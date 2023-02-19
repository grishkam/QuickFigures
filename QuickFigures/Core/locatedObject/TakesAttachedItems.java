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
 * Version: 2023.1
 */
package locatedObject;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import handles.HasSmartHandles;

/**Objects that serve as attachments sites for other items implements this
 * interface*/
public interface TakesAttachedItems extends Mortal, Selectable, HasSmartHandles {
	public void addLockedItem(LocatedObject2D l) ;
	public void removeLockedItem(LocatedObject2D l) ;
	
	
	public void snapLockedItems() ;
	public void snapLockedItem(LocatedObject2D l) ;
	
	public boolean hasLockedItem(LocatedObject2D l);
	public AttachedItemList getLockedItems();
	public ArrayList<LocatedObject2D> getNonLockedItems();
	public Rectangle getBounds();
	public Rectangle2D getContainerForBounds(LocatedObject2D l);
	
	public ObjectContainer getTopLevelContainer();

}
