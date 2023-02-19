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
 * Date Modified: Jan 6, 2021
 * Version: 2023.1
 */
package addObjectMenus;

import java.awt.Font;

import javax.swing.Icon;

import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.UndoAddItem;

/**A simple implementation of the GraphicAdder interface (@see GraphicAdder).
 subclasses determine which options appear in the adding menu (@see ObjectAddingMenu)
  add a variety o*/
public abstract class BasicGraphicAdder extends BasicMultiSelectionOperator implements GraphicAdder {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected int unique=(int)Math.random()*1000;
	

	/**finds any image panels that are selected, adds the object to those panels
	 * TODO: modify this to that it identifies a selected image panel even if objects other than image panels are selected*/
	public void addLockedItemToSelectedImage(LocatedObject2D ag) {
		
		try {
			
			if (this.selector.getSelecteditems().size() > 0) {
				ZoomableGraphic item = selector.getSelecteditems().get(0);
				if (item instanceof ImagePanelGraphic) {
					ImagePanelGraphic it = (ImagePanelGraphic) item;
					it.addLockedItem(ag);
				}
			}
		} catch (Throwable e) {
			IssueLog.logT(e);
		}
	}
	
	public Character getKey() {
		return null;
	}
	
	
	protected double getScaleToIcon() {
		return 0.3;
	}
	public Icon getIcon() {
		BasicGraphicalObject m = getModelForIcon();
		if (m==null)return null;
		return new GraphicDisplayComponent(getModelForIcon(), getScaleToIcon());
	}



	protected BasicGraphicalObject getModelForIcon() {
		return null;
	}

	@Override
	public String getMenuPath() {
		return null;
	}

	@Override
	public Font getMenuItemFont() {
		return null;
	}
	
	/**performs the action. Subclasses need only implement the add method*/
	public void run() {
		GraphicLayer l = null;
		if(selector!=null &&selector.getSelectedLayer()!=null)l=selector.getSelectedLayer();
	
		if(l==null)
			l=this.selector.getWorksheet().getTopLevelLayer();
		
		ZoomableGraphic item = this.add(l);
		
		if(item!=null) {
			this.getUndoManager().addEdit(new UndoAddItem(l, item));
		}
	}

	

}
