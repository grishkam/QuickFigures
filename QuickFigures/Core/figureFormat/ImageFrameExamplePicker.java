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
 * Date Created: April 24, 2022
 * Date Modified: April 24, 2022
 * Version: 2023.2
 */
package figureFormat;

import javax.swing.undo.AbstractUndoableEdit;

import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.FigureType;
import graphicalObjects.KnowsParentLayer;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;

/**An implementation of graphical item picker for scale bars*/
public class ImageFrameExamplePicker extends GraphicalItemPicker<ImagePanelGraphic>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	{super.optionname="Choose frame for image panels";}
	
	/**creates a scale bar picker with the given example scale bar*/
	public ImageFrameExamplePicker(ImagePanelGraphic model) {
		super(model);
	}

	/**returns true if the object is an image panel in a type of figure that requres a frame*/
	boolean isDesirableItem(Object o) {
		if (!(o instanceof ImagePanelGraphic))
			return false;
		if(o instanceof KnowsParentLayer) {
			FigureType figureType = FigureOrganizingLayerPane.findFigureOrganizer((KnowsParentLayer) o).getFigureType();
			if(!figureType.shouldHaveFrameforImagePanels()) {
				return false;
			}
			}
		return true;
	}
	
	/**if the targeted item is a scale bar, alters its appearance to match the example scale bar.
	 * Does nothing unless there is an example scale bar to act as a model*/
	@Override
	public AbstractUndoableEdit applyProperties(Object item) {
		if (this.getModelItem()==null) return null;
		
		if (item instanceof ImagePanelGraphic) {
				ImagePanelGraphic item2=(ImagePanelGraphic) item;
				AbstractUndoableEdit undo = item2.provideUndoForDialog();
				
				item2.setFrameWidth(this.getModelItem().getFrameWidthH());
				item2.setFrameColor(this.getModelItem().getFrameColor());
				
			return undo;
		}
		return null;
	}
	




}
