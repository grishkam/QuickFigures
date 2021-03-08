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
package figureFormat;

import javax.swing.undo.AbstractUndoableEdit;

import figureOrganizer.MultichannelDisplayLayer;
import logging.IssueLog;
import undo.CombinedEdit;
import undo.PanelManagerUndo;

/**A subclass of item picker for multichannel display layers*/
public class MultichannelDisplayPicker extends
		ItemPicker<MultichannelDisplayLayer> {
	
	/**set to true if the scale factor from the model should also be used, false otherwise*/
	public boolean doesPreprocess=true;

	public MultichannelDisplayPicker() {
		this(null);
	}

	public MultichannelDisplayPicker(MultichannelDisplayLayer model) {
		super(model);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	boolean isDesirableItem(Object o) {
		if (o instanceof MultichannelDisplayLayer) {
			return true;
		}
		return false;
	}

	/**if the object given is a multichannel display layer, changes its settings to match the model*/
	@Override
	public AbstractUndoableEdit applyProperties(Object item) {
		
		if (!isDesirableItem(item)) {
			return null;
			
		}
		try {
		MultichannelDisplayLayer imageMulti=(MultichannelDisplayLayer) item;
		CombinedEdit undo = PanelManagerUndo.createFor(imageMulti);
		imageMulti.partialCopyTraitsFrom(this.getModelItem(), doesPreprocess);
		
		return undo;
		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	/**returns true if the scale used by the model available is reasonable for the target image.
	 * Reasonable is defined as producing final image panels sizes that are below a certain limit */
	public boolean isProprocessSuitable(MultichannelDisplayLayer imageMulti) {
		double pScale=1;
		if (getModelItem()!=null)
			pScale = getModelItem().getPreprocessScale();
		/**decides whether the 'preprocess' is appropriate or not*/
		boolean b = imageMulti.getMultiChannelImage().getDimensions().getWidth()*pScale<500;
		return b;
	}

	@Override
	public String getOptionName() {
		return "Pick Multichannel Image";
	}
	
	/**sets the multi channel display layer that is to be used as a model item*/
	public void setModelItem(Object modelItem) {
		if (!this.isDesirableItem(modelItem)) return;
		try{super.setModelItem(((MultichannelDisplayLayer)modelItem).copy());} catch (Exception e) {
			e.printStackTrace();
			IssueLog.log("problem. wrong class");
		}
	}

}
