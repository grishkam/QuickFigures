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
 * Date Modified: April 11, 2021
 * Version: 2021.1
 */
package figureFormat;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.undo.AbstractUndoableEdit;

import channelMerging.PreProcessInformation;
import figureOrganizer.MultichannelDisplayLayer;
import logging.IssueLog;
import messages.ShowMessage;
import undo.CombinedEdit;
import undo.PanelManagerUndo;

/**A subclass of item picker for multichannel display layers*/
public class MultichannelDisplayPicker extends
		ItemPicker<MultichannelDisplayLayer> {
	
	/**set to true if the scale factor from the model should also be used, false otherwise*/
	public boolean doesPreprocess=true;
	
	/**if this is set to a value, will overrule the scale factor */
	public Double forceScale=null;

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
			
			compareSizes(imageMulti);
			
			if(forceScale!=null) {
				imageMulti.setPreprocessScale(forceScale);
				imageMulti.updatePanels();
				
			}
			
		
		return undo;
		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	/**work in progress, will compare size of panels in the target to the model
	 * If there is a large size difference
	 * @param imageMulti
	 */
	protected void compareSizes(MultichannelDisplayLayer imageMulti) {
		if(forceScale!=null)
			return;//
		
		try {
			
		} catch (Exception e) {
			IssueLog.logT(e);
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

	/**sets up this picker for the target image display layer
	 * if image or roi is small, will change scale
	 * @param multichannelDisplayLayer
	 */
	public void setScaleAppropriateFor(MultichannelDisplayLayer multichannelDisplayLayer) {
		if (getModelItem()==null) {
			this.setModelItem(multichannelDisplayLayer.similar());
		}
		if( multichannelDisplayLayer==null)
		{IssueLog.log("cannot work with null");}
			else
			{
				
				computeScaleFactorForSmallImage(multichannelDisplayLayer);
			}
		
	}

	/**Calculates a scale factor that will allow reasonable looking figures to be made from a small region of interest 
	 * @param multichannelDisplayLayer
	 */
	protected void computeScaleFactorForSmallImage(MultichannelDisplayLayer multichannelDisplayLayer) {
		Dimension dimensions = multichannelDisplayLayer.getSlot().getMultichannelImage().getDimensions();
		PreProcessInformation modifications = multichannelDisplayLayer.getSlot().getModifications();
		if(modifications==null)  modifications=new PreProcessInformation(1);
		Rectangle2D info = modifications.getOutputDimensions(dimensions);
		double scale=multichannelDisplayLayer.getPreprocessScale();
		double targetSize=200;
		
		int minHeight = 80;
		if(info.getWidth()<minHeight||info.getHeight()<minHeight) {
			
			double newscale = Math.ceil(targetSize/info.getHeight())*scale;
				if (ShowMessage.showOptionalMessage("Small image", true, "It looks like your region of interest or your image is very small", "will chnage the scale for the default template and scale up the image", "You can delete or override the default template later", "Is this ok?"))
				  forceScale=newscale;
		}
	}

}
