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
 * Version: 2022.2
 */
package graphicalObjects_LayerTypes;


/**Interphase to be called when some multilayer model 
  has on object move between layers*/
public interface LayerStructureChangeListener<Item, Layer extends Item> {
	/**called when an item is added to a graphic container*/
	public void itemsSwappedInContainer( Layer gc, Item z1, Item z2 );
	/**called when an item is added to a graphic container*/
	public void itemRemovedFromContainer( Layer gc, Item z);
	/**called when an item is added to a graphic container*/
	public void itemAddedToContainer( Layer gc, Item z) ;
	
	/***/
	public Layer getSelectedLayer();
	

}
