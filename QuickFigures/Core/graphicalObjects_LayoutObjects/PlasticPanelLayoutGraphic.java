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
 * Version: 2023.2
 */
package graphicalObjects_LayoutObjects;

import layout.plasticPanels.PlasticPanelLayout;
import locatedObject.LocatedObject2D;
import menuUtil.PopupMenuSupplier;
import popupMenusForComplexObjects.PlasticPanelLayoutPanelMenu;

/**A layout consisting of panels that can be set to arbitrary sizes
 * and aligned for even spacing*/
public class PlasticPanelLayoutGraphic extends SpacedPanelLayoutGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	{layout=new PlasticPanelLayout(3);}

	
	public PlasticPanelLayout getPanelLayout() {
		if (this.layout instanceof PlasticPanelLayout) return (PlasticPanelLayout) this.layout;
	return null;	
	}
	
	public PopupMenuSupplier getMenuSupplier(){
		
		return new  PlasticPanelLayoutPanelMenu(this);
	}
	
	 void resizePanelsToFit(LocatedObject2D l) {
		 	Integer loc = this.getPanelLocations().get(l);
		this.getPanelLayout().setPanelWidth(loc, l.getBounds().width);
		this.getPanelLayout().setPanelHeight(loc, l.getBounds().height);
		this.repack();
	}
	

}
