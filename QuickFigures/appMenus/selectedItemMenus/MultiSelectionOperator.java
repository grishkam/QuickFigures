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
package selectedItemMenus;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;


import graphicalObjects.ZoomableGraphic;

/**Implementations of this interface perform some task */
public interface MultiSelectionOperator extends Serializable, MenuItemInstall {
	
	public static final int ICON_SIZE = 25;
	public static final Color TRANSPARENT_COLOR = new Color(0,0,0,0);
	
	/**performs the action.*/
	public void run();
	
	/**Sets the layer selector for this operation*/
	public void setSelector(LayerSelector graphicTreeUI);//sets how selection works
	/**Sets a list of selected items. Note: some implementations of this interface will 
	  call this method themselves*/
	public void setSelection(ArrayList<ZoomableGraphic> array) ;


	/**returns true if any of the selected objects are valid targets*/
	public boolean canUseObjects(LayerSelector graphicTreeUI);
	
	/**returns true if the type of layer selector is compativle with this object
	  */
	public boolean isValidForLayerSelector(LayerSelector graphicTreeUI);
	
	/**Some objects of this class also return a component that provides another way
	  access it. The layer selector of this item must be set before using this*/
	public Component getInputPanel() ;
}
